package dev.baseio.efoundation.domain.repositories

import dev.baseio.efoundation.domain.StreamingFile

interface PhotoFetchListener {
  fun onReceive(streamingFile: StreamingFile)
  fun onFailed(throwable: Throwable)
  fun onComplete()
}
