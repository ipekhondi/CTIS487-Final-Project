package com.ctis487_fp.finalproject.service.module

import com.ctis487_fp.finalproject.service.AccountService
import com.ctis487_fp.finalproject.service.StorageService
import com.ctis487_fp.finalproject.service.impl.AccountServiceImpl
import com.ctis487_fp.finalproject.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
  @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

  @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService
}
