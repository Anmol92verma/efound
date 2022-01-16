package dev.baseio.efoundation.domain.usecases

import kotlinx.coroutines.flow.Flow

interface BaseUseCase<in T, out U> {
  suspend fun perform(input: T? = null): U?
}


interface BaseStreamingUseCase<in T, out U> : BaseUseCase<T,U>{
  fun performStreaming(input: T? = null): Flow<U>
}
