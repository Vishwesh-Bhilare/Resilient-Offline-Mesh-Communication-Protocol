package com.mesh.app.di;

import com.mesh.app.data.local.db.AppDatabase;
import com.mesh.app.data.local.db.PeerDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvidePeerDaoFactory implements Factory<PeerDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvidePeerDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public PeerDao get() {
    return providePeerDao(dbProvider.get());
  }

  public static DatabaseModule_ProvidePeerDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvidePeerDaoFactory(dbProvider);
  }

  public static PeerDao providePeerDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.providePeerDao(db));
  }
}
