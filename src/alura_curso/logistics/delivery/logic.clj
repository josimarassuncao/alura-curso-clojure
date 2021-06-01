(ns alura-curso.logistics.delivery.logic
  (:use clojure.pprint))

(defn- build-some-id!
  []
  (long (Math/floor (* (Math/random) 1000000000))))

(defprotocol ShippableCalculator
  (calculate-freight [this])
  (delivery-estimate [this]))

(defrecord Shipment [order-id customer-id delivery-address])
(defrecord InsuredShipment [order-id customer-id delivery-address insurance])
(defrecord GuardedShipment [order-id customer-id delivery-address guard-company])

(extend-type Shipment ShippableCalculator
  (calculate-freight [this] 10.0M)
  (delivery-estimate [this] "+5 days"))

(extend-type InsuredShipment ShippableCalculator
  (calculate-freight [this] 35.35M)
  (delivery-estimate [this] "+12 days"))

(extend-type GuardedShipment ShippableCalculator
  (calculate-freight [this] 144.99M)
  (delivery-estimate [this] "+24 days"))

(def simple-order (->Shipment "S-288682017" "C-503998700" "Rua X, 404"))
(println "\nSimple shipment")
(pprint simple-order)

(def insured-order
  (map->InsuredShipment {
                          :order-id "S-877078286"
                          :customer-id "C-131146820"
                          :delivery-address "Rua Y, 100"
                          :insurance :company-alpha}))
(println "\nInsured shipment")
(pprint insured-order)

(def guarded-order (GuardedShipment. "S-606354057" "C-685875098" "Rua Z, 13" :company-gama))
(println "\nGuarded shipment")
(pprint guarded-order)

(let [s-freight (calculate-freight simple-order)
      i-freight (calculate-freight insured-order)
      g-freight (calculate-freight guarded-order)]
  (println "\n>> Freight value for:")
  (println "simple" s-freight)
  (println "insured" i-freight)
  (println "guarded" g-freight)
  )

(let [s-estimate (delivery-estimate simple-order)
      i-estimate (delivery-estimate insured-order)
      g-estimate (delivery-estimate guarded-order)]
  (println "\n>> Delivery estimate date for:")
  (println "simple" s-estimate)
  (println "insured" i-estimate)
  (println "guarded" g-estimate)
  )
