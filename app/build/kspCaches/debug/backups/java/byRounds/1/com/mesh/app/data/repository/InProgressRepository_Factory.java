package com.mesh.app.data.repository;

import com.mesh.app.data.local.db.InProgressDao;
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
public final class InProgressRepository_Factory implements Factory<InProgressRepository> {
  private final Provider<InProgressDao> daoProvider;

  public InProgressRepository_Factory(Provider<InProgressDao> daoProvider) {
    this.daoProvider = daoProvider;
  }

  @Override
  public InProgressRepository get() {
    return newInstance(daoProvider.get());
  }

  public static InProgressRepository_Factory create(Provider<InProgressDao> daoProvider) {
    return new InProgressRepository_Factory(daoProvider);
  }

  public static InProgressRepository newInstance(InProgressDao dao) {
    return new InProgressRepository(dao);
  }
}
