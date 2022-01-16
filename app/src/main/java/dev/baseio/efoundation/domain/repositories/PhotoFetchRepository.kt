package dev.baseio.efoundation.domain.repositories

interface PhotoFetchRepository {
  suspend fun fetchPhoto(
    url: String,
  )

  fun setListener(listener: PhotoFetchListener)
  fun removeListener(listener: PhotoFetchListener)
}
