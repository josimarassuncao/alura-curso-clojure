(ns alura-curso.store.category.db.db
  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/store")

(pprint (d/create-database db-uri))
(def conn (d/connect db-uri))

(def schema-v3 [
                ; Produto
                {:db/ident       :produto/nome
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc         "O nome de um produto"}
                {:db/ident       :produto/slug
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc         "O caminho para acessar esse produto via http"}
                {:db/ident       :produto/preco
                 :db/valueType   :db.type/bigdec
                 :db/cardinality :db.cardinality/one
                 :db/doc         "O preco de um produto com precisão monetária"}
                {:db/ident       :produto/palavras-chave
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/many
                 :db/doc         "As palavras-chave de produto"}
                {:db/ident       :produto/id
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one
                 :db/unique      :db.unique/identity
                 :db/doc         "Id do produto dentro da aplicação"}
                {:db/ident       :produto/categoria
                 :db/valueType   :db.type/ref
                 :db/cardinality :db.cardinality/one
                 :db/doc         "A associação do produto com uma categoria"}

                ; Categoria
                {:db/ident       :categoria/id
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one
                 :db/unique      :db.unique/identity
                 :db/doc         "Id da categoria dentro da aplicação"}
                {:db/ident       :categoria/nome
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc         "O nome da categoria"}

                ; Seção
                {:db/ident       :secao/id
                 :db/valueType   :db.type/uuid
                 :db/cardinality :db.cardinality/one
                 :db/unique      :db.unique/identity
                 :db/doc         "Id da seção dentro da aplicação"}
                {:db/ident       :secao/nome
                 :db/valueType   :db.type/string
                 :db/cardinality :db.cardinality/one
                 :db/doc         "O nome da Seção"}
                {:db/ident       :secao/categoria
                 :db/valueType   :db.type/ref
                 :db/cardinality :db.cardinality/one
                 :db/doc         "A categoria associada com a seção"}

                ; Transações
                {:db/ident :tx-data/ip
                 :db/valueType :db.type/string
                 :db/cardinality :db.cardinality/one}
                ])

(d/transact conn schema-v3)

(defn uuid
  []
  (java.util.UUID/randomUUID))

;; Inclui os produtos
(defn novo-produto
  ([nome slug preco] (novo-produto (uuid) nome slug preco))
  ([uuid nome slug preco]
   {:produto/id    uuid
    :produto/nome  nome
    :produto/slug  slug
    :produto/preco preco}))

(def pc-novo (novo-produto (uuid) "Computador Novo", "/computador-novo", 1500.99M))
(def pc-veio (novo-produto "Computador Véio", "/computador-véio", 50.0M))
(def calculadora-fin (novo-produto "Calculadora Financeira", "/calculadora-financeira", 100.10M))
(def adesivos (novo-produto "Adesivos", "/adesivos", 5.10M))
(def chaveiros (novo-produto "Chaveiros", "/chaveiros", 5.10M))
(def chaveiro-calc {:produto/nome "Chaveiro Calculadora", :produto/slug "/chaveiro-calculadora"})
;; Cria um metadado para a transacao
(def tx-ip [:db/add "datomic.tx" :tx-data/ip "200.200.123.12"])

;(d/transact conn [pc-novo pc-veio calculadora-fin chaveiro-calc adesivos chaveiros])
;; agrupa o metadado da transacao com os produtos
(d/transact conn (conj [pc-novo pc-veio calculadora-fin chaveiro-calc adesivos chaveiros] tx-ip))

;;  Inclui as categorias
(defn nova-categoria
  ([nome] (nova-categoria (uuid) nome))
  ([uuid nome] {:categoria/id   uuid
                :categoria/nome nome
                }))

(def eletronicos (nova-categoria "Eletrônicos"))
(def souvenir (nova-categoria (uuid) "Souvenir"))
(d/transact conn [eletronicos souvenir])

;; Relaciona produtos e categorias
(defn prod_x_categ
  [prod categ]
  [:db/add [:produto/id (:produto/id prod)] :produto/categoria [:categoria/id (:categoria/id categ)]]
  )

(def pc-novo_x_eletronicos (prod_x_categ pc-novo eletronicos))
(def pc-veio_x_eletronicos (prod_x_categ pc-veio eletronicos))
(def calc-fin_x_eletronicos (prod_x_categ calculadora-fin eletronicos))
(def adesivo_x_souvenir (prod_x_categ adesivos souvenir))
(def chav_x_souvenir (prod_x_categ chaveiros souvenir))
;(def chav-calc_x_souvenir (prod_x_categ chaveiro-calc souvenir))  ; Não tem id e por isso dá problema

(d/transact conn [pc-novo_x_eletronicos,
                  pc-veio_x_eletronicos,
                  calc-fin_x_eletronicos,
                  adesivo_x_souvenir,
                  chav_x_souvenir])


(pprint (d/q '[:find (pull ?produto [*]), ?nome-categoria
               :keys produto categoria
               :where [?produto :produto/nome]
               [?produto :produto/categoria ?categoria]
               [?categoria :categoria/nome ?nome-categoria]
               ] (d/db conn)))


;; Forward navigation (nome do atributo diretamente)
(pprint (d/q '[:find (pull ?produto [* {:produto/categoria [*]}])
               :where [?produto :produto/nome]] (d/db conn)))

;; Backward navigation (nome do atributo precedido por _)
(pprint (d/q '[:find (pull ?entity [*, {:produto/_categoria [:produto/nome]}])
               :where [?entity :categoria/nome]] (d/db conn)))

