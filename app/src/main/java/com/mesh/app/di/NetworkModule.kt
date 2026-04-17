package com.mesh.app.di

import android.content.Context
import com.mesh.app.gateway.ApiService
import com.mesh.app.gateway.GatewayManager
import com.mesh.app.gateway.RetrofitClient
import com.mesh.app.data.repository.MessageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides @Singleton fun provideApiService(): ApiService = RetrofitClient.api
    @Provides @Singleton fun provideGatewayManager(@ApplicationContext context: Context, messageRepository: MessageRepository, api: ApiService): GatewayManager =
        GatewayManager(context, messageRepository, api)
}
