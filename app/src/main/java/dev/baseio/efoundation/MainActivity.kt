package dev.baseio.efoundation

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.baseio.efoundation.databinding.ViewRandomPhotosBinding
import dev.baseio.efoundation.domain.StreamingFile
import java.io.FileNotFoundException

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  private val viewModel: RandomViewModel by viewModels()
  private lateinit var binding: ViewRandomPhotosBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ViewRandomPhotosBinding.inflate(LayoutInflater.from(this))
    setContentView(binding.root)
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
        setImageForFile(randomPhotoViewState.result)
      }
      is RandomViewModel.RandomPhotoViewState.Exception -> {
        Log.e(this.javaClass.name, randomPhotoViewState.throwable.message ?: "")
        when (randomPhotoViewState.throwable) {
          is FileNotFoundException -> {
            showSnackbarMessage(getString(R.string.except_file_not_found))
          }
          else -> {
            showSnackbarMessage(randomPhotoViewState.throwable.message ?: "")
          }
        }
      }
      is RandomViewModel.RandomPhotoViewState.Streaming -> {
        binding.progressText.visibility = View.VISIBLE

        if (randomPhotoViewState.result.isComplete) {
          setImageForFile(randomPhotoViewState.result)
        } else {
          binding.progressText.text =
            getString(R.string.downloading_glue, randomPhotoViewState.result.progress)
        }
        Log.d(
          this.javaClass.name,
          "${randomPhotoViewState.result.file.absolutePath} ${randomPhotoViewState.result.file.length()}"
        )
      }
    }
  }

  private fun setImageForFile(file: StreamingFile?) {
    binding.photoView.setImageResource(R.drawable.ic_launcher_background) // to avoid redraw issues
    file?.let { streamingFile ->
      binding.photoView.setImageURI(Uri.fromFile(streamingFile.file))
      binding.progressText.visibility = View.VISIBLE
      binding.progressText.text =
        getString(R.string.downloaded_glue, streamingFile.file.length())
    }
  }

  private fun showSnackbarMessage(message: String) {
    Snackbar.make(binding.root, message, LENGTH_SHORT).show()
  }
}