package org.jboss.forge.addon.maven.projects.plexus;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Provider;

import org.codehaus.plexus.ContainerConfiguration;
import org.codehaus.plexus.DefaultContainerConfiguration;
import org.codehaus.plexus.MutablePlexusContainer;
import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.codehaus.plexus.component.repository.ComponentDescriptor;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextMapAdapter;
import org.codehaus.plexus.context.DefaultContext;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;
import org.eclipse.sisu.inject.DefaultBeanLocator;
import org.eclipse.sisu.inject.DefaultRankingFunction;
import org.eclipse.sisu.inject.DeferredClass;
import org.eclipse.sisu.inject.DeferredProvider;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.inject.RankingFunction;
import org.eclipse.sisu.plexus.ClassRealmUtils;
import org.eclipse.sisu.plexus.ComponentDescriptorBeanModule;
import org.eclipse.sisu.plexus.DefaultPlexusBeanLocator;
import org.eclipse.sisu.plexus.Hints;
import org.eclipse.sisu.plexus.PlexusAnnotatedBeanModule;
import org.eclipse.sisu.plexus.PlexusBean;
import org.eclipse.sisu.plexus.PlexusBeanConverter;
import org.eclipse.sisu.plexus.PlexusBeanLocator;
import org.eclipse.sisu.plexus.PlexusBeanManager;
import org.eclipse.sisu.plexus.PlexusBeanModule;
import org.eclipse.sisu.plexus.PlexusBindingModule;
import org.eclipse.sisu.plexus.PlexusDateTypeConverter;
import org.eclipse.sisu.plexus.PlexusLifecycleManager;
import org.eclipse.sisu.plexus.PlexusXmlBeanConverter;
import org.eclipse.sisu.plexus.PlexusXmlBeanModule;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.LoadedClass;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.EntryListAdapter;
import org.eclipse.sisu.wire.EntryMapAdapter;
import org.eclipse.sisu.wire.MergedModule;
import org.eclipse.sisu.wire.ParameterKeys;
import org.eclipse.sisu.wire.WireModule;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Providers;

/**
 * {@link PlexusContainer} shim that delegates to a Plexus-aware Guice {@link Injector}.
 */
