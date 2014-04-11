(ns clj-swing.frame
  (:use [clj-swing.core :only [group-container-args icon-setters auto-setters]]
	[clojure.contrib.swing-utils :only [do-swing]])
  (:import (javax.swing JFrame ImageIcon))
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
	      ~@(if (:layout opts)
		  ['_ `(.setLayout ~frame ~(:layout opts))])
	      ~@(if (:constrains opts)
		  `(~constrains ~(:constrains opts)))
	      ~@(if (:constrains opts)
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
		[`(.setLocationRelativeTo ~(:centered opts))]))
	    ~@forms
	    (doto ~frame
	      ~@(if (:pack opts)
		  [`(.pack)])
	      ~@(if (:show opts)
		  [`(.setVisible true)]))))))