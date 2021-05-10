(ns alura-curso.price-test
  (:require [clojure.test :refer :all]
            [alura-curso.price :refer :all]))

(deftest a-discount
  (testing "gets 10% discount on 123.00"
    (is (= 110.7 (discounted-price 123)))))

(deftest non-discount-value
  (testing "it returns false for below and equal to 100"
  (is (= false (apply-discount 99)))))

(deftest ok-discount-value
  (testing "it returns true for above 100"
    (is (= true (apply-discount 101)))))
