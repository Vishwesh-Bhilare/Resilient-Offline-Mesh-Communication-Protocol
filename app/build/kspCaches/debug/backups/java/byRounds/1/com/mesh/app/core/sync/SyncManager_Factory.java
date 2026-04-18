package com.mesh.app.core.sync;

import com.mesh.app.ble.BleAdvertiser;
import com.mesh.app.core.protocol.BloomFilter;
import com.mesh.app.data.repository.InProgressRepository;
import com.mesh.app.data.repository.MessageRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SyncManager_Factory implements Factory<SyncManager> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<InProgressRepository> inProgressRepositoryProvider;

  private final Provider<BloomFilter> bloomFilterProvider;

  private final Provider<BleAdvertiser> advertiserProvider;

  public SyncManager_Factory(Provider<MessageRepository> messageRepositoryProvider,
      Provider<InProgressRepository> inProgressRepositoryProvider,
      Provider<BloomFilter> bloomFilterProvider, Provider<BleAdvertiser> advertiserProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.inProgressRepositoryProvider = inProgressRepositoryProvider;
    this.bloomFilterProvider = bloomFilterProvider;
    this.advertiserProvider = advertiserProvider;
  }

  @Override
  public SyncManager get() {
    return newInstance(messageRepositoryProvider.get(), inProgressRepositoryProvider.get(), bloomFilterProvider.get(), advertiserProvider.get());
  }

  public static SyncManager_Factory create(Provider<MessageRepository> messageRepositoryProvider,
      Provider<InProgressRepository> inProgressRepositoryProvider,
      Provider<BloomFilter> bloomFilterProvider, Provider<BleAdvertiser> advertiserProvider) {
    return new SyncManager_Factory(messageRepositoryProvider, inProgressRepositoryProvider, bloomFilterProvider, advertiserProvider);
  }

  public static SyncManager newInstance(MessageRepository messageRepository,
      InProgressRepository inProgressRepository, BloomFilter bloomFilter,
      BleAdvertiser advertiser) {
    return new SyncManager(messageRepository, inProgressRepository, bloomFilter, advertiser);
  }
}
