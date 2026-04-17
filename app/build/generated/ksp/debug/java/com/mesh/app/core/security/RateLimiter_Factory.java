package com.mesh.app.core.security;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class RateLimiter_Factory implements Factory<RateLimiter> {
  @Override
  public RateLimiter get() {
    return newInstance();
  }

  public static RateLimiter_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static RateLimiter newInstance() {
    return new RateLimiter();
  }

  private static final class InstanceHolder {
    private static final RateLimiter_Factory INSTANCE = new RateLimiter_Factory();
  }
}
