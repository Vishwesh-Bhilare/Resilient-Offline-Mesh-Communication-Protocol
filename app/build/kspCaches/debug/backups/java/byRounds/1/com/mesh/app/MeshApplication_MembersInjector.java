package com.mesh.app;

import androidx.hilt.work.HiltWorkerFactory;
import com.mesh.app.core.protocol.BloomFilter;
import com.mesh.app.data.repository.MessageRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MeshApplication_MembersInjector implements MembersInjector<MeshApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<BloomFilter> bloomFilterProvider;

  public MeshApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<BloomFilter> bloomFilterProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.bloomFilterProvider = bloomFilterProvider;
  }

  public static MembersInjector<MeshApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<MessageRepository> messageRepositoryProvider,
      Provider<BloomFilter> bloomFilterProvider) {
    return new MeshApplication_MembersInjector(workerFactoryProvider, messageRepositoryProvider, bloomFilterProvider);
  }

  @Override
  public void injectMembers(MeshApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectMessageRepository(instance, messageRepositoryProvider.get());
    injectBloomFilter(instance, bloomFilterProvider.get());
  }

  @InjectedFieldSignature("com.mesh.app.MeshApplication.workerFactory")
  public static void injectWorkerFactory(MeshApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.mesh.app.MeshApplication.messageRepository")
  public static void injectMessageRepository(MeshApplication instance,
      MessageRepository messageRepository) {
    instance.messageRepository = messageRepository;
  }

  @InjectedFieldSignature("com.mesh.app.MeshApplication.bloomFilter")
  public static void injectBloomFilter(MeshApplication instance, BloomFilter bloomFilter) {
    instance.bloomFilter = bloomFilter;
  }
}
