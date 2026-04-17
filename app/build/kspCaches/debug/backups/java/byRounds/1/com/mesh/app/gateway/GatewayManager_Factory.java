package com.mesh.app.gateway;

import android.content.Context;
import com.mesh.app.data.repository.MessageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class GatewayManager_Factory implements Factory<GatewayManager> {
  private final Provider<Context> contextProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<ApiService> apiServiceProvider;

  public GatewayManager_Factory(Provider<Context> contextProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<ApiService> apiServiceProvider) {
    this.contextProvider = contextProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public GatewayManager get() {
    return newInstance(contextProvider.get(), messageRepositoryProvider.get(), apiServiceProvider.get());
  }

  public static GatewayManager_Factory create(Provider<Context> contextProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<ApiService> apiServiceProvider) {
    return new GatewayManager_Factory(contextProvider, messageRepositoryProvider, apiServiceProvider);
  }

  public static GatewayManager newInstance(Context context, MessageRepository messageRepository,
      ApiService apiService) {
    return new GatewayManager(context, messageRepository, apiService);
  }
}
