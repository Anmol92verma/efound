package dev.baseio.efoundation.data

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.logging.*
import io.ktor.client.features.observer.*
import io.ktor.client.request.*
import io.ktor.http.*

object NetworkClient {
  private const val TIME_OUT = 60_000

  fun buildNetClient() = HttpClient(Android) {
    engine {
      connectTimeout = TIME_OUT
      socketTimeout = TIME_OUT
    }

    install(Logging) {
      logger = object : Logger {
        override fun log(message: String) {
          Log.d("Logger Ktor =>", message)
        }

      }
      level = LogLevel.ALL
    }

    install(ResponseObserver) {
      onResponse { response ->
        Log.d("HTTP status:", "${response.status.value}")
      }
    }
  }
}