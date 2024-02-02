package com.jzel.spotifybackupper

import com.jzel.spotifybackupper.mailalert.EmailSenderService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.jzel.spotifybackupper.config")
class SpotifyBackupperApplication

fun main(args: Array<String>) {
    val context = runApplication<SpotifyBackupperApplication>(*args)

    context.getBean(EmailSenderService::class.java).sendStartupAlert()
}
