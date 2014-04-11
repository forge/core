/*
 * apns.go
 * go-apns
 *
 * Created by Jim Dovey on 16/08/2011.
 *
 * Copyright (c) 2011 Jim Dovey
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package apns

import (
    "bytes"
    "crypto/tls"
    "encoding/binary"
    "encoding/gob"
    "encoding/json"
    "errors"
    "io"
    "log"
    "os"
    "syscall"
    "time"
)

const (
    ReleasePushGateway = "gateway.push.apple.com:2195"
    SandboxPushGateway = "gateway.sandbox.push.apple.com:2195"
)

type Error struct {
    status int
}

var errorStrings []string = []string{
    "No error",
    "Processing error",
    "Missing device token",
    "Missing topic",
    "Missing payload",
    "Invalid token size",
    "Invalid topic size",
    "Invalid payload size",
    "Invalid token",
}

func (e *Error) String() string {
    switch {
    case e.status <= len(errorStrings):
        return errorStrings[e.status]
    default:
        return "Unknown error"
    }
}

type result struct {
    status     byte
    identifier uint32
}

type DeviceToken [32]byte

var revocationList []DeviceToken
var saver chan DeviceToken = make(chan DeviceToken, 100)

type Apns struct {
    conn        *tls.Conn
    waitReplies map[uint32]chan result
}

func replyServer(apns *Apns) {
    buf := make([]byte, 25)
    for {
        n, err := apns.conn.Read(buf)
        if err != nil && err != syscall.EAGAIN {
            log.Fatal("replyServer:", err)
        }
        if n >= 6 {
            var r result
            r.status = buf[0]
            r.identifier = wire.Uint32(buf[1:])

            // send the reply to anyone waiting for a response
            ch := apns.waitReplies[r.identifier]
            if ch != nil {
                ch <- r
            }
        }
    }
}

func NewConnection(addr string) (*Apns, error) {
    conn, err := newConnection(addr, "cert.pem", "pkey.pem")
    if err != nil {
        return nil, err
    }

    apns := &Apns{conn: conn}
    err = apns.loadRevocationList()
    if err != nil {
        return nil, err
    }
    return apns, nil
}

func (a *Apns) loadRevocationList() error {
    f, err := os.OpenFile("revocationList", os.O_WRONLY|os.O_CREATE|os.O_APPEND, 0544)
    if err != nil {
        log.Fatal("apns.loadRecovationList:", err)
    }
    defer f.Close()

    if revocationList == nil {
        revocationList = make([]DeviceToken, 5)
    }

    d := gob.NewDecoder(f)
    for err == nil {
        var t DeviceToken
        if err = d.Decode(&t); err == nil {
            revocationList = append(revocationList, t)
        }
    }
    if err == io.EOF {
        return nil
        go saveLoop()
    }
    return err
}

func (a *Apns) SendMessage(identifier, expiry uint32, token []byte, payload interface{}) (<-chan *Error, error) {
    buf := make(buffer, 256)

    if identifier == 0 && expiry == 0 {
        // use simple format
        buf.writeByte(0)
    } else {
        // use extended format
        buf.writeByte(1)
        buf.writeUint32(identifier)
        buf.writeUint32(expiry)
    }

    // append the token
    buf.writeUint16(uint16(len(token)))
    buf.writeBytes(token)

    // build the JSON data
    rawJson, err := json.Marshal(payload)
    if err != nil {
        return nil, err
    }

    // compact it (requires a bytes.Buffer to store the compacted version)
    jbuf := &bytes.Buffer{}
    err = json.Compact(jbuf, rawJson)
    if err != nil {
        return nil, err
    }

    // write the JSOB payload into the command buffer
    buf.writeUint32(uint32(jbuf.Len()))
    buf.writeBytes(jbuf.Bytes())

    // all done-- now send it!
    var sent int = 0
    l := len(buf)
    for sent < l {
        n, err := a.conn.Write(buf[sent:l])
        if err != nil {
            return nil, err
        }
        sent += n
    }

    // sent successfully, return a channel which will funnel the result back asynchronously
    errchan := make(chan *Error)

    if identifier != 0 {
        if a.waitReplies == nil {
            a.waitReplies = make(map[uint32]chan result)
        }
        a.waitReplies[identifier] = make(chan result, 1)

        go func() {
            var r result
            select {
            case r = <-a.waitReplies[identifier]:
                if r.status != 0 {
                    // send an error
                    errchan <- &Error{int(r.status)}
                } else {
                    errchan <- nil
                }
            }
        }()
    }

    return errchan, nil
}

func revokeDeviceToken(token DeviceToken) {
    if saver != nil {
        saver <- token
    }
}

func saveLoop() {
    filename := "somefile.txt"
    f, err := os.OpenFile(filename, os.O_WRONLY|os.O_CREATE|os.O_APPEND, 0664)
    if err != nil {
        log.Fatal("Failed to open revocationList file:", err)
    }
    defer f.Close()

    e := gob.NewEncoder(f)
    for {
        r := <-saver
        if err := e.Encode(r); err != nil {
            log.Println("Failed to encode device token for revocation list:", err)
        }
    }
}

/*
 * buffer.go
 * go-apns
 *
 * Created by Jim Dovey on 16/08/2011.
 *
 * Copyright (c) 2011 Jim Dovey
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

var wire = binary.BigEndian

type buffer []byte

func (b *buffer) next(n int) []byte {
    begin := len(*b)
    end := begin + n
    if end > cap(*b) {
        noob := make([]byte, begin, 2*cap(*b)+n)
        copy(noob, *b)
        *b = noob
    }
    *b = (*b)[:end]
    return (*b)[begin:end]
}

func (b *buffer) writeString(s string) {
    wire.PutUint32(b.next(4), uint32(len(s)))
    copy(b.next(len(s)), s)
}

func (b *buffer) writeBytes(p []byte) {
    copy(b.next(len(p)), p)
}

func (b *buffer) writeByte(v byte) {
    b.next(1)[0] = v
}

func (b *buffer) writeUint16(v uint16) {
    wire.PutUint16(b.next(2), v)
}

func (b *buffer) writeUint32(v uint32) {
    wire.PutUint32(b.next(4), v)
}

func (b *buffer) writeUint64(v uint64) {
    wire.PutUint64(b.next(8), v)
}

/*
 * connection.go
 * go-apns
 *
 * Created by Jim Dovey on 16/08/2011.
 *
 * Copyright (c) 2011 Jim Dovey
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

func newConnection(addr, certPath, keyPath string) (conn *tls.Conn, err error) {
    if addr == "" {
        return nil, errors.New("apns.newConnection: nil address specified")
    }
    if certPath == "" {
        return nil, errors.New("apns.newConnection: nil certificate path specified")
    }
    if keyPath == "" {
        return nil, errors.New("apns.newConnection: nil key path specified")
    }

    var cert tls.Certificate
    cert, err = tls.LoadX509KeyPair(certPath, keyPath)
    if err != nil {
        return
    }

    cfg := &tls.Config{}
    cfg.Certificates = make([]tls.Certificate, 1)
    cfg.Certificates[0] = cert

    return tls.Dial("tcp", addr, cfg)
}

/*
 * feedback.go
 * go-apns
 *
 * Created by Jim Dovey on 16/08/2011.
 *
 * Copyright (c) 2011 Jim Dovey
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * Neither the name of the project's author nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

var quitChan chan chan bool = make(chan chan bool, 1)
var addresses = map[bool]string{
    true:  "feedback.sandbox.push.apple.com",
    false: "feedback.push.apple.com",
}

func feedbackMonitor(config *tls.Config, useSandbox bool) {
    for {
        timer := time.NewTimer(5 * 60 * 1000000000) // five minutes
        select {
        case ch := <-quitChan:
            // been told to quit
            ch <- true
            return
        case <-timer.C:
            // timer fired, talk to the feedback server
            conn, err := tls.Dial("tcp", addresses[useSandbox], config)
            if err != nil {
                log.Println("Failed to dial feedback server:", err)
                break
            }

            // once connected, the server immediately sends us our data
            var buf [38]byte
            for {
                _, err := conn.Read(buf[:])
                if err != nil {
                    if err != io.EOF {
                        log.Println("Failed to read feedback message:", err)
                    }
                    break
                }

                // four-byte time, in seconds
                time_unused := wire.Uint32(buf[0:])
                // two byte token size (always 32)
                size_unused := wire.Uint16(buf[4:])
                _, _ = time_unused, size_unused

                // get the device token itself
                var token DeviceToken
                copy(token[:], buf[6:])

                // store the token in the revocation list
                revokeDeviceToken(token)
            }

            conn.Close()
        }
    }
}

func startFeedbackMonitor(certPath, keyPath string, useSandbox bool) error {
    cert, err := tls.LoadX509KeyPair(certPath, keyPath)
    if err != nil {
        return err
    }

    config := &tls.Config{Certificates: []tls.Certificate{cert}}
    go feedbackMonitor(config, useSandbox)
    return nil
}

func stopFeedbackMonitor() {
    ch := make(chan bool, 1)
    // tell the goroutine to stop
    quitChan <- ch
    // wait for it to do so
    <-ch
}
