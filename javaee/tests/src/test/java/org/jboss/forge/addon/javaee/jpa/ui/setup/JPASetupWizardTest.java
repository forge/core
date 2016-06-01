/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceMetaModelFacet;
import org.jboss.forge.addon.javaee.jpa.PersistenceOperations;
import org.jboss.forge.addon.javaee.jpa.containers.CustomJTAContainer;
import org.jboss.forge.addon.javaee.jpa.containers.JBossEAP6Container;
import org.jboss.forge.addon.javaee.jpa.providers.HibernateMetaModelProvider;
import org.jboss.forge.addon.javaee.jpa.providers.JavaEEDefaultProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.command.AbstractCommandExecutionListener;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.controller.WizardCommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDependencies;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceUnitCommon;
import org.jboss.shrinkwrap.descriptor.api.persistence.PropertiesCommon;
import org.jboss.shrinkwrap.descriptor.api.persistence21.Property;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class JPASetupWizardTest
{
   @Deployment
   @AddonDependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:maven"),
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap.create(AddonArchive.class).addBeansXML();
   }

   @Inject
   private JavaEEDefaultProvider defaultProvider;

   @Inject
   private CustomJTAContainer customJTAProvider;

   @Inject
   private JBossEAP6Container eap6Container;

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private UITestHarness uiTestHarness;

   private Project project;

   @Before
   public void setUp()
   {
      project = projectFactory.createTempProject();
   }

   @Test
   public void testSetup() throws Exception
   {
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPASetupWizard.class,
               project.getRoot()))
      {
         controller.initialize();

         Assert.assertFalse(controller.canMoveToPreviousStep());
         // Setting UI values
         controller.setValueFor("jpaVersion", "2.1");
         controller.setValueFor("jpaProvider", defaultProvider);
         controller.setValueFor("jpaContainer", customJTAProvider);
         Assert.assertTrue(controller.canMoveToNextStep());

         controller.next().initialize();

         Assert.assertFalse(controller.isValid());
         controller.setValueFor("dataSourceName", "java:jboss:jta-ds");
         Assert.assertTrue(controller.isValid());
         final AtomicInteger counter = new AtomicInteger();
         controller.getContext().addCommandExecutionListener(new AbstractCommandExecutionListener()
         {
            @Override
            public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
            {
               counter.incrementAndGet();
            }
         });
         controller.execute();

         UISelection<Object> selection = controller.getContext().getSelection();

         Assert.assertFalse(selection.isEmpty());
         Assert.assertTrue(selection.get() instanceof FileResource);
         Assert.assertEquals("persistence.xml", ((FileResource) selection.get()).getName());

         // Ensure that the two pages were invoked
         Assert.assertEquals(2, counter.get());
      }

      // Reload to refresh facets.
      project = projectFactory.findProject(project.getRoot());

      // Check SUT values
      PersistenceCommonDescriptor config = (PersistenceCommonDescriptor) project.getFacet(JPAFacet.class).getConfig();
      List<PersistenceUnitCommon> allUnits = config.getAllPersistenceUnit();
      Assert.assertEquals("java:jboss:jta-ds", allUnits.get(0).getJtaDataSource());
      assertDefaultProviderProperties(allUnits.get(0).getOrCreateProperties(), project.getFacet(MetadataFacet.class)
               .getProjectName());
   }

   @Test
   public void testSetupDuplicateUnitName() throws Exception
   {
      // Execute SUT
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPASetupWizard.class,
               project.getRoot()))
      {
         controller.initialize();

         Assert.assertFalse(controller.canMoveToPreviousStep());
         // Setting UI values
         controller.setValueFor("jpaProvider", defaultProvider);
         controller.setValueFor("jpaContainer", eap6Container);
         Assert.assertTrue(controller.canMoveToNextStep());

         controller.next().initialize();

         Result result = controller.execute();
         Assert.assertFalse(result instanceof Failed);

         // Check SUT values
         PersistenceCommonDescriptor config = (PersistenceCommonDescriptor) project.getFacet(JPAFacet.class)
                  .getConfig();
         List<PersistenceUnitCommon> allUnits = config.getAllPersistenceUnit();
         Assert.assertEquals(project.getFacet(MetadataFacet.class).getProjectName()
                  + PersistenceOperations.DEFAULT_UNIT_SUFFIX, allUnits.get(0).getName());
         Assert.assertEquals(1, allUnits.size());

         WizardCommandController tester2 = uiTestHarness.createWizardController(JPASetupWizard.class,
                  project.getRoot());

         // Launch
         tester2.initialize();

         Assert.assertFalse(tester2.canMoveToPreviousStep());
         // Setting UI values
         tester2.setValueFor("jpaProvider", defaultProvider);
         tester2.setValueFor("jpaContainer", eap6Container);
         Assert.assertTrue(tester2.canMoveToNextStep());

         tester2.next().initialize();

         result = tester2.execute();
         Assert.assertFalse(result instanceof Failed);

         config = (PersistenceCommonDescriptor) project.getFacet(JPAFacet.class).getConfig();
         allUnits = config.getAllPersistenceUnit();
         Assert.assertEquals(project.getFacet(MetadataFacet.class).getProjectName()
                  + PersistenceOperations.DEFAULT_UNIT_SUFFIX, allUnits.get(0).getName());
         Assert.assertEquals(project.getFacet(MetadataFacet.class).getProjectName()
                  + PersistenceOperations.DEFAULT_UNIT_SUFFIX + "-1", allUnits.get(1).getName());
         Assert.assertEquals(2, allUnits.size());

         // testing the overwriting of the first persistence unit
         WizardCommandController tester3 = uiTestHarness.createWizardController(JPASetupWizard.class,
                  project.getRoot());
         // Launch
         tester3.initialize();
         Assert.assertFalse(tester3.canMoveToPreviousStep());
         // Setting UI values
         tester3.setValueFor("jpaProvider", defaultProvider);
         tester3.setValueFor("jpaContainer", eap6Container);

         Assert.assertTrue(tester3.canMoveToNextStep());
         tester3.next().initialize();
         tester3.setValueFor("persistenceUnitName", project.getFacet(MetadataFacet.class).getProjectName()
                  + PersistenceOperations.DEFAULT_UNIT_SUFFIX);
         tester3.setValueFor("overwritePersistenceUnit", true);

         result = tester3.execute();
         Assert.assertFalse(result instanceof Failed);

         // Check SUT values
         config = (PersistenceCommonDescriptor) project.getFacet(JPAFacet.class).getConfig();
         allUnits = config.getAllPersistenceUnit();
         Assert.assertEquals(project.getFacet(MetadataFacet.class).getProjectName()
                  + PersistenceOperations.DEFAULT_UNIT_SUFFIX, allUnits.get(0).getName());
         Assert.assertEquals(project.getFacet(MetadataFacet.class).getProjectName()
                  + PersistenceOperations.DEFAULT_UNIT_SUFFIX + "-1", allUnits.get(1).getName());
         Assert.assertEquals(2, allUnits.size());
      }
   }

   @Test
   public void testSetupMetadata() throws Exception
   {
      // Execute SUT
      try (WizardCommandController controller = uiTestHarness.createWizardController(JPASetupWizard.class,
               project.getRoot()))
      {
         controller.initialize();

         Assert.assertFalse(controller.canMoveToPreviousStep());
         // Setting UI values
         controller.setValueFor("jpaProvider", defaultProvider);
         controller.setValueFor("jpaContainer", customJTAProvider);
         controller.setValueFor("configureMetadata", Boolean.TRUE);
         Assert.assertTrue(controller.canMoveToNextStep());

         controller.next().initialize();

         controller.setValueFor("dataSourceName", "java:jboss:jta-ds");
         final AtomicInteger counter = new AtomicInteger();
         controller.getContext().addCommandExecutionListener(new AbstractCommandExecutionListener()
         {
            @Override
            public void postCommandExecuted(UICommand command, UIExecutionContext context, Result result)
            {
               counter.incrementAndGet();
            }
         });
         controller.execute();

         // Ensure that the two pages were invoked
         Assert.assertEquals(2, counter.get());
      }

      // Reload to refresh facets.
      project = projectFactory.findProject(project.getRoot());

      // Check SUT values
      PersistenceCommonDescriptor config = (PersistenceCommonDescriptor) project.getFacet(JPAFacet.class).getConfig();
      List<PersistenceUnitCommon> allUnits = config.getAllPersistenceUnit();

      Assert.assertEquals("java:jboss:jta-ds", allUnits.get(0).getJtaDataSource());

      Assert.assertTrue(project.hasFacet(PersistenceMetaModelFacet.class));
      PersistenceMetaModelFacet facet = project.getFacet(PersistenceMetaModelFacet.class);
      Assert.assertEquals(new HibernateMetaModelProvider().getProcessor(), facet.getProcessor());
   }

   private void assertDefaultProviderProperties(PropertiesCommon puProperties, String projectName)
   {
      assertPropertyValue(puProperties, "javax.persistence.schema-generation.database.action", "drop-and-create");
      assertPropertyValue(puProperties, "javax.persistence.schema-generation.scripts.action", "drop-and-create");
      assertPropertyValue(puProperties, "javax.persistence.schema-generation.scripts.create-target", projectName
               + "Create.ddl");
      assertPropertyValue(puProperties, "javax.persistence.schema-generation.scripts.drop-target", projectName
               + "Drop.ddl");
   }

   private void assertPropertyValue(PropertiesCommon puProperties, String name, String expectedValue)
   {
      List<Property<?>> allProperties = puProperties.getAllProperty();
      for (Property property : allProperties)
      {
         if (property.getName().equals(name))
         {
            Assert.assertEquals(expectedValue, property.getValue());
            return;
         }
      }
      Assert.fail("No property with name " + name + " was defined for this persistence unit.");
   }
}
