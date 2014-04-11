(import '(java.awt Frame)
	'(java.awt.event WindowListener WindowAdapter)
	'(javax.media.opengl GLCanvas GLEventListener GL)
	'(com.sun.opengl.util Animator ))

(def gear1 (ref nil))
(def gear2 (ref nil))
(def gear3 (ref nil))
(def angle (ref 0.0))

(defmacro gl-beg-end 
  [gl mode & body]
  (list 'do 
	(list '. 'gl 'glBegin (list '. 'GL mode))
	(cons 'do body)
	(list '. 'gl 'glEnd)))
  

(defn gear 
  [#^javax.media.opengl.GL gl inner-radius outer-radius width teeth tooth-depth]
  (let [r0 inner-radius
	r1 (- outer-radius (/ tooth-depth 2.0))
	r2 (+ outer-radius (/ tooth-depth 2.0))
	da (* 2.0 (/ (. Math PI) teeth 4.0))
	+width (* width 0.5)
	-width (* width -0.5)]
    (doto gl
      (glShadeModel (. GL GL_FLAT))
      (glNormal3f 0.0 0.0 1.0))

    ; draw front face
    (gl-beg-end gl GL_QUAD_STRIP
      (doseq i (range (+ 1 teeth))
	(let [angle (/ (* i 2.0 (. Math PI)) teeth)]
	  (doto gl
	    (glVertex3f (* r0 (. Math cos angle)) (* r0 (. Math sin angle)) +width)
	    (glVertex3f (* r1 (. Math cos angle)) (* r1 (. Math sin angle)) +width))
	  (when (< i (+ 1 teeth))
	    (doto gl
	      (glVertex3f (* r0 (. Math cos angle)) (* r0 (. Math sin angle)) +width)
	      (glVertex3f (* r1 (. Math cos (+ angle (* 3.0 da)))) (* r1 (. Math sin (+ angle (* 3.0 da)))) +width))))))
    
    ; draw front sides of teeth
    (gl-beg-end gl GL_QUADS
      (doseq i (range (+ 1 teeth))
	(let [angle (/ (* i 2.0 (. Math PI)) teeth)]
	  (doto gl
	    (glVertex3f (* r1 (. Math cos angle)) (* r1 (. Math sin angle)) +width)
	    (glVertex3f (* r2 (. Math cos (+ da angle))) (* r2 (. Math sin (+ angle da))) +width)
	    (glVertex3f (* r2 (. Math cos (+ angle (* 2.0 da)))) (* r2 (. Math sin (+ angle (* 2.0 da)))) +width)
	    (glVertex3f (* r1 (. Math cos (+ angle (* 3.0 da)))) (* r1 (. Math sin (+ angle (* 3.0 da)))) +width)))))    

    ;; draw back face
    (gl-beg-end gl GL_QUAD_STRIP
      (doseq i (range (+ 1 teeth))
	(let [angle (/ (* i 2.0 (. Math PI)) teeth)]
	  (doto gl
	    (glVertex3f (* r1 (. Math cos angle)) (* r1 (. Math sin angle)) -width)
	    (glVertex3f (* r0 (. Math cos angle)) (* r0 (. Math sin angle)) -width)
	    (glVertex3f (* r1 (. Math cos (+ angle (* 3.0 da)))) (* r1 (. Math sin (+ angle (* 3.0 da)))) -width)
	    (glVertex3f (* r0 (. Math cos angle)) (* r0 (. Math sin angle)) -width)))))

    ;; draw back sides of teeth
    (gl-beg-end gl GL_QUADS
      (doseq i (range (+ 1 teeth))
	(let [angle (/ (* i 2.0 (. Math PI)) teeth)]
	  (doto gl
	    (glVertex3f (* r1 (. Math cos (+ angle (* 3.0 da)))) (* r1 (. Math sin (+ angle (* 3.0 da)))) -width)
	    (glVertex3f (* r2 (. Math cos (+ angle (* 2.0 da)))) (* r2 (. Math sin (+ angle (* 2.0 da)))) -width)
	    (glVertex3f (* r2 (. Math cos (+ angle da))) (* r2 (. Math sin (+ angle da))) -width)
	    (glVertex3f (* r1 (. Math cos angle)) (* r1 (. Math sin angle)) -width)))))

    ;; draw outward faces of teeth
    (gl-beg-end gl GL_QUAD_STRIP
      (doseq i (range (+ 1 teeth))
	(let [angle (/ (* i 2.0 (. Math PI)) teeth)
	      u-div (- (* r2 (. Math cos (+ angle da))) (* r1 (. Math cos angle)))
	      v-div (- (* r2 (. Math sin (+ angle da))) (* r1 (. Math sin angle)))
	      len (. Math sqrt (+ (* u-div u-div) (* v-div v-div)))
	      u1 (/ u-div len)
	      v1 (/ v-div len)
	      u2 (- (* r1 (. Math cos (+ angle (* 3 da)))) (* r2 (. Math cos (+ angle (* 2 da)))))
	      v2 (- (* r1 (. Math sin (+ angle (* 3 da)))) (* r2 (. Math sin (+ angle (* 2 da)))))]
	  (doto gl
	    (glVertex3f (* r1 (. Math cos angle)) (* r1 (. Math sin angle)) +width)
	    (glVertex3f (* r1 (. Math cos angle)) (* r1 (. Math sin angle)) -width)
	    (glNormal3f v1 (- u1) 0)
	    (glVertex3f (* r2 (. Math cos (+ angle da))) (* r2 (. Math sin (+ angle da))) +width)
	    (glVertex3f (* r2 (. Math cos (+ angle da))) (* r2 (. Math sin (+ angle da))) -width)
	    (glNormal3f (. Math cos angle) (. Math sin angle) 0.0)
	    (glVertex3f (* r2 (. Math cos (+ angle (* 2 da)))) (* r2 (. Math sin (+ angle (* 2 da)))) +width)
	    (glVertex3f (* r2 (. Math cos (+ angle (* 2 da)))) (* r2 (. Math sin (+ angle (* 2 da)))) -width)
	    (glNormal3f v2 (- u2) 0.0)
	    (glVertex3f (* r1 (. Math cos (+ angle (* 3 da)))) (* r1 (. Math sin (+ angle (* 3 da)))) +width)
	    (glVertex3f (* r1 (. Math cos (+ angle (* 3 da)))) (* r1 (. Math sin (+ angle (* 3 da)))) -width)
	    (glNormal3f (. Math cos angle) (. Math sin angle) 0.0))))
      (doto gl
	(glVertex3f (* r1 (. Math cos 0)) (* r1 (. Math sin 0)) +width)
	(glVertex3f (* r1 (. Math cos 0)) (* r1 (. Math sin 0)) -width)))
    
    (. gl glShadeModel (. GL GL_SMOOTH))
    
    ;; draw inside radius cylinder
    (gl-beg-end gl GL_QUAD_STRIP
      (doseq i (range (+ 1 teeth))
	(let [angle (/ (* i 2.0 (. Math PI)) teeth)]
	  (doto gl
	    (glNormal3f (- (. Math cos angle)) (- (. Math sin angle)) 0.0)
	    (glVertex3f (* r0 (. Math cos angle)) (* r0 (. Math sin angle)) -width)
	    (glVertex3f (* r0 (. Math cos angle)) (* r0 (. Math sin angle)) +width)))))))
  
(defn go []
  (let [frame (new Frame)
	gl-canvas (new GLCanvas)
	animator (new Animator gl-canvas)
	view-rotx 20.0
	view-roty 30.0
	view-rotz 0.0]
    (. gl-canvas 
	 (addGLEventListener
	  (proxy [GLEventListener] []
	    (display [#^javax.media.opengl.GLAutoDrawable drawable]
		     (dosync
		      (ref-set angle (+ 2.0 @angle)))
		     (let [gl (. drawable getGL)]
		       (doto gl
			 (glClear (. GL GL_DEPTH_BUFFER_BIT))
			 (glClear (. GL GL_COLOR_BUFFER_BIT))
			 (glPushMatrix)
			 (glRotatef view-rotx 1.0 0.0 0.0)
			 (glRotatef view-roty 0.0 1.0 0.0)
			 (glRotatef view-rotz 0.0 0.0 1.0)
			 
			 (glPushMatrix)
			 (glTranslatef -3.0 -2.0 0.0)
			 (glRotatef @angle 0.0 0.0 1.0)
			 (glCallList @gear1)
			 (glPopMatrix)
			 
			 (glPushMatrix)
			 (glTranslatef 3.1 -2.0 0.0)
			 (glRotatef (- (* -2.0 @angle) 9.0) 0.0 0.0 1.0)
			 (glCallList @gear2)
			 (glPopMatrix)
			 
			 (glPushMatrix)
			 (glTranslatef -3.1 4.2 0.0)
			 (glRotatef (- (* -2.0 @angle) 25.0) 0.0 0.0 1.0)
			 (glCallList @gear3)
			 (glPopMatrix)
			 
			 (glPopMatrix))))
	    (displayChanged [drawable mode-changed device-changed])
	    (init [#^javax.media.opengl.GLAutoDrawable drawable]
		  (let [gl (. drawable getGL)
			pos (float-array 4 '(5.0 5.0 10.0 0.0))
			red (float-array 4 '(0.8 0.1 0.0 1.0))
			green (float-array 4 '(0.0 0.8 0.2 1.0))
			blue (float-array 4 '(0.2 0.2 1.0 1.0))]
		    (.. System out (println (str "INIT GL IS: " (.. gl (getClass) (getName)))))
		    (.. System out (println (str "Chosen GLCapabilities: " (. drawable getChosenGLCapabilities))))
		    (. gl setSwapInterval 1)
		    (. gl glLightfv (. GL GL_LIGHT0) (. GL GL_POSITION) pos 0)
		    (. gl glEnable (. GL GL_CULL_FACE))
		    (. gl glEnable (. GL GL_LIGHTING))
		    (. gl glEnable (. GL GL_LIGHT0))
		    (. gl glEnable (. GL GL_DEPTH_TEST))
		    
  		    (dosync (ref-set gear1 (. gl glGenLists 1)))
		    (. gl glNewList @gear1 (. GL GL_COMPILE))
		    (. gl glMaterialfv (. GL GL_FRONT) (. GL GL_AMBIENT_AND_DIFFUSE) red 0)
		    (gear gl 1.0 4.0 1.0 20 0.7)
		    (. gl glEndList)
		    
		    (dosync (ref-set gear2 (. gl glGenLists 1)))
		    (. gl glNewList @gear2 (. GL GL_COMPILE))
		    (. gl glMaterialfv (. GL GL_FRONT) (. GL GL_AMBIENT_AND_DIFFUSE) green 0)
		    (gear gl 0.5 2.0 2.0 10 0.7)
		    (. gl glEndList)
		    
		    (dosync (ref-set gear3 (. gl glGenLists 1)))
		    (. gl glNewList @gear3 (. GL GL_COMPILE))
		    (. gl glMaterialfv (. GL GL_FRONT) (. GL GL_AMBIENT_AND_DIFFUSE) blue 0)
		    (gear gl 1.3 2.0 0.5 10 0.7)
		    (. gl glEndList)
		    
		    (. gl glEnable (. GL GL_NORMALIZE))))
	    
	    (reshape [#^javax.media.opengl.GLAutoDrawable drawable x y width height]
		     (let [gl (. drawable getGL)
			   h (/ height width)]
		       (doto gl
			 (glMatrixMode (. GL GL_PROJECTION))
			 (glLoadIdentity)
			 (glFrustum -1.0 1.0 (- h) h 5.0 60.0)
			 (glMatrixMode (. GL GL_MODELVIEW))
			 (glLoadIdentity)
			 (glTranslatef 0.0 0.0 -40.0))
		       (.. System out (println (str "GL_VENDOR: " (. gl glGetString (. GL GL_VENDOR)))))
		       (.. System out (println (str "GL_RENDERER: " (. gl glGetString (. GL GL_RENDERER)))))
		       (.. System out (println (str "GL_VERSION: " (. gl glGetString (. GL GL_VERSION))))))))))
      (. frame add gl-canvas)
      (. frame (setSize 300 300))
      (. frame
	 (addWindowListener
	  (proxy [WindowAdapter] []
	    (windowClosing [event]
			   (. (new Thread
				   (fn []
				     (. animator stop)
				     (. frame dispose))) start)))))
      (. frame show)
      (. animator start)))