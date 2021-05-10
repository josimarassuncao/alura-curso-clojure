(ns alura-curso.products)

(def list-of-products {:backpack {:quantity 10, :price 59.99} ,
                       :shoes {:quantity 10, :price 159.99},
                       :belt {:quantity 10, :price 19.99},
                       :hat {:quantity 10 :price 109.99}
                       :shirt {:quantity 10, :price 129.99}})

(defn get-products-data []
  "returns the list of products and quantities to sell"
  list-of-products)

(defn get-only-prices []
  "returns the raw list of prices from the products"
  (->> (get-products-data)
       vals
       (map #(:price %))))

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

(defn calculate-item
  "multiplies quantity and price of an item"
  [[key value]]
  (println value)
  (* (:price value) (:quantity value)))

(defn inventory-balance []
  "calculates the value of the whole of the inventory"
  (->> (get-products-data)
      (map calculate-item)
      (reduce +)))

