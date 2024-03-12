package com.mlyngvo

import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration


@AutoConfiguration
@ComponentScan
@EnableConfigurationProperties(JwtProperties::class)
class JwtAutoConfiguration {

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager

    @Configuration
    class CustomFlywayConfiguration : FlywayConfigurationCustomizer {

        override fun customize(configuration: FluentConfiguration?) {
            configuration
                ?.javaMigrations(*arrayOf(*configuration.javaMigrations, JwtRefreshTokenEntity.MIGRATION))

        }
    }
}