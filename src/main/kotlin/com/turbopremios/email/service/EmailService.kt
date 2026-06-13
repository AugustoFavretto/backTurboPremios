package com.turbopremios.email.service

interface EmailService {

    fun sendPasswordResetEmail(
        email: String,
        token: String
    )
}