package com.mesh.app.ble;

import android.content.Context;
import com.mesh.app.core.identity.KeyManager;
import com.mesh.app.core.protocol.BloomFilter;
import com.mesh.app.core.sync.SyncManager;
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
public final class BleConnectionManager_Factory implements Factory<BleConnectionManager> {
  private final Provider<Context> contextProvider;

  private final Provider<BleScanner> scannerProvider;

  private final Provider<SyncManager> syncManagerProvider;

  private final Provider<KeyManager> keyManagerProvider;

  private final Provider<BloomFilter> bloomFilterProvider;

  public BleConnectionManager_Factory(Provider<Context> contextProvider,
      Provider<BleScanner> scannerProvider, Provider<SyncManager> syncManagerProvider,
      Provider<KeyManager> keyManagerProvider, Provider<BloomFilter> bloomFilterProvider) {
    this.contextProvider = contextProvider;
    this.scannerProvider = scannerProvider;
    this.syncManagerProvider = syncManagerProvider;
    this.keyManagerProvider = keyManagerProvider;
    this.bloomFilterProvider = bloomFilterProvider;
  }

  @Override
  public BleConnectionManager get() {
    return newInstance(contextProvider.get(), scannerProvider.get(), syncManagerProvider.get(), keyManagerProvider.get(), bloomFilterProvider.get());
  }

  public static BleConnectionManager_Factory create(Provider<Context> contextProvider,
      Provider<BleScanner> scannerProvider, Provider<SyncManager> syncManagerProvider,
      Provider<KeyManager> keyManagerProvider, Provider<BloomFilter> bloomFilterProvider) {
    return new BleConnectionManager_Factory(contextProvider, scannerProvider, syncManagerProvider, keyManagerProvider, bloomFilterProvider);
  }

  public static BleConnectionManager newInstance(Context context, BleScanner scanner,
      SyncManager syncManager, KeyManager keyManager, BloomFilter bloomFilter) {
    return new BleConnectionManager(context, scanner, syncManager, keyManager, bloomFilter);
  }
}
