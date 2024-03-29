package com.jzel.spotifybackupper.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spring.mail")
data class MailConfig(
    val username: String,
    val to: String,
    val reauthUrl: String
)
