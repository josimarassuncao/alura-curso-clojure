(ns alura-curso.logistics.shipping.core
  (:require [alura-curso.logistics.shipping.model :as s-model]
            [alura-curso.logistics.shipping.logic :as s-logic])
  (:use [clojure pprint]))

(defn start-queue!
  []
  (def ^:private distribution-center (atom (s-model/new-distribution!))))

(println "Starts distribution center - shipping")
(start-queue!)
(pprint distribution-center)

(defn enqueue!
  [department item]
  (swap! distribution-center s-logic/add-shipping department item))

(defn transfer!
  [department-from department-to]
  (swap! distribution-center s-logic/transfer department-from department-to))

(def ^:private items-list-1 [[:general "111"] [:general "222"] [:general "333"] [:general "444"]])
(def ^:private transfers-1 [[:general :sports] [:general :sports] [:general :souvenirs] [:general :souvenirs]])

(def ^:private items-list-2 [[:general "555"] [:general "666"] [:general "777"] [:general "888"]])
(def ^:private transfers-2 [[:sports :clothes] [:general :electronics] [:general :electronics] [:general :electronics] [:general :clothes]])

(defn simulates-a-day
  []
  ;; "Adds data to general queue"
  (doseq [[queue item] items-list-1]
    (enqueue! queue item))

  (println "1 > General queue data")
  (pprint distribution-center)

  ;; Transfers items between queues
  (doseq [[d-from d-to] transfers-1]
    (transfer! d-from d-to))
  (println "2 > General queue data")
  (pprint @distribution-center)

  ;; "Adds data to general queue"
  (doseq [[queue item] items-list-2]
    (enqueue! queue item))
  (println "3 > General queue data")
  (pprint (deref distribution-center))

  ;; Transfers items between queues
  (doseq [[d-from d-to] transfers-2]
    (transfer! d-from d-to))
  (println "4 > General queue data")
  (pprint distribution-center)
  )

(simulates-a-day)
