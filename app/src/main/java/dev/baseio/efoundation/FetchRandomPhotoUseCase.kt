package dev.baseio.efoundation

import dev.baseio.efoundation.domain.StreamingFile
import dev.baseio.efoundation.domain.repositories.PhotoFetchListener
import dev.baseio.efoundation.domain.repositories.PhotoFetchRepository
import dev.baseio.efoundation.domain.usecases.BaseStreamingUseCase
import dev.baseio.efoundation.domain.usecases.BaseUseCase
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import kotlin.coroutines.cancellation.CancellationException

class FetchRandomPhotoUseCase(private val photoFetchRepository: PhotoFetchRepository) :
  BaseStreamingUseCase<String, StreamingFile> {

  override suspend fun perform(input: String?): StreamingFile? {
    return photoFetchRepository.fetchPhoto(input ?: PIC_SUM_URL)
  }

  override fun performStreaming(input: String?): Flow<StreamingFile> {
    return callbackFlow {
      val listener = photoFetchListener()
      photoFetchRepository.setListener(listener)
      photoFetchRepository.fetchPhoto(input ?: PIC_SUM_URL)
      awaitClose {
        photoFetchRepository.removeListener(listener)
      }
    }
  }

  private fun ProducerScope<StreamingFile>.photoFetchListener() =
    object : PhotoFetchListener {
      override fun onReceive(streamingFile: StreamingFile) {
        trySend(streamingFile).onFailure {
          cancel(CancellationException("Download Error", it))
        }
      }

      override fun onFailed(throwable: Throwable) {
        cancel(CancellationException("Download Error", throwable))
      }

      override fun onComplete(){
        channel.close()
      }
    }
}
