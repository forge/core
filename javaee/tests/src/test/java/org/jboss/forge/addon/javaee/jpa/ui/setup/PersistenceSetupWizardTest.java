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
import org.jboss.forge.addon.javaee.jpa.PersistenceFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceMetaModelFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.javaee.jpa.containers.CustomJTAContainer;
import org.jboss.forge.addon.javaee.jpa.containers.JBossEAP6Container;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateMetaModelProvider;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.wizard.UIWizard;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.ui.test.WizardListener;
import org.jboss.forge.ui.test.WizardTester;
import org.jboss.shrinkwrap.api.ShrinkWrap;
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
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );
   }

   @Inject
   private HibernateProvider defaultProvider;

   @Inject
   private CustomJTAContainer customJTAProvider;

   @Inject
   private JBossEAP6Container eap6Container;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private WizardTester<PersistenceSetupWizard> tester;
   @Inject
   private WizardTester<PersistenceSetupWizard> tester2;

   @Test
   public void testSetup() throws Exception
   {
      // Execute SUT
      final Project project = projectFactory.createTempProject();
      tester.setInitialSelection(project.getProjectRoot());

      // Launch
      tester.launch();

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
      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnit<PersistenceDescriptor>> allUnits = config.getAllPersistenceUnit();
      PersistenceUnit<PersistenceDescriptor> unit = allUnits.get(0);

      Assert.assertEquals("java:jboss:jta-ds", unit.getJtaDataSource());
   }

   @Test
   public void testSetupDuplicateUnitName() throws Exception
   {
      // Execute SUT
      final Project project = projectFactory.createTempProject();
      tester.setInitialSelection(project.getProjectRoot());

      // Launch
      tester.launch();

      Assert.assertFalse(tester.canFlipToPreviousPage());
      // Setting UI values
      tester.setValueFor("providers", defaultProvider);
      tester.setValueFor("containers", eap6Container);
      Assert.assertTrue(tester.canFlipToNextPage());

      String result = tester.next();
      Assert.assertNull(result);

      tester.finish(new WizardListener()
      {
         @Override
         public void wizardExecuted(UIWizard wizard, Result result)
         {
            Assert.assertFalse(result instanceof Failed);
         }
      });

      // Check SUT values
      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnit<PersistenceDescriptor>> allUnits = config.getAllPersistenceUnit();
      PersistenceUnit<PersistenceDescriptor> unit = allUnits.get(0);
      Assert.assertEquals(PersistenceOperations.DEFAULT_UNIT_NAME, unit.getName());

      tester2.setInitialSelection(project.getProjectRoot());

      // Launch
      tester2.launch();

      Assert.assertFalse(tester2.canFlipToPreviousPage());
      // Setting UI values
      tester2.setValueFor("providers", defaultProvider);
      tester2.setValueFor("containers", eap6Container);
      Assert.assertTrue(tester2.canFlipToNextPage());

      result = tester2.next();
      Assert.assertNull(result);

      tester2.finish(new WizardListener()
      {
         @Override
         public void wizardExecuted(UIWizard wizard, Result result)
         {
            Assert.assertFalse(result instanceof Failed);
         }
      });

      // Check SUT values
      config = project.getFacet(PersistenceFacet.class).getConfig();
      allUnits = config.getAllPersistenceUnit();
      unit = allUnits.get(0);
      Assert.assertEquals(PersistenceOperations.DEFAULT_UNIT_NAME, unit.getName());
      unit = allUnits.get(1);
      Assert.assertEquals(PersistenceOperations.DEFAULT_UNIT_NAME + "-1", unit.getName());
   }

   @Test
   public void testSetupMetadata() throws Exception
   {
      // Execute SUT
      final Project project = projectFactory.createTempProject();
      tester.setInitialSelection(project.getProjectRoot());

      // Launch
      tester.launch();

      Assert.assertFalse(tester.canFlipToPreviousPage());
      // Setting UI values
      tester.setValueFor("providers", defaultProvider);
      tester.setValueFor("containers", customJTAProvider);
      tester.setValueFor("configureMetadata", Boolean.TRUE);
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
      PersistenceDescriptor config = project.getFacet(PersistenceFacet.class).getConfig();
      List<PersistenceUnit<PersistenceDescriptor>> allUnits = config.getAllPersistenceUnit();
      PersistenceUnit<PersistenceDescriptor> unit = allUnits.get(0);

      Assert.assertEquals("java:jboss:jta-ds", unit.getJtaDataSource());

      Assert.assertTrue(project.hasFacet(PersistenceMetaModelFacet.class));
      PersistenceMetaModelFacet facet = project.getFacet(PersistenceMetaModelFacet.class);
      Assert.assertEquals(new HibernateMetaModelProvider().getProcessor(), facet.getProcessor());
   }
}
