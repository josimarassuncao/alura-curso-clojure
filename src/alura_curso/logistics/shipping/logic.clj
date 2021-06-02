(ns alura-curso.logistics.shipping.logic)

(def ^:private queue-limit 5)

(defn can-item-be-added?
  "Evaluates whether an item can be added to shipping queue"
  [main-queue department]
  {
   :pre [(contains? main-queue department), (not (nil? (department main-queue)))]
   }
  (-> (department main-queue)
      (count)
      (< queue-limit)))

(defn add-shipping
  "Adds a product to the queue and then shipped"
  [main-queue department item]
  {
   :pre [(contains? main-queue department)]
   }
  (let [can-add? (can-item-be-added? main-queue department)]
    (if can-add?
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

(defn- same-qt-of-people
  [ret-queue orig-queue q-from q-to]
  (= (+ (q-from orig-queue) (q-to orig-queue))
     (+ (q-from ret-queue) (q-to ret-queue))))

(defn transfer
  "Applies the transference of the items between queues"
  [main-queue q-from q-to]
  {
   :pre  [(contains? main-queue q-from), (contains? main-queue q-to)]
   :post [(same-qt-of-people % main-queue q-from q-to)]
   }
  (let [item (get-next-item main-queue q-from)]
    (-> main-queue
        (remove-from-queue q-from)
        (add-shipping q-to item))))
