package com.mesh.app.di;

import com.mesh.app.core.protocol.BloomFilter;
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
public final class AppModule_ProvideBloomFilterFactory implements Factory<BloomFilter> {
  @Override
  public BloomFilter get() {
    return provideBloomFilter();
  }

  public static AppModule_ProvideBloomFilterFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BloomFilter provideBloomFilter() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBloomFilter());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideBloomFilterFactory INSTANCE = new AppModule_ProvideBloomFilterFactory();
  }
}
