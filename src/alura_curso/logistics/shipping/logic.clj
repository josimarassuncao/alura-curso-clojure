(ns alura-curso.logistics.shipping.logic)

(def ^:private queue-limit 5)

(defn- can-item-be-added?
  "Evaluates whether an item can be added to shipping queue"
  [main-queue department]
  (-> (department main-queue)
      (count)
      (< queue-limit)))

(defn add-shipping
  "Adds a product to the queue and then shipped"
  [main-queue department item]
  ;; FIXME: when department does not exist is being build with incorrect queue data structure
  (let [can-add (can-item-be-added? main-queue department)]
    (if can-add
      (update main-queue department conj item)
      (throw (ex-info "The queue is full" {:adding-item item})))))

(defn- remove-from-queue
  "Removes a product from the queue"
  [main-queue department]
  (update main-queue department pop))

(defn- get-next-item
  [main-queue q-from]
  (-> (q-from main-queue)
      peek))

(defn transfer
  "Applies the transference of the items between queues"
  [main-queue q-from q-to]
  ;; FIXME: when there is no item remaining in the queue should not move into the destiny
  (let [item (get-next-item main-queue q-from)]
    (-> main-queue
    (remove-from-queue q-from)
    (add-shipping q-to item)))
  )
