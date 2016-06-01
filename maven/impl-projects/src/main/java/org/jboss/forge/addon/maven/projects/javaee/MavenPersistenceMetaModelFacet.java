/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.javaee;

import java.util.List;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.DependencyRepository;
import org.jboss.forge.addon.dependencies.builder.CoordinateBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.dependencies.util.NonSnapshotDependencyFilter;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.MetaModelProvider;
import org.jboss.forge.addon.javaee.jpa.PersistenceMetaModelFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceProvider;
import org.jboss.forge.addon.maven.plugins.Configuration;
import org.jboss.forge.addon.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.addon.maven.plugins.ConfigurationElement;
import org.jboss.forge.addon.maven.plugins.ExecutionBuilder;
import org.jboss.forge.addon.maven.plugins.MavenPlugin;
import org.jboss.forge.addon.maven.plugins.MavenPluginAdapter;
import org.jboss.forge.addon.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.addon.maven.projects.MavenPluginFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.services.Imported;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;

/**
 * Implementation of {@link PersistenceMetaModelFacet}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraints({
         @FacetConstraint(JPAFacet.class),
         @FacetConstraint(MavenPluginFacet.class)
})
public class MavenPersistenceMetaModelFacet extends AbstractFacet<Project>implements PersistenceMetaModelFacet
{
   private MetaModelProvider metaModelProvider;

   @Override
   public boolean install()
   {
      MetaModelProvider provider = getMetaModelProvider();
      addProcessorPlugin(provider);
      modifyCompilerPlugin();
      addPluginRepository(provider);
      return true;
   }

   @Override
   public void setMetaModelProvider(MetaModelProvider provider)
   {
      this.metaModelProvider = provider;
   }

   @Override
   public boolean isInstalled()
   {
      return processorConfigured(getMetaModelProvider());
   }

   public MetaModelProvider getMetaModelProvider()
   {
      if (this.metaModelProvider == null)
      {
         this.metaModelProvider = lookupProvider();
      }
      return this.metaModelProvider;
   }

   @Override
   public String getProcessor()
   {
      return getMetaModelProvider().getProcessor();
   }

   @Override
   public String getCompilerArgs()
   {
      return getMetaModelProvider().getCompilerArguments();
   }

   @Override
   public Coordinate getProcessorCoordinate()
   {
      return getMetaModelProvider().getAptCoordinate();
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private MetaModelProvider lookupProvider()
   {
      PersistenceCommonDescriptor config = (PersistenceCommonDescriptor) getFaceted().getFacet(JPAFacet.class)
               .getConfig();
      List<PersistenceUnitCommon> allPersistenceUnit = config.getAllPersistenceUnit();
      String providerName = allPersistenceUnit.size() > 0 ? allPersistenceUnit.get(0)
               .getProvider() : null;

      Imported<PersistenceProvider> services = SimpleContainer.getServices(getClass().getClassLoader(),
               PersistenceProvider.class);
      for (PersistenceProvider candidate : services)
      {
         try
         {
            if (Strings.compare(candidate.getProvider(), providerName))
            {
               return candidate.getMetaModelProvider();
            }
         }
         finally
         {
            services.release(candidate);
         }
      }
      // return Hibernate
      return services.get().getMetaModelProvider();
   }

   private void addProcessorPlugin(MetaModelProvider provider)
   {
      MavenPluginFacet facet = getFaceted().getFacet(MavenPluginFacet.class);
      CoordinateBuilder processorDependency = createProcessorCoordinate();
      if (facet.hasPlugin(processorDependency))
      {
         return;
      }
      Coordinate versioned = getLatestVersion(processorDependency);

      ConfigurationBuilder configuration = ConfigurationBuilder.create();
      configuration.createConfigurationElement("processors")
               .addChild("processor").setText(provider.getProcessor());
      if (!Strings.isNullOrEmpty(provider.getCompilerArguments()))
      {
         configuration.createConfigurationElement("compilerArguments")
                  .setText(provider.getCompilerArguments());
      }

      ExecutionBuilder execution = ExecutionBuilder.create()
               .setId("process")
               .setPhase("generate-sources")
               .addGoal("process")
               .setConfig(configuration);

      Coordinate aptDependency = provider.getAptCoordinate();
      if (Strings.isNullOrEmpty(aptDependency.getVersion()))
      {
         aptDependency = getLatestVersion(aptDependency);
      }

      final MavenPlugin processorPlugin = MavenPluginBuilder.create()
               .setCoordinate(versioned)
               .addExecution(execution)
               .addPluginDependency(DependencyBuilder.create().setCoordinate(aptDependency));
      facet.addPlugin(processorPlugin);
      // FORGE-700
      getFaceted().getFacet(DependencyFacet.class).addDirectDependency(
               DependencyBuilder.create().setCoordinate(aptDependency).setScopeType("provided"));
   }

   private CoordinateBuilder createProcessorCoordinate()
   {
      return CoordinateBuilder.create()
               .setGroupId("org.bsc.maven")
               .setArtifactId("maven-processor-plugin");
   }

   private void modifyCompilerPlugin()
   {
      Coordinate compilerDependency = CoordinateBuilder.create()
               .setGroupId("org.apache.maven.plugins")
               .setArtifactId("maven-compiler-plugin");
      MavenPluginFacet pluginFacet = getFaceted().getFacet(MavenPluginFacet.class);
      final MavenPluginAdapter compiler;
      if (pluginFacet.hasPlugin(compilerDependency))
      {
         compiler = new MavenPluginAdapter(pluginFacet.getPlugin(compilerDependency));
      }
      else
      {
         compiler = new MavenPluginAdapter(MavenPluginBuilder.create().setCoordinate(compilerDependency));
      }
      Configuration config = compiler.getConfig();
      if (!config.hasConfigurationElement("proc"))
      {
         ConfigurationElement proc = ConfigurationBuilder.create().createConfigurationElement("proc").setText("none");
         config.addConfigurationElement(proc);
         compiler.setConfig(config);
      }
      pluginFacet.updatePlugin(compiler);
   }

   private boolean processorConfigured(MetaModelProvider provider)
   {
      CoordinateBuilder dependency = createProcessorCoordinate().setVersion(null);
      MavenPluginFacet pluginFacet = getFaceted().getFacet(MavenPluginFacet.class);
      if (pluginFacet.hasPlugin(dependency))
      {
         MavenPlugin plugin = pluginFacet.getPlugin(dependency);
         if (plugin.listExecutions().size() > 0)
         {
            Configuration config = plugin.listExecutions().get(0).getConfig();
            if (config.hasConfigurationElement("processors"))
            {
               ConfigurationElement element = config.getConfigurationElement("processors").getChildByName("processor");
               return element.getText().equals(provider.getProcessor());
            }
         }
      }
      return false;
   }

   private void addPluginRepository(MetaModelProvider provider)
   {
      DependencyRepository repository = provider.getAptPluginRepository();
      if (repository != null)
      {
         MavenPluginFacet pluginFacet = getFaceted().getFacet(MavenPluginFacet.class);
         pluginFacet.addPluginRepository(repository.getId(), repository.getUrl());
      }
   }

   private Coordinate getLatestVersion(Coordinate dependency)
   {
      DependencyFacet dependencyFacet = getFaceted().getFacet(DependencyFacet.class);
      Coordinate result = dependency;
      List<Coordinate> versions = dependencyFacet.resolveAvailableVersions(DependencyQueryBuilder.create(dependency)
               .setFilter(new NonSnapshotDependencyFilter()));
      if (versions.size() > 0)
      {
         result = versions.get(versions.size() - 1);
      }
      return result;
   }
}
