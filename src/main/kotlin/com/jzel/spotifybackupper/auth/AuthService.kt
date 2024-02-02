package com.jzel.spotifybackupper.auth

import com.jzel.spotifybackupper.config.SpotifyConfig
import com.jzel.spotifybackupper.mailalert.EmailSenderService
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.util.*

@Service
class AuthService @Autowired constructor(
    private val auth: Auth,
    private val config: SpotifyConfig,
    private val emailSenderService: EmailSenderService
) {
    private val tokenEndpoint = "https://accounts.spotify.com/api/token"
    private val authUrl = "https://accounts.spotify.com/authorize"
    private val scopes = "user-library-read"

    @Scheduled(fixedRate = 3540 * 1000)
    fun hourlyRefresh() {
        auth.refreshToken?.let { refreshToken() }
    }

    private fun refreshToken() {
        val client = OkHttpClient()
        val credentials = Base64.getEncoder()
            .encodeToString((config.clientId + ":" + config.clientSecret).toByteArray())

        val body: FormBody = FormBody.Builder().add("grant_type", "refresh_token")
            .add("refresh_token", auth.refreshToken!!)
            .build()

        val request: Request = Request.Builder().url(tokenEndpoint)
            .post(body)
            .addHeader("Authorization", "Basic $credentials")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string())
                    val newAccessToken: String = jsonResponse.getString("access_token")
                    auth.accessToken = newAccessToken
                } else {
                    auth.resetRefreshToken()
                    emailSenderService.sendReauthAlert()
                    throw RuntimeException("Failed to refresh token: " + response.body?.string())
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    fun getAuthenticationURI(): URI {
        val url =
            ((authUrl + "?client_id=" + config.clientId) + "&response_type=code" + "&redirect_uri="
                    + config.redirect) + "&scope=" + scopes
        return URI.create(url)
    }

    fun getTokensFromCode(code: String): Pair<String, String> {
        val client = OkHttpClient()
        val credentials = Base64.getEncoder()
            .encodeToString((config.clientId + ":" + config.clientSecret).toByteArray())

        val body: FormBody = FormBody.Builder().add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", config.redirect)
            .build()

        val request: Request = Request.Builder().url(tokenEndpoint)
            .post(body)
            .addHeader("Authorization", "Basic $credentials")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val jsonResponse = JSONObject(response.body?.string())
                    val accessToken: String = jsonResponse.getString("access_token")
                    val refreshToken: String = jsonResponse.getString("refresh_token")
                    return Pair(accessToken, refreshToken)
                } else {
                    throw RuntimeException("Failed to fetch tokens: " + response.body?.string())
                }
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
