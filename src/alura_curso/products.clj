(ns alura-curso.products)

(def list-of-products {:backpack 10,
                       :shoes 10,
                       :belt 10
                       :shirt 10})

(defn get-products-data []
  "returns the list of products and quantities to sell"
  list-of-products)

(defn remove-backpack []
  "remove the key :backpack from the collection"
  (def list-of-products (dissoc list-of-products :backpack))
  list-of-products)

(defn add-backpack []
  "remove the key :backpack from the collection"
  (def list-of-products (assoc list-of-products :backpack 10))
  list-of-products)

(defn add-backpack-quantity
  "adds a quantity for the backpack"
  [quantity]
  (update list-of-products :backpack #(+ %1 quantity)))
