/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.devtools.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class JavaEqualsHashcodeCommandTest
{

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:parser-java"),
            @AddonDeployment(name = "org.jboss.forge.addon:dev-tools-java"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static AddonArchive getDeployment()
   {
      return ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:dev-tools-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness")
               );
   }

   @Inject
   private ProjectFactory projectFactory;

   @Inject
   private UITestHarness testHarness;

   @Inject
   private FacetFactory facetFactory;

   private Project project;

   private JavaClassSource targetClass;

   private CommandController commandController;

   @Before
   public void setup() throws Exception
   {
      project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testNoEqualsAndHashcode() throws Exception
   {
      targetClass = Roaster.parse(JavaClassSource.class, "public class Test{private int id;}");
      project.getFacet(JavaSourceFacet.class).saveJavaSource(targetClass);
      commandController = testHarness.createCommandController(JavaEqualsHashcodeCommand.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass));

      commandController.initialize();
      commandController.setValueFor("targetClass", "Test");
      UISelectMany<String> component = (UISelectMany<String>) commandController.getInputs().get("fields");
      Iterable<String> valueChoices = component.getValueChoices();
      commandController.setValueFor("fields", valueChoices);
      commandController.execute();
      targetClass = Roaster.parse(JavaClassSource.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass)
                        .getUnderlyingResourceObject());

      assertNotNull(targetClass.getField("id"));
      assertTrue(targetClass.getField("id").isPrivate());
      assertNotNull(targetClass.getMethod("equals", Object.class));
      assertNotNull(targetClass.getMethod("hashCode"));
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHashCodeAndEqualsAlreadyInClass() throws Exception
   {
      targetClass = Roaster.parse(JavaClassSource.class, "public class Test{private int id; @Override\n" +
               "   public boolean equals(Object obj)\n" +
               "   {\n" +
               "      return true;\n" +
               "   }\n" +
               "\n" +
               "   @Override\n" +
               "   public int hashCode()\n" +
               "   {\n" +
               "      return 1111;\n" +
               "   }}");
      project.getFacet(JavaSourceFacet.class).saveJavaSource(targetClass);

      commandController = testHarness.createCommandController(JavaEqualsHashcodeCommand.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass));

      commandController.initialize();
      commandController.setValueFor("targetClass", "Test");
      UISelectMany<String> component = (UISelectMany<String>) commandController.getInputs().get("fields");
      Iterable<String> valueChoices = component.getValueChoices();
      commandController.setValueFor("fields", valueChoices);
      commandController.execute();
      targetClass = Roaster.parse(JavaClassSource.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass)
                        .getUnderlyingResourceObject());
      assertNotNull(targetClass.getField("id"));
      assertTrue(targetClass.getField("id").isPrivate());
      MethodSource<JavaClassSource> hashCodeMethod = targetClass.getMethod("hashCode");
      MethodSource<JavaClassSource> equalsMethod = targetClass.getMethod("equals", Object.class);
      assertNotNull(hashCodeMethod);
      assertNotNull(equalsMethod);
      assertEquals("return true;", equalsMethod.getBody());
      assertEquals("return 1111;", hashCodeMethod.getBody());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testHashCodeAlreadyInClass() throws Exception
   {
      targetClass = Roaster.parse(JavaClassSource.class, "public class Test{private int id; @Override\n" +
               "   @Override\n" +
               "   public int hashCode()\n" +
               "   {\n" +
               "      return 1111;\n" +
               "   }}");
      project.getFacet(JavaSourceFacet.class).saveJavaSource(targetClass);

      commandController = testHarness.createCommandController(JavaEqualsHashcodeCommand.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass));

      commandController.initialize();
      commandController.setValueFor("targetClass", "Test");
      UISelectMany<String> component = (UISelectMany<String>) commandController.getInputs().get("fields");
      Iterable<String> valueChoices = component.getValueChoices();
      commandController.setValueFor("fields", valueChoices);
      commandController.execute();
      targetClass = Roaster.parse(JavaClassSource.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass)
                        .getUnderlyingResourceObject());
      assertNotNull(targetClass.getField("id"));
      assertTrue(targetClass.getField("id").isPrivate());
      assertNotNull(targetClass.getMethod("equals", Object.class));
      MethodSource<JavaClassSource> hashCodeMethod = targetClass.getMethod("hashCode");
      assertNotNull(hashCodeMethod);
      String body = hashCodeMethod.getBody();
      assertEquals("return 1111;", body);
   }

   @SuppressWarnings("unchecked")
   @Test
   public void testEqualsAlreadyInClass() throws Exception
   {
      targetClass = Roaster.parse(JavaClassSource.class, "public class Test{private int id; @Override\n" +
               "   public boolean equals(Object obj)\n" +
               "   {\n" +
               "      return true;\n" +
               "   }\n" +
               "\n" +
               "}");
      project.getFacet(JavaSourceFacet.class).saveJavaSource(targetClass);

      commandController = testHarness.createCommandController(JavaEqualsHashcodeCommand.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass));

      commandController.initialize();
      commandController.setValueFor("targetClass", "Test");
      UISelectMany<String> component = (UISelectMany<String>) commandController.getInputs().get("fields");
      Iterable<String> valueChoices = component.getValueChoices();
      commandController.setValueFor("fields", valueChoices);
      commandController.execute();
      targetClass = Roaster.parse(JavaClassSource.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass)
                        .getUnderlyingResourceObject());
      assertNotNull(targetClass.getField("id"));
      assertTrue(targetClass.getField("id").isPrivate());
      assertNotNull(targetClass.getMethod("hashCode"));
      MethodSource<JavaClassSource> equalsMethod = targetClass.getMethod("equals", Object.class);
      assertNotNull(equalsMethod);
      assertEquals("return true;", equalsMethod.getBody());
   }

}