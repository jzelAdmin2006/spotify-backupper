package com.jzel.spotifybackupper.auth

import org.springframework.stereotype.Component

@Component
class Auth {
    var accessToken: String? = null
    var refreshToken: String? = null

    fun resetRefreshToken() {
        refreshToken = null
    }
}
