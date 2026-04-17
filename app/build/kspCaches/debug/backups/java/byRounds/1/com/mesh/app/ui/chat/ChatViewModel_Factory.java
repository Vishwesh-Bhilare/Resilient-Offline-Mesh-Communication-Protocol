package com.mesh.app.ui.chat;

import com.mesh.app.ble.BleAdvertiser;
import com.mesh.app.core.identity.KeyManager;
import com.mesh.app.core.protocol.HlcClock;
import com.mesh.app.data.repository.MessageRepository;
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
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<KeyManager> keyManagerProvider;

  private final Provider<HlcClock> hlcClockProvider;

  private final Provider<BleAdvertiser> advertiserProvider;

  public ChatViewModel_Factory(Provider<MessageRepository> messageRepositoryProvider,
      Provider<KeyManager> keyManagerProvider, Provider<HlcClock> hlcClockProvider,
      Provider<BleAdvertiser> advertiserProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.keyManagerProvider = keyManagerProvider;
    this.hlcClockProvider = hlcClockProvider;
    this.advertiserProvider = advertiserProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(messageRepositoryProvider.get(), keyManagerProvider.get(), hlcClockProvider.get(), advertiserProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<MessageRepository> messageRepositoryProvider,
      Provider<KeyManager> keyManagerProvider, Provider<HlcClock> hlcClockProvider,
      Provider<BleAdvertiser> advertiserProvider) {
    return new ChatViewModel_Factory(messageRepositoryProvider, keyManagerProvider, hlcClockProvider, advertiserProvider);
  }

  public static ChatViewModel newInstance(MessageRepository messageRepository,
      KeyManager keyManager, HlcClock hlcClock, BleAdvertiser advertiser) {
    return new ChatViewModel(messageRepository, keyManager, hlcClock, advertiser);
  }
}
