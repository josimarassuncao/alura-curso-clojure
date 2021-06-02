(ns alura-curso.logistics.shipping.model
  (:import (clojure.lang PersistentQueue))
  (:use (clojure pprint)))

(def empty-queue PersistentQueue/EMPTY)

(defn new-distribution!
  "Creates a new distribution line of products"
  []
  {:general     empty-queue
   :sports      empty-queue
   :clothes     empty-queue
   :electronics empty-queue
   :souvenirs   empty-queue
   })
