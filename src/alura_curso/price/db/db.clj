(ns alura-curso.price.db.db  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/store")

(pprint (d/create-database db-uri))
(def conn (d/connect db-uri))

(def schema-v1 [{:db/ident :produto/nome
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc "O nome de um produto"}
                {:db/ident :produto/slug
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc "O caminho para acessar esse produto via http"}
                {:db/ident :produto/preco
                 :db/valueType :db.type/bigdec
                 :db/cardinality :db.cardinality/one
                 :db/doc "O preco de um produto com precisão monetária"} ])

(d/transact conn schema-v1)

