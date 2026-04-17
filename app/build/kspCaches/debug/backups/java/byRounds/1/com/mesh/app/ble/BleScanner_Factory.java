package com.mesh.app.ble;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class BleScanner_Factory implements Factory<BleScanner> {
  @Override
  public BleScanner get() {
    return newInstance();
  }

  public static BleScanner_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static BleScanner newInstance() {
    return new BleScanner();
  }

  private static final class InstanceHolder {
    private static final BleScanner_Factory INSTANCE = new BleScanner_Factory();
  }
}
