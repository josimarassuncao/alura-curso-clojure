(ns alura-curso.core
  (:use clojure.pprint)
  (:require [alura-curso.products :as p]))

(let [content (p/get-products-data)]
  (pprint content))
