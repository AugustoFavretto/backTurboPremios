package com.turbopremios.email.service

import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender
) : EmailService {

    override fun sendPasswordResetEmail(
        email: String,
        token: String
    ) {

        val message = SimpleMailMessage()

        message.setTo(email)

        message.subject = "Recuperação de senha"

        message.text =
            """
            Olá!

            Clique no link abaixo para redefinir sua senha:

            https://turbopremios.com/redefinir-senha?token=$token

            Esse link expira em 1 hora.

            Se você não solicitou esta alteração,
            ignore este e-mail.
            """.trimIndent()

        mailSender.send(message)
    }
}