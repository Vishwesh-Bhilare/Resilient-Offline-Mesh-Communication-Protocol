package com.mesh.app.di

import com.mesh.app.gateway.ApiService
import com.mesh.app.gateway.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun provideApiService(): ApiService = RetrofitClient.api
}
