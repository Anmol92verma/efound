package dev.baseio.efoundation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dev.baseio.efoundation.databinding.ViewRandomPhotosBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val viewModel: RandomViewModel by viewModels()
  private lateinit var binding: ViewRandomPhotosBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.view_random_photos)
    binding = ViewRandomPhotosBinding.inflate(LayoutInflater.from(this))

    viewModel.randomViewState.observe(this, { randomPhotoViewState ->
      randomPhotoViewState?.let {
        receiveViewState(randomPhotoViewState)
      }
    })
    binding.randomPhotoButton.setOnClickListener {
      viewModel.fetchRandomPhoto()
    }
  }

  private fun receiveViewState(randomPhotoViewState: RandomViewModel.RandomPhotoViewState) {
    when (randomPhotoViewState) {
      is RandomViewModel.RandomPhotoViewState.Empty -> {
        Log.e(this.javaClass.name, "No Data")
      }
      is RandomViewModel.RandomPhotoViewState.Exception -> {
        Log.e(this.javaClass.name, randomPhotoViewState.throwable.message ?: "")
      }
      is RandomViewModel.RandomPhotoViewState.Streaming -> {
        Log.d(
          this.javaClass.name,
          "${randomPhotoViewState.result.progress} ${randomPhotoViewState.result.file.absolutePath} ${randomPhotoViewState.result.file.length()}"
        )
      }
    }
  }
}