package com.mesh.app.di;

import android.content.Context;
import com.mesh.app.data.repository.MessageRepository;
import com.mesh.app.gateway.ApiService;
import com.mesh.app.gateway.GatewayManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class NetworkModule_ProvideGatewayManagerFactory implements Factory<GatewayManager> {
  private final Provider<Context> contextProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<ApiService> apiProvider;

  public NetworkModule_ProvideGatewayManagerFactory(Provider<Context> contextProvider,
      Provider<MessageRepository> messageRepositoryProvider, Provider<ApiService> apiProvider) {
    this.contextProvider = contextProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.apiProvider = apiProvider;
  }

  @Override
  public GatewayManager get() {
    return provideGatewayManager(contextProvider.get(), messageRepositoryProvider.get(), apiProvider.get());
  }

  public static NetworkModule_ProvideGatewayManagerFactory create(Provider<Context> contextProvider,
      Provider<MessageRepository> messageRepositoryProvider, Provider<ApiService> apiProvider) {
    return new NetworkModule_ProvideGatewayManagerFactory(contextProvider, messageRepositoryProvider, apiProvider);
  }

  public static GatewayManager provideGatewayManager(Context context,
      MessageRepository messageRepository, ApiService api) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideGatewayManager(context, messageRepository, api));
  }
}
