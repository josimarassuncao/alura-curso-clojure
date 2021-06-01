(defproject alura-curso "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [com.datomic/datomic-pro "1.0.6269"]
                 [org.clojure/data.json "0.2.6"]
                 [org.apache.kafka/kafka-clients "2.7.0"]
                 [prismatic/schema "1.1.12"]]
  :repl-options {:init-ns alura-curso.core})
