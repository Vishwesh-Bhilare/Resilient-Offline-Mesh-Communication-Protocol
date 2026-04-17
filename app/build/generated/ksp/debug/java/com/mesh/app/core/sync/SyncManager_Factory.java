package com.mesh.app.core.sync;

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

  public SyncManager_Factory(Provider<MessageRepository> messageRepositoryProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
  }

  @Override
  public SyncManager get() {
    return newInstance(messageRepositoryProvider.get());
  }

  public static SyncManager_Factory create(Provider<MessageRepository> messageRepositoryProvider) {
    return new SyncManager_Factory(messageRepositoryProvider);
  }

  public static SyncManager newInstance(MessageRepository messageRepository) {
    return new SyncManager(messageRepository);
  }
}
