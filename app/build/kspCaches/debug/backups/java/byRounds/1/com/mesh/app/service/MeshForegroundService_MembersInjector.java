package com.mesh.app.service;

import com.mesh.app.ble.BleAdvertiser;
import com.mesh.app.ble.BleConnectionManager;
import com.mesh.app.ble.BleScanner;
import com.mesh.app.gateway.GatewayManager;
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
public final class MeshForegroundService_MembersInjector implements MembersInjector<MeshForegroundService> {
  private final Provider<BleAdvertiser> advertiserProvider;

  private final Provider<BleScanner> scannerProvider;

  private final Provider<BleConnectionManager> connectionManagerProvider;

  private final Provider<GatewayManager> gatewayManagerProvider;

  public MeshForegroundService_MembersInjector(Provider<BleAdvertiser> advertiserProvider,
      Provider<BleScanner> scannerProvider,
      Provider<BleConnectionManager> connectionManagerProvider,
      Provider<GatewayManager> gatewayManagerProvider) {
    this.advertiserProvider = advertiserProvider;
    this.scannerProvider = scannerProvider;
    this.connectionManagerProvider = connectionManagerProvider;
    this.gatewayManagerProvider = gatewayManagerProvider;
  }

  public static MembersInjector<MeshForegroundService> create(
      Provider<BleAdvertiser> advertiserProvider, Provider<BleScanner> scannerProvider,
      Provider<BleConnectionManager> connectionManagerProvider,
      Provider<GatewayManager> gatewayManagerProvider) {
    return new MeshForegroundService_MembersInjector(advertiserProvider, scannerProvider, connectionManagerProvider, gatewayManagerProvider);
  }

  @Override
  public void injectMembers(MeshForegroundService instance) {
    injectAdvertiser(instance, advertiserProvider.get());
    injectScanner(instance, scannerProvider.get());
    injectConnectionManager(instance, connectionManagerProvider.get());
    injectGatewayManager(instance, gatewayManagerProvider.get());
  }

  @InjectedFieldSignature("com.mesh.app.service.MeshForegroundService.advertiser")
  public static void injectAdvertiser(MeshForegroundService instance, BleAdvertiser advertiser) {
    instance.advertiser = advertiser;
  }

  @InjectedFieldSignature("com.mesh.app.service.MeshForegroundService.scanner")
  public static void injectScanner(MeshForegroundService instance, BleScanner scanner) {
    instance.scanner = scanner;
  }

  @InjectedFieldSignature("com.mesh.app.service.MeshForegroundService.connectionManager")
  public static void injectConnectionManager(MeshForegroundService instance,
      BleConnectionManager connectionManager) {
    instance.connectionManager = connectionManager;
  }

  @InjectedFieldSignature("com.mesh.app.service.MeshForegroundService.gatewayManager")
  public static void injectGatewayManager(MeshForegroundService instance,
      GatewayManager gatewayManager) {
    instance.gatewayManager = gatewayManager;
  }
}
