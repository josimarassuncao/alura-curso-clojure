(ns alura-curso.logistics.shipping.core-test
  (:require [clojure.test :refer :all]
            [alura-curso.logistics.shipping.core :refer :all]))

(deftest enqueue!-testing
  (testing "Can add an item for an empty queue"
    (start-queue!)
    (let [new-queue (enqueue! :general "item-1")]
      (is (= 1 (count (:general new-queue)))))))

(deftest transfer!-testing
  (testing "Can transfer an item from a queue to another"
    (start-queue!)
    (enqueue! :general "item-1")
    (let [after-transfer (transfer! :general :sports)]
      (is (= 1 (count (:sports after-transfer)))))))
