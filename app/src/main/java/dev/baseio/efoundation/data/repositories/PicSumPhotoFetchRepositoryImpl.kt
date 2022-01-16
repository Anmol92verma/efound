package dev.baseio.efoundation.data.repositories

import android.util.Log
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

  override suspend fun fetchPhoto(url: String): Unit =
    withContext(coroutineContext) {
      try {
        val file = fileCreationService.getTempFile()
        val response: HttpResponse = networkClient.get(url) {
          onDownload { bytesSentTotal, contentLength ->
            Log.d(this.javaClass.name,"${bytesSentTotal}/${contentLength}")
            prepareCallback(bytesSentTotal, file)
          }
        }
        val bytes = response.receive<ByteArray>()
        file.writeBytes(bytes)
        fileDownloadListener?.onReceive(
          StreamingFile(
            file.length(),
            file, isComplete = true
          )
        )
        fileDownloadListener?.onComplete()
      } catch (ex: Exception) {
        fileDownloadListener?.onFailed(ex)
      }
    }

  private fun prepareCallback(
    bytesSentTotal: Long,
    file: File
  ) {
    fileDownloadListener?.onReceive(
      StreamingFile(
        bytesSentTotal,
        file, isComplete = false
      )
    )
  }
}