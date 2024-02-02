package com.jzel.spotifybackupper.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "spotify")
data class SpotifyConfig(
    val clientId: String,
    val clientSecret: String,
    val redirect: String
)
