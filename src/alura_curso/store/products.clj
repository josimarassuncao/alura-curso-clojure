(ns alura-curso.store.products)

(def ^:private list-of-products {:backpack {:quantity 10, :price 59.99} ,
                       :shoes {:quantity 10, :price 159.99},
                       :belt {:quantity 10, :price 19.99},
                       :hat {:quantity 10 :price 109.99},
                       :socks {:quantity 10, :price 9.99},
                       :shirt {:quantity 10, :price 129.99},
                       :stickers {:quantity 100},
                       :stamps {:quantity 100, :price 0.00}})

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
  [value]
  ;(println value)
  (* (:price value) (:quantity value)))

(defn inventory-balance []
  "calculates the value of the whole of the inventory"
  (->> (get-products-data)
       vals
      (map calculate-item)
      (reduce +)))

(defn- for-free?
  "check if the item has price 0"
  [data]
  ;(println data)
  (<= (get data :price 0) 0))

(defn get-free-stuff []
  "returns the free products in the list"
  (->> (get-products-data)
       (filter #(for-free? (second %)))))

(def ^:private paid? (comp not for-free?))

(defn get-paid-stuff []
  "returns the paid items from the list"
  (->> (get-products-data)
       (filter #(paid? (second %)))))

