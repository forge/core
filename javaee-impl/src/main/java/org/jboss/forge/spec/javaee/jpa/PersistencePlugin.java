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
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
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
import org.jboss.shrinkwrap.descriptor.api.spec.jpa.persistence.Property;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
@Alias("persistence")
@RequiresFacet(JavaSourceFacet.class)
@RequiresProject
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

   @Inject
   private DependencyInstaller installer;

   @DefaultCommand
   public void show(final PipeOut out, @Option(name = "all", shortName = "a") final boolean showAll)
   {
      if (project.hasFacet(PersistenceFacet.class))
      {
         PersistenceFacet jpa = project.getFacet(PersistenceFacet.class);
         PersistenceDescriptor config = jpa.getConfig();

         ShellMessages.info(out, "Displaying current JPA configuration:");

         if (!config.listUnits().isEmpty())
            out.println();

         for (PersistenceUnitDef unit : config.listUnits())
         {
            out.println(out.renderColor(ShellColor.BOLD, "Unit: ") + unit.getName() + "\t"
                     + out.renderColor(ShellColor.BOLD, "transaction-type: ") + unit.getTransactionType());
            out.println("description:\t" + unit.getDescription());
            out.println("provider:\t" + unit.getProvider());
            out.println("jta-data-source:\t" + unit.getJtaDataSource());
            out.println("non-jta-data-source:\t" + unit.getNonJtaDataSource());
            out.println("exclude-unlisted-classes:\t" + !unit.includesUnlistedClasses());
            out.println("shared-cache-mode:\t" + unit.getSharedCacheMode());
            out.println("validation-mode:\t" + unit.getValidationMode());

            if (!unit.getProperties().isEmpty())
            {
               out.println();
               out.println(ShellColor.BOLD, "Properties:");
               for (Property p : unit.getProperties())
               {
                  out.println(p.getName() + ":\t" + p.getValue());
               }
            }

            if (!unit.getClasses().isEmpty() && showAll)
            {
               out.println();
               out.println(ShellColor.BOLD, "Selected Entity Classes:");
               for (String c : unit.getClasses())
               {
                  out.println(c);
               }
            }

            if (!unit.getMappingFiles().isEmpty() && showAll)
            {
               out.println();
               out.println(ShellColor.BOLD, "Mapping Files:");
               for (String f : unit.getMappingFiles())
               {
                  out.println(f);
               }
            }
            out.println();
         }
      }
      else
      {
         ShellMessages.info(out, "JPA is not installed. Use 'setup persistence' to continue.");
      }
   }

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
            @Option(name = "jta", flagOnly = true) final boolean jta,
            @Option(name = "named", defaultValue = DEFAULT_UNIT_NAME) final String unitName,
            final PipeOut out)
   {
      installPersistence();
      PersistenceFacet jpa = project.getFacet(PersistenceFacet.class);
      PersistenceDescriptor config = jpa.getConfig();

      PersistenceUnitDef unit = config.persistenceUnit(unitName);

      unit.name(unitName).description(DEFAULT_UNIT_DESC);

      PersistenceContainer container = jpac.getContainer(manager);
      PersistenceProvider provider = jpap.getProvider(manager);

      JPADataSource ds = new JPADataSource()
               .setJndiDataSource(jtaDataSource)
               .setDatabaseType(databaseType)
               .setJdbcDriver(jdbcDriver)
               .setDatabaseURL(jdbcURL)
               .setUsername(jdbcUsername)
               .setPassword(jdbcPassword)
               .setContainer(container)
               .setProvider(provider);

      unit.transactionType(container.getTransactionType());
      unit.provider(provider.getProvider());
      container.setupConnection(unit, ds);
      provider.configure(unit, ds);

      jpa.saveConfig(config);

      installAdditionalDependencies(out, container, jpap, provider, providerVersion);

      if (project.hasFacet(PersistenceFacet.class))
      {
         ShellMessages.success(out, "Persistence (JPA) is installed.");
      }
   }

   private void installAdditionalDependencies(final PipeOut out, final PersistenceContainer container,
            final JPAProvider jpap, final PersistenceProvider provider,
            final String providerVersion)
   {
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
            installer.install(project, dependency);
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
