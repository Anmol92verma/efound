package dev.baseio.efoundation.data.repositories

import dev.baseio.efoundation.domain.StreamingFile
import dev.baseio.efoundation.domain.repositories.PhotoFetchListener
import dev.baseio.efoundation.domain.repositories.PhotoFetchRepository
import dev.baseio.efoundation.domain.repositories.RandomFileService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class PicSumPhotoFetchRepositoryImpl @Inject constructor(
  private val coroutineContext: CoroutineContext,
  private val networkClient: HttpClient,
  private val fileCreationService: RandomFileService
) :
  PhotoFetchRepository {

  private var fileDownloadListener: PhotoFetchListener? = null

  override fun setListener(listener: PhotoFetchListener) {
    fileDownloadListener = listener
  }

  override fun removeListener(listener: PhotoFetchListener) {
    fileDownloadListener = null
  }

  override suspend fun fetchPhoto(url: String) =
    withContext(coroutineContext) {
      try {
        val file = fileCreationService.createFile()
        val response: HttpResponse = networkClient.get(url) {
          onDownload { bytesSentTotal, contentLength ->
            prepareCallback(bytesSentTotal, contentLength, file)
          }
        }
        val bytes = response.receive<ByteArray>()
        file.writeBytes(bytes)
        fileDownloadListener?.onComplete()
        StreamingFile(
          100.0,
          file
        )
      } catch (ex: Exception) {
        fileDownloadListener?.onFailed(ex)
        null
      }
    }

  private fun prepareCallback(
    bytesSentTotal: Long,
    contentLength: Long,
    file: File
  ) {
    val progress: Double = (bytesSentTotal / contentLength).times(100.0)
    fileDownloadListener?.onReceive(
      StreamingFile(
        progress,
        file
      )
    )
  }
}