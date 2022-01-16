package dev.baseio.efoundation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.baseio.efoundation.domain.StreamingFile
import dev.baseio.efoundation.domain.repositories.RandomFileService
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RandomViewModel @Inject constructor(
  private val fetchPhotoUseCase: FetchRandomPhotoUseCase,
  randomFileService: RandomFileService
) :
  ViewModel() {
  private var fetchJob: Job? = null
  private val viewState = MutableLiveData<RandomPhotoViewState>(
    RandomPhotoViewState.Empty(null)
  )

  init {
    val file = randomFileService.getTempFile()
    val initialState = RandomPhotoViewState.Empty(
      StreamingFile(file.length(), file, true)
    )
    viewState.value = initialState
  }

  val randomViewState: LiveData<RandomPhotoViewState> = viewState

  private val exceptionHandler: CoroutineExceptionHandler =
    CoroutineExceptionHandler { _, throwable ->
      viewState.value = RandomPhotoViewState.Exception(throwable)
    }

  fun fetchRandomPhoto() {
    fetchJob?.cancel()
    fetchJob = viewModelScope.launch(exceptionHandler) {
      fetchPhotoUseCase.performStreaming().collectLatest { streamingFile ->
        viewState.value = RandomPhotoViewState.Streaming(streamingFile)
      }
    }
  }

  sealed class RandomPhotoViewState {
    class Exception(val throwable: Throwable) : RandomPhotoViewState()
    class Streaming(val result: StreamingFile) : RandomPhotoViewState()
    class Empty(val result: StreamingFile?) : RandomPhotoViewState()
  }
}