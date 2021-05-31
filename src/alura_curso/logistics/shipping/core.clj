(ns alura-curso.logistics.shipping.core
  (:require [alura-curso.logistics.shipping.model :as s-model]
            [alura-curso.logistics.shipping.logic :as s-logic])
  (:use [clojure pprint]))

(def ^:private distribution-center (atom (s-model/new-distribution!)))
(println "Starts distribution center - shipping")
(pprint distribution-center)

(defn simulates-a-day
  []
  ;; "Adds data to general queue"
  (swap! distribution-center s-logic/add-shipping :general "111")
  (swap! distribution-center s-logic/add-shipping :general "222")
  (swap! distribution-center s-logic/add-shipping :general "333")
  (swap! distribution-center s-logic/add-shipping :general "444")
  (swap! distribution-center s-logic/add-shipping :sports "sports-1")
  (swap! distribution-center s-logic/add-shipping :electronics "elec-1")
  (swap! distribution-center s-logic/add-shipping :clothes "clot-1")
  (swap! distribution-center s-logic/add-shipping :any "any-1")
  (swap! distribution-center s-logic/add-shipping :any "any-2")
  (println "1 > General queue data")
  (pprint distribution-center)
  ;; Removes data from general queue
  (swap! distribution-center s-logic/shipping-prepared! :general)
  (swap! distribution-center s-logic/shipping-prepared! :sports)
  (swap! distribution-center s-logic/shipping-prepared! :electronics)
  (swap! distribution-center s-logic/shipping-prepared! :any)
  (swap! distribution-center s-logic/shipping-prepared! :clothes)
  (println "2 > General queue data")
  (pprint distribution-center)
  ;; Limits the size of the queues to 5 items
  (swap! distribution-center s-logic/add-shipping :general "555")
  (swap! distribution-center s-logic/add-shipping :general "666")
  ;(swap! distribution-center s-logic/add-shipping :general "777")
  ;(swap! distribution-center s-logic/add-shipping :general "888")
  (println "3 > General queue data")
  (pprint distribution-center)
  )

(simulates-a-day)
