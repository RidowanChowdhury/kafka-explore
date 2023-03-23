package com.example.demo.configs;


import com.example.demo.controllers.Producer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
@EnableKafkaStreams
public class KafkaStreamsConfig {

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kStreamsConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "john");
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "myGroup");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29094");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        return new KafkaStreamsConfiguration(props);
    }

    @Bean
    public StreamsBuilderFactoryBeanConfigurer configurer() {
        return fb -> fb.setStateListener((newState, oldState) -> {
            System.out.println("State transition from " + oldState + " to " + newState);
        });
    }


    @Bean
    public KTable<String, String> kStream(StreamsBuilder builder) {
        // Serializers/deserializers (serde) for String and Long types
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Long> longSerde = Serdes.Long();

// Construct a `KStream` from the input topic "streams-plaintext-input", where message values
// represent lines of text (for the sake of this example, we ignore whatever may be stored
// in the message keys).
        KStream<String, String> textLines = builder.stream(Producer.topic3, Consumed.with(stringSerde, stringSerde));

        KTable<String, String> wordCounts = textLines

                // Split each text line, by whitespace, into words.  The text lines are the message
                // values, i.e. we can ignore whatever data is in the message keys and thus invoke
                // `flatMapValues` instead of the more generic `flatMap`.
                .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
                // We use `groupBy` to ensure the words are available as message keys

                .groupBy((key, value) -> value)

                // Count the occurrences of each word (message key).
                .count().mapValues((s, aLong) -> aLong.toString())
                ;

// Convert the `KTable<String, Long>` into a `KStream<String, Long>` and write to the output topic.
        wordCounts.toStream().to(Producer.topic4, Produced.with(stringSerde, stringSerde));
        return wordCounts;
    }
//    @Bean
//    public KStream<String, String> kStreamUppercase(StreamsBuilder kStreamBuilder) {
//
//        KStream<String, String> stream = kStreamBuilder.stream(Producer.topic3);
//        stream
//                .mapValues((ValueMapper<String, String>) String::toUpperCase)
//                .groupByKey()
//                .windowedBy(TimeWindows.of(Duration.ofMillis(20000)))
//                .reduce((String value1, String value2) -> value1 + value2,
//                        Named.as("windowStore"))
//                .toStream()
//                .map((windowedId, value) -> new KeyValue<>(windowedId.key(), value))
//                .to(Producer.topic4);
//
//        stream.print(Printed.toSysOut());
//
//        return stream;
//    }

}
