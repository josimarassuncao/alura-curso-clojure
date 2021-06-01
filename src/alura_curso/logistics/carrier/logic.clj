(ns alura-curso.logistics.carrier.logic
  (:use clojure.pprint))

(defn- build-some-id!
  []
  (long (Math/floor (* (Math/random) 1000000000))))

(defn- new-carrier
  [id name]
  {:id id
   :name name})

(defn- adds-carrier
  [carriers-list carrier]
  (if-let [id (:id carrier)]
    (assoc carriers-list id carrier)
    (throw (ex-info "Carrier with no :id provided" {:carrier carrier}))
    ))

(defn- add-carrier-destiny
  [carriers-list carrier-id destinies]
  ;; TODO: is it necessary to ensure having the same data always?
  ;; How it is written the first time is a vector, after an update it is a seq
  (if (contains? carriers-list carrier-id)
    (update carriers-list carrier-id concat destinies)
    (assoc carriers-list carrier-id destinies))
  )

(defn- get-destinies-for-carrier
  [carriers-list carrier-id]
  (get carriers-list carrier-id))

(defn test-carriers []
  (let [carrier-1 (new-carrier "CA-307159425" "ACME Transports")
        carrier-2 (new-carrier "CA-325240231" "Carrier Eased")
        carrier-3 (new-carrier "CA-853891712" "We Carry All Stuff")
        carriers (reduce adds-carrier {} [carrier-1 carrier-2 carrier-3])

        destinies {}
        destinies (add-carrier-destiny destinies "CA-307159425" ["dest-1" "dest-2" "dest-3"])
        destinies (add-carrier-destiny destinies "CA-853891712" ["dest-10" "dest-20" "dest-30"])
        destinies (add-carrier-destiny destinies "CA-307159425" ["dest-5" "dest-6"])
        destinies (add-carrier-destiny destinies "CA-325240231" ["dest-15" "dest-16" "dest-13"])
        ]

      (println carriers)
      (println destinies)
      (println "destinies for carrier 1 > \"CA-307159425\"")
      (pprint (get-destinies-for-carrier destinies "CA-307159425"))
      (println "destinies for carrier 2 > \"CA-325240231\"")
      (pprint (get-destinies-for-carrier destinies "CA-325240231"))
      (println "destinies for carrier 3 > \"CA-853891712\"")
      (pprint (get-destinies-for-carrier destinies "CA-853891712"))
    ))

(test-carriers)
