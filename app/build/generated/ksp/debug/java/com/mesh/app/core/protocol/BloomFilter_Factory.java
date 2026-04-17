package com.mesh.app.core.protocol;

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
public final class BloomFilter_Factory implements Factory<BloomFilter> {
  @Override
  public BloomFilter get() {
    return newInstance();
  }

  public static BloomFilter_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BloomFilter newInstance() {
    return new BloomFilter();
  }

  private static final class InstanceHolder {
    private static final BloomFilter_Factory INSTANCE = new BloomFilter_Factory();
  }
}
