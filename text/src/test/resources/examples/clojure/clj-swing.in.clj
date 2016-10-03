(ns clj-swing.button
  (:use [clj-swing.core :only [add-action-listener icon-setters auto-setters]])
  (:import (javax.swing JButton JCheckBox JCheckBoxMenuItem JRadioButton JRadioButtonMenuItem JToggleButton ButtonGroup)))


(def *button-icon-keys* 
     [:icon :disabled-icon :selected-icon :pressed-icon :disabled-selected-icon :rollover-icon :rollover-selected-icon ])
(def *button-known-keys*
     (concat [:action :caption :name] *button-icon-keys*))


(defmacro general-button [cl {caption :caption action :action name :name :as opts}]
  (let [b (or name (gensym "btn"))]
    `(let [~b  (new ~cl)]
       (doto ~b
	 ~@(if caption  
	     [`(.setText ~caption)])
	 ~@(if action  
	     [`(add-action-listener ~action)])
	 ~@(icon-setters *button-icon-keys*  opts)
	 ~@(auto-setters JButton *button-known-keys* opts)))))

(defmacro button [caption & {:as opts}]
  `(general-button JButton ~(assoc opts :caption caption)))

(defmacro check-box [ & {:as opts}]
  `(general-button JCheckBox ~opts))

(defmacro check-box-menu-item [ & {:as opts}]
  `(general-button JCheckBoxMenuItem ~opts))

(defmacro radio-button [ & {:as opts}]
  `(general-button JRadioButton ~opts))

(defmacro radio-button-menu-item [ & {:as opts}]
  `(general-button JRadioButtonMenuItem ~opts))

(defmacro toggle-button [ & {:as opts}]
  `(general-button JToggleButton ~opts))

(defmacro button-group [& buttons]
  `(doto (ButtonGroup.)
     ~@(map (fn [btn]
	      `(.add ~btn)) buttons)))(ns clj-swing.combo-box
  (:use [clj-swing.core]
	[clojure.contrib.swing-utils :only [do-swing]])
  (:import (javax.swing JComboBox ComboBoxModel MutableComboBoxModel)
	   (javax.swing.event ListDataEvent ListDataListener)))

(def *cb-known-keys* [:action])

(defmacro commbo-box-model [& {mutable :mutable
			       [[] & size-code] :size
			       [[get-idx] & get-code] :get
			       [[a-l-l] & add-listener-code] :add-listener
			       [[r-l-l] & remove-listener-code] :remove-listener
			       
			       [[] & get-selected-item-code] :get-selected-item
			       [[s-s-i-item] & set-selected-item-code] :set-selected-item

			       [[add-item] & add-code] :add 
			       [[add-at-item add-at-idx] & add-at-code] :add-at 
			       [[remove-item] & remove-code] :remove 
			       [[remove-idx] & remove-at-code] :remove-at 

}]
  `(proxy [~(if mutable 'MutableComboBoxModel 'ComboBoxModel)] []

;; Code for List model
     (getSize []
	      ~@size-code)
     (getElementAt [~get-idx]
		   ~@get-code)
     (addListDataListener [~a-l-l]
			  ~@add-listener-code)
     (removeListDataListener [~r-l-l]
			  ~@remove-listener-code)

;; Code for Combobox Model
     (getSelectedItem []
		      ~@get-selected-item-code)
     (setSelectedItem [~s-s-i-item]
		      ~@set-selected-item-code)

;; Code for mutable combobox model
     ~@(if mutable
       [
	`(addElement [~add-item] 
		     ~@add-code)
	`(insertElementAt [~add-at-item ~add-at-idx] 
		     ~@add-at-code)
	`(removeElement [~remove-item] 
		     ~@remove-code)
	`(removeElementAt [~remove-idx] 
		     ~@remove-at-code)])))

(defn seq-ref-combobox-model [seq-ref & [selected]]
  (let [selected (or selected (atom nil))
	listeners (atom #{})
	key (gensym "seq-ref-combobox-model-watch")
	m (commbo-box-model
	   :mutable true
	   :size ([] (count @seq-ref))
	   :get-selected-item ([] (dosync (if (and @selected (some #(= @selected %) @seq-ref)) @selected (swap! selected (constantly nil)))))
	   :set-selected-item ([i] (dosync (if (and i (some #(= i %) @seq-ref)) (swap! selected (constantly i)) (swap! selected (constantly nil)))))
	   :add-listener ([l] (swap! listeners conj l))
	   :remove-listener ([l] (swap! listeners disj l))
	   :get ([i] (if (has-index? @seq-ref i) (nth @seq-ref i) nil))
	   :add ([itm] (dosync (alter seq-ref conj itm)))
	   :add-at ([itm idx] (dosync 
			       (if (vector? @seq-ref)
				 (alter seq-ref #(vec (insert-at % idx itm)))
				 (alter seq-ref insert-at idx itm))))
	   :remove ([itm] (dosync 
			       (if (vector? @seq-ref)
				 (alter seq-ref #(vec (remove (partial = itm) %)))
				 (alter seq-ref #(remove (partial = itm) %)))))
	   :remove-at ([idx] (dosync 
			       (if (vector? @seq-ref)
				 (alter seq-ref #(vec (drop-nth % idx)))
				 (alter seq-ref drop-nth idx)))))]
    (add-watch seq-ref key 
		    (fn [_ _ _ state]
		      (do-swing
		       (let [m (ListDataEvent. m (ListDataEvent/CONTENTS_CHANGED) 0 (count state))]
			(doseq [l @listeners]
			  (.contentsChanged l m))))))
    m))
    
     


(defmacro combo-box [[& items] & {action :action :as opts}]
  `(doto (JComboBox.)
     ~@(if action  
	 [`(add-action-listener ~action)])
     ~@(auto-setters JComboBox *cb-known-keys* opts)
     ~@(map #(list '.addItem %) items)))

(defn selected-item [obj]
  (.getSelectedItem obj))


;; TODO Add list cell renderer proxy stuff(ns clj-swing.core
  (:import (java.awt.event ActionListener)
	   (javax.swing ImageIcon))
  (:require [clojure.contrib.string :as st]))

(defn kw-to-setter [kw]
  (symbol (apply str "set" (map st/capitalize (st/split #"-" (name kw))))))

(defn group-container-args [args]
  (reduce 
   (fn [{options :options kw :kw state :state :as r} arg]
     (cond
      (= state :forms)
      (update-in r [:forms] conj arg)
      kw
      (assoc r :options (assoc options kw arg) :kw nil)
      (keyword? arg)
      (assoc r :kw arg)
      (vector? arg)
      (assoc r :bindings arg :state :forms)))
   {:options {} :kw nil :state :options :forms []} args))

(defn remove-known-keys [m ks]
  (reduce dissoc m ks))

(defn has-index? [seq idx]
  (and (>= idx 0) (< idx (count seq))))

(defn icon-setters [names opts]
  (if opts
    (remove 
     nil?
     (map
      (fn [name] 
	(when-let [icon (opts name)] 
	  `(.  ~(kw-to-setter name) (.getImage (ImageIcon. ~icon))))) names))))

(defn auto-setters [cl known-kws opts]
  (if opts
    (map (fn [[a v]] (list 
		      '. 
		      (kw-to-setter a) 
		      (if (keyword? v)
			`(. ~cl ~(symbol (st/upper-case (name v))))
			v)))
	 (remove-known-keys opts known-kws))))

(defn insert-at [seq idx item]
  (concat (take idx seq) [item] (drop idx seq)))

(defn drop-nth [seq idx]
  (concat (take idx seq) (drop (inc idx) seq)))

(defmacro add-action-listener [obj [[event] & code]]
  `(doto ~obj
     (.addActionListener
      (proxy [ActionListener] []
	(actionPerformed [~event]
			 ~@code)))))


(defn <3 [love & loves] 
  (loop [l (str "I love " love) loves loves]
    (let [[love & loves] loves]
      (if (nil? love)
	(str l ".")
	(if (empty? loves)
	  (str l " and " love ".")
	  (recur (str l ", " love) loves))))))
(ns clj-swing.document
  (:use [clojure.contrib.swing-utils :only [do-swing]])
  (:require [clojure.contrib.string :as st])
  (:import [javax.swing.text AbstractDocument Position Element PlainDocument]
	   [javax.swing.event DocumentEvent DocumentListener]
	   javax.swing.event.DocumentEvent$EventType
	   javax.swing.event.DocumentEvent$ElementChange
	   javax.swing.text.AbstractDocument$Content))

(defn- update-positions [positions offset change]
  (doall 
   (map 
    (fn [p]
      (if (>= @p offset)
	(swap! p + change)
	p))
    positions)))

(defn str-insert [s offset new-s]
  (str (st/take offset s) new-s (st/drop offset s)))

(defn str-remove [s offset cnt]
  (str (st/take offset s) (st/drop (+ offset cnt) s)))


(defn string-ref-content [str-ref]
  (let [positions (atom [])]
    (proxy [AbstractDocument$Content] []
      (createPosition [offset]
		      (let [p (atom offset)]
			(swap! positions conj p)
			(proxy [Position] []
			  (getOffset [] @p))))

      (getChars [where len txt]
		(let [s (max 0 (min where len))
		      e (max 0 (min where len (.length @str-ref)))]
		  (println "A:" @str-ref where len)
		  (set! (. txt array) (into-array Character/TYPE (seq (subs @str-ref s e))))
		  (println "B")
		  (prn (seq (. txt array)))))

      (getString [where len]
		 (let [s (max 0 (min where len))
		      e (max 0 (min where len (.length @str-ref)))]
		   (subs @str-ref s e)))

      (length []
	     (.length @str-ref))

      (insertString [where str]
		    (swap! positions update-positions where (.length str))
		    (dosync
		     (alter str-ref str-insert where str))
		    nil)

      (remove [where nitems] 
	      (swap! positions update-positions where (- 0 nitems))
	      (dosync 
	       (alter str-ref str-remove where nitems))
	      nil))))


(defn plain-str-ref-document [str-ref]
  (PlainDocument. (string-ref-content str-ref)))

(comment defn abstract-str-ref-document [str-ref]
  (let [d (proxy [AbstractDocument]  [(string-ref-content str-ref)])]
    (add-watch str-ref (gensym "abstract-str-ref-document-watch")
	       (fn [_ _ _ state]
		 (.fireChangedUpdate d (proxy [DocumentEvent] []
					 (getChange [elem] (proxy [DocumentEvent$ElementChange] []
							     (getChildrenAdded [] (into-array Element[]))
							     (getChildrenRemoved [] (into-array Element[]))
							     (getElement[] elem) 
							     (getIndex[] 0)))
						    

					 (getDocument [] d)
					 (getLength [] (.length state))
					 (getOffset [] 0)
					 (getType [] (DocumentEvent$EventType/CHANGE))))))
    (.fireChangedUpdate d (proxy [DocumentEvent] []
			    (getChange [elem] (proxy [DocumentEvent$ElementChange] []
						(getChildrenAdded [] (into-array Element[]))
						(getChildrenRemoved [] (into-array Element[]))
						(getElement[] elem) 
						(getIndex[] 0)))
			    
			    
			    (getDocument [] d)
			    (getLength [] (.length state))
			    (getOffset [] 0)
			    (getType [] (DocumentEvent$EventType/CHANGE))))
    d))

(defn add-str-ref-doc-listener [doc-owner str-ref]
  (let [doc (.getDocument doc-owner)
	watch-key (gensym "str-ref-doc-listener-watch")
	watch-fn (fn [l]
		   (fn [a b c state]
		     (do-swing
		      (.removeDocumentListener doc l)
		      (.remove doc 0 (.getLength doc))
		      (.insertString doc 0 state nil)
		      (.addDocumentListener doc l))))
	l (proxy [DocumentListener] []
	    (insertUpdate [event]
			  (let [offset (.getOffset event)]
			     (remove-watch str-ref watch-key)
			     (dosync (alter str-ref str-insert offset (.getText doc offset (.getLength event))))
			     (add-watch str-ref watch-key (watch-fn this))))
	    (removeUpdate [event]
			   (remove-watch  str-ref watch-key)
			   (dosync (alter str-ref str-remove (.getOffset event) (.getLength event)))
			   (add-watch str-ref watch-key (watch-fn this)))
	    (changedUpdate [event]))]
    (if (< 0 (.getLength doc))
      (.remove doc 0 (.getOffset (.getEndPosition doc))))
    (.insertString doc 0 @str-ref nil)
    (.addDocumentListener doc l)
    (add-watch str-ref watch-key (watch-fn l))))
(ns clj-swing.example
  (:use [clj-swing core frame label button combo-box list panel document text-field]))

(import '(javax.swing  UIManager)
	'(java.awt BasicStroke Color Dimension Graphics Graphics2D RenderingHints)
	'(java.awt.geom AffineTransform Ellipse2D)
	'(java.awt GridBagLayout FlowLayout GridLayout GridBagConstraints))

(def sr (ref '["Quick sort" "Bubble Sort"]))
(def lm (ref '["Bla" "Blubb"]))
(def str-ref (ref "A String!"))

(def selected (atom nil))

(def nativeLF (. UIManager getSystemLookAndFeelClassName))

(. UIManager setLookAndFeel nativeLF)

(defn paint-donut [g]
  (println "y!!!o")
  (let [width 360
	height 310
	ellipse (new java.awt.geom.Ellipse2D$Double 0 0 80 130)
	at (AffineTransform/getTranslateInstance (/ width 2) (/ height 2))]
    (doto g
      (.setStroke (BasicStroke. 1))
      (.setColor (. Color gray)))
    (doseq [i (range 0 361 5)]
      (.rotate g (Math/toRadians i))
      (.draw g (.createTransformedShape at ellipse)))))

(defn graphics-example []
  (frame
   :title "Graphics example" 
   :show true :pack true
   [p (panel
       :preferred-size [360 310]
       :focusable true
       :paint ([g]
		 (proxy-super paintComponent g)
		 (paint-donut g)))]))

(defn grid-bag-example []
  (frame :title "Sort Visualizer" :layout :grid-bag
	 :show true :pack true
	 [:gridx 0 :gridy 0 :anchor :LINE_END
	  _ (label "Algorithms")
	  :gridy 1
	  _ (label "Button")
	  :gridx 1 :gridy 0 :anchor :LINE_START
	  _ (combo-box [] :model (seq-ref-combobox-model sr selected))
	  :gridy 1
	  _ (button "Run Algorithm" 
		    :action ([_] (if @selected (dosync (alter lm conj @selected)))))
	  :gridx 0 :gridy 2 :gridwidth 2 :anchor :LINE_START
	  _ (text-field :str-ref str-ref :columns 10)
	  :gridx 3 :gridy 0 :gridheight 3 :anchor :CENTER
	  _ (scroll-panel (jlist :model (seq-ref-list-model lm)) :preferred-size [150 100])]))

(defn button-example []
  (frame :title "A Button Example"
	 :layout (FlowLayout.)
	 :size [220 90]
	 :show true
	 [b1 (button "First")
	  b2 (button "Second")
	  lab (label "Press a button")]
	 (add-action-listener b1 ([e] (println "listener called")))))


(defn c-to-f [c]
  (+ (* c 9/5) 32))

(defn f-to-c [f]
  (* (- f 32) 5/9))


(defn convert-temp [converter temp]
  (constantly (str (Math/round (float (converter (Double/parseDouble (.trim temp))))))))

(defn converter-example []
  (let [c (ref "")
	f (ref "")]
    (frame :layout :grid-bag :show true :pack truef
	  [:gridx 0 :gridy 0 :anchor :LINE_END
	   _ (label "C")
	   :gridy 1
	   _ (label "F")
	   :gridx 1 :anchor :LINE_START
	   _ (text-field :str-ref f :columns 10
			 :action ([_]
				    (println @f)
				    (if (re-find #"^\d+$" @f)
				      (dosync (alter c (convert-temp f-to-c  @f))))))
	   :gridy 0
	   _ (text-field :str-ref c :columns 10
			 :action ([_]
				    (println @c)
				    (if (re-find #"^\d+$" @c)
				      (dosync (alter f (convert-temp c-to-f  @c))))))])))(ns clj-swing.frame
  (:use [clj-swing.core :only [group-container-args icon-setters auto-setters]]
	[clojure.contrib.swing-utils :only [do-swing]])
  (:import (javax.swing JFrame ImageIcon)
	   (java.awt GridBagLayout GridLayout GridBagConstraints))
  (:require [clojure.contrib.java-utils :as java]))


(defmacro set-constraint! [constraints field value]
  `(set! (. ~constraints ~(symbol (name field)))
         ~(if (keyword? value)
	    `(java/wall-hack-field  (class ~constraints) '~(symbol (name value))  (class ~constraints))
            value)))

(def *frame-on-close-actions*
  {:do-nothing (JFrame/DO_NOTHING_ON_CLOSE)
   :exit (JFrame/EXIT_ON_CLOSE)
   :hide (JFrame/HIDE_ON_CLOSE)
   :dispose (JFrame/DISPOSE_ON_CLOSE)})


(def *frame-known-keys*
     [:name :icon :title :layout :constrains :on-close :size :bounds :location :pack :show :centered])

(defmacro frame [& args]
  "options are:
:name - internal name of the frame.
:icon - icon, will be passed to javax.swing.ImageIcon.
:title - title for the frame.
:layout - layout manager.
:constrains - constrains object for the layout manager.
:on-close - one of :do-nothing, :exit, :hide, :dispose, 
  sets the default on close action for the frame.
:size - [w, h]
:bounds - [x, y, w, h]
:location - [x y]

:pack - shall the frame autopack at the end?
:show - shall the frame autoshow at the end?
"
    (let [default-opts {}
	  {forms :forms opts :options bindings :bindings} (group-container-args args)
	  opts (merge default-opts opts)
	  frame (or (:name opts) (gensym "frame"))
	  constrains (gensym "constrains")
	  manager (gensym "manager")]
      `(do-swing 
	(let [~frame  ~(if (:title opts)
			 `(JFrame. ~(:title opts))
			 `(JFrame.))
	      ~@(cond 
		  (= (:layout opts) :grid-bag)
		  (list '_ `(.setLayout ~frame (new GridBagLayout))
		    `~constrains `(new GridBagConstraints))
		  (= (:layout opts) :grid)
		  ['_ `(.setLayout ~frame (new GridLayout))]
		  (:layout opts)
		  ['_ `(.setLayout ~frame ~(:layout opts))])
	      ~@(if (:constrains opts)
		 `[~constrains ~(:constrains opts)])
	      ~@(if (or (:constrains opts) (keyword? (:layout opts)))
		  (reverse 
		   (reduce
		    (fn [l [f s]]
		      (if (keyword? f)
			(conj (conj l '_) `(set-constraint! ~constrains ~f ~s))
			(conj (conj (conj (conj l f) s) '_) `(.add ~frame ~f ~constrains))))
		    '() (partition 2 bindings)))
		  (reverse 
		   (reduce
		    (fn [l [f s]]
		      (conj (conj (conj (conj l f) s) '_) `(.add ~frame ~f)))
		    '() (partition 2 bindings))))]
	  (doto ~frame
	    ~@(icon-setters [:icon]  opts)
	    ~@(auto-setters JFrame *frame-known-keys* opts)
	    ~@(when-let [on-close (*frame-on-close-actions* (:on-close opts))]
		[`(.setDefaultCloseOperation ~on-close)])
	    ~@(when-let [[w h] (:size opts)]
		[`(.setSize ~w ~h)])
	    ~@(when-let [[x y w h] (:bounds opts)]
		[`(.setBounds ~x ~y ~w ~h)])
	    ~@(when-let [[x y] (:location opts)]
		[`(.setLocation ~x ~y)])
	    ~@(if (contains? opts :centered)
		[`(.setLocationRelativeTo ~(:centered opts))])
	    
	    ~@forms
	    
	    ~@(if (:pack opts)
		[`(.pack)])
	    ~@(if (:show opts)
		[`(.setVisible true)]))))))(ns clj-swing.label
  (:use [clj-swing.core :only [add-action-listener icon-setters auto-setters]])
  (:import (javax.swing JLabel ImageIcon)))

(defmacro label 
  [caption & {obj :for :as opts}]
  `(let [l# (JLabel. ~caption)]
     (doto l#
       ~@(if obj 
	   [`(.setLabelFor ~obj)])
       ~@(icon-setters [:icon :disabled-icon]  opts)
       ~@(auto-setters JLabel [:for] opts))))
(ns clj-swing.list
  (:use [clj-swing core panel]
	[clojure.contrib.swing-utils :only [do-swing]])

  (:import (javax.swing JList ListModel)
	   (javax.swing.event ListDataEvent ListDataListener ListSelectionListener)))

(def *list-known-keys* [:action :on-selection-change])


(defmacro add-list-selection-listener [obj [[event] & code]]
  `(doto ~obj
     (.addListSelectionListener
      (proxy [ListSelectionListener] []
	(valueChanged [~event]
			 ~@code)))))

(defmacro list-model [& {[[] & size-code] :size
			 [[get-idx] & get-code] :get
			 [[a-l-l] & add-listener-code] :add-listener
			 [[r-l-l] & remove-listener-code] :remove-listener}]
  `(proxy [ListModel] []
     (getSize []
	      ~@size-code)
     (getElementAt [~get-idx]
		   ~@get-code)
     (addListDataListener [~a-l-l]
			  ~@add-listener-code)
     (removeListDataListener [~r-l-l]
			     ~@remove-listener-code)))


(defn seq-ref-list-model [seq-ref]
  (let [listeners (atom #{})
	key (gensym "seq-ref-list-model-watch")
	m (list-model
	   :size ([] (count @seq-ref))
	   :add-listener ([l] (swap! listeners conj l))
	   :remove-listener ([l] (swap! listeners disj l))
	   :get ([i] (if (has-index? @seq-ref i) (nth @seq-ref i) nil))
	   )]
    (add-watch seq-ref key 
	       (fn [_ _ _ state]
		 (do-swing
		  (let [m (ListDataEvent. m (ListDataEvent/CONTENTS_CHANGED) 0 (count state))]
		    (doseq [l @listeners]
		      (.contentsChanged l m))))))
    m))

(defmacro jlist [& {action :action on-selection-change :on-selection-change items :items scrolling :scrolling :as opts}]
  (let [l (gensym "jlist")]
  `(let [~l (doto (JList.)
	      ~@(if action  
		  [`(add-action-listener ~action)])
	      ~@(if on-selection-change  
		  [`(add-list-selection-listener ~on-selection-change)])    
	      ~@(auto-setters JList *list-known-keys* opts)
	      ~@(map #(list '.addItem %) items))]
     
     ~@(if scrolling 
	`[(scroll-panel ~l)]
	`[~l]))))

;; TODO Add list cell renderer proxy stuff(ns clj-swing.panel
  (:use [clj-swing.core :only [group-container-args auto-setters icon-setters]])

  (:import (javax.swing JPanel JSplitPane JScrollPane)
	   (java.awt Dimension))
  (:require [clojure.contrib.java-utils :as java]))


(def *panel-known-keys*
     [:name :icon :title :layout :constrains :size :bounds :location :pack :preferred-size :paint])


(defmacro general-panel [cl args]
  "options are:
:name - internal name of the frame.
:layout - layout manager.
:constrains - constrains object for the layout manager.
:on-close - one of :do-nothing, :exit, :hide, :dispose, 
  sets the default on close action for the frame.
:preferred-size - [w, h]
:bounds - [x, y, w, h]
:location - [x y]
:paint - paint function
:pack - shall the frame autopack at the end?
:show - shall the frame autoshow at the end?
"
    (let [default-opts {}
	  {forms :forms {[[paint-obj] & paint-code] :paint :as opts} :options bindings :bindings} (group-container-args args)
	  opts (merge default-opts opts)
	  panel (or (:name opts) (gensym "panel"))
	  constrains (gensym "constrains")
	  manager (gensym "manager")]
      `(let [~panel  ~(if (:paint opts)
			`(proxy [~cl] []
			  (paintComponent [~paint-obj] 
					  ~@paint-code))
			`(new ~cl))
	     ~@(if (:layout opts)
		 ['_ `(.setLayout ~panel ~(:layout opts))])
	     ~@(if (:constrains opts)
		 `(~constrains ~(:constrains opts)))
	     ~@(if (:constrains opts)
		 (reverse 
		  (reduce
		   (fn [l [f s]]
		     (if (keyword? f)
		       (conj (conj l '_) `(set-constraint! ~constrains ~f ~s))
		       (conj (conj (conj (conj l f) s) '_) `(.add ~panel ~f ~constrains))))
		   '() (partition 2 bindings)))
		 (reverse 
		  (reduce
		   (fn [l [f s]]
		     (conj (conj (conj (conj l f) s) '_) `(.add ~panel ~f)))
		   '() (partition 2 bindings))))]
	 (doto ~panel
	   ~@(icon-setters [:icon]  opts)
	   ~@(auto-setters cl *panel-known-keys* opts)
	   ~@(when-let [[w h] (:preferred-size opts)]
	      [`(.setPreferredSize (Dimension. ~w ~h))])
	   ~@(when-let [[x y w h] (:bounds opts)]
	      [`(.setBounds ~x ~y ~w ~h)])
	   ~@(when-let [[x y] (:location opts)]
	      [`(.setLocation ~x ~y)])
	   ~@forms))))

(defmacro panel [& args]
  `(general-panel JPanel ~args))

(defmacro scroll-panel [obj & { :as opts}]
  `(doto (new JScrollPane ~obj)
     ~@(auto-setters JScrollPane [:preferred-size] opts)
     ~@(when-let [[w h] (:preferred-size opts)]
	 [`(.setPreferredSize (Dimension. ~w ~h))])))


(ns clj-swing.text-field
  (:use [clj-swing core document])
  (:import (javax.swing JTextField ListModel)
	   (javax.swing.event ListDataEvent ListDataListener ListSelectionListener)))

(def *text-field-known-keys* [:action :str-ref])

(defmacro text-field [& {action :action str-ref :str-ref :as opts}]
  `(doto (JTextField.)
     ~@(if action  
	 [`(add-action-listener ~action)])
     ~@(if str-ref  
	 [`(add-str-ref-doc-listener ~str-ref)])
     ~@(auto-setters JTextField *text-field-known-keys* opts)))


