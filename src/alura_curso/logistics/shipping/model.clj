(ns alura-curso.logistics.shipping.model
  (:import (clojure.lang PersistentQueue))
  (:use (clojure pprint)))

(def empty-queue PersistentQueue/EMPTY)

(defn- pair-key-empty
  [h-map key]
  (assoc h-map key empty-queue))

(defn new-distribution!
  "Creates a new distribution line of products"
  ([] (new-distribution! [:general, :sports, :clothes, :electronics :souvenirs]))
  ([q-ids]
   (reduce pair-key-empty {} q-ids)))
