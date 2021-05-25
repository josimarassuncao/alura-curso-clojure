# Introduction to alura-curso



## Registro do curso com Kafka comandos úteis.

inicia o zookeeper
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

inicia o kafka

```bash
bin/kafka-server-start.sh config/server.properties`
```

cria o tópico LOJA_NOVO_PEDIDO

```bash
bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic LOJA_NOVO_PEDIDO`
```

lista os tópicos

```bash
bin/kafka-topics.sh --list --bootstrap-server localhost:9092`
```

produz mensagens no tópico LOJA_NOVO_PEDIDO

```bash
bin/kafka-console-producer.sh --broker-list localhost:9092 --topic LOJA_NOVO_PEDIDO`
```

consome as mensagens no tópico LOJA_NOVO_PEDIDO
:: conforme novas mensagens forem adicionadas

```bash
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO`
```

:: a partir do começo das mensagens disponíveis no tópico

```bash
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO --from-beginning`
```

lista e descreve as configurações dos tópicos

```bash
bin/kafka-topics.sh --describe --bootstrap-server localhost:9092`
```

lista e descreve os grupos de consumo dos tópicos

```bash
bin/kafka-consumer-groups.sh --all-groups --bootstrap-server localhost:9092 --describe`
```

altera as propriedades de um tópico
:: troca a quantidade de partições para 3 do tópico LOJA_NOVO_PEDIDO

```bash
bin/kafka-topics --alter --zookeeper localhost:2181 --topic LOJA_NOVO_PEDIDO --partitions 3`
```
