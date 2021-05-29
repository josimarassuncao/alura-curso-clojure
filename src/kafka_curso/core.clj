(ns kafka-curso.core
  (:gen-class)
  (:use clojure.pprint)
  (:require
    [clojure.data.json :as json]
    [clojure.edn :as edn])
  (:import
    (java.util Properties UUID)
    (org.apache.kafka.clients.admin AdminClient NewTopic)
    (org.apache.kafka.clients.producer Callback KafkaProducer ProducerConfig ProducerRecord)
    (org.apache.kafka.clients.consumer ConsumerConfig KafkaConsumer)
    (org.apache.kafka.common.errors TopicExistsException)
    (java.io ByteArrayOutputStream ByteArrayInputStream)
    (org.apache.kafka.common.serialization ByteArrayDeserializer ByteArraySerializer)
    (java.time Duration)))

;; Producer code
(defn- build-producer-properties []
  (doto (Properties.)
    (.putAll {ProducerConfig/BOOTSTRAP_SERVERS_CONFIG      "127.0.0.1:9092"
              ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG   "org.apache.kafka.common.serialization.StringSerializer"
              ;ProducerConfig/KEY_SERIALIZER_CLASS_CONFIG   (.getName ByteArraySerializer)
              ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringSerializer"
              ;ProducerConfig/VALUE_SERIALIZER_CLASS_CONFIG (.getName ByteArraySerializer)
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

(defn producer! [topic topic-message]
  (let [props (build-producer-properties)
        print-ex (comp println (partial str "Failed to deliver message: "))
        print-metadata #(printf "Produced record to topic %s partition [%d] @ offest %d\n"
                                (.topic %)
                                (.partition %)
                                (.offset %))
        ;create-msg #(create-user-message user-alice)
        partitions 3
        replication 3]
    ;; with-open is a function that closes a handle once the process terminated
    (with-open [producer (KafkaProducer. props)]
      (println "creating topic")
      (ensure-topic-created! topic partitions replication props)
      ;; 1> We can use callbacks to handle the result of a send, like this:
      ;(let [callback (reify Callback
      ;                 (onCompletion [this metadata exception]
      ;                   (if exception
      ;                     (print-ex exception)
      ;                     (do
      ;                       (print "callback-")
      ;                       (print-metadata metadata)))))]
      ;
      ;  (.send producer topic-message callback)
      ;  (.flush producer))

      ;; 2> Or we could wait for the returned futures to resolve, like this:
      (println "creating message")
      (let [future-message (.send producer topic-message)]
        (.flush producer)
        (do
          (try
            (let [metadata (deref future-message)]
              (print "futures-")
              (print-metadata metadata))
            (catch Exception e
              (print-ex e)))))
      (printf "1 message were produced to topic %s!\n" topic))))

;; Produces the messages
(def ^:private user-topic-name "USER-CREATED")

(defn- serialize-message
  [user-msg]
  (pr-str user-msg)
  ;(with-open [b-out (ByteArrayOutputStream.)]
  ;  (.write b-out (.getBytes (pr-str user-msg)))
  ;  (let [content (.toByteArray b-out)]
  ;    content))
  )

(defn- create-user-message
  [user-msg]
  (let [msg-key (:user-id user-msg)
        ;msg-value (json/write-str user-msg)
        msg-value (serialize-message user-msg)
        topic "USER-CREATED"]
    (ProducerRecord. topic msg-key msg-value)))

(defn- build-user-id!
  []
  (long (Math/floor (* (Math/random) 1000000000000000000))))

;(def user-alice {:user-id  #uuid"da1eec90-a0fc-480c-be55-51dcaf7e69d8"
;                 :name     "Alice"
;                 })

(def user-alice {:user-id 770198102934854528
                 :name    "Alice"
                 })

(producer! user-topic-name (create-user-message user-alice))

;; Consumer code
(defn- build-consumer-properties
  [group-id, max-poll]
  (doto (Properties.)
    (.putAll {ConsumerConfig/BOOTSTRAP_SERVERS_CONFIG,       "127.0.0.1:9092"
              ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG   "org.apache.kafka.common.serialization.StringDeserializer"
              ;ConsumerConfig/KEY_DESERIALIZER_CLASS_CONFIG   (.getName ByteArrayDeserializer)
              ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG "org.apache.kafka.common.serialization.StringDeserializer"
              ;ConsumerConfig/VALUE_DESERIALIZER_CLASS_CONFIG (.getName ByteArrayDeserializer)
              ConsumerConfig/GROUP_ID_CONFIG                 group-id
              ConsumerConfig/CLIENT_ID_CONFIG                (str (UUID/randomUUID))
              ConsumerConfig/MAX_POLL_RECORDS_CONFIG         (str max-poll)
              ConsumerConfig/AUTO_OFFSET_RESET_CONFIG        "earliest"})
    ))

(defn- prt-record
  [record]
  (println (str record))
  (let [record-value (.value record)
        record-key (.key record)]
    (printf "Consumed record with key: %s \n" record-key)
    (printf "Consumed record with value %s\n" record-value)))

(defn consumer! [topic]
  (with-open [consumer (KafkaConsumer. (build-consumer-properties "READ-USER-CREATED" 1))]
    (.subscribe consumer [topic])
    (loop []
      ;(println "Waiting for message in KafkaConsumer.poll")
      (let [duration (Duration/ofMillis 100)
            poll-records (seq (.poll consumer duration))]
        ;; limits polling for while still has records,
        ;; in case needs it running remove the when condition
        (if poll-records
          (do
            (prt-record (first poll-records))
            (recur))
          (println "finishing topic" user-topic-name "reading!")))
      )))

(println "\n\n\n\n\nGetting data with KafkaConsumer")
(consumer! user-topic-name)
