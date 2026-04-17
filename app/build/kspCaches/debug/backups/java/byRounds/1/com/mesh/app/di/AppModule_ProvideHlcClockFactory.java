package com.mesh.app.di;

import com.mesh.app.core.identity.KeyManager;
import com.mesh.app.core.protocol.HlcClock;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppModule_ProvideHlcClockFactory implements Factory<HlcClock> {
  private final Provider<KeyManager> keyManagerProvider;

  public AppModule_ProvideHlcClockFactory(Provider<KeyManager> keyManagerProvider) {
    this.keyManagerProvider = keyManagerProvider;
  }

  @Override
  public HlcClock get() {
    return provideHlcClock(keyManagerProvider.get());
  }

  public static AppModule_ProvideHlcClockFactory create(Provider<KeyManager> keyManagerProvider) {
    return new AppModule_ProvideHlcClockFactory(keyManagerProvider);
  }

  public static HlcClock provideHlcClock(KeyManager keyManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideHlcClock(keyManager));
  }
}
