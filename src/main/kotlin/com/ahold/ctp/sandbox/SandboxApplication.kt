package com.ahold.ctp.sandbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication
@EnableJpaRepositories("com.ahold.ctp.sandbox.*")
@ComponentScan(basePackages = ["com.ahold.ctp.sandbox.*"])
@EntityScan("com.ahold.ctp.sandbox.*")
class SandboxApplication

fun main(args: Array<String>) {
    runApplication<SandboxApplication>(*args)
}
