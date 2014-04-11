(ns clj-highlight.core
  (:use clj-highlight.mangler)
  (:import java.util.Scanner))


(defn- next-token [s idx token-def defs states]
  (if (empty? defs)
    [[:error (str (first (subs s idx)))  {:state states :index idx}] states token-def (get token-def (first states))]
    (let [[matcher & defs] defs] 
      (if-let [result (matcher s idx states token-def)]
	result
	(recur s idx token-def defs states)))))

(defn- token-seq* [s idx size token-def defs states] 
  (lazy-seq
   (if (= idx size)
     '()
     (let [[token states token-def defs] (next-token s idx token-def defs states)]
       (cons token (token-seq* s (+ idx (count (fnext token))) size token-def defs states))))))

(defn tokenizer 
  "Creates a tokenizer for a given syntax definition."
  [syntax]
  (let [tkn
    	(fn tokenizer*
	  ([string state]
	     (token-seq* string 0 (count string) syntax (get syntax state) (list state)))
	  ([string]
	  (tokenizer* string :initial)))]
    (if-let [keywords (:keywords syntax)]
      (mangle-tokens :identifier (fn [k t s] (if (keywords t) [:keyword t s] [k t s])) tkn)
      tkn)))

(defn highlighter
  "Creates a highlighter consisting of a given syntax, a output generator and a set of manglers.
The manglers are applied to the token stream in the order before it is passed to the output generator."
  [syntax output & manglers]
  (let [tkn (reduce (fn [tkn mgl] (mgl tkn)) (tokenizer syntax) manglers)]
    (fn highlighter* [code]
      (output (tkn code)))))

       (ns clj-highlight.mangler)

(defn mangle-tokens [kind mangle-fn tokenizer]
  (fn mangle-tokens*
    ([string state]
       (map (fn [[k t s]]
	 (if (or (nil? kind) (= k kind))
	   (mangle-fn k t s)
	   [k t s]))
	    (tokenizer string state)))
    ([string]
       (mangle-tokens* string :initial))))

(defn new-mangler [kind mangle-fn]
  (fn [tokenizer]
    (mangle-tokens kind mangle-fn tokenizer)))
(ns clj-highlight.output.hiccup
  (:use clj-highlight.mangler))

; Copied from hiccup.
(defn- escape-html
  "Change special characters into HTML character entities."
  [text]
  (.. #^String (str text)
    (replace "&"  "&amp;")
    (replace "<"  "&lt;")
    (replace ">"  "&gt;")
    (replace "\"" "&quot;")))

(def default-stype-map
     {:identifier nil
      :keyword     "r"
      :symbol     "sy"
      :string     "s"
      :paren      "of"
      :comment    "c"
      :operator   "cl"
      :number     "i"
      })

(defn- hiccupify-tokens [style-map tokens last last-cl]
  (lazy-seq 
   (if (empty? tokens)
     (list (if last-cl [:span {:class last-cl} last] last))
     (let [[k t _] (first tokens)
	   cl (style-map k)]
       (cond
	(nil? last)
	(hiccupify-tokens style-map (rest tokens) t cl)
	(= last-cl cl)
	(hiccupify-tokens style-map (rest tokens) (str last t) cl)
	:else
	(cons (if last-cl [:span {:class last-cl} last] last) (hiccupify-tokens style-map (rest tokens) t cl)))))))

  
(defn to-hiccup 
  ([style-map root-class]
     (fn [tokens]
       (vec (concat [:span {:class root-class}] (hiccupify-tokens style-map tokens nil nil)))))
  ([]
     (to-hiccup default-stype-map "code" )))


(def newline-to-br-mangler 
     (new-mangler
      :space
      (fn [k t s]
	[k (.replace t "\n" "<br/>") s])))

(def html-escape-mangler 
     (new-mangler
      :space
      (fn [k t s]
	[k (escape-html t) s])))(ns clj-highlight.output.html
  (:use clj-highlight.mangler clj-highlight.output.hiccup))


(defn span [c content]
  (str "<span class='" c "'>" (escape-html content) "</span>"))

(defn html-to-stream [stream root-class style-map tokenstream]
  (binding [*out* stream]
    (print (str "<span class='" root-class "'>"))
    (loop [[kind token & _] (first tokenstream) tkns (next tokenstream)]
      (let [style (get style-map kind (name kind))]
	(print (span style token))
	(if-let [[f & n] (next tokenstream)]
	  (recur f n))))
    (print "</span>")))(ns clj-highlight.syntax.clojure
  (:use clj-highlight.syntax.general))

