(ns alura-curso.products.db.db
  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/store")

(pprint (d/create-database db-uri))

(def conn (d/connect db-uri))
(pprint conn)


(defn novo-produto [nome slug preco]
  {:produto/nome nome
   :produto/slug slug
   :produto/preco preco})

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

(def pc-novo (novo-produto "Computador Novo", "/computador-novo", 100.10M))
(def pc-veio (novo-produto "Computador Véio", "/computador-véio", 0.10M))
(def calculadora-fin (novo-produto "Calculadora Financeira", "/calculadora-financeira", 1.10M))
(def chaveiro-calc {:produto/nome "Chaveiro Calculadora", :produto/slug "/chaveiro-calculadora"})

(d/transact conn [pc-novo pc-veio calculadora-fin chaveiro-calc])

;; Exemplo de select * from - simples e que recupera todos os atributos
(pprint (d/q '[:find (pull ?entity [*])
               :where [?entity :produto/slug]] (d/db conn)))

;; Exemplo de select fields from
(pprint (d/q '[:find (pull ?entity [:db/id :produto/nome :produto/slug])
               :where [?entity :produto/slug]] (d/db conn)))

;; Query com limitação para só um atributo e bind - ATENCAO
;; Do jeito que está retorna só 1 item da lista
(pprint (d/q '[:find [?entity, ?slug-product]
               :where [?entity :produto/slug ?slug-product]] (d/db conn)))
;; retorna todos os elementos
(pprint (d/q '[:find ?entity, ?slug-product
               :where [?entity :produto/slug ?slug-product]] (d/db conn)))

;; Query com exemplo de execuções de outras condições
(pprint (d/q '[:find [?entity, ?nome-product, ?slug-product]
               :where [?entity :produto/slug ?slug-product]
               [?entity :produto/nome ?nome-product]] (d/db conn)))

(pprint (d/q '[:find ?entity, ?nome-product, ?slug-product
               :where [?entity :produto/slug ?slug-product]
               [?entity :produto/nome ?nome-product]] (d/db conn)))

;; Query passando parâmetros para restrições
(defn get-computador-veio
  [db slug]
  (d/q '[:find ?entity, ?nome-product, ?slug-searched
         :in $ ?slug-searched
         :where [?entity :produto/slug ?slug-searched]
         [?entity :produto/nome ?nome-product]] db slug))

(pprint (get-computador-veio (d/db conn) "/computador-véio"))

;; Query aplicando alias aos conteúdos retornados
(pprint (d/q '[:find ?entity, ?nome-product, ?slug-searched
               :keys produto/id, produto/nome, produto/slug
               :where [?entity :produto/slug ?slug-searched]
               [?entity :produto/nome ?nome-product]] (d/db conn)))


;; Aplica schema com opção Many
(def schema-v2 [{:db/ident :produto/nome
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
                 :db/doc "O preco de um produto com precisão monetária"}
                {:db/ident :produto/palavras-chave
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/many
                 :db/doc "As palavras-chave de produto"} ])

(d/transact conn schema-v2)

(pprint (d/transact conn [[:db/add 17592186045418 :produto/palavras-chave "Notebook"]
                          [:db/add 17592186045419 :produto/palavras-chave "Desktop"]
                          ]))

(pprint (d/transact conn [[:db/add 17592186045420 :produto/palavras-chave "Calculadora"]
                          [:db/add 17592186045421 :produto/palavras-chave "Calculadora"]
                          ]))

;; Exemplo de select * from - simples e que recupera todos os atributos
(pprint (d/q '[:find (pull ?entity [*])
               :where [?entity :produto/slug]] (d/db conn)))

;; O plano de execução precisa ser montado manualmente considerando a sequência de restrições
(pprint (d/q '[:find ?entity, ?preco, ?nome
               :where [?entity :produto/preco ?preco]
               [(> ?preco 50)]
               [?entity :produto/nome ?nome]] (d/db conn)))

;; Os relacionamentos 1-N continuam com comparação de igualdade simples nas restrições
(pprint (d/q '[:find (pull ?entity [*])
               :in $ ?in-palavra-chave
               :where [?entity :produto/palavras-chave ?in-palavra-chave]
               ] (d/db conn) "Desktop"))

(pprint (d/q '[:find (pull ?entity [*])
               :in $ ?in-palavra-chave
               :where [?entity :produto/palavras-chave ?in-palavra-chave]
               ] (d/db conn) "Calculadora"))


(pprint (d/transact conn [[:db/retract 17592186045420 :produto/palavras-chave "Calculadora"]
                          ;[:db/add 17592186045421 :produto/palavras-chave "Calculadora"]
                          ]))

(pprint (d/q '[:find (pull ?entity [*])
               :in $ ?in-palavra-chave
               :where [?entity :produto/palavras-chave ?in-palavra-chave]
               ] (d/db conn) "Calculadora"))

(pprint (d/q '[:find (pull ?entity [*])
               :in $ ?in-palavra-chave
               :where [?entity :produto/palavras-chave ?in-palavra-chave]
               ] (d/as-of (d/db conn) #inst "2021-05-18T17:11:26.000-00:00") "Calculadora"))

(pprint (d/delete-database db-uri))
