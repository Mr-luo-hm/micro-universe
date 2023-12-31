package com.again.micro.component.kafka;

import com.again.micro.component.kafka.builder.KafkaConsumerBuilder;
import com.again.micro.component.kafka.builder.KafkaProducerBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

/**
 * lyj 2020/7/28 21:17
 */
@Slf4j
@RequiredArgsConstructor
@EnableConfigurationProperties({ KafkaProperties.class })
public class KafkaAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(KafkaExtendProducer.class)
	public KafkaExtendProducer<String, String> stringKafkaExtendProducer(KafkaProperties properties) {
		KafkaProducerBuilder builder = new KafkaProducerBuilder()
				.addAllBootstrapServers(properties.getBootstrapServers()).putAll(properties.getExtend());
		builder.keySerializer(properties.getKeySerializerClassName());
		builder.valueSerializer(properties.getValueSerializerClassName());
		if (properties.getClientId() != null) {
			builder.clientId(properties.getClientId());
		}
		return builder.build();
	}

	@Bean
	@Scope("prototype")
	@ConditionalOnMissingBean(KafkaConsumerBuilder.class)
	public KafkaConsumerBuilder consumerBuilder(KafkaProperties properties) {
		return new KafkaConsumerBuilder().addAllBootstrapServers(properties.getBootstrapServers())
				.keyDeserializer(properties.getKeyDeserializerClassName())
				.valueDeserializer(properties.getValueDeserializerClassName()).groupId(properties.getGroupId());
	}

}
