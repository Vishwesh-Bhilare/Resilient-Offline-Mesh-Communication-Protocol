package com.mesh.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import com.mesh.app.ble.BleAdvertiser;
import com.mesh.app.ble.BleConnectionManager;
import com.mesh.app.ble.BleScanner;
import com.mesh.app.core.identity.KeyManager;
import com.mesh.app.core.protocol.BloomFilter;
import com.mesh.app.core.protocol.HlcClock;
import com.mesh.app.core.security.RateLimiter;
import com.mesh.app.core.sync.SyncManager;
import com.mesh.app.data.local.db.AppDatabase;
import com.mesh.app.data.local.db.InProgressDao;
import com.mesh.app.data.local.db.MessageDao;
import com.mesh.app.data.local.db.PeerDao;
import com.mesh.app.data.repository.InProgressRepository;
import com.mesh.app.data.repository.MessageRepository;
import com.mesh.app.data.repository.PeerRepository;
import com.mesh.app.di.AppModule_ProvideBloomFilterFactory;
import com.mesh.app.di.AppModule_ProvideHlcClockFactory;
import com.mesh.app.di.AppModule_ProvideRateLimiterFactory;
import com.mesh.app.di.DatabaseModule_ProvideDatabaseFactory;
import com.mesh.app.di.DatabaseModule_ProvideInProgressDaoFactory;
import com.mesh.app.di.DatabaseModule_ProvideMessageDaoFactory;
import com.mesh.app.di.DatabaseModule_ProvidePeerDaoFactory;
import com.mesh.app.di.NetworkModule_ProvideApiServiceFactory;
import com.mesh.app.gateway.ApiService;
import com.mesh.app.gateway.GatewayManager;
import com.mesh.app.service.MeshForegroundService;
import com.mesh.app.service.MeshForegroundService_MembersInjector;
import com.mesh.app.ui.chat.ChatViewModel;
import com.mesh.app.ui.chat.ChatViewModel_HiltModules;
import com.mesh.app.ui.main.MainActivity;
import com.mesh.app.ui.main.MainViewModel;
import com.mesh.app.ui.main.MainViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerMeshApplication_HiltComponents_SingletonC {
  private DaggerMeshApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public MeshApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements MeshApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public MeshApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements MeshApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public MeshApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements MeshApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public MeshApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements MeshApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MeshApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements MeshApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public MeshApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements MeshApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public MeshApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements MeshApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public MeshApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends MeshApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends MeshApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends MeshApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends MeshApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(2).put(LazyClassKeyProvider.com_mesh_app_ui_chat_ChatViewModel, ChatViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_mesh_app_ui_main_MainViewModel, MainViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_mesh_app_ui_chat_ChatViewModel = "com.mesh.app.ui.chat.ChatViewModel";

      static String com_mesh_app_ui_main_MainViewModel = "com.mesh.app.ui.main.MainViewModel";

      @KeepFieldType
      ChatViewModel com_mesh_app_ui_chat_ChatViewModel2;

      @KeepFieldType
      MainViewModel com_mesh_app_ui_main_MainViewModel2;
    }
  }

  private static final class ViewModelCImpl extends MeshApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ChatViewModel> chatViewModelProvider;

    private Provider<MainViewModel> mainViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.chatViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.mainViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(2).put(LazyClassKeyProvider.com_mesh_app_ui_chat_ChatViewModel, ((Provider) chatViewModelProvider)).put(LazyClassKeyProvider.com_mesh_app_ui_main_MainViewModel, ((Provider) mainViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_mesh_app_ui_chat_ChatViewModel = "com.mesh.app.ui.chat.ChatViewModel";

      static String com_mesh_app_ui_main_MainViewModel = "com.mesh.app.ui.main.MainViewModel";

      @KeepFieldType
      ChatViewModel com_mesh_app_ui_chat_ChatViewModel2;

      @KeepFieldType
      MainViewModel com_mesh_app_ui_main_MainViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.mesh.app.ui.chat.ChatViewModel 
          return (T) new ChatViewModel(singletonCImpl.messageRepositoryProvider.get(), singletonCImpl.keyManagerProvider.get(), singletonCImpl.provideHlcClockProvider.get(), singletonCImpl.bleAdvertiserProvider.get(), singletonCImpl.provideBloomFilterProvider.get());

          case 1: // com.mesh.app.ui.main.MainViewModel 
          return (T) new MainViewModel(singletonCImpl.peerRepositoryProvider.get(), singletonCImpl.gatewayManagerProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends MeshApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends MeshApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectMeshForegroundService(MeshForegroundService meshForegroundService) {
      injectMeshForegroundService2(meshForegroundService);
    }

    private MeshForegroundService injectMeshForegroundService2(MeshForegroundService instance) {
      MeshForegroundService_MembersInjector.injectAdvertiser(instance, singletonCImpl.bleAdvertiserProvider.get());
      MeshForegroundService_MembersInjector.injectScanner(instance, singletonCImpl.bleScannerProvider.get());
      MeshForegroundService_MembersInjector.injectConnectionManager(instance, singletonCImpl.bleConnectionManagerProvider.get());
      MeshForegroundService_MembersInjector.injectGatewayManager(instance, singletonCImpl.gatewayManagerProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends MeshApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideDatabaseProvider;

    private Provider<MessageRepository> messageRepositoryProvider;

    private Provider<BloomFilter> provideBloomFilterProvider;

    private Provider<KeyManager> keyManagerProvider;

    private Provider<HlcClock> provideHlcClockProvider;

    private Provider<ApiService> provideApiServiceProvider;

    private Provider<GatewayManager> gatewayManagerProvider;

    private Provider<BleAdvertiser> bleAdvertiserProvider;

    private Provider<PeerRepository> peerRepositoryProvider;

    private Provider<BleScanner> bleScannerProvider;

    private Provider<InProgressRepository> inProgressRepositoryProvider;

    private Provider<SyncManager> syncManagerProvider;

    private Provider<RateLimiter> provideRateLimiterProvider;

    private Provider<BleConnectionManager> bleConnectionManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(Collections.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>emptyMap());
    }

    private MessageDao messageDao() {
      return DatabaseModule_ProvideMessageDaoFactory.provideMessageDao(provideDatabaseProvider.get());
    }

    private PeerDao peerDao() {
      return DatabaseModule_ProvidePeerDaoFactory.providePeerDao(provideDatabaseProvider.get());
    }

    private InProgressDao inProgressDao() {
      return DatabaseModule_ProvideInProgressDaoFactory.provideInProgressDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 1));
      this.messageRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<MessageRepository>(singletonCImpl, 0));
      this.provideBloomFilterProvider = DoubleCheck.provider(new SwitchingProvider<BloomFilter>(singletonCImpl, 2));
      this.keyManagerProvider = DoubleCheck.provider(new SwitchingProvider<KeyManager>(singletonCImpl, 3));
      this.provideHlcClockProvider = DoubleCheck.provider(new SwitchingProvider<HlcClock>(singletonCImpl, 4));
      this.provideApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<ApiService>(singletonCImpl, 7));
      this.gatewayManagerProvider = DoubleCheck.provider(new SwitchingProvider<GatewayManager>(singletonCImpl, 6));
      this.bleAdvertiserProvider = DoubleCheck.provider(new SwitchingProvider<BleAdvertiser>(singletonCImpl, 5));
      this.peerRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PeerRepository>(singletonCImpl, 8));
      this.bleScannerProvider = DoubleCheck.provider(new SwitchingProvider<BleScanner>(singletonCImpl, 9));
      this.inProgressRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<InProgressRepository>(singletonCImpl, 12));
      this.syncManagerProvider = DoubleCheck.provider(new SwitchingProvider<SyncManager>(singletonCImpl, 11));
      this.provideRateLimiterProvider = DoubleCheck.provider(new SwitchingProvider<RateLimiter>(singletonCImpl, 13));
      this.bleConnectionManagerProvider = DoubleCheck.provider(new SwitchingProvider<BleConnectionManager>(singletonCImpl, 10));
    }

    @Override
    public void injectMeshApplication(MeshApplication meshApplication) {
      injectMeshApplication2(meshApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private MeshApplication injectMeshApplication2(MeshApplication instance) {
      MeshApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      MeshApplication_MembersInjector.injectMessageRepository(instance, messageRepositoryProvider.get());
      MeshApplication_MembersInjector.injectBloomFilter(instance, provideBloomFilterProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.mesh.app.data.repository.MessageRepository 
          return (T) new MessageRepository(singletonCImpl.messageDao());

          case 1: // com.mesh.app.data.local.db.AppDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.mesh.app.core.protocol.BloomFilter 
          return (T) AppModule_ProvideBloomFilterFactory.provideBloomFilter();

          case 3: // com.mesh.app.core.identity.KeyManager 
          return (T) new KeyManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 4: // com.mesh.app.core.protocol.HlcClock 
          return (T) AppModule_ProvideHlcClockFactory.provideHlcClock(singletonCImpl.keyManagerProvider.get());

          case 5: // com.mesh.app.ble.BleAdvertiser 
          return (T) new BleAdvertiser(singletonCImpl.keyManagerProvider.get(), singletonCImpl.provideBloomFilterProvider.get(), singletonCImpl.gatewayManagerProvider.get());

          case 6: // com.mesh.app.gateway.GatewayManager 
          return (T) new GatewayManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.messageRepositoryProvider.get(), singletonCImpl.provideApiServiceProvider.get());

          case 7: // com.mesh.app.gateway.ApiService 
          return (T) NetworkModule_ProvideApiServiceFactory.provideApiService();

          case 8: // com.mesh.app.data.repository.PeerRepository 
          return (T) new PeerRepository(singletonCImpl.peerDao());

          case 9: // com.mesh.app.ble.BleScanner 
          return (T) new BleScanner();

          case 10: // com.mesh.app.ble.BleConnectionManager 
          return (T) new BleConnectionManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.bleScannerProvider.get(), singletonCImpl.syncManagerProvider.get(), singletonCImpl.keyManagerProvider.get(), singletonCImpl.provideBloomFilterProvider.get(), singletonCImpl.peerRepositoryProvider.get(), singletonCImpl.provideRateLimiterProvider.get());

          case 11: // com.mesh.app.core.sync.SyncManager 
          return (T) new SyncManager(singletonCImpl.messageRepositoryProvider.get(), singletonCImpl.inProgressRepositoryProvider.get(), singletonCImpl.provideBloomFilterProvider.get(), singletonCImpl.bleAdvertiserProvider.get());

          case 12: // com.mesh.app.data.repository.InProgressRepository 
          return (T) new InProgressRepository(singletonCImpl.inProgressDao());

          case 13: // com.mesh.app.core.security.RateLimiter 
          return (T) AppModule_ProvideRateLimiterFactory.provideRateLimiter();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
