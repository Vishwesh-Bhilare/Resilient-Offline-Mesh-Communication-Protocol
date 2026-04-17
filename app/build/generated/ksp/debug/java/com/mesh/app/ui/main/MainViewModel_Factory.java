package com.mesh.app.ui.main;

import com.mesh.app.data.repository.PeerRepository;
import com.mesh.app.gateway.GatewayManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class MainViewModel_Factory implements Factory<MainViewModel> {
  private final Provider<PeerRepository> peerRepositoryProvider;

  private final Provider<GatewayManager> gatewayManagerProvider;

  public MainViewModel_Factory(Provider<PeerRepository> peerRepositoryProvider,
      Provider<GatewayManager> gatewayManagerProvider) {
    this.peerRepositoryProvider = peerRepositoryProvider;
    this.gatewayManagerProvider = gatewayManagerProvider;
  }

  @Override
  public MainViewModel get() {
    return newInstance(peerRepositoryProvider.get(), gatewayManagerProvider.get());
  }

  public static MainViewModel_Factory create(Provider<PeerRepository> peerRepositoryProvider,
      Provider<GatewayManager> gatewayManagerProvider) {
    return new MainViewModel_Factory(peerRepositoryProvider, gatewayManagerProvider);
  }

  public static MainViewModel newInstance(PeerRepository peerRepository,
      GatewayManager gatewayManager) {
    return new MainViewModel(peerRepository, gatewayManager);
  }
}
