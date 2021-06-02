(ns alura-curso.logistics.shipping.model-test
  (:require [clojure.test :refer :all]
            [alura-curso.logistics.shipping.model :refer :all]))

(deftest gets-new-distribution!-test
  (testing "new distribution gets the correct departments and empty"
    (let [new-q (new-distribution!)]
      ; :general queue and empty
      (is (not= nil (:general new-q)))
      (is (= 0 (count (:general new-q))))
      ; :sports queue and empty
      (is (not= nil (:sports new-q)))
      (is (= 0 (count (:sports new-q))))
      ; :clothes queue and empty
      (is (not= nil (:clothes new-q)))
      (is (= 0 (count (:clothes new-q))))
      ))

  (testing "new distribution gets specific queues id's"
    (let [new-q (new-distribution! [:general :sports])]
      ; :a queue and empty
      (is (not= nil (:general new-q)))
      (is (= 0 (count (:general new-q))))
      ; :sports queue and empty
      (is (not= nil (:sports new-q)))
      (is (= 0 (count (:sports new-q))))
      ; :clothes queue is nil
      (is (nil? (:clothes new-q)))
      )))
