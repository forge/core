/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.events.InstallFacets;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.jpa.api.DatabaseType;
import org.jboss.forge.spec.javaee.jpa.api.JPAContainer;
import org.jboss.forge.spec.javaee.jpa.api.JPADataSource;
import org.jboss.forge.spec.javaee.jpa.api.JPAProvider;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceContainer;
import org.jboss.forge.spec.javaee.jpa.api.PersistenceProvider;
import org.jboss.forge.spec.javaee.jpa.container.JavaEEDefaultContainer;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.PersistenceUnitDef;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("persistence")
@RequiresFacet(JavaSourceFacet.class)
public class PersistencePlugin implements Plugin
{
   public static final String DEFAULT_UNIT_NAME = "forge-default";

   private static final String DEFAULT_UNIT_DESC = "Forge Persistence Unit";

   @Inject
   private Project project;

   @Inject
   private Event<InstallFacets> request;

   @Inject
   private ShellPrompt prompt;

   @Inject
   private BeanManager manager;

   @Command("setup")
   public void setup(
            @Option(name = "provider", required = true) final JPAProvider jpap,
            @Option(name = "provider-version", required = false) final String providerVersion,
            @Option(name = "container", required = true) final JPAContainer jpac,
            @Option(name = "database", defaultValue = "DEFAULT") final DatabaseType databaseType,
            @Option(name = "jndiDataSource") final String jtaDataSource,
            @Option(name = "jdbcDriver") final String jdbcDriver,
            @Option(name = "jdbcURL") final String jdbcURL,
            @Option(name = "jdbcUsername") final String jdbcUsername,
            @Option(name = "jdbcPassword") final String jdbcPassword,
            @Option(name = "named", defaultValue = DEFAULT_UNIT_NAME) final String unitName,
            final PipeOut out)
   {
      installPersistence();
      PersistenceFacet jpa = project.getFacet(PersistenceFacet.class);
      PersistenceDescriptor config = jpa.getConfig();

      PersistenceUnitDef unit = config.persistenceUnit(unitName);

      unit.name(unitName).description(DEFAULT_UNIT_DESC);

      JPADataSource ds = new JPADataSource()
              .setJndiDataSource(jtaDataSource)
              .setDatabaseType(databaseType)
              .setJdbcDriver(jdbcDriver)
              .setDatabaseURL(jdbcURL)
              .setUsername(jdbcUsername)
              .setPassword(jdbcPassword);

      PersistenceContainer container = null;

      if (containerConflictsWithOtherSettings(jpac, ds))
      {
         boolean useContainer = prompt.promptBoolean("You specified a non-custom container, this will overwrite other settings you provided. Do you want to do this?", false);
         if (!useContainer)
         {
            container = setCustomContainer(ds);
         }
      }

      if (container == null)
      {
         container = jpac.getContainer(manager);
      }

      PersistenceProvider provider = jpap.getProvider(manager);

      unit.provider(provider.getProvider());
      container.setupConnection(unit, ds);
      provider.configure(unit, ds);

      jpa.saveConfig(config);

      installAdditionalDependencies(out, container, jpap, provider, providerVersion);
   }

   private boolean containerConflictsWithOtherSettings(JPAContainer jpac, JPADataSource ds)
   {
      return !isCustomerContainer(jpac) && ds.getDatabase() != null;
   }

   private PersistenceContainer setCustomContainer(JPADataSource ds)
   {
      PersistenceContainer container;JPAContainer customContainer;

      if (ds.getJndiDataSource() != null)
      {
         customContainer = JPAContainer.CUSTOM_JTA;
      } else
      {
         customContainer = JPAContainer.CUSTOM_JDBC;
      }

      container = customContainer.getContainer(manager);
      return container;
   }

   private boolean isCustomerContainer(JPAContainer jpac)
   {
      return jpac == JPAContainer.CUSTOM_JTA || jpac == JPAContainer.CUSTOM_NON_JTA || jpac == JPAContainer.CUSTOM_JDBC;
   }

   private void installAdditionalDependencies(final PipeOut out, final PersistenceContainer container,
                                              final JPAProvider jpap, final PersistenceProvider provider,
                                              final String providerVersion)
   {
      DependencyFacet dependencyFacet = project.getFacet(DependencyFacet.class);

      List<Dependency> dependencies = new ArrayList<Dependency>();
      if (!provider.listDependencies().isEmpty()
              && prompt.promptBoolean("The JPA provider [" + jpap
              + "], also supplies extended APIs. Install these as well?", false))
      {
         if (providerVersion != null)
         {
            for (Dependency dependency : provider.listDependencies())
            {
               if (container instanceof JavaEEDefaultContainer)
               {
                  dependency = DependencyBuilder.create(dependency).setScopeType(ScopeType.PROVIDED);
               }
               dependencies.add(DependencyBuilder.create(dependency).setVersion(providerVersion));
            }
         }
         else
         {
            DependencyFacet deps = project.getFacet(DependencyFacet.class);
            for (Dependency dependency : provider.listDependencies())
            {
               List<Dependency> versions = deps.resolveAvailableVersions(dependency);
               if (!versions.isEmpty())
               {
                  Dependency choice = prompt.promptChoiceTyped("Install which version of [" + dependency + "]?",
                           versions, versions.get(versions.size() - 1));

                  if (container instanceof JavaEEDefaultContainer)
                  {
                     choice = DependencyBuilder.create(choice).setScopeType(ScopeType.PROVIDED);
                  }
                  dependencies.add(choice);
               }
               else
               {
                  ShellMessages.info(out, "Could not resolve versions for dependency [" + dependency + "]");
               }
            }
         }

         for (Dependency dependency : dependencies)
         {
            dependencyFacet.addDependency(dependency);
         }
      }
   }

   private void installPersistence()
   {
      if (!project.hasFacet(PersistenceFacet.class))
      {
         request.fire(new InstallFacets(PersistenceFacet.class));
      }
   }
}
