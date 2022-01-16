package dev.baseio.efoundation.domain.repositories

import java.io.File

interface RandomFileService {
  fun getTempFile(): File
}
