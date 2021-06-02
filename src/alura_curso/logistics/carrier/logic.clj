(ns alura-curso.logistics.carrier.logic
  (:require [schema.core :as s])
  (:use clojure.pprint))

;; turns the validation from this point ahead
(s/set-fn-validation! true)

;; predicates
(def StrictlyPositive (s/pred pos? 'strictly-positive))

(pprint (s/validate StrictlyPositive 1))
;(pprint (s/validate StrictlyPositive 0))
;(pprint (s/validate StrictlyPositive -1))

(s/defn rating-limit-ok? :- s/Bool
  "evaluates wheter a number is between 0 and 10"
  [n :- s/Num]
  (<= 0 n 10))

;(def RatingLimit (s/pred rating-limit-ok? 'ratings-limit))

(s/def ^:private CarrierSchema
  "Schema for a carrier"
  {:id s/Str
   :name s/Str
   :rating (s/constrained s/Num rating-limit-ok?)
   })

(defn- build-some-id!
  []
  (long (Math/floor (* (Math/random) 1000000000))))

(s/defn new-carrier :- CarrierSchema
  [id :- s/Str, name :- s/Str, rating :- s/Num]
  {:id id
   :name name
   :rating rating})

; Following lines throw errors due to -1 and 10.01 not being between 0 and 10
;(pprint (new-carrier "CA-307159425" "ACME Transports" -1))
;(pprint (new-carrier "CA-307159425" "ACME Transports" 10.01))
(pprint (new-carrier "CA-307159425" "ACME Transports" 8))

(s/defschema CarriersList {s/Str CarrierSchema})

(s/defn adds-carrier :- CarriersList
  [carriers-list :- CarriersList
   carrier :- CarrierSchema]
  (let [id (:id carrier)]
    (assoc carriers-list id carrier)
    ))

(s/defschema CarriersDestiny {s/Str [s/Str]})

(s/defn add-carrier-destiny :- CarriersDestiny
  [carriers-destinies :- CarriersDestiny
   carrier-id :- s/Str
   destinies :- [s/Str]]
  ;; TODO: is it necessary to ensure having the same data always?
  ;; How it is written the first time is a vector, after an update it is a seq
  (if (contains? carriers-destinies carrier-id)
    (update carriers-destinies carrier-id concat destinies)
    (assoc carriers-destinies carrier-id destinies))
  )

(s/defn get-destinies-for-carrier :- [s/Str]
  [carriers-destiny-list :- CarriersDestiny
   carrier-id :- s/Str]
  (get carriers-destiny-list carrier-id))

(defn test-carriers []
  (let [carrier-1 (new-carrier "CA-307159425" "ACME Transports" 8.1M)
        carrier-2 (new-carrier "CA-325240231" "Carrier Eased" 7.7)
        carrier-3 (new-carrier "CA-853891712" "We Carry All Stuff" 9.5)
        carriers (reduce adds-carrier {} [carrier-1 carrier-2 carrier-3])
        ;; Throws an error due to s/Str not being an CarrierSchema
        ;; carriers (reduce adds-carrier {} ["CA-853891712" carrier-2 carrier-3])

        destinies {}
        destinies (add-carrier-destiny destinies "CA-307159425" ["dest-1" "dest-2" "dest-3"])
        destinies (add-carrier-destiny destinies "CA-853891712" ["dest-10" "dest-20" "dest-30"])
        destinies (add-carrier-destiny destinies "CA-307159425" ["dest-5" "dest-6"])
        destinies (add-carrier-destiny destinies "CA-325240231" ["dest-15" "dest-16" "dest-13"])
        ;; Throws an exception given carrier-1 not being an s/Str
        ;; destinies (add-carrier-destiny destinies carrier-1 ["dest-15" "dest-16" "dest-13"])
        ]

      (println carriers)
      (println destinies)
      (println "destinies for carrier 1 > \"CA-307159425\"")
      (pprint (get-destinies-for-carrier destinies "CA-307159425"))
      (println "destinies for carrier 2 > \"CA-325240231\"")
      (pprint (get-destinies-for-carrier destinies "CA-325240231"))
      (println "destinies for carrier 3 > \"CA-853891712\"")
      (pprint (get-destinies-for-carrier destinies "CA-853891712"))
      ;; Throws an error because of the carrier-1 not being a s/Str
      ;;(pprint (get-destinies-for-carrier destinies carrier-1))
    ))

(test-carriers)

