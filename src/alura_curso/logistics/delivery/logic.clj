(ns alura-curso.logistics.delivery.logic
  (:use clojure.pprint))

(defn- build-some-id!
  []
  (long (Math/floor (* (Math/random) 1000000000))))

(defprotocol ShippableCalculator
  (calculate-freight [this])
  (delivery-estimate [this]))

(defrecord Shipment [order-id customer-id delivery-address total-value])
(defrecord InsuredShipment [order-id customer-id delivery-address total-value insurance])
(defrecord GuardedShipment [order-id customer-id delivery-address total-value guard-company])

(extend-type Shipment ShippableCalculator
  (calculate-freight [this]
    (println "calculating freight for id" (:order-id this))
    (Thread/sleep (rand 2000))
    10.0M)
  (delivery-estimate [this] "+5 days"))

(extend-type InsuredShipment ShippableCalculator
  (calculate-freight [this]
    (println "calculating freight for id" (:order-id this))
    (Thread/sleep (rand 2000))
    35.35M)
  (delivery-estimate [this] "+12 days"))

(extend-type GuardedShipment ShippableCalculator
  (calculate-freight [this]
    (println "calculating freight for id" (:order-id this))
    (Thread/sleep (rand 2000))
    144.99M)
  (delivery-estimate [this] "+24 days"))

(def simple-order1 (->Shipment "O-288682017" "C-503998700" "Rua X, 404" 600M))
(def simple-order2 (->Shipment "O-750160899" "C-503998700" "Rua X, 404" 125M))
(println "\nSimple shipment")
(pprint simple-order1)
(pprint simple-order2)

(def insured-order1
  (map->InsuredShipment {
                         :order-id         "O-877078286"
                         :customer-id      "C-131146820"
                         :delivery-address "Rua Y, 100"
                         :total-value      100M
                         :insurance        :company-alpha}))
(def insured-order2
  (map->InsuredShipment {
                         :order-id         "O-617046407"
                         :customer-id      "C-131146820"
                         :delivery-address "Rua Y, 100"
                         :total-value      900M
                         :insurance        :company-alpha}))
(println "\nInsured shipment")
(pprint insured-order1)
(pprint insured-order2)

(def guarded-order1 (GuardedShipment. "O-606354057" "C-685875098" "Rua Z, 13" 1500M :company-gama))
(def guarded-order2 (GuardedShipment. "O-129265452" "C-685875098" "Rua Z, 13" 50M :company-gama))
(println "\nGuarded shipment")
(pprint guarded-order1)
(pprint guarded-order2)

(let [s-freight1 (calculate-freight simple-order1)
      i-freight1 (calculate-freight insured-order1)
      g-freight1 (calculate-freight guarded-order1)
      s-freight2 (calculate-freight simple-order2)
      i-freight2 (calculate-freight insured-order2)
      g-freight2 (calculate-freight guarded-order2)]
  (println "\n>> Freight value for:")
  (println "simple" (:order-id simple-order1) s-freight1)
  (println "simple" (:order-id simple-order2) s-freight2)
  (println "insured" (:order-id insured-order1) i-freight1)
  (println "insured" (:order-id insured-order2) i-freight2)
  (println "guarded" (:order-id guarded-order1) g-freight1)
  (println "guarded" (:order-id guarded-order2) g-freight2)
  )

(let [s-estimate1 (delivery-estimate simple-order1)
      i-estimate1 (delivery-estimate insured-order1)
      g-estimate1 (delivery-estimate guarded-order1)
      s-estimate2 (delivery-estimate simple-order2)
      i-estimate2 (delivery-estimate insured-order2)
      g-estimate2 (delivery-estimate guarded-order2)]
  (println "\n>> Delivery estimate date for:")
  (println "simple" (:order-id simple-order1) s-estimate1)
  (println "simple" (:order-id simple-order2) s-estimate2)
  (println "insured" (:order-id insured-order1) i-estimate1)
  (println "insured" (:order-id insured-order2) i-estimate2)
  (println "guarded" (:order-id guarded-order1) g-estimate1)
  (println "guarded" (:order-id guarded-order2) g-estimate2)
  )

(defn- approval-from-manager
  [shipment]
  ;(Thread/sleep 5000)
  false)

(defn- approval-from-supervisor
  [shipment]
  ;(Thread/sleep 1000)
  true)

(defn- approval-from-whom?
  [shipment]
  (let [is-guarded? (contains? shipment :guard-company)
        is-insured? (contains? shipment :insurance)
        value (:total-value shipment)]
    (cond (true? is-guarded?) :manager-approval
          (and (true? is-insured?) (> value 500)) :supervisor-approval
          :else :no-approval))
  )

(defmulti shipment-approved? approval-from-whom?)
(defmethod shipment-approved? :manager-approval
  [order-shipment]
  (approval-from-manager order-shipment))

(defmethod shipment-approved? :supervisor-approval
  [order-shipment]
  (approval-from-supervisor order-shipment))

(defmethod shipment-approved? :no-approval
  [order-shipment]
  true)

(let [s-estimate1 (shipment-approved? simple-order1)
      i-estimate1 (shipment-approved? insured-order1)
      g-estimate1 (shipment-approved? guarded-order1)
      s-estimate2 (shipment-approved? simple-order2)
      i-estimate2 (shipment-approved? insured-order2)
      g-estimate2 (shipment-approved? guarded-order2)]
  (println "\nGetting approval for shipments")
  (println "simple" (:order-id simple-order1) s-estimate1)
  (println "simple" (:order-id simple-order2) s-estimate2)
  (println "insured" (:order-id insured-order1) i-estimate1)
  (println "insured" (:order-id insured-order2) i-estimate2)
  (println "guarded" (:order-id guarded-order1) g-estimate1)
  (println "guarded" (:order-id guarded-order2) g-estimate2)
  )
