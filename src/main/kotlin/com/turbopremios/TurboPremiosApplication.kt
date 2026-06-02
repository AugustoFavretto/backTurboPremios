package com.turbopremios

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TurboPremiosApplication

fun main(args: Array<String>) {
    runApplication<TurboPremiosApplication>(*args)
}