;;  Possibilidade de incluir de forma aninhada entidades relacionadas
(def moletom {:produto/id        (uuid)
              :produto/nome      "Moletom"
              :produto/slug      "/moletom"
              :produto/preco     89.99M
              :produto/categoria {:categoria/id (uuid), :categoria/nome "Roupas"}})

(def roupas (-> moletom :produto/categoria))
@(d/transact conn [moletom])

;; Uma entidade que já existe não gera erro
(def casaco {:produto/id        (uuid)
             :produto/nome      "Casaco"
             :produto/slug      "/casaco"
             :produto/preco     289.99M
             :produto/categoria roupas})
@(d/transact conn [casaco])

;; Uma entidade que já existe não gera erro
(def legging {:produto/id        (uuid)
              :produto/nome      "Legging"
              :produto/slug      "/legging"
              :produto/preco     59.99M
              :produto/categoria [:categoria/id (:categoria/id roupas)]})
@(d/transact conn [legging])

;; Pesquisa os relacionamentos
(pprint (d/q '[:find (pull ?entity [*, {:produto/_categoria [:produto/nome]}])
               :where [?entity :categoria/nome]] (d/db conn)))

;; Agrupando com operações SUM, COUNT, MAX, MIN
(pprint (d/q '[:find (min ?preco) (max ?preco) (count ?preco) (sum ?preco)
               :with ?produto
               :keys minimo maximo qtde soma
               :where [?produto :produto/preco ?preco]
               ] (d/db conn)))

;; Atencao para ao usar agrupadores ter a clausula with diferença do resultado com e sem with
;[{:minimo 5.10M, :maximo 1500.99M, :qtde 7, :soma 2096.16M}]
;[{:minimo 5.10M, :maximo 1500.99M, :qtde 8, :soma 2101.26M}]

;; (pprint (d/q '[:find (min ?preco) (max ?preco) (count ?preco) (sum ?preco)
;               :keys minimo maximo qtde soma
;               :where [_ :produto/preco ?preco]
;               ] (d/db conn)))
;[{:minimo 5.10M, :maximo 1500.99M, :qtde 7, :soma 2096.16M}]
;=> nil
;(pprint (d/q '[:find (min ?preco) (max ?preco) (count ?preco) (sum ?preco)
;               :with ?produto
;               :keys minimo maximo qtde soma
;               :where [?produto :produto/preco ?preco]
;               ] (d/db conn)))
;[{:minimo 5.10M, :maximo 1500.99M, :qtde 8, :soma 2101.26M}]
;=> nil


;; Agrupando e nomeando o resultado
(pprint (d/q '[:find ?nome-categoria (min ?preco) (max ?preco) (count ?preco) (sum ?preco)
               :with ?produto
               :keys categoria minimo maximo qtde soma
               :where [?produto :produto/preco ?preco]
               [?produto :produto/categoria ?categoria]
               [?categoria :categoria/nome ?nome-categoria]
               ] (d/db conn)))

;; Query dentro de query
(defn get-produtos-maior-preco-v1
  [db]
  (let [maior-preco (ffirst (d/q '[:find (max ?preco)
                                   :where [_ :produto/preco ?preco]
                                   ] db))
        ]
    ;(pprint maior-preco)
    (d/q '[:find (pull ?produto [* {:produto/categoria [*]}])
                   :in $ ?preco
                   :where [?produto :produto/preco ?preco]
                   ] db maior-preco)
    ))

(pprint (get-produtos-maior-preco-v1 (d/db conn)))


(defn get-produtos-maior-preco-v2
  [db]
  (d/q '[:find (pull ?produto [* {:produto/categoria [*]}])
         :where [(q '[:find (max ?preco)
                     :where [_ :produto/preco ?preco]
                     ] $) [[ ?preco ]]]
         [?produto :produto/preco ?preco]
         ] db)
  )
(pprint (get-produtos-maior-preco-v2 (d/db conn)))


(defn get-produtos-menor-preco-v2
  [db]
  (d/q '[:find (pull ?produto [* {:produto/categoria [*]}])
         :where [(q '[:find (min ?preco)
                      :where [_ :produto/preco ?preco]
                      ] $) [[ ?preco ]]]
         [?produto :produto/preco ?preco]
         ] db)
  )
(pprint (get-produtos-menor-preco-v2 (d/db conn)))

;; Query usando metadado associado com a transacao
(defn produtos-adicionados-pelo-ip
  [db ip]
  (d/q '[:find (pull ?produto [*, {:produto/categoria [:categoria/nome]}])
         :in $ ?ip-procurado
         :where [?transacao :tx-data/ip ?ip-procurado]
                 [?produto :produto/nome _ ?transacao]
         ] db ip)
  )

(pprint (produtos-adicionados-pelo-ip (d/db conn) "200.200.123.12"))
(pprint (produtos-adicionados-pelo-ip (d/db conn) "200.200.123.13"))

;; Busca categoria por uuid
(pprint (d/q '[:find ?categoria
               :in $ ?id-categ
               :where [?categoria :categoria/id ?id-categ]]
             (d/db conn) (:categoria/id roupas)))

(def secao-vestes {:secao/id (uuid)
                   :secao/nome "Vestes"
                   :secao/categoria roupas
                   })
@(d/transact conn [secao-vestes])

@(d/transact conn [[:db/add [:produto/id (:produto/id legging)]
               :produto/categoria
               [:secao/id (:secao/id secao-vestes)]]
              ])

(pprint (d/q '[:find (pull ?produto [*, {:produto/categoria [*, {:secao/categoria [*]}]}])
               :in $ ?prod-id
               :where [?produto :produto/id ?prod-id]]
             (d/db conn) (:produto/id legging)))


;(pprint (d/delete-database db-uri))
