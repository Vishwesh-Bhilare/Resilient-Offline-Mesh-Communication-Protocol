package com.mesh.app.data.repository;

import android.content.Context;
import com.mesh.app.core.security.RateLimiter;
import com.mesh.app.data.local.db.MessageDao;
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
public final class MessageRepository_Factory implements Factory<MessageRepository> {
  private final Provider<MessageDao> daoProvider;

  private final Provider<RateLimiter> rateLimiterProvider;

  private final Provider<Context> contextProvider;

  public MessageRepository_Factory(Provider<MessageDao> daoProvider,
      Provider<RateLimiter> rateLimiterProvider, Provider<Context> contextProvider) {
    this.daoProvider = daoProvider;
    this.rateLimiterProvider = rateLimiterProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public MessageRepository get() {
    return newInstance(daoProvider.get(), rateLimiterProvider.get(), contextProvider.get());
  }

  public static MessageRepository_Factory create(Provider<MessageDao> daoProvider,
      Provider<RateLimiter> rateLimiterProvider, Provider<Context> contextProvider) {
    return new MessageRepository_Factory(daoProvider, rateLimiterProvider, contextProvider);
  }

  public static MessageRepository newInstance(MessageDao dao, RateLimiter rateLimiter,
      Context context) {
    return new MessageRepository(dao, rateLimiter, context);
  }
}
