(ns alura-curso.price)

(defn discounted-price
  "applies 10% of discount"
  [original_value]
  (let [percent_value (/ 9 10.0)]
    (* percent_value original_value)))

(defn apply-discount
  "Check whether should apply a discount"
  [price]
  (> price 100))

(defn get-prices []
  (let [list [35, 122, 780, 56, 157, 972]]
    list))

(map discounted-price (filter apply-discount (get-prices)))
