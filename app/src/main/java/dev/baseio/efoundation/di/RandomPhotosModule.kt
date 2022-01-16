package dev.baseio.efoundation.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.baseio.efoundation.FetchRandomPhotoUseCase
import dev.baseio.efoundation.data.NetworkClient
import dev.baseio.efoundation.data.repositories.PicSumPhotoFetchRepositoryImpl
import dev.baseio.efoundation.data.repositories.RandomFileServiceImpl
import dev.baseio.efoundation.domain.repositories.PhotoFetchRepository
import dev.baseio.efoundation.domain.repositories.RandomFileService
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class RandomPhotosModule {

  @Provides
  @Singleton
  fun providesHttpClient() = NetworkClient.buildNetClient()

  @Provides
  @Singleton
  fun provideCorContext() : CoroutineContext = Dispatchers.IO

  @Provides
  @Singleton
  fun provideContext(@ApplicationContext context: Context) = context

  @Provides
  @Singleton
  fun provideFetchRandomPhotoUseCase(photoFetchRepository: PhotoFetchRepository) =
    FetchRandomPhotoUseCase(photoFetchRepository)

}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

  @Binds
  abstract fun bindRandomFileService(randomFileServiceImpl: RandomFileServiceImpl): RandomFileService

  @Binds
  abstract fun bindPhotoFetchRepository(photoFetchRepositoryImpl: PicSumPhotoFetchRepositoryImpl): PhotoFetchRepository
}