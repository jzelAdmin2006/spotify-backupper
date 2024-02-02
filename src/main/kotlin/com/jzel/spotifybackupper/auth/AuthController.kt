package com.jzel.spotifybackupper.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/spotify")
class AuthController @Autowired constructor(
    private val spotifyAuthService: AuthService,
    private val auth: Auth
) {
    @GetMapping("/token")
    fun initiateAuthentication(): ResponseEntity<Void> {
        val spotifyAuthURI: URI = spotifyAuthService.getAuthenticationURI()
        return ResponseEntity.status(HttpStatus.FOUND).location(spotifyAuthURI).build()
    }

    @GetMapping("/callback")
    fun callback(@RequestParam code: String): ResponseEntity<ClassPathResource> {
        val tokens: Pair<String, String> = spotifyAuthService.getTokensFromCode(code)
        auth.accessToken = tokens.first
        auth.refreshToken = tokens.second
        return ResponseEntity.ok().body(ClassPathResource("static/login-success.html"))
    }
}