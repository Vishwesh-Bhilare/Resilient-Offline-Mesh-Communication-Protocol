package com.mesh.app.service;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.mesh.app.data.local.db.InProgressDao;
import com.mesh.app.data.repository.MessageRepository;
import com.mesh.app.gateway.GatewayManager;
import dagger.internal.DaggerGenerated;
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
public final class SyncWorker_Factory {
  private final Provider<MessageRepository> messageRepositoryProvider;

  private final Provider<GatewayManager> gatewayManagerProvider;

  private final Provider<InProgressDao> inProgressDaoProvider;

  public SyncWorker_Factory(Provider<MessageRepository> messageRepositoryProvider,
      Provider<GatewayManager> gatewayManagerProvider,
      Provider<InProgressDao> inProgressDaoProvider) {
    this.messageRepositoryProvider = messageRepositoryProvider;
    this.gatewayManagerProvider = gatewayManagerProvider;
    this.inProgressDaoProvider = inProgressDaoProvider;
  }

  public SyncWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, messageRepositoryProvider.get(), gatewayManagerProvider.get(), inProgressDaoProvider.get());
  }

  public static SyncWorker_Factory create(Provider<MessageRepository> messageRepositoryProvider,
      Provider<GatewayManager> gatewayManagerProvider,
      Provider<InProgressDao> inProgressDaoProvider) {
    return new SyncWorker_Factory(messageRepositoryProvider, gatewayManagerProvider, inProgressDaoProvider);
  }

  public static SyncWorker newInstance(Context context, WorkerParameters params,
      MessageRepository messageRepository, GatewayManager gatewayManager,
      InProgressDao inProgressDao) {
    return new SyncWorker(context, params, messageRepository, gatewayManager, inProgressDao);
  }
}
