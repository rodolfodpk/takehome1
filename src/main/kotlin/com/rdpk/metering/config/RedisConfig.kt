package com.rdpk.metering.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.api.RedissonReactiveClient
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy

@Configuration
class RedisConfig {

    @Value("\${spring.data.redis.host:localhost}")
    private lateinit var redisHost: String

    @Value("\${spring.data.redis.port:6379}")
    private var redisPort: Int = 6379

    @Value("\${spring.data.redis.password:}")
    private var redisPassword: String? = null

    @Value("\${spring.redis.pool.max-size:32}")
    private var connectionPoolSize: Int = 32

    @Value("\${spring.redis.pool.min-idle-size:8}")
    private var connectionMinimumIdleSize: Int = 8

    @Bean
    @Lazy
    fun redissonClient(): RedissonClient {
        val config = Config()
        val address = "redis://$redisHost:$redisPort"
        config.useSingleServer()
            .setAddress(address)
            .apply {
                if (redisPassword != null && redisPassword!!.isNotBlank()) {
                    setPassword(redisPassword)
                }
            }
            .setConnectionPoolSize(connectionPoolSize)
            .setConnectionMinimumIdleSize(connectionMinimumIdleSize)
            .setConnectTimeout(15000) // Increased for Testcontainers
            .setTimeout(15000) // Increased for Testcontainers
            .setRetryAttempts(10) // More retries for Testcontainers
            .setRetryInterval(1000) // Shorter interval for faster retries
            .setKeepAlive(true)
            .setTcpNoDelay(true)
            .setPingConnectionInterval(30000) // Ping to keep connection alive
            .setDnsMonitoringInterval(5000) // DNS monitoring
        
        return Redisson.create(config)
    }

    @Bean
    @Lazy
    fun redissonReactiveClient(redissonClient: RedissonClient): RedissonReactiveClient {
        return redissonClient.reactive()
    }
}

