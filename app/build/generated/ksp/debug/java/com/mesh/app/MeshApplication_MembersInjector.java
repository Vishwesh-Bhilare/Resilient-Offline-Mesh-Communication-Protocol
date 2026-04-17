package com.mesh.app;

import androidx.hilt.work.HiltWorkerFactory;
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

  public MeshApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<MeshApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new MeshApplication_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(MeshApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.mesh.app.MeshApplication.workerFactory")
  public static void injectWorkerFactory(MeshApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
