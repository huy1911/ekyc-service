package com.lpb.mid.ekyc.config.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

@Configuration
public class ProducerConfig {
    @Value("${kafka.reply.topic}")
    private String topic;

    @Bean(name = "replyingTemplate") //register and configure replying kafka template
    public ReplyingKafkaTemplate<String, Object, Object> replyingTemplate(
            ProducerFactory<String, Object> producerFactory,
            ConcurrentMessageListenerContainer<String, Object> repliesContainer) {
        ReplyingKafkaTemplate<String, Object, Object> replyTemplate = new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
        replyTemplate.setDefaultReplyTimeout(Duration.ofSeconds(50));
        replyTemplate.setSharedReplyTopic(true);
        return replyTemplate;
    }
    @Bean(name = "repliesContainer") //register ConcurrentMessageListenerContainer bean
    public ConcurrentMessageListenerContainer<String, Object> repliesContainer(ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory) {
        ConcurrentMessageListenerContainer<String, Object> repliesContainer = kafkaListenerContainerFactory.createContainer(topic);
        repliesContainer.getContainerProperties().setGroupId(UUID.randomUUID().toString()); // unique
        Properties props = new Properties();
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // so the new group doesn't get old replies
        repliesContainer.getContainerProperties().setKafkaConsumerProperties(props);
        return repliesContainer;
    }
}
