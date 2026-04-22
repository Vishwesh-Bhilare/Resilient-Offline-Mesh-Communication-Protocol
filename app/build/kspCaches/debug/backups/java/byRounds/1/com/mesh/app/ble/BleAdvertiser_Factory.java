package com.mesh.app.ble;

import android.content.Context;
import com.mesh.app.core.identity.KeyManager;
import com.mesh.app.core.protocol.BloomFilter;
import com.mesh.app.gateway.GatewayManager;
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
public final class BleAdvertiser_Factory implements Factory<BleAdvertiser> {
  private final Provider<Context> contextProvider;

  private final Provider<KeyManager> keyManagerProvider;

  private final Provider<BloomFilter> bloomFilterProvider;

  private final Provider<GatewayManager> gatewayManagerProvider;

  public BleAdvertiser_Factory(Provider<Context> contextProvider,
      Provider<KeyManager> keyManagerProvider, Provider<BloomFilter> bloomFilterProvider,
      Provider<GatewayManager> gatewayManagerProvider) {
    this.contextProvider = contextProvider;
    this.keyManagerProvider = keyManagerProvider;
    this.bloomFilterProvider = bloomFilterProvider;
    this.gatewayManagerProvider = gatewayManagerProvider;
  }

  @Override
  public BleAdvertiser get() {
    return newInstance(contextProvider.get(), keyManagerProvider.get(), bloomFilterProvider.get(), gatewayManagerProvider.get());
  }

  public static BleAdvertiser_Factory create(Provider<Context> contextProvider,
      Provider<KeyManager> keyManagerProvider, Provider<BloomFilter> bloomFilterProvider,
      Provider<GatewayManager> gatewayManagerProvider) {
    return new BleAdvertiser_Factory(contextProvider, keyManagerProvider, bloomFilterProvider, gatewayManagerProvider);
  }

  public static BleAdvertiser newInstance(Context context, KeyManager keyManager,
      BloomFilter bloomFilter, GatewayManager gatewayManager) {
    return new BleAdvertiser(context, keyManager, bloomFilter, gatewayManager);
  }
}
