(ns clojure-docs.core
  (:use clojure.pprint))

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

;; Vectors and Hashes

(def v1 [1 2 3 4 5 6])
(contains? v1 3)
(contains? v1 9)

;; these next sentence fails, and I'm clueless know why?
(contains? (conj v1 9 10) 9)

(def v2 (conj v1 9 10))
(contains? v2 9)

(def h1 #{1 2 3 4 5 6})
(contains? h1 3)
(contains? h1 9)

(contains? (conj h1 9 10) 9)

(def h2 (conj h1 9 10))
(contains? h2 9)

(into h2 v2)
(into v2 h2)

;; Define a record structure
(defrecord Person [first-name last-name age occupation ])

;; Positional constructor - generated
(def kelly-1 (->Person "Kelly" "Keen" 32 "Programmer"))

;; Map constructor - generated
(def kelly-2 (map->Person
             {:first-name "Kelly"
              :last-name "Keen"
              :age 32
              :occupation "Programmer"}))

(defrecord Scope [name])
(defrecord Function [name scopes])

(def s-1 (->Scope "manager"))
(def s-2 (->Scope "lead"))
(def s-3 (->Scope "engineer"))

(def fu-1 (->Function "IT manager" [s-1]))
(def fu-2 (->Function "Dev specialist" [s-2 s-3]))

(pprint fu-1)
(pprint fu-2)

(first (seq (:scopes fu-2)))
(rest (seq (:scopes fu-2)))

;; on this next command something curious happen
;; the seq after the first, turns the keys and values into pairs in a vector
;; and because of that no error is raised =O
(rest (seq (first (seq (:scopes fu-2)))))

(seq {:a 1 :b 2})

;; this next command throws an error
;(seq (first '(1)))
(seq (first []))
(seq (first {:a 1 :b 2}))
