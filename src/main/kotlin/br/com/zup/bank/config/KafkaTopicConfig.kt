package br.com.zup.bank.config

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin
import java.util.*

@Configuration
class KafkaTopicConfig {
    @Value("\${kafka.bootstrapAddress}")
    private lateinit var bootstrap: String

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrap

        return KafkaAdmin(configs)
    }

    @Bean
    fun topic(): NewTopic {
        return NewTopic("bank_api", 1, 1)
    }
}