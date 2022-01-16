package dev.baseio.efoundation.domain.repositories

import dev.baseio.efoundation.domain.StreamingFile

interface PhotoFetchRepository {
  suspend fun fetchPhoto(
    url: String,
  ): StreamingFile?

  fun setListener(listener: PhotoFetchListener)
  fun removeListener(listener: PhotoFetchListener)
}
