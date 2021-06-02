(ns alura-curso.logistics.shipping.logic
  (:require [clojure.test :refer :all]
            [alura-curso.logistics.shipping.logic :refer :all]
            [schema.core :as s]))

;; turns the validation from this point ahead
(s/set-fn-validation! true)

(deftest can-item-be-added?-test
  (testing "can-item-be-added? doesn't allow non existent"
    (let [base-queue {:general [] :sports []}]
      ;(is (can-item-be-added? base-queue :non-existent))))
      (is (thrown? java.lang.AssertionError (can-item-be-added? base-queue :non-existent)))))

  (testing "can-item-be-added? using nil item"
    (let [base-queue {:general nil :sports []}]
      (is (thrown? java.lang.AssertionError (can-item-be-added? base-queue :general)))))

  (testing "can-item-be-added? using nil list"
    (let [base-queue nil]
      (is (thrown? java.lang.AssertionError (can-item-be-added? base-queue :general)))))
  )
