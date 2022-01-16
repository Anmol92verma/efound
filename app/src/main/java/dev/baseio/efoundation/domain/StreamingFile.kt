package dev.baseio.efoundation.domain

import java.io.File

data class StreamingFile(
  var progress: Long,
  var file: File,
  var isComplete: Boolean,
)
