package com.mesh.app.di;

import com.mesh.app.gateway.ApiService;
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
public final class NetworkModule_ProvideApiServiceFactory implements Factory<ApiService> {
  @Override
  public ApiService get() {
    return provideApiService();
  }

  public static NetworkModule_ProvideApiServiceFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ApiService provideApiService() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideApiService());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideApiServiceFactory INSTANCE = new NetworkModule_ProvideApiServiceFactory();
  }
}
