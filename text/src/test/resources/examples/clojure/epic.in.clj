(defproject EPIC "1.0.0-SNAPSHOT" 
	:description "FIXME: write" 
	:dependencies [[org.clojure/clojure "1.1.0"] 
	[clj-sandbox/clj-sandbox "0.2.9-SNAPSHOT"] 
	[org.clojure/clojure-contrib "1.1.0"]] 
	:dev-dependencies [
	[swank-clojure/swank-clojure "1.1.0"]]
	:main net.licenser.epic) 
(ns net.licenser.epic.game.basic
  (:use net.licenser.epic.utils)
  (:require [net.licenser.epic.units :as units]))

(defn unit-at
  [game x y]
  (deref (get-field (:map game) x y)))
	   

(defn move-unit*
  [game unit x y]
  (if (and
       (integer? x)
       (integer? y))
    (dosync
     (let [u-x (:x @unit)
	   u-y (:y @unit)
	   id (:id @unit)
	   map (:map game)
	   f-u (get-field map u-x u-y)
	   f-d (get-field map x y)]
       (if (empty? @f-d)
	 (do 
	   (combat-log :move
		       {:unit id
			:position {:x x :y y}})
	   (trace "game.basic.move-unit*" "Moving" id "to" x "/" y)
	   (alter unit assoc :x x :y y)
	   (alter f-u  #(doall (remove (partial = id) %)))
	   (alter f-d conj id)
	   game)
	 game)
       game))))(ns net.licenser.epic.game.logic
  (:require [net.licenser.epic.game.basic :as basic])
  (:use clojure.stacktrace)
  (:use clojure.contrib.str-utils)
  (:require (net.licenser.epic [modules :as modules] [units :as units] [utils :as utils])))

(defn directions
  ([start i d] 
     (lazy-seq 
      (cons 
       (mod (+ start (* d i)) 6) 
       (cons 
	(mod (- start (* d i)) 6) 
	(directions start (inc i) d))))) 
  ([start d] 
     (take 6 (cons (mod start 6) (directions start 1 d)))))


(defn register-target
  [unit target]
  (dosync (alter unit assoc :last-targe target)))


(defn intercept-unit
  ([game unit target distance visited]
     (utils/trace "logic.intercept-unit" "start")
     (dosync
      (loop [game game unit unit target target distance distance visited visited]
	(let [u-id (:id @unit)
	      t-id (:id @target)
	      d (utils/map-distance @unit @target)
	      visited (conj visited [(:x @unit) (:y @unit)])]
	  (if (or
	       (zero? (units/unit-range @unit))
	       (= distance d)
	       (not (utils/use-energy unit (utils/module-spec (units/main-engine @unit) :energy-usage))))
	    (do
	      (if (zero? (units/unit-range @unit))
		(utils/trace "logic.intercept" "out of gass:" (units/main-engine @unit))
		(utils/trace "logic.intercept" "destinateion reached:" d "of" distance))
	      game)
	    (let [best-direction (if (or true (> d distance))
				   (utils/friction-direction-to @unit @target)
				   (mod (+ (utils/friction-direction-to @unit @target) 3) 6))
		  rounded-direction (int (Math/round #^Double best-direction))
		  rounded-direction (if (< distance d) rounded-direction (+ rounded-direction 3))
		  delta (if (< best-direction rounded-direction) 1 -1)
		  dirs (directions rounded-direction delta)
		  _ (utils/trace "logic.intercept" "possible directions:" dirs)
		  positions (filter (fn [p] 
				      (and (every? (partial not= p) visited) (empty? (basic/unit-at game (first p)  (second p)))))
				    (map (partial utils/in-direction (:x @unit) (:y @unit)) dirs))
		  _ (utils/trace "logic.intercept" "possible new and empty positions:" positions)
		  [x y] (first positions)]
	      (utils/trace "logic.intercept" "moving from" (:x @unit) (:y @unit) "to" x y)
	      (let [g (basic/move-unit* game unit x y)
		    e (units/main-engine @unit)
		    g (utils/update-module-specs unit (:id e) :times-used inc)]
		(recur game unit target distance visited))))))))
     ([game unit target distance]
	(intercept-unit game unit target distance '())))


 (defn find-units
   [game pred]
   (let [units (vals (:units game))
	 units (filter (fn [u]
			  (utils/trace "find-units" "testing unit:" (:id @u))
			  (if (and utils/*trace* (pred @u))
			    (utils/trace "find-units" "testing unit:" (:id @u) "positive!")
			    (utils/trace "find-units" "testing unit:" (:id @u) "negativ!"))
			  (pred @u)) units)
	 units (if utils/*trace* (doall units) units)]
   units))

 (defn find-hostile-units
   ([game unit]
       (let [t (int (:team @unit))]
	 (utils/trace "find-hostile-units" "Looking for units in team:" t)
	 (find-units game #(and
			    (not (:destroyed %))
			    (not= t (int (:team  %)))))))
   ([game unit range]
      (let [t (:team @unit)]
	 (utils/trace "find-hostile-units" "Looking for units in team:" t "in range" range)
	 (find-units
	  game
	  #(and
	    (not (:destroyed %))
	    (not= t (:team %))
	    (>= range (utils/map-distance @unit  %)))))))


 (defn can-fire-at? [unit weapon target]
   (dosync
    (let [spec (utils/module-spec weapon)
	  dist (utils/map-distance target unit)]
      (and
       (not (modules/module-destroyed? weapon))
       (not (units/unit-destroyed? target))
       (<= (- (:range spec) (:variation spec)) dist (+ (:range spec) (:variation spec)))
       (<= (:times-used spec) (:max-usage spec))))))

(defn fire-weapon [game unit weapon-id target]
  (dosync
   (let [w (utils/get-module @unit weapon-id)]
     (utils/trace "game.logic.fire-weapon" "u:" (:id @unit) "t:" (:id @target)  "w:" weapon-id)
     (if (and
	  (can-fire-at? @unit w @target)
	  (utils/use-energy unit (utils/module-spec w :energy-usage)))
       (do
	 (utils/trace "game.logic.fire-weapon" "Can Fire: attacker:" (:id @unit) "| target:" (:id @target))
	 (if ((utils/module-spec w :unit-hit-fn) unit w target)
	   (units/hit-unit game target unit (utils/module-spec w :damage))
	   game))
       (do
	 (utils/trace "game.logic.fire-weapon" "Can't-Fire attacker:" (:id @unit) "| target:" (:id @target))
	 game)))))

(defn emply-point-defense
  [game unit]
  (let [pd-s (filter #(re-find #"Point Defense" (:name %)) (utils/get-modules @unit :weapon))]
    (if (not (empty? pd-s))
      (let [r (reduce #(max %1 %2) (map #(+ (utils/module-spec % :range) (utils/module-spec % :variation)) pd-s))
	    ts (find-hostile-units game unit r)]
	(doall (map (fn [w]
		      (doall (map (fn [t] (fire-weapon game unit (:id w) t)) ts))) pd-s)))))
    game)

(defn fire-all [game unit target]
  (let [modules (map #(:id %) (utils/get-modules @unit :weapon))]
   (utils/trace "fire-all" "modules:" modules "of" (:id @unit) "at" (:id @target))
   (reduce
    (fn [game weapon-id] (utils/trace "fire-all" "fiering:" weapon-id) (fire-weapon game unit weapon-id target) game)
    game
    modules)))(ns net.licenser.epic.game
  (:require (net.licenser.epic [units :as units] [utils :as utils]))
  (:use clojure.stacktrace)
  (:use (net.licenser.epic.game [basic])))

(defstruct game :map :units)

(defn add-unit
  [game unit]
  (update-in game [:units (:id unit)] (utils/static (ref unit))))

(defn create-game
  ([]
     (struct game (ref {}) {}))
  ([map-size]
     (let [g (create-game)
	   s (* -1 map-size)
	   e (inc map-size)]
       (dorun
	(map (fn [x] (map (fn [y] (utils/get-field (:map g) x y)) (range s e))) (range s e)))
       g)))

(defn- cycle-reduce-fn
  [game unit-id]
  (units/cycle-unit game unit-id))

(defn- cycle-unit-fn
  [game unit-id]
  (utils/trace "cycle-unit-fn" "cyceling unit" unit-id)
  (units/cycle-unit game (utils/get-unit game unit-id)))

(defn cycle-game*
  ([game]
     (dorun
      (map (partial cycle-unit-fn game) (keys (:units game)))))
  ([game partition-size]
     (try
      (dorun (pmap
	      (bound-fn [batch] (doall (map (fn [unit] (cycle-unit-fn game unit)) batch)))
	      (partition partition-size partition-size nil (filter #(not (units/unit-destroyed? @(utils/get-unit game %))) (keys (:units game))))))
      (catch StackOverflowError e
	(println "In cycle-game*")
	(print-stack-trace e 5)))))
(ns net.licenser.epic.modules.cycle
  (:require [net.licenser.epic.utils :as utils]))

(defn- apply-cycle-script
  [game unit module-id]
  (let [s (:cycle-script (utils/get-module @unit module-id))]
    (if s
      (s game unit module-id)
      unit)))

;(defn default-module-cycle
;  ([game unit module-id]
;     (apply-cycle-script game unit module-id))
;  ([game unit module-id update-fn]
;     (utils/update-module (apply-cycle-script game unit module-id) module-id #(update-fn  %))))

(defn default-module-cycle
  ([game unit module-id]
     unit)
  ([game unit module-id update-fn]
     (utils/update-module unit module-id #(update-fn  %))))

(defn cycle-generic
  [m])

(defmulti cycle-module
  "This method is called for every module at the start of the tick"
  (fn [game unit module-id] 
    (let [type (:type (utils/get-module @unit module-id))]
    (utils/trace "modules.cycle" "Determining type: " type) 
    type)))

(defmethod cycle-module :reactor
  [game unit module-id]
  (default-module-cycle game unit module-id 
    (fn [module]
      (let [specs (:specification module)]
	(update-in
	 module [:specification :energy] #(min (:capacity specs) (+ % (:output specs))))))))

(defmethod cycle-module :engine
  [game unit module-id]
  (default-module-cycle game unit module-id
    (fn [module]
      (let [specs (:specification module)]
	(update-in module [:specification :times-used] (utils/static 0))))))

(defmethod cycle-module :weapon
  [game unit module-id]
  (default-module-cycle game unit module-id
    (fn [module]
      (let [specs (:specification module)]
	(update-in module [:specification :times-used] (utils/static 0))))))


(defmethod cycle-module :default
  [game unit module-id]
  (utils/trace "modules.cycle"  "Cyceling module:" module-id) 
  (default-module-cycle game unit module-id))(ns net.licenser.epic.modules.damage
  (:use net.licenser.epic.utils)
  (:use net.licenser.epic.modules))

(defmulti module-hit-static
  "This method is called for every module if it be hit."
  (fn [module _ _] (trace "module-hit-static(dispatcher)" "got:" (:type module)) (:type module)))

(defmethod module-hit-static :armor
  [module damage partial]
  (let [my-dmg damage
	damage-absorbation (:damage-absorbation (:specification module))
	hull (max 0 (- (:hull module) (:damage module)))
	damage-absorbation (min hull damage-absorbation)
	damage (max 0 (- damage damage-absorbation))]
    {:module (damage-module module my-dmg)
     :damage damage
     :partial (if (>= damage-absorbation 0) (cons {:type :armor_impact :damage my-dmg :hp hull} partial) partial)
     :continue (if (<= damage 0) false true)}))

(defmethod module-hit-static :shield
  [module damage partial]
  (let [e (:energy (:specification module))
	my-dmg (min damage e)
	damage (- damage my-dmg)
	hull (max 0 (- (:hull module) damage))]
    {:module (update-in (damage-module module damage)
	       [:specification :energy] #(- % my-dmg))
     :partial (if (= 0 damage) (cons {:type :shield_impact :damage my-dmg :hp hull} partial) partial)
     :damage damage
     :continue (if (<= damage 0) false true)}))

(defmethod module-hit-static :hull
  [module damage partial]
  (let [m (damage-module module damage)]
    {:module m 
     :partial (cons {:type :impact :damage damage :hp (- (:hull m) (:damage m))} partial)
     :damage damage :continue true}))

(defmethod module-hit-static :default
  [module damage partial]
  {:module (damage-module module damage) :damage damage :continue true :partial partial})

(defn module-hit
  ([module damage prop partial]
     (if (and 
	  (< prop (:hit-propability module))
	  (not (module-destroyed? module)))
       (module-hit-static module damage partial)
       {:module module :damage damage :continue true :partial partial}))
  ([module damage partial]
     (module-hit module damage (rand) partial)))(ns net.licenser.epic.modules.usage
  (:require [net.licenser.epic.utils :as utils]))
(ns net.licenser.epic.modules
  (:use net.licenser.epic.utils))

(defstruct module
  :id :type :name :size :cycle-script :specification :mass :hull :damage :hit-propability :hit-priority)

(defn default-unit-hit-fn
  [attacker weapon target]
  (let [dist (int (map-distance @attacker @target))
	weapon-hull (:hull weapon)
	weapon-damage (:damage weapon)
	damage-penalty (/ (- weapon-hull weapon-damage) weapon-hull)
	accuracy (module-spec weapon :accuracy)
	variation (int (inc (module-spec weapon :variation)))
	range (int (module-spec weapon :range))
	rotatability (module-spec weapon :rotatability)
	a-manuv (module-spec (first (get-modules @attacker :hull)) :maneuverability)
	t-manuv (module-spec (first (get-modules @target :hull)) :maneuverability)
	aim (+ 
	     (* damage-penalty (/ (* accuracy (+ 2 (rand)))3)) 
	     (/ (- variation (int (Math/abs (- range dist)))) variation 2))
	mass (Math/log10 (+ (/ (Math/pow (unit-mass target) 1/3) (max dist 1)) 1))
	aiming (*
		(+
		 (/ (* a-manuv (+ 2 (rand))) 3)
		 rotatability)
		aim)
	evade (/ (* t-manuv (+ 2 (rand))) 3)]
    (> 1 (* (/ aiming evade) mass))))

(defn default-module-hit-fn
  [m]
  (> (rand) (:hit-propability m)))

(defn create-hull
  [name size mass hull maneuverability]
  (struct module nil :hull name size nil {:maneuverability maneuverability} mass hull 0 1.0 0.0))

(defn create-generic
  [type name size mass hull hit-propability hit-priority max-usage energy-usage]
  (struct module nil type name size nil {:max-usage max-usage :times-used 0 :energy-usage energy-usage} mass hull 0 hit-priority energy-usage))

(defn create-engine
  [name size mass hull hit-propability hit-priority range energy-usage]
  (create-generic :engine name size mass hull hit-propability hit-priority range energy-usage))

(defn create-weapon
  ([name  size  mass hull hit-propability hit-priority damage fire-rate range variation accuracy rotatability energy-usage unit-hit-fn module-hit-fn]
  (update-in 
   (create-generic :weapon name size mass hull hit-propability hit-priority fire-rate energy-usage)
   [:specification]
   #(assoc % 
      :damage damage
      :accuracy accuracy
      :rotatability rotatability
      :energy-usage energy-usage
      :unit-hit-fn unit-hit-fn
      :module-hit-fn module-hit-fn
      :range range
      :variation variation
      )))
  ([name  size  mass hull hit-propability hit-priority damage fire-rate range variation accuracy rotatability energy-usage]
     (create-weapon
     name  size  mass hull hit-propability hit-priority damage fire-rate range variation accuracy rotatability energy-usage
      default-unit-hit-fn default-module-hit-fn)))


(defn create-shield
  [name size mass hull energy]
  (struct module nil :shield name size nil {:max-energy energy :energy energy} mass hull 0 1.0 0.99))


(defn create-reactor
  [name size mass hull hit-propability hit-priority discharge-rate output capacity efficientcy]
  (struct module nil :reactor
	  name size nil
	  {:capacity capacity :energy capacity :output output :discharge-rate discharge-rate :discharged 0 :efficientcy efficientcy}
	  mass hull 0
	  hit-propability hit-priority))

(defn create-armor
  [name size mass hull hit-propability hit-priority damage-absorbtion]
  (struct module nil :armor
	  name size nil
	  {:damage-absorbation damage-absorbtion}
	  mass hull 0
	  hit-propability hit-priority))

(defn damage-module
  [module damage]
  (update-in module [:damage] #(min (:hull module) (+ % damage))))
  
(defn module-destroyed?
  [module]
  (>= (:damage module) (:hull module)))

(defn remaining-usages
  [module]
  (let [s (module-spec module)]
    (- (:max-usage s) (:times-used s))))
(ns net.licenser.epic.units.functions)

(defn move-unit
  [game unit-id x y])
(ns net.licenser.epic.units
  (:require (net.licenser.epic [modules :as modules] [utils :as utils]))
  (:require [net.licenser.epic.modules.damage :as modules.damage])
  (:require [net.licenser.epic.modules.cycle :as modules.cycle]))

(defstruct unit :id :name :team :cycle-script :x :y :modules :destroyed :last-target)

(defn no-script- [game unit-id]
  game)

(defn create-unit
  [id name team cycle-script x y & modules]
  (struct unit id name team (or cycle-script no-script-) x y (reduce #(let [id (utils/uuid)] (assoc %1 id (assoc %2 :id id))) {} modules) false nil))

(defn main-engine
  [unit]
   (first (sort-by (partial utils/module-spec :range) (utils/get-modules unit :engine))))

(defn unit-range
  [unit]
   (let [r (main-engine unit)]
     (modules/remaining-usages r)))


(defn unit-destroyed?
  [unit]
   (let [h (first (utils/get-modules unit :hull))]
     (>= (:damage h) (:hull h))))

(defn- update-unit
  [unit update-fn]
  (dosync
     (alter unit update-fn)))

(defn- make-module-list
  [modules]
  (if (empty? modules)
    {}
    (let [m (first modules)]
      (assoc (make-module-list (rest modules)) (:id m) m))))

(defn- hit-unit-modules
  [modules damage partial]
  (if modules
    (let [m (first modules)
	  ms (next modules)]
      (if (modules/module-destroyed? m)
	(let [{ms :modules ps :partial} (hit-unit-modules ms damage partial)]
	  {:modules (assoc ms (:id m) m) :partial (concat ps partial)})
	(let[t (modules.damage/module-hit m damage partial)
	     {damage :damage m :module continue :continue ps :partial} t
	     ps (if (modules/module-destroyed? m) (cons {:type :module_destroyed :module (:id m)} ps) ps)]
	  (utils/trace "hit-unit-modules" "hitting module:" m)  
	  (if (or (not continue) (<= damage 0))
	    (do 
	      (utils/trace "hit-unit-modules" "last module:" m)
	      (let [ms (make-module-list ms)]
		(utils/trace "hit-unit-modules" "generated module list:" ms)
		{:modules (assoc ms (:id m) m) :partial (concat ps partial)}))
	    (do
	      (utils/trace "hit-unit-modules" "handled module:" m)
	      (let [{ms :modules partial :partial} (hit-unit-modules ms damage partial)]
	      {:modules (assoc ms (:id m) m) :partial (concat ps partial)}))))))
    {:modules {} :partial partial}))


(defn init-unit
  [unit]
  (assoc unit :id (utils/uuid)))

(defn- hit-unit-priv
  [damage attacker unit]
   (let [id (:id unit)
	 ms (reverse (sort-by #(:hit-priority %) (vals (:modules unit))))
	 {ms :modules ps :partial} (hit-unit-modules ms damage '())
	 unit (assoc unit :modules ms)]
     (utils/combat-log :attack
		       {
			:unit (:id attacker)
			:target id
			:damage 5
			:partials ps
			})
     (if (unit-destroyed? unit)
       (do
	 (utils/combat-log :destroyed {:unit (:id unit) :partials ps})
	 (assoc unit :destroyed true))
       unit)))


(defn unit-energy
  [unit]
  (dosync
   (reduce + (map (partial utils/module-spec :energy) (utils/get-modules @unit :reactor)))))

(defn hit-unit
  [game unit attacker damage]
  (utils/trace "hit-unit" "hitting unit:" (type unit) "with:" damage "attacker:" (type attacker))
  (dosync
   (update-unit
    unit
    (partial hit-unit-priv damage @attacker))
   (if (unit-destroyed? @unit)
     (alter (utils/get-field (:map game) (:x @unit) (:y @unit))  #(doall (remove (partial = (:id @unit)) %)))))
  unit)

(defn unit-can-move-to?
  [unit x y]
  (let [d (utils/map-distance (:x unit) (:y unit) x y)]
    (and
     (>= (unit-range unit) d)
     (>= (unit-energy unit) (* (utils/module-spec :energy-usage (main-engine unit)) d)))))

(defn cycle-unit
  [game unit]
    (if (:destroyed @unit)
      (do
	(utils/trace 'units "Cycling unit: " (:id @unit) "(destroyed)")
	game)
      (do
	(utils/trace 'units "Cycling unit: " (:id @unit))
	(let [mods (map #(:id %) (utils/get-modules @unit))]
	  (dorun (map #(modules.cycle/cycle-module game unit %) mods)))
	(if (:cycle-script @unit)
	  (do
	    (utils/trace "units.cycle-unit" "Running custom script")
	    ((:cycle-script @unit) {'net.licenser.epic.utils/*cycle-log* utils/*cycle-log* '*out* *out*})
	    game)
	  game))))(ns net.licenser.epic.utils
  ;(:require [clojure.contrib.io :as c.c.io])
  (:import java.util.UUID))

(def *trace* false)

(declare *log*)

(defn module-spec
  ([module spec]
     (get (:specification module) spec))
  ([module] (:specification module)))

(defn get-module
  [unit module-id]
    (get (:modules unit) module-id nil))

(defn get-modules
  ([unit]
     (vals (:modules unit)))
  ([unit type]
     (filter #(= type (:type %)) (get-modules unit))))

(defn unit-energy
  [unit]
  (reduce + 0 (map #(module-spec % :energy) (get-modules unit :reactor))))

(defn use-energy
  [unit energy]
     (let [energy (ref energy)]
       (dosync
	(if (< @energy (unit-energy @unit))
	  (do
	    (alter 
	     unit assoc :modules 
	     (reduce 
	      #(assoc %1 (:id %2) %2) {}
	      (map (fn [m] (if (= (:type m) :reactor)
			     (let [e (module-spec m :energy)
				   used-e (min @energy e)]
			       (alter energy - used-e)
			       (update-in m [:specification :energy] #(- % used-e)))
			     m))
		   (vals (:modules @unit)))))
	    true)
	  false))))

(defn unit-mass
  [unit]
  (reduce + (map (fn [m] (:mass m)) (get-modules unit))))

(defn unit-hull [unit]
  (:hull (first (get-modules unit :hull))))

(defn unit-data [unit]
  {:id (:id unit)
   :damage 0
   :type {
	  :name (:name (first (get-modules unit :hull)))
	  :hull (unit-hull unit)}
   :team (:team unit)
   :mass (unit-mass unit)
   :modules '()})

(def *cycle-log* (ref []))

(defn cycle-log []
  (dosync
   (alter *log* conj @*cycle-log*)
   (ref-set *cycle-log* [])))

(defn combat-log [type msg]
  (dosync
   (alter net.licenser.epic.utils/*cycle-log* conj (assoc msg :type type))))

(defn log [prefix id & msgs]
  (binding [*out* *err*]
	(apply println (str prefix "<" id ">:") msgs)))

;(defmacro trace [id & msgs]
;  (if *trace*
;    `(apply log "Trace" ~id (seq ~msgs))))

(defn trace [id & msgs]
  (if *trace*
    (apply log "Trace" id msgs)))


(defn update-module
  [unit module-id update-fn]
  (trace "update-module" "updating" module-id (str "(" (:type (get-module @unit module-id)) ")") "of" (:id @unit))
  (dosync
   (alter unit update-in [:modules module-id] update-fn)))

(defn update-module-specs
  ([unit module-id update-fn]
     (update-module unit module-id (fn [m] (assoc m :specification (update-fn (:specification  m))))))
  ([unit module-id spec update-fn]
     (update-module-specs unit module-id (fn [s] (assoc s spec (update-fn (get s spec)))))))


(defn get-unit
  [game unit-id]
  (get (:units game) unit-id nil))

(defn in-direction
  ([x y direction]
  (condp = (int direction)
    0 [(inc x) (inc y)]
    1 [(inc x) y]
    2 [x (dec y)]
    3 [(dec x) (dec y)]
    4 [(dec x) y]
    5 [x (inc y)]))
  ([u direction]
     (in-direction (:x u) (:y u) direction)))

(defn to-evil [x y]
  [(- x y) (int (Math/floor (/ (* -1 (+ x y)) 2.0)))])

(defn- to-cartesian [x y]
  (let [[e-x e-y] (to-evil x y)
	c-x e-x
	c-y (if (odd? e-x) (+ 0.5 e-y) e-y)
	c-y (* -1 c-y (Math/sqrt 3))]
  [c-x c-y]))

(defn friction-direction-to
  ([x1 y1 x2 y2]

     (let [[c-x1 c-y1] (to-cartesian x1 y1)
	   [c-x2 c-y2] (to-cartesian x2 y2)
	   a (Math/atan2 (- c-y2 c-y1) (- c-x2 c-x1))
	   a (- 0 (/ a Math/PI))
	   a (+ a 0.5)
	   a (* a 3)
	   a (mod a 6)]
       a))
  ([u1 u2]
     (friction-direction-to (:x u1) (:y u1) (:x u2) (:y u2))))

(defn direction-to
  ([x1 y1 x2 y2]
     (Math/round #^Double (friction-direction-to x1 y1 x2 y2)))
  ([u1 u2]
     (direction-to (:x u1) (:y u1) (:x u2) (:y u2))))

(defn uuid
  []
  (.toString (UUID/randomUUID)))

(defn static
  [val]
  (fn [ & _ ] val))

(defn map-distance
  ([u1 u2]
     (map-distance (:x u1) (:y u1) (:x u2) (:y u2)))
  ([x1 y1  x2 y2]
    (let [x1 (int x1)
	  x2 (int x2)
	  y1 (int y1)
	  y2 (int y2)
	  dx (- x1 x2)
	  dy (- y1 y2)
	  ax (Math/abs dx)
	  ay (Math/abs dy)]
    (if (= (< dx 0) (< dy 0))
      (max ax ay)
      (+ ax ay)))))

(defn get-field [map x y]
  (dosync
   (trace "fet-fied" x y)
   (let [f-x (get @map x)]
     (if f-x
       (let [f-y (get @f-x y)]
	 (trace "fet-fied" "x already present")
	 (if f-y
	   (do
	     (trace "fet-fied" "y already present")
	     f-y)
	   (let [f-y (ref '())]
	     (alter f-x assoc y f-y)
	     f-y)))
       (let [f-y (ref '())
	     f-x (ref {y f-y})]
	 (alter map assoc x f-x)
	 f-y)))))(ns net.licenser.epic
  (:use net.licenser.epic.utils)
  (:use net.licenser.epic.game)
  (:use net.licenser.sandbox)
  (:use net.licenser.sandbox.tester)

  (:require (net.licenser.epic [modules :as modules] [units :as units]))
  (:require [clojure.contrib.json.write :as json])
  (:require [clojure.contrib.json.read :as jr])  
  (:require [clojure.contrib.duck-streams :as io])
  (:use net.licenser.epic.game.logic)
  (:use net.licenser.epic.game.basic)
  (:use clojure.template)
  (:use clojure.stacktrace)
  (:use clojure.contrib.command-line)
  (:gen-class))

(def *compiler* 
     (stringify-sandbox (new-sandbox-compiler :namespace 'net.licenser.epic.sandbox :tester debug-tester :timeout 500)))
(declare *game* *unit-id*)

(def *tick-hard-limit* 500)

(defn load-data-file
  [file add-fn]
  (doall
   (map (fn [data] (add-fn data)) (jr/read-json (slurp file)))))

(def *modules* (ref {}))

(def *scripts* (ref {}))

(defn add-hull
  [{name "name" size "size" maneuverability "maneuverability" hull "hull" mass "mass" :as data}]
  (dosync (alter *modules* assoc name (modules/create-hull name size mass hull maneuverability))))

(defn add-engine
  [{name "name" size "size" mass "mass" hull "hull" hit-propability "hit-propability" hit-priority "hit-priority" energy-usage "energy-usage" range "range"}]
  (dosync (alter *modules* assoc name (modules/create-engine name size mass hull hit-propability hit-priority range energy-usage))))

(defn add-armor
  [{name "name" size "size" mass "mass" hull "hull" hit-propability "hit-propability" hit-priority "hit-priority" damage-absorbtion "damage-absorbtion"}]
  (dosync (alter *modules* assoc name (modules/create-armor name size mass hull hit-propability hit-priority damage-absorbtion))))

(defn add-reactor 
  [{name "name" size "size" mass "mass" hull "hull" hit-propability "hit-propability" hit-priority "hit-priority" discharge-rate "discharge-rate" output "output" capacity "capacity" efficientcy "efficientcy"}]
  (dosync (alter *modules* assoc name (modules/create-reactor name size mass hull hit-propability hit-priority discharge-rate output capacity efficientcy))))

(defn add-shield
  [{name "name" size "size" mass "mass" hull "hull" energy "energy"}]
  (dosync (alter *modules* assoc name (modules/create-shield name size mass hull energy))))

(defn add-weapon
  [{
    name "name" 
    size "size" 
    mass "mass"
    hull "hull"
    hit-propability "hit-propability"
    hit-priority "hit-priority"
    damage "damage"
    fire-rate "fire-rate"
    range "range"
    variation "variation"
    accuracy "accuracy"
    rotatability "rotatability"
    energy-usage "energy-usage"}]
  (dosync (alter *modules* assoc name (modules/create-weapon name size mass hull hit-propability hit-priority damage fire-rate range variation accuracy rotatability energy-usage))))


(def *data-directory* "./data")

(def *pp-json* false)

(defn bind-game
  ([game]
     {:cycle-log (ref [])
      :game-log (ref [])
      :game game})
  ([]
     (bind-game (create-game 50))))

(defn cycle-game
  ([game-data]
     (binding [*cycle-log* (:cycle-log game-data)
	       *log* (:game-log game-data)]
       (let [t1  (filter #(= "one" (:team (deref %))) (vals (:units (:game game-data))))
	     c-t1 (count t1)
	     t1-a (filter #(not (:destroyed (deref %))) t1)
	     c-t1-a (count t1-a)
	     t2  (filter #(= "two" (:team (deref %))) (vals (:units (:game game-data))))
	     c-t2 (count t2)
	     t2-a (filter #(not (:destroyed (deref %))) t2)
	     c-t2-a (count t2-a)]
	 (println "team one:" c-t1-a "of" c-t1)
	 (println "team two:" c-t2-a "of" c-t2)
	 (if (and
	      (not (zero? c-t1-a))
	      (not (zero? c-t2-a)))
	   (do
	     (cycle-log)
	     (time (cycle-game* (:game game-data) 200))
	     (println "log:" (count @*log*))
	     (println "cycle-log:" (count @*cycle-log*))
	     @*cycle-log*)
	   nil))))
  ([] (cycle-game *game*)))


(defn best-target
  ([hostiles perfect-fn better-fn target]
     (if (not (empty? hostiles))
       (let [t (first hostiles)]
	 (if (or (nil? target) (better-fn @t @target))
	   (if (perfect-fn @t)
	     t
	     (recur (rest hostiles) perfect-fn better-fn t))
	   (recur (rest hostiles) perfect-fn better-fn target)))
       target))
     ([hostiles perfect-fn better-fn]
	(best-target hostiles perfect-fn better-fn nil)))

(defn ff-cycle-script
  [game unit]
    (let [d (module-spec (first (get-modules @unit :weapon)) :range)
	  hostiles (find-hostile-units game unit 100)
	  target (best-target 
		  hostiles 
		  (fn [t] (and (< 1 (map-distance @unit t) 3) (> 100 (unit-mass t))))
		  (fn [new-t old-t] (< (map-distance @unit new-t) (map-distance @unit old-t))))]
      (if target
	(dosync 
	  (register-target unit target)
	  (fire-all (intercept-unit game unit target 2) unit target))
	game)))


(defn dd-cycle-script
  [game unit]
  (let [pd-range 5
	weapon-range 15
	last (:last-target @unit)]
    (if (and last (not (:destroyed @last)))
      (dosync 
	(fire-all (intercept-unit game unit last (if (> (unit-mass @last) 10000) weapon-range pd-range)) unit last)
	(emply-point-defense game unit)
	game)
      (let [hostiles (find-hostile-units game unit 100)
	    target (best-target 
		    hostiles 
		    (fn [t] (and (< 13 (map-distance @unit t) 17) (< 10000 (unit-mass t))))
		    (fn [new-t old-t]
		      (let [n-m (unit-mass new-t)
			    o-m (unit-mass old-t)]
		      (or 
		       (> (unit-mass new-t) (unit-mass old-t))
		       (and
			(< (* o-m 0.1) (Math/abs #^Integer (- o-m n-m)))
			(< (map-distance @unit new-t)  (map-distance @unit old-t)))))))]
	(trace "cyclescript" "cycle for" (:id @unit) "attacking:" target)
	(if target
	  (dosync
	   (combat-log :target {:unit (:id @unit) :target (:id @target)})
	   (register-target unit assoc target)
	   (fire-all (intercept-unit game unit target (if (> (unit-mass @target) 10000) weapon-range pd-range)) unit target)
	   (emply-point-defense game unit))
	  game)))))

(dosync
 (alter *scripts* assoc "fighter" ff-cycle-script)
 (alter *scripts* assoc "destroyer" dd-cycle-script))

(defn build-unit
  [team unit]
  (let [modules (get unit "modules")
	code (*compiler* (get unit "script") 
			 'move
			 'intercept
			 'foes-in-range 
			 'unit-at 
			 'fire-at
			 'mass-of
			 'fire-all
			 'distance-to)
	n (str (gensym "") "-" team)
	u (units/init-unit 
	   (apply units/create-unit 
		  0 n team code 0 0
		  (map #(get @*modules* %) modules)))]
    (assoc u :id n)))


(defn init-unit-script
  [game unit]
  (let [script (:cycle-script @unit)]
    (trace "init-unit-script" script)
    (dosync (alter unit assoc  :cycle-script
		   (fn cycle-script [bindings] 
		     (trace "init-unit-script-cycle-script" script)
		     (script bindings 
			     ; I am  not certain what happens here but I seem to need this nil ...
					;move
			     (fn move [direction]
			       (trace "script/move" "Moving to" direction)
			       (throw (RuntimeException. "Not implemented yet."))
			       nil)
					;intercept
			     (fn intercept [target distance]
			       (trace "script/intercept" "Intercepting:" target)
			       (when-let [target (get-unit game target)]
				 (dosync (intercept-unit game unit target distance))
				 (map-distance @unit @target)))
					;foes-in-range
			     (fn foes-in-range [range]
			       (trace "script/foes-in-range" "Getting foes in range" range)
			       (map (fn [u] (:id @u)) (find-hostile-units game unit range)))
					;unit-at 
			     (fn unit-at [x y]
			       43)
					;fire-at
			     (fn fire-at [weapon target]
			       (trace "script/fire-at" "Fiering weapon" weapon "at" target)
			       (when-let [target (get-unit game target)]
				 (dosync (fire-weapon game unit weapon target)))
			       nil)
					;mass-of
			     (fn mass-of [target]
			       (trace "script/mass-of" "Getting mass of" target)
			       (when-let [target (get-unit game target)]
				 (unit-mass @target)))
					;fire-all
			     (fn fire-all-for-unit [target]
			       (trace "script/fire-all" "Fiering all at" target)
			       (when-let [target (get-unit game target)]
				 (dosync (fire-all game unit target)))
			       nil)
					;distance-to
			     (fn distance-to [target]
			       (trace "script/distance-to" "Calculating distance to" target)
			       (when-let [target (get-unit game target)]
				 (map-distance @unit @target)))))))
    unit))
  
		  
(defn valid-unit
  [unit]
  (and 
   (= 1 (count (get-modules unit :hull)))
   (= 1 (count (get-modules unit :engine)))
   (>= 1 (count (get-modules unit :generator)))))

(defn compile-game
  [data]
  (let [game (bind-game)]
    (binding [*cycle-log* (:cycle-log game)
	      *log* (:game-log game)]
      (let [game (assoc 
		     game :game 
		     (reduce (fn [game [team data]]
			       (let[classes (get data "classes" {})
				    units (get data "units")
				    start-x (get data "start-x")
				    start-y (get data "start-y")
				    d-x (get data "d-x")
				    d-y (get data "d-y")
				    row-size (get data "row-size")]
				 (reduce 
				  (fn [game [unit i]]
				    (let [class (get unit "class")
					  unit (if class (merge unit (get classes class)) unit)
					  u (build-unit team unit)]
				      (if (valid-unit u)
					(let [x (- start-x (mod i row-size))
					      x (int (+ x (* d-x (Math/floor (/ i row-size)))))
					      y (- start-y (mod i row-size))
					      y (int (+ y (* d-y (Math/floor (/ i row-size)))))
					      g (add-unit game u)
					      u (get-unit g (:id u))
					     ]
					  (combat-log :spawn {:unit (:id @u) :team team :data (unit-data @u)})
					  (move-unit* g u x y))
					(do 
					  (println "Invalid unit:" unit)
					  game)))) game (map (fn [a b] [a b]) units (iterate inc 0))))) (:game game) data))]
	(dorun (map (partial init-unit-script (:game game)) (vals (:units (:game game)))))
	game))))

(defn load-fight
  [file]
  (compile-game (jr/read-json (slurp file))))
  

(defn make-cycle-seq
     [game]
     (lazy-seq 
      (let [l (cycle-game game)]
	(if (and (not (empty? l)) (> *tick-hard-limit* (count @(:game-log game))))
	  (cons l (make-cycle-seq game))
	  nil))))

(defn save-log 
  [game file]
  (binding [*cycle-log* (:cycle-log game)
	    *log* (:game-log game)]
    (dosync
       (if (not (empty? @*cycle-log*)) (cycle-log))
       (io/with-out-writer (io/writer file)
	 ((if *pp-json* json/print-json json/print-json) @*log*)))))

(defn save-log1
  [game]
  (save-log game "x:/interface/log.json"))
       


(defn multi-game-seq
     [games]
     (lazy-seq 
      (let [_ (println "=====START=====")
	    f (time (doall (map first games)))
	    _ (println "======END======")]
	(if (every? nil? f)
	  nil
	(cons f (multi-game-seq (map rest games)))))))

(defn load-data
  [data-directory]
  (load-data-file (str data-directory "/hulls.json") add-hull)
  (load-data-file (str data-directory "/engines.json") add-engine)
  (load-data-file (str data-directory "/armors.json") add-armor)
  (load-data-file (str data-directory "/generators.json") add-reactor)
  (load-data-file (str data-directory "/shields.json") add-shield)
  (load-data-file (str data-directory "/weapons.json") add-weapon))


(defn -main
  [& args]
  (with-command-line args
    "EPIC shell"
    [[data-directory "specifies a data directory." "./data"]
     [in-file "json fight definition" "./fight.json"]
     [out-file "output json file" "./log.json"]
     ]
    (load-data data-directory)
    (let [a-game (load-fight in-file)
	  g (make-cycle-seq a-game)]
      (println "START")
      (time (def x (dorun g)))
      (save-log a-game out-file))))
(require '[clojure.contrib.test-is :as test-is])

(deftest module-tick-reactor
  (test-is/are
   (= _1 (:energy (module-tick {:type :reactor :energy _2 :max-energy 42 :output 10})))
   42 42
   11 1))