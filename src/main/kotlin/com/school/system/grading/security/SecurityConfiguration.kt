package com.school.system.grading.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebSecurity
class SecurityConfiguration: WebSecurityConfigurerAdapter() {
    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()

    //Enable all methods and origins for development and testing purpose
    @Bean
    fun corsConfigure(): WebMvcConfigurer? {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("*")
            }
        }
    }

    // for now, every request is permitted and crsf disabled for development/testing purpose
    override fun configure(http: HttpSecurity?) {
        http?.authorizeRequests()?.antMatchers("/**")?.permitAll()
        http?.csrf()?.disable()

    }
}