(def clojure-keywords
     #{"def", "if", "do", "let", "quote", "var", "fn", "loop", "recur", "throw", "try", "catch", "monitor-enter", "monitor-exit", ".", "new", "nil"
       "+", "-", "->", "->>", "..", "/", "<", "<=", "=", "==", ">", ">=", "accessor", "aclone", "add-classpath", "add-watch", "agent", "agent-error", "agent-errors", "aget", "alength", "alias", "all-ns", "alter", "alter-meta!", "alter-var-root", "amap", "ancestors", "and", "apply", "areduce", "array-map", "aset", "aset-boolean", "aset-byte", "aset-char", "aset-double", "aset-float", "aset-int", "aset-long", "aset-short", "assert", "assoc", "assoc!", "assoc-in", "associative?", "atom", "await", "await-for", "bases", "bean", "bigdec", "bigint", "binding", "bit-and", "bit-and-not", "bit-clear", "bit-flip", "bit-not", "bit-or", "bit-set", "bit-shift-left", "bit-shift-right", "bit-test", "bit-xor", "boolean", "boolean-array", "booleans", "bound-fn", "bound-fn*", "bound?", "butlast", "byte", "byte-array", "bytes", "case", "cast", "char", "char-array", "char-escape-string", "char-name-string", "char?", "chars", "class", "class?", "clear-agent-errors", "clojure-version", "coll?", "comment", "commute", "comp", "comparator", "compare", "compare-and-set!", "compile", "complement", "concat", "cond", "condp", "conj", "conj!", "cons", "constantly", "construct-proxy", "contains?", "count", "counted?", "create-ns", "create-struct", "cycle", "dec", "decimal?", "declare", "definline", "defmacro", "defmethod", "defmulti", "defn", "defn-", "defonce", "defprotocol", "defrecord", "defstruct", "deftype", "delay", "delay?", "deliver", "denominator", "deref", "derive", "descendants", "disj", "disj!", "dissoc", "dissoc!", "distinct", "distinct?", "doall", "doc", "dorun", "doseq", "dosync", "dotimes", "doto", "double", "double-array", "doubles", "drop", "drop-last", "drop-while", "empty", "empty?", "ensure", "enumeration-seq", "error-handler", "error-mode", "eval", "even?", "every?", "extend", "extend-protocol", "extend-type", "extenders", "extends?", "false?", "ffirst", "file-seq", "filter", "find", "find-doc", "find-ns", "find-var", "first", "float", "float-array", "float?", "floats", "flush", "fn?", "fnext", "for", "force", "format", "future", "future-call", "future-cancel", "future-cancelled?", "future-done?", "future?", "gen-class", "gen-interface", "gensym", "get", "get-in", "get-method", "get-proxy-class", "get-thread-bindings", "get-validator", "hash", "hash-map", "hash-set", "identical?", "identity", "if-let", "if-not", "ifn?", "import", "in-ns", "inc", "init-proxy", "instance?", "int", "int-array", "integer?", "interleave", "intern", "interpose", "into", "into-array", "ints", "io!", "isa?", "iterate", "iterator-seq", "juxt", "key", "keys", "keyword", "keyword?", "last", "lazy-cat", "lazy-seq", "letfn", "line-seq", "list", "list*", "list?", "load", "load-file", "load-reader", "load-string", "loaded-libs", "locking", "long", "long-array", "longs", "macroexpand", "macroexpand-1", "make-array", "make-hierarchy", "map", "map?", "mapcat", "max", "max-key", "memfn", "memoize", "merge", "merge-with", "meta", "methods", "min", "min-key", "mod", "name", "namespace", "neg?", "newline", "next", "nfirst", "nil?", "nnext", "not", "not-any?", "not-empty", "not-every?", "not=", "ns", "ns-aliases", "ns-imports", "ns-interns", "ns-map", "ns-name", "ns-publics", "ns-refers", "ns-resolve", "ns-unalias", "ns-unmap", "nth", "nthnext", "num", "number?", "numerator", "object-array", "odd?", "or", "parents", "partial", "partition", "pcalls", "peek", "persistent!", "pmap", "pop", "pop!", "pop-thread-bindings", "pos?", "pr", "pr-str", "prefer-method", "prefers", "print", "print-namespace-doc", "print-str", "printf", "println", "println-str", "prn", "prn-str", "promise", "proxy", "proxy-mappings", "proxy-super", "push-thread-bindings", "pvalues", "quot", "rand", "rand-int", "range", "ratio?", "rationalize", "re-find", "re-groups", "re-matcher", "re-matches", "re-pattern", "re-seq", "read", "read-line", "read-string", "reduce", "ref", "ref-history-count", "ref-max-history", "ref-min-history", "ref-set", "refer", "refer-clojure", "reify", "release-pending-sends", "rem", "remove", "remove-all-methods", "remove-method", "remove-ns", "remove-watch", "repeat", "repeatedly", "replace", "replicate", "require", "reset!", "reset-meta!", "resolve", "rest", "restart-agent", "resultset-seq", "reverse", "reversible?", "rseq", "rsubseq", "satisfies?", "second", "select-keys", "send", "send-off", "seq", "seq?", "seque", "sequence", "sequential?", "set", "set-error-handler!", "set-error-mode!", "set-validator!", "set?", "short", "short-array", "shorts", "shutdown-agents", "slurp", "some", "sort", "sort-by", "sorted-map", "sorted-map-by", "sorted-set", "sorted-set-by", "sorted?", "special-form-anchor", "special-symbol?", "split-at", "split-with", "str", "string?", "struct", "struct-map", "subs", "subseq", "subvec", "supers", "swap!", "symbol", "symbol?", "sync", "syntax-symbol-anchor", "take", "take-last", "take-nth", "take-while", "test", "the-ns", "thread-bound?", "time", "to-array", "to-array-2d", "trampoline", "transient", "tree-seq", "true?", "type", "unchecked-add", "unchecked-dec", "unchecked-divide", "unchecked-inc", "unchecked-multiply", "unchecked-negate", "unchecked-remainder", "unchecked-subtract", "underive", "update-in", "update-proxy", "use", "val", "vals", "var-get", "var-set", "var?", "vary-meta", "vec", "vector", "vector-of", "vector?", "when", "when-first", "when-let", "when-not", "while", "with-bindings", "with-bindings*", "with-in-str", "with-local-vars", "with-meta", "with-open", "with-out-str", "with-precision", "xml-seq", "zero?", "zipmap", "true", "false", "*", "*1", "*2", "*3", "*agent*", "*clojure-version*", "*command-line-args*", "*compile-files*", "*compile-path*", "*e", "*err*", "*file*", "*flush-on-newline*", "*in*", "*ns*", "*out*", "*print-dup*", "*print-length*", "*print-level*", "*print-meta*", "*print-readably*", "*read-eval*", "*warn-on-reflection*"}) 

