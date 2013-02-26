/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jboss.forge.shell.console.jline.internal;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;

/**
 * This is awkward and inefficient, but probably the minimal way to add UTF-8 support to JLine
 * 
 * @author <a href="mailto:Marc.Herbert@continuent.com">Marc Herbert</a>
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 * @since 2.0
 */
public final class ReplayPrefixOneCharInputStream
         extends InputStream
{
   private byte firstByte;

   private int byteLength;

   private InputStream wrappedStream;

   private int byteRead;

   private final String encoding;

   public ReplayPrefixOneCharInputStream(final String encoding)
   {
      assert encoding != null;
      this.encoding = encoding;
   }

   public String getEncoding()
   {
      return encoding;
   }

   public void setInput(final int recorded, final InputStream wrapped) throws IOException
   {
      this.byteRead = 0;
      this.firstByte = (byte) recorded;
      this.wrappedStream = wrapped;

      byteLength = 1;
      if (encoding.equalsIgnoreCase("UTF-8"))
      {
         setInputUTF8(recorded, wrapped);
      }
      else if (encoding.equalsIgnoreCase("UTF-16"))
      {
         byteLength = 2;
      }
      else if (encoding.equalsIgnoreCase("UTF-32"))
      {
         byteLength = 4;
      }
   }

   public void setInputUTF8(final int recorded, final InputStream wrapped) throws IOException
   {
      // 110yyyyy 10zzzzzz
      if ((firstByte & (byte) 0xE0) == (byte) 0xC0)
      {
         this.byteLength = 2;
      }
      // 1110xxxx 10yyyyyy 10zzzzzz
      else if ((firstByte & (byte) 0xF0) == (byte) 0xE0)
      {
         this.byteLength = 3;
      }
      // 11110www 10xxxxxx 10yyyyyy 10zzzzzz
      else if ((firstByte & (byte) 0xF8) == (byte) 0xF0)
      {
         this.byteLength = 4;
      }
      else
      {
         throw new IOException(MessageFormat.format("Invalid UTF-8 first byte: {0}", firstByte));
      }
   }

   @Override
   public int read() throws IOException
   {
      if (available() == 0)
      {
         return -1;
      }

      byteRead++;

      if (byteRead == 1)
      {
         return firstByte;
      }

      return wrappedStream.read();
   }

   /**
    * InputStreamReader is greedy and will try to read bytes in advance. We do NOT want this to happen since we use a
    * temporary/"losing bytes" InputStreamReader above, that's why we hide the real wrappedStream.available() here.
    */
   @Override
   public int available()
   {
      return byteLength - byteRead;
   }
}