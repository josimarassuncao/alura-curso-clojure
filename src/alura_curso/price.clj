(ns alura-curso.price)

(defn discounted-price
  "applies 10% of discount"
  [original_value]
  (let [percent_value (/ 9 10.0)]
    (* percent_value original_value)))

(defn apply-discount?
  "Check whether should apply a discount"
  [price]
  (> price 100))

(defn get-prices []
  (let [list [35, 122, 780, 56, 157, 972]]
    list))
; Collections are indexed from 0
; (list 0) // gets first element
; (get list 0) // gets first element

; Using the function get avoids getting errors of non-existent indexes, it returns the value nil
; It is also possible to provide a default value in case of an non-existent index
; (get list 17 -1) // -1 is the default value

; (update collection index function)
; (update prices 0 inc)
; (update prices 1 dec)
; (get (update (get-prices) 0 inc) 0)

(map discounted-price (filter apply-discount? (get-prices)))

(reduce + (filter apply-discount? (get-prices)))

(defn my-add [val1, val2] (+ val1 val2))

; Throws an error due to the number of arguments for the function my-add
; However when using the function + the error is not thrown
; (reduce my-add [])
(reduce + [])
