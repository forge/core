(ns clj-swing.example
  (:use [clj-swing  core frame label button combo-box list panel document text-field tree]))

(import '(javax.swing  UIManager)
	'(java.awt BasicStroke Color Dimension Graphics Graphics2D RenderingHints)
	'(java.awt.geom AffineTransform Ellipse2D)
	'(java.awt GridBagLayout FlowLayout GridLayout GridBagConstraints)
	clj-swing.tree.Pathed)

(def sr (ref '["Quick sort" "Bubble Sort"]))
(def lm (ref '["Bla" "Blubb"]))
(def str-ref (ref "A String!"))

(def selected (atom nil))
(def nativeLF (. UIManager getSystemLookAndFeelClassName))

(. UIManager setLookAndFeel nativeLF)

(defn paint-donut [g]
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
  (frame :title "Sort Visualizer" :layout (GridBagLayout.) :constrains (java.awt.GridBagConstraints.) :name fr
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

(defn tree-example []
  (let [sr (ref "")
	m (ref {{:name "Cookies"} {{:name "Chocolat"} 1 {:name "Vanilla"} { {:name "with sparkles"} 2 {:name "without sparkles"} 3}}})
	path (atom nil)]
  (frame :title "Tree example" :show true :size [400 200]
	 [_ (stack
	     [tf (text-field :str-ref sr)
	      _ (tree
		 :name tr
		 :action ([old new]
			    (prn (.getSelectionPath tr))
			    (if new
			      (dosync
			       (swap! path (constantly new))
			       (alter sr (constantly (:name (last new)))))))
		 :model (mapref-tree-model m {:name "Food"}
					   :node-wrapper (fn [node path] (Pathed. node (str (:name node)) path))))]
	     (add-action-listener
	      tf
	      ([_]
		 (dosync
		  (let [c (get-in @m @path)]
		    (prn 10 c @sr @m)
		    (if-let [r (butlast @path)]
		      (do
			(alter m update-in r dissoc (last @path))
			(alter m update-in r assoc {:name @sr} c))
		      (do
			(alter m dissoc (last @path))
			(alter m assoc {:name @sr} c)))))
		 (prn @m))))])))