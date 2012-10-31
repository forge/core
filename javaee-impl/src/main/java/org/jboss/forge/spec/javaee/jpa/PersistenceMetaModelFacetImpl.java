/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jpa;

import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.maven.model.Repository;
import org.jboss.forge.maven.MavenPluginFacet;
import org.jboss.forge.maven.plugins.Configuration;
import org.jboss.forge.maven.plugins.ConfigurationBuilder;
import org.jboss.forge.maven.plugins.ConfigurationElement;
import org.jboss.forge.maven.plugins.ExecutionBuilder;
import org.jboss.forge.maven.plugins.MavenPlugin;
import org.jboss.forge.maven.plugins.MavenPluginBuilder;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyQueryBuilder;
import org.jboss.forge.project.dependencies.NonSnapshotDependencyFilter;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.PersistenceMetaModelFacet;
import org.jboss.forge.spec.javaee.jpa.api.JPAProvider;
import org.jboss.forge.spec.javaee.jpa.api.MetaModelProvider;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceProvider;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;

@Alias("forge.spec.jpa.metamodel")
@RequiresFacet({ PersistenceFacet.class, MavenPluginFacet.class })
public class PersistenceMetaModelFacetImpl extends BaseFacet implements PersistenceMetaModelFacet
{

   @Inject
   private BeanManager manager;

   @Inject
   private ShellPrompt prompt;

   @Override
   public boolean install()
   {
      MetaModelProvider provider = lookupProvider();
      addProcessorPlugin(provider);
      modifyCompilerPlugin();
      addPluginRepository(provider);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return processorConfigured(lookupProvider());
   }

   @Override
   public String getProcessor()
   {
      return lookupProvider().getProcessor();
   }

   @Override
   public String getCompilerArgs()
   {
      return lookupProvider().getCompilerArguments();
   }

   @Override
   public Dependency getProcessorDependency()
   {
      return lookupProvider().getAptDependency();
   }

   private MetaModelProvider lookupProvider()
   {
      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      String providerName = config.listUnits().size() > 0 ? config.listUnits().get(0).getProvider() : null;
      for (JPAProvider jpaProvider : JPAProvider.values())
      {
         PersistenceProvider candidate = jpaProvider.getProvider(manager);
         if (candidate.getProvider().equals(providerName))
         {
            return candidate.getMetaModelProvider();
         }
      }
      return JPAProvider.HIBERNATE.getProvider(manager).getMetaModelProvider();
   }

   private void addProcessorPlugin(MetaModelProvider provider)
   {
      DependencyBuilder processorDependency = createProcessorDependency();
      Dependency versioned = promptVersion(processorDependency);

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

      Dependency aptDependency = provider.getAptDependency();
      if (Strings.isNullOrEmpty(aptDependency.getVersion()))
      {
         aptDependency = promptVersion(aptDependency);
      }
      MavenPluginBuilder processorPlugin = MavenPluginBuilder.create()
               .setDependency(versioned)
               .addExecution(execution)
               .addPluginDependency(aptDependency);

      project.getFacet(MavenPluginFacet.class).addPlugin(processorPlugin);
      // FORGE-700
      project.getFacet(DependencyFacet.class).addDirectDependency(
               DependencyBuilder.create(aptDependency).setScopeType("provided"));
   }

   private DependencyBuilder createProcessorDependency()
   {
      DependencyBuilder processorDependency = DependencyBuilder.create()
               .setGroupId("org.bsc.maven")
               .setArtifactId("maven-processor-plugin");
      return processorDependency;
   }

   private void modifyCompilerPlugin()
   {
      Dependency compilerDependency = DependencyBuilder.create()
               .setGroupId("org.apache.maven.plugins")
               .setArtifactId("maven-compiler-plugin");
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
      MavenPlugin compiler = pluginFacet.getPlugin(compilerDependency);
      Configuration config = compiler.getConfig();
      if (!config.hasConfigurationElement("proc"))
      {
         ConfigurationElement proc = ConfigurationBuilder.create().createConfigurationElement("proc").setText("none");
         config.addConfigurationElement(proc);
      }
      pluginFacet.updatePlugin(compiler);
   }

   private boolean processorConfigured(MetaModelProvider provider)
   {
      DependencyBuilder dependency = createProcessorDependency().setVersion(null);
      MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
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
      Repository repository = provider.getAptPluginRepository();
      if (repository != null)
      {
         MavenPluginFacet pluginFacet = project.getFacet(MavenPluginFacet.class);
         pluginFacet.addPluginRepository(repository.getName(), repository.getUrl());
      }
   }

   private Dependency promptVersion(Dependency dependency)
   {
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);
      Dependency result = dependency;
      List<Dependency> versions = dependencyFacet.resolveAvailableVersions(DependencyQueryBuilder.create(dependency)
               .setFilter(new NonSnapshotDependencyFilter()));
      if (versions.size() > 0)
      {
         Dependency deflt = versions.get(versions.size() - 1);
         result = prompt.promptChoiceTyped("Use which version of '" + dependency.getArtifactId()
                  + "' ?", versions, deflt);
      }
      return result;
   }

}