(let [basic-identifier* "[a-zA-Z$%$*_+!?&<>=-][a-zA-Z0-9§$&*=+!_?<>-]*"
      identifier* (str basic-identifier*"(?:\\."basic-identifier*")*+(?:/"basic-identifier*")?+")
      symbol* (str "::?"identifier*"\\.?+")
      number-matcher (fn [s idx]
		       (let [s (subs s idx)]
			 (if-let [p (re-find #"^(\d+)r" s)]
			   (let [[prefix precision] p
				 precision (Integer/parseInt precision)
				 number-re (re-pattern (str "^" prefix "[" (subs didgets 0 precision) "]++"))]
			     (if-let [num (re-find number-re s)]
			       num
			       nil))
			   (if-let [num (re-find #"^\d+(?:(?:/\d+)|(?:(?:\.\d*)?(?:e[+-]\d+)?))" s)]
			     num
			     nil))))
      ]
  (def clj-syntax
       {:keywords clojure-keywords
	:initial
	(list
;	 (fn space-token [string idx states token-def] (
	 (re-token #"[\s\n,]++" :space)
;							string idx states token-def))
;	 (fn paren-token [string idx states token-def] (
	 (re-token #"[\(\)\[\]{}]" :paren)
;							string idx states token-def))
;	 (fn ident-token [string idx states token-def] (
	 (re-token identifier* :identifier) 
;							string idx states token-def))
;	 (fn ident2-token [string idx states token-def] (
	 (re-token #"\.++" :identifier)
;							 string idx states token-def))
;	 (fn sym-token [string idx states token-def] (
	 (re-token symbol* :symbol)
;						      string idx states token-def))
;	 (fn sym2-token [string idx states token-def] (
	 (re-token #"::?\(.++" :symbol)
;						       string idx states token-def))
;	 (fn num-token [string idx states token-def] (
	 (token number-matcher :number)
;						      string idx states token-def))
;	 (fn op-token [string idx states token-def] (
	 (re-token #"[#.'`~@^]++" :operator)
;						     string idx states token-def))
;	 (fn char-token [string idx states token-def] (
	 (re-token #"\"[^\"\\]*(?:[^\"\\]|\\.)*+\"" :string)
;						       string idx states token-def))
;	 (fn ident3-token [string idx states token-def] (
	 (re-token #"/" :identifier)
;							 string idx states token-def))
;	 (fn comm-token [string idx states token-def] (
	 (re-token #";[^\n]*+" :comment)
;						       string idx states token-def))
;	 (fn str-token [string idx states token-def] (
	 (re-token #"\\(?:.|[a-z]++)" :string)
;						      string idx states token-def))
	 )}))
(ns clj-highlight.syntax.general)

(def didgets "0123456789abcdefghijklmnopqrstuvwxyz")

(def *do-profile* false)

(def *profile* (agent {}))

(defn- prof [a idx time]
  (update-in a [idx] conj time))

(defn report-profileing [profiling]
  (sort-by second (map (fn [[idx times]] 
	 (let [cnt (count times)]
	   [idx cnt (reduce + times) (apply min times) (apply max times)])) profiling)))

(defmacro benchmark 
  ([idx form]
     (if *do-profile*
       `(let [t0# (. System currentTimeMillis)
	      r# ~form
	      t1# (. System currentTimeMillis)]
	  (send *profile* prof ~idx (- t1# t0#))
	  r#)))
  ([form] 
     (if *do-profile*
       `(let [t0# (. System currentTimeMillis)
	      r# ~form
	      t1# (. System currentTimeMillis)]
	  (send *profile* prof (first '~form) (- t1# t0#))
	  r#))))

(defmacro profiled [form]
  `(binding [*do-profile* true]
     ~form))
	   

(defn token [matcher kind & [new-state info-fn]]
  (cond
   (nil? new-state)
   (fn [string idx states token-def]
     (if-let [token (matcher string idx)]
       [[kind token (if info-fn (info-fn kind token states) {:state states :index idx})] states token-def ((first states) token-def)]))
   (= new-state :pop)
   (fn [string idx states token-def]
     (if-let [token (matcher string idx)]
       (let [sts (next states)]
	 [[kind token (if info-fn (info-fn kind token states) {:state states :index idx})] sts token-def ((first sts) token-def)])))
   :else
   (fn [string idx states token-def]
     (if-let [token (matcher string idx)]
       (let [sts (conj states new-state)]
	 [[kind token (if info-fn (info-fn kind token states) {:state states :index idx})] sts token-def ((first sts) token-def)])))))


(defn re-token [re kind & [new-state]]
  (let [pattern (re-pattern (str "^(?:" re ")"))]
    (token 
     (fn [string idx]
       (benchmark pattern (re-find pattern (subs string idx))))
     kind
     new-state)))(ns clj-highlight.syntax.java
  (:use clj-highlight.syntax.general))

(def java-keywords #{})

(defn str-matcher [token]
  (fn [string idx]
    (if (.startsWith string token idx)
      token)))

(defn java-ident-token 
  [default-kind]
  (fn java-ident-token* [string idx states token-def]   
    (if-let [token (re-find #"^[a-zA-Z_][a-zA-Z_0-9]*" (subs string idx))]
      (let [token (str token)]
	(condp = token
	  "import"
	  (let [states (conj states :include)]
	    [[:keyword token {:state states}] states token-def ((first states) token-def)])
	  "package"
	  (let [states (conj states :namespace)]
	    [[:keyword token {:state states}] states token-def ((first states) token-def)])
	  "class"
	  (let [states (conj states :class)]
	    [[:keyword token {:state states}] states token-def ((first states) token-def)])
	  "interface"
	  (let [states (conj states :class)]
	    [[:keyword token {:state states}] states token-def ((first states) token-def)])
	  token
	  [[default-kind token {:state states}] states token-def ((first states) token-def)])))))


(defn java-number-token [string idx states token-def]
     (let [s (subs string idx)]
       (if (re-find #"^[\d.]" s)
	 (if-let [token (re-find #"^\d+[fFdD]|\d*\.\d+(?:[eE][+-]?\d+)?[fFdD]?|\d+[eE][+-]?\d+[fFdD]?" s)]
	   [[:float token {:state states}] states token-def ((first states) token-def)]
	   (if-let [token (re-find #"^0[xX][0-9A-Fa-f]+" s)]
	     [[:hex token {:state states}] states token-def ((first states) token-def)]
	     (if-let [token (re-find #"^(?>0[0-7]+)(?![89.eEfF])" s)]
	       [[:oct token {:state states}] states token-def ((first states) token-def)]
	       (if-let [token (re-find #"^\d+[lL]?" s)]
		 [[:integer token {:state states}] states token-def ((first states) token-def)]
		 nil)))))))

(def  java-default-tokens
     (list
      (re-token #"\s+|\n" :space)
      (re-token #"\.(?!\d)|[,?:()\[\]{};]|--|\+\+|&&|\|\||\*\*=?|[-+*\/%^~&|<>=!]=?|<<<?=?|>>>?=?" :opperator)
      java-number-token
      (token (str-matcher "\"") :string :string1)
      (token (str-matcher "'") :string :string2)
      (re-token #"//[^\n\\]*(?:\\.[^\n\\]*)*" :comment)
      (re-token #"/\*(?s:.*?)\*/" :comment)
      (re-token #"@[a-zA-Z_][A-Za-z_0-9]*" :annotation)
      ))

(def java-sub-tokens
     (list (token (str-matcher ";") :opperator :pop)
	   (token (str-matcher "{") :opperator :pop)
	   (java-ident-token :ident)))

(def java-syntax
     {:keywords java-keywords
      :initial (concat java-default-tokens 
		       (list
			(re-token #"[{;]" :opperator)
			(java-ident-token :ident)))
      :include 
      (concat (list (re-token #"[a-zA-Z_][A-Za-z_0-9]*+(?:\.[a-zA-Z_][A-Za-z_0-9]*+)*+(?:\.\*)?" :include))
	      java-sub-tokens
	      java-default-tokens)
      :namespace 
      (concat (list (re-token #"[a-zA-Z_][A-Za-z_0-9]*+(?:\.[a-zA-Z_][A-Za-z_0-9]*+)*+" :namespace))
	      java-sub-tokens
	      java-default-tokens)
      :class 
      (concat (list 
	       (re-token #"[a-zA-Z_][A-Za-z_0-9]*" :class))
	      java-sub-tokens
	      java-default-tokens)
     :string1 (list
	       (re-token #"[^\"\\]+" :string)
	       (token (str-matcher "\"") :string :pop)
	       (re-token #"\\(?:[bfnrtv\n\"'\\]|x[a-fA-F0-9]{1,2}|[0-7]{1,3}|u[a-fA-F0-9]{4}|U[a-fA-F0-9]{8})" :string))
     :string2 (list
	       (re-token #"[^'\\]+" :string)
	       (token (str-matcher "'") :string :pop)
	       (re-token #"\\(?:[bfnrtv\n\"'\\]|x[a-fA-F0-9]{1,2}|[0-7]{1,3}|u[a-fA-F0-9]{4}|U[a-fA-F0-9]{8})" :string))})(ns clj-highlight.test
  (:use clj-highlight.core 
	[clj-highlight.syntax java clojure])
  (:gen-class))


(defn time-method [fun]
  (let [_ (dotimes [n 1] (fun) (print ".") (flush))
	a (. System currentTimeMillis)
	r (fun)
	b (. System currentTimeMillis)]
    [r (int (/ (- b a) 2))]))

(defn test-scanner [syntax in-file]
     (let [code (slurp in-file)
	   tkn (highlighter syntax identity)
	   _(print "Timing scanner")
	   [[c e] t] (time-method (fn [] [(count (tkn code)) (count (filter #(= :error (first %)) (tkn code)))]))
	   _ (println "done.")]
       (println "Parsed" in-file "with" c "tokens " (str "(" e " errors)") "in" (str t "ms (" (int (/ c t)) " kTok/s)." ))))

(defn -main []
  (test-scanner java-syntax "benchmarks/jruby.in.java")
  (test-scanner clj-syntax "benchmarks/core.clj"))



;(comment 
;  (def code (slurp "src/clj_highlight/syntax/clojure.clj"))
;  (use 'clj-highlight.core 'clj-highlight.syntax.clojure :reload-all)
;  (def tkn (highlighter clj-syntax identity))
;  clj_highlight.**
;  , sun.rmi.transport.*, clojure.*, swank.*, sun.rmi.*
;  (time (dotimes [i 10] (count (tkn code))))
;)
  