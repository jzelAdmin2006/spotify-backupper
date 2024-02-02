package com.jzel.spotifybackupper.mailalert

import com.jzel.spotifybackupper.config.MailConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailSenderService @Autowired constructor(
    private val mailSender: JavaMailSender,
    private val mailConfig: MailConfig
) {
    fun sendStartupAlert() {
        send(
            "\uD83D\uDC31 Spotify Backupper gestartet (Erstanmeldung erforderlich)!",
            """
            Der Spotify Backupper wurde gestartet.
            
            Bitte unter folgendem Link anmelden:
            
            
            
            """.trimIndent() + mailConfig.reauthUrl
        )
    }

    fun sendReauthAlert() {
        send(
            "\uD83D\uDE40 Spotify Backupper Neuanmeldung erforderlich!",
            """
            Die Erneuerung des Spotify Access Tokens ist fehlgeschlagen.
            Die Spotify Backupper App benötigt eine erneute Anmeldung bei Spotify.
            
            Bitte unter folgendem Link anmelden:
            
            
            
            """.trimIndent() + mailConfig.reauthUrl
        )
    }

    private fun send(subject: String, text: String) {
        val msg = SimpleMailMessage()
        msg.from = mailConfig.username
        msg.setTo(mailConfig.to)
        msg.text = text
        msg.subject = subject

        mailSender.send(msg)
    }
}
