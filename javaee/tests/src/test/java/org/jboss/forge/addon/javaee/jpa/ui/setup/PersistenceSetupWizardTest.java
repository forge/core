/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui.setup;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.javaee.JavaEETestHelper;
import org.jboss.forge.addon.javaee.facets.PersistenceFacet;
import org.jboss.forge.addon.javaee.jpa.containers.CustomJTAContainer;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.context.AbstractUIContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence20.PersistenceUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class PersistenceSetupWizardTest
{
   @Deployment
   @Dependencies({
            @Addon(name = "org.jboss.forge.addon:ui", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:javaee", version = "2.0.0-SNAPSHOT"),
            @Addon(name = "org.jboss.forge.addon:maven", version = "2.0.0-SNAPSHOT")
   })
   public static ForgeArchive getDeployment()
   {
      return JavaEETestHelper.getDeployment().addClass(UISelectionImpl.class).addAsAddonDependencies(
               AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT")));
   }

   @Inject
   private PersistenceSetupWizard wizard;

   @Inject
   private PersistenceSetupConnectionStep connectionStep;

   @Inject
   private AddonRegistry addonRegistry;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Test
   public void testNewEntity() throws Exception
   {
      // Execute SUT
      final Project project = projectFactory.createTempProject();

      // TODO: Improve this
      final UIContext context = new AbstractUIContext()
      {
         @SuppressWarnings({ "unchecked", "rawtypes" })
         @Override
         public UISelectionImpl<?> getInitialSelection()
         {
            List list = Arrays.asList(project.getProjectRoot());
            return new UISelectionImpl(list);
         }
      };

      final UIBuilder builder = new UIBuilder()
      {

         @Override
         public UIContext getUIContext()
         {
            return context;
         }

         @Override
         public UIBuilder add(InputComponent<?, ?> input)
         {
            return this;
         }
      };
      wizard.initializeUI(builder);

      // Setting UI values
      wizard.getProviders().setValue(addonRegistry.getExportedInstance(HibernateProvider.class).get());
      wizard.getContainers().setValue(addonRegistry.getExportedInstance(CustomJTAContainer.class).get());
      wizard.next(context);

      connectionStep.initializeUI(builder);
      connectionStep.getDataSourceName().setValue("java:jboss:jta-ds");

      wizard.execute(context);
      connectionStep.execute(context);

      // Check SUT values
      PersistenceDescriptor config = facetFactory.install(PersistenceFacet.class,project).getConfig();
      List<PersistenceUnit<PersistenceDescriptor>> allUnits = config.getAllPersistenceUnit();
      PersistenceUnit<PersistenceDescriptor> unit = allUnits.get(0);

      Assert.assertEquals("java:jboss:jta-ds", unit.getJtaDataSource());
   }
}
