package com.mesh.app.data.repository;

import com.mesh.app.data.local.db.PeerDao;
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
public final class PeerRepository_Factory implements Factory<PeerRepository> {
  private final Provider<PeerDao> peerDaoProvider;

  public PeerRepository_Factory(Provider<PeerDao> peerDaoProvider) {
    this.peerDaoProvider = peerDaoProvider;
  }

  @Override
  public PeerRepository get() {
    return newInstance(peerDaoProvider.get());
  }

  public static PeerRepository_Factory create(Provider<PeerDao> peerDaoProvider) {
    return new PeerRepository_Factory(peerDaoProvider);
  }

  public static PeerRepository newInstance(PeerDao peerDao) {
    return new PeerRepository(peerDao);
  }
}