@SuppressWarnings( { "unchecked", "rawtypes" } )
public final class DefaultPlexusContainer
    implements MutablePlexusContainer
{
    // ----------------------------------------------------------------------
    // Static initialization
    // ----------------------------------------------------------------------

    static
    {
        System.setProperty( "guice.disable.misplaced.annotation.check", "true" );
    }

    // ----------------------------------------------------------------------
    // Constants
    // ----------------------------------------------------------------------

    private static final String DEFAULT_REALM_NAME = "plexus.core";

    private static final Module[] NO_CUSTOM_MODULES = {};

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    final Set<String> realmIds = new HashSet<String>();

    final AtomicInteger plexusRank = new AtomicInteger();

    final Map<ClassRealm, List<ComponentDescriptor<?>>> descriptorMap =
        new IdentityHashMap<ClassRealm, List<ComponentDescriptor<?>>>();

    ClassRealm lookupRealm;

    final LoggerManagerProvider loggerManagerProvider = new LoggerManagerProvider();

    final MutableBeanLocator qualifiedBeanLocator = new DefaultBeanLocator();

    final Context context;

    final Map<?, ?> variables;

    final ClassRealm containerRealm;

    final PlexusBeanLocator plexusBeanLocator;

    final PlexusBeanManager plexusBeanManager;

    private final String componentVisibility;

    private final boolean isAutoWiringEnabled;

    private final BeanScanning scanning;

    private final Module containerModule = new ContainerModule();

    private final Module defaultsModule = new DefaultsModule();

    private LoggerManager loggerManager = new ConsoleLoggerManager();

    private Logger logger;

    private boolean disposing;

    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    public DefaultPlexusContainer()
        throws PlexusContainerException
    {
        this( new DefaultContainerConfiguration() );
    }

    public DefaultPlexusContainer( final ContainerConfiguration configuration )
        throws PlexusContainerException
    {
        this( configuration, NO_CUSTOM_MODULES );
    }

    @SuppressWarnings( "finally" )
    public DefaultPlexusContainer( final ContainerConfiguration configuration, final Module... customModules )
        throws PlexusContainerException
    {
        final URL plexusXml = lookupPlexusXml( configuration );

        context = getContextComponent( configuration );
        context.put( PlexusConstants.PLEXUS_KEY, this );
        variables = new ContextMapAdapter( context );

        containerRealm = lookupContainerRealm( configuration );

        componentVisibility = configuration.getComponentVisibility();
        isAutoWiringEnabled = configuration.getAutoWiring();

        scanning = parseScanningOption( configuration.getClassPathScanning() );

        plexusBeanLocator = new DefaultPlexusBeanLocator( qualifiedBeanLocator, componentVisibility );
        plexusBeanManager = new PlexusLifecycleManager( Providers.of( context ), loggerManagerProvider, //
                                                        new SLF4JLoggerFactoryProvider() ); // SLF4J (optional)

        realmIds.add( containerRealm.getId() );
        setLookupRealm( containerRealm );

        final List<PlexusBeanModule> beanModules = new ArrayList<PlexusBeanModule>();

        final ClassSpace space = new URLClassSpace( containerRealm );
        beanModules.add( new PlexusXmlBeanModule( space, variables, plexusXml ) );
        final BeanScanning global = BeanScanning.INDEX == scanning ? BeanScanning.GLOBAL_INDEX : scanning;
        beanModules.add( new PlexusAnnotatedBeanModule( space, variables, global ) );

        try
        {
            addPlexusInjector( beanModules, new BootModule( customModules ) );
        }
        catch ( final RuntimeException e )
        {
            try
            {
                dispose(); // cleanup as much as possible
            }
            finally
            {
                throw e; // always report original failure
            }
        }
    }

    // ----------------------------------------------------------------------
    // Context methods
    // ----------------------------------------------------------------------

    @Override
   public Context getContext()
    {
        return context;
    }

    // ----------------------------------------------------------------------
    // Lookup methods
    // ----------------------------------------------------------------------

    @Override
   public Object lookup( final String role )
        throws ComponentLookupException
    {
        return lookup( role, "" );
    }

    @Override
   public Object lookup( final String role, final String hint )
        throws ComponentLookupException
    {
        return lookup( null, role, hint );
    }

    @Override
   public <T> T lookup( final Class<T> role )
        throws ComponentLookupException
    {
        return lookup( role, "" );
    }

    @Override
   public <T> T lookup( final Class<T> role, final String hint )
        throws ComponentLookupException
    {
        return lookup( role, null, hint );
    }

    @Override
   public <T> T lookup( final Class<T> type, final String role, final String hint )
        throws ComponentLookupException
    {
        try
        {
            return locate( role, type, hint ).iterator().next().getValue();
        }
        catch ( final RuntimeException e )
        {
            throw new ComponentLookupException( e, null != type ? type.getName() : role, hint );
        }
    }

    @Override
   public List<Object> lookupList( final String role )
        throws ComponentLookupException
    {
        return new EntryListAdapter<String, Object>( locate( role, null ) );
    }

    @Override
   public <T> List<T> lookupList( final Class<T> role )
        throws ComponentLookupException
    {
        return new EntryListAdapter<String, T>( locate( null, role ) );
    }

    @Override
   public Map<String, Object> lookupMap( final String role )
        throws ComponentLookupException
    {
        return new EntryMapAdapter<String, Object>( locate( role, null ) );
    }

    @Override
   public <T> Map<String, T> lookupMap( final Class<T> role )
        throws ComponentLookupException
    {
        return new EntryMapAdapter<String, T>( locate( null, role ) );
    }

    // ----------------------------------------------------------------------
    // Query methods
    // ----------------------------------------------------------------------

    @Override
   public boolean hasComponent( final String role )
    {
        return hasComponent( role, "" );
    }

    @Override
   public boolean hasComponent( final String role, final String hint )
    {
        return hasComponent( null, role, hint );
    }

    @Override
   public boolean hasComponent( final Class role )
    {
        return hasComponent( role, "" );
    }

    @Override
   public boolean hasComponent( final Class role, final String hint )
    {
        return hasComponent( role, null, hint );
    }

    @Override
   public boolean hasComponent( final Class type, final String role, final String hint )
    {
        return hasPlexusBeans( locate( role, type, hint ) );
    }

    // ----------------------------------------------------------------------
    // Component descriptor methods
    // ----------------------------------------------------------------------

    @Override
   public void addComponent( final Object component, final String role )
    {
        try
        {
            addComponent( component, component.getClass().getClassLoader().loadClass( role ), Hints.DEFAULT_HINT );
        }
        catch ( final ClassNotFoundException e )
        {
            throw new TypeNotPresentException( role, e );
        }
    }

    @Override
   @SuppressWarnings( "deprecation" )
    public <T> void addComponent( final T component, final Class<?> role, final String hint )
    {
        // this is only used in Maven3 tests, so keep it simple...
        qualifiedBeanLocator.add( Guice.createInjector( new Module()
        {
            @Override
            public void configure( final Binder binder )
            {
                if ( Hints.isDefaultHint( hint ) )
                {
                    binder.bind( (Class) role ).toInstance( component );
                }
                else
                {
                    binder.bind( (Class) role ).annotatedWith( Names.named( hint ) ).toInstance( component );
                }
            }
        } ), plexusRank.incrementAndGet() );
    }

    @Override
   public <T> void addComponentDescriptor( final ComponentDescriptor<T> descriptor )
    {
        ClassRealm realm = descriptor.getRealm();
        if ( null == realm )
        {
            realm = containerRealm;
            descriptor.setRealm( realm );
        }
        synchronized ( descriptorMap )
        {
            List<ComponentDescriptor<?>> descriptors = descriptorMap.get( realm );
            if ( null == descriptors )
            {
                descriptors = new ArrayList<ComponentDescriptor<?>>();
                descriptorMap.put( realm, descriptors );
            }
            descriptors.add( descriptor );
        }
        if ( containerRealm == realm )
        {
            discoverComponents( containerRealm ); // for Maven3 testing
        }
    }

    @Override
   public ComponentDescriptor<?> getComponentDescriptor( final String role, final String hint )
    {
        return getComponentDescriptor( null, role, hint );
    }

    @Override
   public <T> ComponentDescriptor<T> getComponentDescriptor( final Class<T> type, final String role, final String hint )
    {
        final Iterator<PlexusBean<T>> i = locate( role, type, hint ).iterator();
        if ( i.hasNext() )
        {
            final PlexusBean<T> bean = i.next();
            if ( bean.getImplementationClass() != null )
            {
                return newComponentDescriptor( role, bean );
            }
        }
        return null;
    }

    @Override
   public List getComponentDescriptorList( final String role )
    {
        return getComponentDescriptorList( null, role );
    }

    @Override
   public <T> List<ComponentDescriptor<T>> getComponentDescriptorList( final Class<T> type, final String role )
    {
        final List<ComponentDescriptor<T>> tempList = new ArrayList<ComponentDescriptor<T>>();
        for ( final PlexusBean<T> bean : locate( role, type ) )
        {
            tempList.add( newComponentDescriptor( role, bean ) );
        }
        return tempList;
    }

    @Override
   public Map getComponentDescriptorMap( final String role )
    {
        return getComponentDescriptorMap( null, role );
    }

    @Override
   public <T> Map<String, ComponentDescriptor<T>> getComponentDescriptorMap( final Class<T> type, final String role )
    {
        final Map<String, ComponentDescriptor<T>> tempMap = new LinkedHashMap<String, ComponentDescriptor<T>>();
        for ( final PlexusBean<T> bean : locate( role, type ) )
        {
            tempMap.put( bean.getKey(), newComponentDescriptor( role, bean ) );
        }
        return tempMap;
    }

    @Override
   public List<ComponentDescriptor<?>> discoverComponents( final ClassRealm realm )
    {
        return discoverComponents( realm, NO_CUSTOM_MODULES );
    }

    public List<ComponentDescriptor<?>> discoverComponents( final ClassRealm realm, final Module... customModules )
    {
        try
        {
            final List<PlexusBeanModule> beanModules = new ArrayList<PlexusBeanModule>();
            synchronized ( descriptorMap )
            {
                final ClassSpace space = new URLClassSpace( realm );
                final List<ComponentDescriptor<?>> descriptors = descriptorMap.remove( realm );
                if ( null != descriptors )
                {
                    beanModules.add( new ComponentDescriptorBeanModule( space, descriptors ) );
                }
                if ( realmIds.add( realm.getId() ) )
                {
                    beanModules.add( new PlexusXmlBeanModule( space, variables ) );
                    final BeanScanning local = BeanScanning.GLOBAL_INDEX == scanning ? BeanScanning.INDEX : scanning;
                    beanModules.add( new PlexusAnnotatedBeanModule( space, variables, local ) );
                }
            }
            if ( !beanModules.isEmpty() )
            {
                addPlexusInjector( beanModules, customModules );
            }
        }
        catch ( final RuntimeException e )
        {
            getLogger().warn( realm.toString(), e );
        }

        return null; // no-one actually seems to use or check the returned component list!
    }

    public void addPlexusInjector( final List<? extends PlexusBeanModule> beanModules, final Module... customModules )
    {
        final List<Module> modules = new ArrayList<Module>();

        modules.add( containerModule );
        Collections.addAll( modules, customModules );
        modules.add( new PlexusBindingModule( plexusBeanManager, beanModules ) );
        modules.add( defaultsModule );

        Guice.createInjector( isAutoWiringEnabled ? new WireModule( modules ) : new MergedModule( modules ) );
    }

    // ----------------------------------------------------------------------
    // Class realm methods
    // ----------------------------------------------------------------------

    @Override
   public ClassWorld getClassWorld()
    {
        return containerRealm.getWorld();
    }

    @Override
   public ClassRealm getContainerRealm()
    {
        return containerRealm;
    }

    @Override
   public ClassRealm setLookupRealm( final ClassRealm realm )
    {
        final ClassRealm oldRealm = lookupRealm;
        lookupRealm = realm ;
        return oldRealm;
    }

    @Override
   public ClassRealm getLookupRealm()
    {
        return lookupRealm;
    }

    @Override
   public ClassRealm createChildRealm( final String id )
    {
        try
        {
            return containerRealm.createChildRealm( id );
        }
        catch ( final DuplicateRealmException e1 )
        {
            try
            {
                return getClassWorld().getRealm( id );
            }
            catch ( final NoSuchRealmException e2 )
            {
                return null; // should never happen!
            }
        }
    }

    // ----------------------------------------------------------------------
    // Logger methods
    // ----------------------------------------------------------------------

    @Override
   public synchronized LoggerManager getLoggerManager()
    {
        return loggerManager;
    }

    @Override
   @Inject( optional = true )
    public synchronized void setLoggerManager( final LoggerManager loggerManager )
    {
        if ( null != loggerManager )
        {
            this.loggerManager = loggerManager;
        }
        else
        {
            this.loggerManager = new ConsoleLoggerManager();
        }
        logger = null; // refresh our local logger
    }

    @Override
   public synchronized Logger getLogger()
    {
        if ( null == logger )
        {
            logger = loggerManager.getLoggerForComponent( PlexusContainer.class.getName(), null );
        }
        return logger;
    }

    // ----------------------------------------------------------------------
    // Shutdown methods
    // ----------------------------------------------------------------------

    @Override
   public void release( final Object component )
    {
        plexusBeanManager.unmanage( component );
    }

    @Override
   public void releaseAll( final Map<String, ?> components )
    {
        for ( final Object o : components.values() )
        {
            release( o );
        }
    }

    @Override
   public void releaseAll( final List<?> components )
    {
        for ( final Object o : components )
        {
            release( o );
        }
    }

    @Override
   public void dispose()
    {
        disposing = true;

        plexusBeanManager.unmanage();
        containerRealm.setParentRealm( null );
        qualifiedBeanLocator.clear();

        lookupRealm = null;
    }

    // ----------------------------------------------------------------------
    // Implementation methods
    // ----------------------------------------------------------------------

    private static BeanScanning parseScanningOption( final String scanning )
    {
        for ( final BeanScanning option : BeanScanning.values() )
        {
            if ( option.name().equalsIgnoreCase( scanning ) )
            {
                return option;
            }
        }
        return BeanScanning.OFF;
    }

    /**
     * Finds container {@link ClassRealm}, taking existing {@link ClassWorld}s or {@link ClassLoader}s into account.
     * 
     * @param configuration The container configuration
     * @return Container class realm
     */
    private static ClassRealm lookupContainerRealm( final ContainerConfiguration configuration )
        throws PlexusContainerException
    {
        ClassRealm realm = configuration.getRealm();
        if ( null == realm )
        {
            ClassWorld world = configuration.getClassWorld();
            if ( null == world )
            {
                world = new ClassWorld( DEFAULT_REALM_NAME, Thread.currentThread().getContextClassLoader() );
            }
            try
            {
                realm = world.getRealm( DEFAULT_REALM_NAME );
            }
            catch ( final NoSuchRealmException e )
            {
                final Iterator<?> realmIterator = world.getRealms().iterator();
                if ( realmIterator.hasNext() )
                {
                    realm = (ClassRealm) realmIterator.next();
                }
            }
        }
        if ( null == realm )
        {
            throw new PlexusContainerException( "Missing container class realm: " + DEFAULT_REALM_NAME );
        }
        return realm;
    }

    /**
     * Finds container configuration URL, may search the container {@link ClassRealm} and local file-system.
     * 
     * @param configuration The container configuration
     * @return Local or remote URL
     */
    private URL lookupPlexusXml( final ContainerConfiguration configuration )
    {
        URL url = configuration.getContainerConfigurationURL();
        if ( null == url )
        {
            final String configurationPath = configuration.getContainerConfiguration();
            if ( null != configurationPath )
            {
                int index = 0;
                while ( index < configurationPath.length() && configurationPath.charAt( index ) == '/' )
                {
                    index++;
                }

                url = getClass().getClassLoader().getResource( configurationPath.substring( index ) );
                if ( null == url )
                {
                    final File file = new File( configurationPath );
                    if ( file.isFile() )
                    {
                        try
                        {
                            url = file.toURI().toURL();
                        }
                        catch ( final MalformedURLException e ) // NOPMD
                        {
                            // drop through and recover
                        }
                    }
                }
                if ( null == url )
                {
                    getLogger().debug( "Missing container configuration: " + configurationPath );
                }
            }
        }
        return url;
    }

    private static Context getContextComponent( final ContainerConfiguration configuration )
    {
        final Map<?, ?> contextData = configuration.getContext();
        final Context contextComponent = configuration.getContextComponent();
        if ( null == contextComponent )
        {
            return new DefaultContext( contextData );
        }
        if ( null != contextData )
        {
            for ( final Entry<?, ?> entry : contextData.entrySet() )
            {
                contextComponent.put( entry.getKey(), entry.getValue() );
            }
        }
        return contextComponent;
    }

    private <T> Iterable<PlexusBean<T>> locate( final String role, final Class<T> type, final String... hints )
    {
        if ( disposing )
        {
            return Collections.EMPTY_SET;
        }
        final String[] canonicalHints = Hints.canonicalHints( hints );
        if ( null == role || null != type && type.getName().equals( role ) )
        {
            return plexusBeanLocator.locate( TypeLiteral.get( type ), canonicalHints );
        }
        final Set<Class> candidates = new HashSet<Class>();
        for ( final ClassRealm realm : getVisibleRealms() )
        {
            try
            {
                final Class clazz = realm.loadClass( role );
                if ( candidates.add( clazz ) )
                {
                    final Iterable beans = plexusBeanLocator.locate( TypeLiteral.get( clazz ), canonicalHints );
                    if ( hasPlexusBeans( beans ) )
                    {
                        return beans;
                    }
                }
            }
            catch ( final Exception e )
            {
                // drop through...
            }
            catch ( final LinkageError e )
            {
                // drop through...
            }
        }
        return Collections.EMPTY_SET;
    }

    private Collection<ClassRealm> getVisibleRealms()
    {
        final Object[] realms = getClassWorld().getRealms().toArray();
        final Set<ClassRealm> visibleRealms = new LinkedHashSet<ClassRealm>( realms.length );
        final ClassRealm currentLookupRealm = getLookupRealm();
        if ( null != currentLookupRealm )
        {
            visibleRealms.add( currentLookupRealm );
        }
        final ClassRealm threadContextRealm = ClassRealmUtils.contextRealm();
        if ( null != threadContextRealm )
        {
            visibleRealms.add( threadContextRealm );
        }
        if ( PlexusConstants.REALM_VISIBILITY.equalsIgnoreCase( componentVisibility ) )
        {
            final Collection<String> realmNames = ClassRealmUtils.visibleRealmNames( threadContextRealm );
            if ( null != realmNames && realmNames.size() > 0 )
            {
                for ( int i = realms.length - 1; i >= 0; i-- )
                {
                    final ClassRealm r = (ClassRealm) realms[i];
                    if ( realmNames.contains( r.toString() ) )
                    {
                        visibleRealms.add( r );
                    }
                }
                return visibleRealms;
            }
        }
        for ( int i = realms.length - 1; i >= 0; i-- )
        {
            visibleRealms.add( (ClassRealm) realms[i] );
        }
        return visibleRealms;
    }

    private static <T> boolean hasPlexusBeans( final Iterable<PlexusBean<T>> beans )
    {
        final Iterator<PlexusBean<T>> i = beans.iterator();
        return i.hasNext() && i.next().getImplementationClass() != null;
    }

    private static <T> ComponentDescriptor<T> newComponentDescriptor( final String role, final PlexusBean<T> bean )
    {
        final ComponentDescriptor<T> cd = new ComponentDescriptor<T>();
        cd.setRole( role );
        cd.setRoleHint( bean.getKey() );
        cd.setImplementationClass( bean.getImplementationClass() );
        cd.setDescription( bean.getDescription() );
        return cd;
    }

    final class BootModule
        implements Module
    {
        private final Module[] customBootModules;

        BootModule( final Module[] customBootModules )
        {
            this.customBootModules = customBootModules;
        }

        @Override
      public void configure( final Binder binder )
        {
            binder.requestInjection( DefaultPlexusContainer.this );
            for ( final Module m : customBootModules )
            {
                binder.install( m );
            }
        }
    }

    final class ContainerModule
        implements Module
    {
        @Override
      public void configure( final Binder binder )
        {
            binder.bind( Context.class ).toInstance( context );
            binder.bind( ParameterKeys.PROPERTIES ).toInstance( context.getContextData() );

            binder.bind( MutableBeanLocator.class ).toInstance( qualifiedBeanLocator );
            binder.bind( PlexusBeanLocator.class ).toInstance( plexusBeanLocator );
            binder.bind( PlexusBeanManager.class ).toInstance( plexusBeanManager );

            binder.bind( PlexusContainer.class ).to( MutablePlexusContainer.class );
            binder.bind( MutablePlexusContainer.class ).to( DefaultPlexusContainer.class );

            // use provider wrapper to avoid repeated injections later on when configuring plugin injectors
            binder.bind( DefaultPlexusContainer.class ).toProvider( Providers.of( DefaultPlexusContainer.this ) );
        }
    }

    final class DefaultsModule
        implements Module
    {
        private final LoggerProvider loggerProvider = new LoggerProvider();

        private final PlexusDateTypeConverter dateConverter = new PlexusDateTypeConverter();

        private final PlexusXmlBeanConverter beanConverter = new PlexusXmlBeanConverter();

        @Override
      public void configure( final Binder binder )
        {
            binder.bind( LoggerManager.class ).toProvider( loggerManagerProvider );
            binder.bind( Logger.class ).toProvider( loggerProvider );

            // allow plugins to override the default ranking function so we can support component profiles
            final Key<RankingFunction> plexusRankingKey = Key.get( RankingFunction.class, Names.named( "plexus" ) );
            binder.bind( plexusRankingKey ).toInstance( new DefaultRankingFunction( plexusRank.incrementAndGet() ) );
            binder.bind( RankingFunction.class ).to( plexusRankingKey );

            binder.install( dateConverter );

            binder.bind( PlexusBeanConverter.class ).toInstance( beanConverter );
        }
    }

    final class LoggerManagerProvider
        implements DeferredProvider<LoggerManager>
    {
        @Override
      public LoggerManager get()
        {
            return getLoggerManager();
        }

        @Override
      public DeferredClass<LoggerManager> getImplementationClass()
        {
            return new LoadedClass<LoggerManager>( get().getClass() );
        }
    }

    final class LoggerProvider
        implements DeferredProvider<Logger>
    {
        @Override
      public Logger get()
        {
            return getLogger();
        }

        @Override
      public DeferredClass<Logger> getImplementationClass()
        {
            return new LoadedClass<Logger>( get().getClass() );
        }
    }

    final class SLF4JLoggerFactoryProvider
        implements Provider<Object>
    {
        @Override
      public Object get()
        {
            return plexusBeanLocator.locate( TypeLiteral.get( org.slf4j.ILoggerFactory.class ) ).iterator().next().getValue();
        }
    }
}
