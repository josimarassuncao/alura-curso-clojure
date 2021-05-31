(ns alura-curso.logistics.shipping.logic)


(defn add-shipping
  "Adds a product to the queue and then shipped"
  [main-queue department item]
  ;; FIXME: when department does not exist is being build with incorrect queue data structure
  (let [queue (department main-queue)
        current-size (count queue)
        fits-more? (< current-size 5)]
    (if fits-more?
      (update main-queue department conj item)
      main-queue)))



(defn shipping-prepared!
  "Removes a product from the queue"
  [main-queue department]
  (update main-queue department pop))
