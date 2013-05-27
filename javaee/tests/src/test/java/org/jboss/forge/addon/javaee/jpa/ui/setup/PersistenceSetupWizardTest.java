/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui.setup;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.arquillian.Addon;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.AddonId;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.ui.test.WizardListener;
import org.jboss.forge.ui.test.WizardTester;
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
      return JavaEETestHelper.getDeployment().addAsAddonDependencies(
               AddonDependencyEntry.create(AddonId.from("org.jboss.forge.addon:ui", "2.0.0-SNAPSHOT")));
   }

   @Inject
   private HibernateProvider defaultProvider;

   @Inject
   private CustomJTAContainer customJTAProvider;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private FacetFactory facetFactory;

   @Inject
   private WizardTester<PersistenceSetupWizard> tester;

   @Test
   public void testNewEntity() throws Exception
   {
      // Execute SUT
      final Project project = projectFactory.createTempProject();
      tester.setInitialSelection(project.getProjectRoot());

      Assert.assertFalse(tester.canFlipToPreviousPage());
      // Setting UI values
      tester.setValueFor("providers", defaultProvider);
      tester.setValueFor("containers", customJTAProvider);
      Assert.assertTrue(tester.canFlipToNextPage());

      String result = tester.next();
      Assert.assertNull(result);

      tester.setValueFor("dataSourceName", "java:jboss:jta-ds");
      final AtomicInteger counter = new AtomicInteger();
      tester.finish(new WizardListener()
      {
         @Override
         public void wizardExecuted(UIWizard wizard, Result result)
         {
            counter.incrementAndGet();
         }
      });
      // Ensure that the two pages were invoked
      Assert.assertEquals(2, counter.get());

      // Check SUT values
      PersistenceDescriptor config = facetFactory.install(PersistenceFacet.class, project).getConfig();
      List<PersistenceUnit<PersistenceDescriptor>> allUnits = config.getAllPersistenceUnit();
      PersistenceUnit<PersistenceDescriptor> unit = allUnits.get(0);

      Assert.assertEquals("java:jboss:jta-ds", unit.getJtaDataSource());
   }

}
