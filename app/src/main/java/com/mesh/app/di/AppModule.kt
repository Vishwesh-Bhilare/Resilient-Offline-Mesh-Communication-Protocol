package com.mesh.app.di

import com.mesh.app.core.identity.KeyManager
import com.mesh.app.core.protocol.BloomFilter
import com.mesh.app.core.protocol.HlcClock
import com.mesh.app.core.security.RateLimiter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun provideBloomFilter(): BloomFilter = BloomFilter()
    @Provides @Singleton fun provideRateLimiter(): RateLimiter = RateLimiter()
    // FIX: 7 — HlcClock has no @Inject constructor; it is provided exclusively through this module.
    @Provides @Singleton fun provideHlcClock(keyManager: KeyManager): HlcClock = HlcClock(keyManager.getDeviceId())
}
