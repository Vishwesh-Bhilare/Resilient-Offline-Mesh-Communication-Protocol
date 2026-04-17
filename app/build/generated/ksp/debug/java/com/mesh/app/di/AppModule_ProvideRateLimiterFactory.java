package com.mesh.app.di;

import com.mesh.app.core.security.RateLimiter;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class AppModule_ProvideRateLimiterFactory implements Factory<RateLimiter> {
  @Override
  public RateLimiter get() {
    return provideRateLimiter();
  }

  public static AppModule_ProvideRateLimiterFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RateLimiter provideRateLimiter() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideRateLimiter());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideRateLimiterFactory INSTANCE = new AppModule_ProvideRateLimiterFactory();
  }
}
