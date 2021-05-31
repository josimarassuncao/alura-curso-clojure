(ns alura-curso.logistics.shipping.core
  (:require [alura-curso.logistics.shipping.model :as s-model]
            [alura-curso.logistics.shipping.logic :as s-logic])
  (:use [clojure pprint]))

(def distribution-center (s-model/new-distribution!))
(println "Starts distribution center - shipping")
(pprint distribution-center)

(defn- update-queue
  [value]
  (alter-var-root #'distribution-center (constantly value)))

(defn simulates-a-day
  []
  ;; "Adds data to general queue"
  (update-queue (s-logic/add-shipping distribution-center :general "111"))
  (update-queue (s-logic/add-shipping distribution-center :general "222"))
  (update-queue (s-logic/add-shipping distribution-center :general "333"))
  (update-queue (s-logic/add-shipping distribution-center :general "444"))
  (update-queue (s-logic/add-shipping distribution-center :sports "sports-1"))
  (update-queue (s-logic/add-shipping distribution-center :electronics "elec-1"))
  (update-queue (s-logic/add-shipping distribution-center :clothes "clot-1"))
  (update-queue (s-logic/add-shipping distribution-center :any "any-1"))
  (update-queue (s-logic/add-shipping distribution-center :any "any-2"))
  (println "1 > General queue data")
  (pprint distribution-center)
  ;; Removes data from general queue
  (update-queue (s-logic/shipping-prepared! distribution-center :general))
  (update-queue (s-logic/shipping-prepared! distribution-center :sports))
  (update-queue (s-logic/shipping-prepared! distribution-center :electronics))
  (update-queue (s-logic/shipping-prepared! distribution-center :any))
  (update-queue (s-logic/shipping-prepared! distribution-center :clothes))
  (println "2 > General queue data")
  (pprint distribution-center)
  ;; Limits the size of the queues to 5 items
  (update-queue (s-logic/add-shipping distribution-center :general "555"))
  (update-queue (s-logic/add-shipping distribution-center :general "666"))
  (update-queue (s-logic/add-shipping distribution-center :general "777"))
  (println "3 > General queue data")
  (pprint distribution-center)
  )

(simulates-a-day)
