/**
  * author: nevill@163.com
  * weibo / twitter: @nevill
  */

package main

import ( "container/list"
         "encoding/base64"
         "fmt"
         "io/ioutil"
         "net/http"
         "os"
         "regexp"
         "strings"
         )

const BaseUrl = "http://baike.baidu.com/view"
const RootDir = "/tmp/baike.baidu"
const MaxTasks = 1000

type UrlList struct {
    urls list.List
    Count int
} 

func GetFileName(url string) string {
    pos := strings.Index(url, BaseUrl)
    if pos == -1 {
        return ""
    }
    return url[len(BaseUrl):]
}

// Return the content of the given url
func FetchAndSave(url string) (string, bool) {
    fileName := RootDir + "/" + base64.StdEncoding.EncodeToString([]byte(GetFileName(url)))
    //file, ferr := os.Open(fileName)
    content, ferr := ioutil.ReadFile(fileName)
    if ferr == nil {
        return string(content), true
    }

    fmt.Print("File Not exists: " + fileName)

    resp, err := http.Get(url)
    if err != nil {
        return "", true
    }

    responseBody, _ := ioutil.ReadAll(resp.Body)
    ioutil.WriteFile(fileName, responseBody, 0644)
    defer resp.Body.Close()
    fmt.Println(", created !")
    return string(responseBody), false
}

// Strip out all the related URLs to fetch
func ExtractUrls(content string) []string {
    return regexp.MustCompile(`http://baike.baidu.com/view/[/\w\.-]+`).FindAllString(content, -1)
}

func (l *UrlList) AddUrls(links []string) {
    for _, link := range links {
        l.urls.PushBack(link)
    }
}

func (l *UrlList) FetchOne() string {
    if l.urls.Len() > 0 {
        l.Count ++
        ele := l.urls.Remove(l.urls.Front())
        return ele.(string)
    }
    return ""
}

// usage:
//        go run fetcher.go "http://baike.baidu.com/view/1089203.htm"
func main() {
    //url := "http://baike.baidu.com/view/1089203.htm"
    url := os.Args[1]

    lst := UrlList{}
    lst.AddUrls([]string{ url })
    c := make(chan int)

    count := 0

    for link := lst.FetchOne(); count <= MaxTasks && link != ""; {
        go func() {
            //fmt.Println("Fetching ... " + link)
            text, existed := FetchAndSave(link)
            if !existed {
                lst.AddUrls(ExtractUrls(text))
                count ++
                fmt.Println(count)
            }
            c <- 1
        }()
        <- c
        link = lst.FetchOne()
    }
}
