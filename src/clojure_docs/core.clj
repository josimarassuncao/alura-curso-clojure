(ns clojure-docs.core)

(defn greeting
  ([] "Hello, World!")
  ([x] (str "Hello, " x "!"))
  ([x,y] (str x ", " y "!")))

;; For testing
(assert (= "Hello, World!" (greeting)))
(assert (= "Hello, Clojure!" (greeting "Clojure")))
(assert (= "Good morning, Clojure!" (greeting "Good morning" "Clojure")))

(defn do-nothing
  [x]
  x)

(do-nothing 1)
(do-nothing :test)

(defn always-thing
  [& more]
  100)

(defn always-false
  [& more]
  false)

(defn make-thingy
  [x]
  (fn [& more] x))

;; Tests
(let [n (rand-int Integer/MAX_VALUE)
      f (make-thingy n)]
  (assert (= n (f)))
  (assert (= n (f 123)))
  (assert (= n (apply f 123 (range)))))

(defn triplicate
  [f]
  (dotimes [n 3]
    (println "time -> " n)
    (f)))

(triplicate always-thing)
(triplicate (fn [] (apply println [1000])))

(defn opposite
  [f]
  (fn [& args] (not (apply f args))))

((opposite always-thing))
((opposite always-false))

;; same result
((complement always-thing))
((complement always-false))

(defn triplicate2
  [f & more]
  (triplicate (fn [] (apply f more))))

(triplicate2 always-thing)
(triplicate2 println "olah")

(defn cos-of-pi
  []
  (Math/cos Math/PI))

(cos-of-pi)

(defn cos-plus-sin-of-x
  [x]
  (+ (Math/pow (Math/cos x) 2.0) (Math/pow (Math/sin x) 2.0)))

(cos-plus-sin-of-x 60)
(cos-plus-sin-of-x 30)
(cos-plus-sin-of-x 50)
(cos-plus-sin-of-x 45)

(def url-example "http://example.com/")
(defn http-get
  [url]
  (let [url-con (java.net.URL. url)]
    (slurp (.openStream url-con))))

(http-get url-example)

(assert (.contains (http-get url-example) "html"))
(assert (.contains (http-get "https://www.w3.org") "html"))

(defn one-less-arg
  [f x]
  (fn [& more] (apply f x more)))

((one-less-arg println 100))

((partial println 100))

(defn two-fns
  [f g]
  (fn [x] (f (g x))))

(def f1 (fn [x] (+ x 10)))
(def f2 (fn [x] (* x 11)))

((two-fns f1 f2) 3)
((two-fns f1 f2) -1)
; The same as comp
((comp f1 f2) 3)
((comp f1 f2) -1)
