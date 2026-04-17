package com.mesh.app.di;

import com.mesh.app.data.local.db.AppDatabase;
import com.mesh.app.data.local.db.InProgressDao;
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
public final class DatabaseModule_ProvideInProgressDaoFactory implements Factory<InProgressDao> {
  private final Provider<AppDatabase> dbProvider;

  public DatabaseModule_ProvideInProgressDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public InProgressDao get() {
    return provideInProgressDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideInProgressDaoFactory create(
      Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideInProgressDaoFactory(dbProvider);
  }

  public static InProgressDao provideInProgressDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideInProgressDao(db));
  }
}
