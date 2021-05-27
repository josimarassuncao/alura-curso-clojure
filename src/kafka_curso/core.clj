(ns kafka-curso.core
  (:gen-class)
  (:use clojure.pprint)
  (:require
    [clojure.data.json :as json])
  (:import
    (java.util Properties UUID)
    (org.apache.kafka.clients.admin AdminClient NewTopic)
    (org.apache.kafka.clients.producer Callback KafkaProducer ProducerConfig ProducerRecord)
    (org.apache.kafka.clients.consumer ConsumerConfig KafkaConsumer)
    (org.apache.kafka.common.errors TopicExistsException)
    (java.time Duration)))

;; Producer code
(defn- build-properties []
  (doto (Properties.)
    (.putAll {ProducerConfig/BOOTSTRAP_SERVERS_CONFIG      "127.0.0.1:9092"
              ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG   "org.apache.kafka.common.serialization.StringSerializer"
              ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringSerializer"
              ProducerConfig/ACKS_CONFIG                   "all"})
    ))

(defn- ensure-topic-created! [topic partitions replication prop-conn]
  (let [ac (AdminClient/create prop-conn)]
    (try
      ;; This command not waiting for the resolution of the future seems wrong!
      ;; However, this sort of command is inside libraries internally so it is not a worry
      (.createTopics ac [(doto (NewTopic. topic partitions (short replication))
                           (.configs (Properties.)))])      ; version above 2.7 needs extra properties to properly create the topic
      ;; Ignore TopicExistsException, which would get thrown if the topic was previously created
      (catch TopicExistsException e nil)
      (finally
        (.close ac)))))

(defn producer! [topic]
  (let [props (build-properties)
        print-ex (comp println (partial str "Failed to deliver message: "))
        print-metadata #(printf "Produced record to topic %s partition [%d] @ offest %d\n"
                                (.topic %)
                                (.partition %)
                                (.offset %))
        create-msg #(let [k "alice"
                          v (json/write-str {:count %})]
                      (printf "Producing record: %s\t%s\n" k v)
                      (ProducerRecord. topic k v))
        partitions 3
        replication 3]
    ;; with-open is a function that closes a handle once the process terminated
    (with-open [producer (KafkaProducer. props)]
      (ensure-topic-created! topic partitions replication props)
      ;; We can use callbacks to handle the result of a send, like this:
      (let [callback (reify Callback
                       (onCompletion [this metadata exception]
                         (if exception
                           (print-ex exception)
                           (print-metadata metadata))))]
        (doseq [i (range 5)]
          (.send producer (create-msg i) callback))
        (.flush producer))
      ;; Or we could wait for the returned futures to resolve, like this:
      (let [futures (doall (map #(.send producer (create-msg %)) (range 5 10)))]
        (.flush producer)
        (while (not-every? future-done? futures)
          ;; What requires a 50ms wait? Does the JVM requires it?
          (Thread/sleep 50))
        (doseq [fut futures]
          (try
            (let [metadata (deref fut)]
              (print-metadata metadata))
            (catch Exception e
              (print-ex e)))))
      (printf "10 messages were produced to topic %s!\n" topic))))

;; Produces the messages
(def ^:private topic-name "USER-CREATED")
(producer! topic-name)

;; Consumer code
(defn- build-properties-consumer [group-id, max-poll]
  (doto (Properties.)
    (.putAll {ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG,       "127.0.0.1:9092"
              ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG   "org.apache.kafka.common.serialization.StringDeserializer"
              ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringDeserializer"
              ConsumerConfig/GROUP_ID_CONFIG                 group-id
              ConsumerConfig/CLIENT_ID_CONFIG                (str (UUID/randomUUID))
              ConsumerConfig/MAX_POLL_RECORDS_CONFIG         (str max-poll)
              ConsumerConfig/AUTO_OFFSET_RESET_CONFIG        "earliest"})
    ))

;(defn- prt-record
;  [record]
;  (println " record -> " record)
;  (let [value (.value record)]
;    (printf "Consumed record with key %s and value %s, and updated total count to %d\n"
;            (.key record)
;            value
;            n)))

(defn consumer! [topic]
  (with-open [consumer (KafkaConsumer. (build-properties-consumer "READ-USER-CREATED" 1))]
    (.subscribe consumer [topic])
    (loop [tc 0
           records []]
      (let [new-tc (reduce
                     (fn [tc record]
                       (let [value (.value record)
                             cnt (get (json/read-str value) "count")
                             new-tc (+ tc cnt)]
                         (printf "Consumed record with key %s and value %s, and updated total count to %d\n"
                                 (.key record)
                                 value
                                 new-tc)
                         new-tc))
                     tc
                     records)]
        (println "Waiting for message in KafkaConsumer.poll")
        (let [poll-records (seq (.poll consumer (Duration/ofSeconds 5)))]
          ;; limits polling for while still has records or 5 seconds,
          ;; in case needs it running remove the when condition
          (if poll-records
            (recur new-tc poll-records)
            (println "finishing topic" topic-name "reading!"))))
      )))

(println "\n\n\n\n\nGetting data with KafkaConsumer")
(consumer! topic-name)
