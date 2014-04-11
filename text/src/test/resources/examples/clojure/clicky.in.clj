(ns clicky
  (:use compojure)
  (:use net.licenser.sandbox)
  (:use net.licenser.sandbox.matchers)
  (:require  [clojure.contrib.str-utils2 :as su])
  (:use clojure.contrib.duck-streams)
  (:gen-class))
  
  
(def *data-dir* "./data")

(def *base-name* "clicky.sandbox")

(defn parital-namespace-matcher
  "Creates a tester that whitelists all functions within a namespace."
  [& namespaces]
  (fn [form]
    (cond
     (= (type form) clojure.lang.Var)
     (let [ns (str (ns-name (:ns (meta form))))]
       (map #(zero? (.indexOf ns (str %))) namespaces))
     (= (type form) java.lang.Class)
     (let [ns (second (re-find #"^class (.*)\.\w+$" (str form)))]
       (map #(zero? (.indexOf ns (str %))) namespaces))
     true
      '())))

(def *tester* (extend-tester secure-tester (whitelist (function-matcher 'def 'require 'use) (parital-namespace-matcher 'clicky.sandbox))))

(defn page
  [& body]
  (html [:title "Clicky! Bunti!"] (vec (concat [:body] body))))

(defn uri-to-ns
  [uri]
  (symbol (su/replace (su/drop uri 1) \/ \.)))

(defn exec-file
  [file ns]
  (let [sb (new-sandbox-compiler :namespace ns :tester *tester* :timeout 500)]
    (with-open [r (java.io.PushbackReader. (reader file))] ((sb (read r)) {}))))

(defn run-file
  [ns file uri]
  (let [ns (symbol (str *base-name* "." ns))]
    (try
     (let [res (exec-file file ns)]
      (page [:body res [:p [:a {:href (str uri "?edit=") } "edit"]]]))
     (catch Exception e
       (page [:body e])))))

(defn clicly-handler [request]
  (html))
  
(defn uri-to-file-name
  [uri]
  (str *data-dir*  uri ".clj"))

(defroutes my-app
  (GET "/"
       (html [:h1 "Hello World"]))
  (GET "*"
       (let [uri (:uri request)
	     file-name (uri-to-file-name uri)
	     file (java.io.File. file-name)]
	 (if (or (:edit params) (not (.exists file)))
	   (page [:form {:action (:uri request) :method :post}
		  [:textarea {:name "code"} (if (.exists file) (read-lines file-name))] :br [:input  {:type "submit" :value "Save"}]])
	   (run-file (uri-to-ns uri) file uri))))
  (POST "*"
	(let [uri (:uri request)
	      file-name (uri-to-file-name uri)
	      file (java.io.File. file-name)
	      code (:code (:form-params request))]
	  (try
	   (read-string code)
	   (.mkdirs (.getParentFile file))
	   (spit file-name code)
	   (run-file (uri-to-ns uri) file uri)
	   (catch Exception e (page [:h1 "Ohhh no! The code could not be read!"] [:pre code] e))))))

(defn first-update []
  (let [files (filter #(.isFile %) (file-seq (java.io.File. *data-dir*)))]
    (loop [c (* (count files) (count files))]
      (if 
	  (try
	   (dorun
	    (map #(apply exec-file %)
		 (sort-by (fn [& _] (rand-int 42)) (map 
						    (fn [file] (vector file (symbol (str *base-name* "." (su/replace (second (re-find #"^\./data/(.*)\.clj$" (str file))) \/ \.)))))
						    files))))
	   true
	   (catch Exception e false))
	true (if (> c 0) (recur (dec c)))))))

(defn -main []
  (first-update)
  (run-server {:port 8080}
	      "/*" (servlet my-app)))
