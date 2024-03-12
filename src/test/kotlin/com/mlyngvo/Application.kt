package com.mlyngvo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

@SpringBootApplication
class Application {

    @Configuration
    class CustomUserService : UserDetailsService {

        override fun loadUserByUsername(username: String?): UserDetails {
            TODO("Not yet implemented")
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}