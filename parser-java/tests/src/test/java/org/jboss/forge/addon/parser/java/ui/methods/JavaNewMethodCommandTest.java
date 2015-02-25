/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.methods;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.test.UITestHarness;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Visibility;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the {@link org.jboss.forge.addon.parser.java.ui.methods.JavaNewMethodCommand}
 */
@RunWith(Arquillian.class)
public class JavaNewMethodCommandTest
{

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:parser-java"),
            @AddonDeployment(name = "org.jboss.forge.addon:ui-test-harness"),
            @AddonDeployment(name = "org.jboss.forge.addon:projects"),
            @AddonDeployment(name = "org.jboss.forge.addon:maven"),
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-java"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui-test-harness"),
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

   private JavaClassSource targetSuperClass;

   private CommandController commandController;

   @Before
   public void setup() throws Exception
   {
      createTempProject();
   }

   @Test
   public void testGenerateMethodWithDefaults() throws Exception
   {

      createTargetClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;}}");

      createCommandController();
      commandController.initialize();
      String methodName = "methodName";
      setMethodName(methodName);

      commandController.execute();
      reloadTargetClass();

      Assert.assertNotNull(targetClass.getMethod(methodName));
      Assert.assertTrue(targetClass.getMethod(methodName).isPrivate());
      Assert.assertEquals(targetClass.getMethod(methodName).getReturnType().toString(), "String");
      Assert.assertEquals(targetClass.getMethod(methodName).getBody(),
               "throw new UnsupportedOperationException(\"Not supported yet.\");");

   }

   @Test
   public void testGenerateMethodWithNameAndReturn() throws Exception
   {

      createTargetClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setReturnType("int");

      commandController.execute();
      reloadTargetClass();

      Assert.assertNotNull(targetClass.getMethod(methodName));
      Assert.assertTrue(targetClass.getMethod(methodName).isPrivate());
      Assert.assertEquals(targetClass.getMethod(methodName).getReturnType().toString(), "int");
      Assert.assertEquals(targetClass.getMethod(methodName).getBody(),
               "throw new UnsupportedOperationException(\"Not supported yet.\");");

   }

   @Test
   public void testGenerateFieldWithNameAndParameter() throws Exception
   {

      createTargetClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setParameters("int a,String s,int c,int d");

      commandController.execute();
      reloadTargetClass();

      String[] paramTypes = { "int", "String", "int", "int" };

      Assert.assertNotNull(targetClass.getMethod(methodName, paramTypes));
      Assert.assertTrue(targetClass.getMethod(methodName, paramTypes).isPrivate());
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getReturnType().toString(), "String");
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getBody(),
               "throw new UnsupportedOperationException(\"Not supported yet.\");");

   }

   @Test
   public void testGenerateFieldWithNameReturnParameterAndAcess() throws Exception
   {

      createTargetClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setParameters("int a,String s");
      setReturnType("int");
      setAccessType(Visibility.PUBLIC);

      commandController.execute();
      reloadTargetClass();

      String[] paramTypes = { "int", "String" };

      Assert.assertNotNull(targetClass.getMethod(methodName, paramTypes));
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getReturnType().toString(), "int");
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getVisibility(), Visibility.PUBLIC);
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getBody(),
               "throw new UnsupportedOperationException(\"Not supported yet.\");");

   }

   @Test
   public void testGenerateMethodWithDifferentAccessType() throws Exception
   {
      testVisibility(Visibility.PACKAGE_PRIVATE);
      testVisibility(Visibility.PROTECTED);
      testVisibility(Visibility.PUBLIC);
      testVisibility(Visibility.PRIVATE);
   }

   private void testVisibility(Visibility visibility) throws FileNotFoundException, Exception
   {
      createTargetClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setAccessType(visibility);

      commandController.execute();
      reloadTargetClass();

      Assert.assertNotNull(targetClass.getMethod(methodName));
      Assert.assertEquals(targetClass.getMethod(methodName).getVisibility(), visibility);
      Assert.assertEquals(targetClass.getMethod(methodName).getReturnType().toString(), "String");
      Assert.assertEquals(targetClass.getMethod(methodName).getBody(),
               "throw new UnsupportedOperationException(\"Not supported yet.\");");

   }

   @Test
   public void testMethodAlreadyExists() throws Exception
   {
      createTargetClass("public class Test{private String simpleString;public Test setSimpleString(String simple){simpleString = simple;}private String methodName(){int a;}}");

      createCommandController();
      commandController.initialize();

      String name = "methodName";
      setMethodName(name);

      Result result = commandController.execute();

      Assert.assertTrue(result instanceof Failed);
      Assert.assertEquals(result.getMessage(), "Method was already present in the target class");

   }

   @Test
   public void testFinalSuperClassMethodExists() throws Exception
   {
      createTargetClass("public class subTest extends Test {private String simpleString; public subTest setSimpleString(String simple){simpleString=simple;}}");
      createTargetSuperClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;} final String methodName(){int a;}}");

      createCommandController();
      commandController.initialize();

      String name = "methodName";
      setMethodName(name);

      Result result = commandController.execute();

      Assert.assertTrue(result instanceof Failed);
      Assert.assertEquals(result.getMessage(), "Method was already present and was final in the super class");

   }

   @Test
   public void testNotFinalSuperClassMethodExists() throws Exception
   {
      createTargetClass("public class subTest extends Test {private String simpleString; public subTest setSimpleString(String simple){simpleString=simple;}}");
      createTargetSuperClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;} String methodName(int a,String s,int c,int d){int a;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setParameters("int a,String s,int c,int d");

      commandController.execute();
      reloadTargetClass();

      String[] paramTypes = { "int", "String", "int", "int" };

      Assert.assertNotNull(targetClass.getMethod(methodName, paramTypes));
      Assert.assertTrue(targetClass.getMethod(methodName, paramTypes).isPrivate());
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getReturnType().toString(), "String");
      Assert.assertNotNull(targetClass.getMethod(methodName, paramTypes).getAnnotation("Override"));
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getBody(),
               "throw new UnsupportedOperationException(\"Not supported yet.\");");

   }

   @Test
   public void testHigherAccessSuperClassMethodExists() throws Exception
   {
      createTargetClass("public class subTest extends Test {private String simpleString; public subTest setSimpleString(String simple){simpleString=simple;}}");
      createTargetSuperClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;} public String methodName(){int a;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setAccessType(Visibility.PRIVATE);

      Result result = commandController.execute();

      Assert.assertTrue(result instanceof Failed);
      Assert.assertEquals(result.getMessage(), "Method was already present and had higher access in the super class");

   }

   @Test
   public void testLowerAccessSuperClassMethodExists() throws Exception
   {
      createTargetClass("public class subTest extends Test {private String simpleString; public subTest setSimpleString(String simple){simpleString=simple;}}");
      createTargetSuperClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;} String methodName(int a,String s,int c,int d){int a;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setAccessType(Visibility.PUBLIC);
      setParameters("int a,String s,int c,int d");

      commandController.execute();
      reloadTargetClass();

      String[] paramTypes = { "int", "String", "int", "int" };

      Assert.assertNotNull(targetClass.getMethod(methodName, paramTypes));
      Assert.assertTrue(targetClass.getMethod(methodName, paramTypes).isPublic());
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getReturnType().toString(), "String");
      Assert.assertNotNull(targetClass.getMethod(methodName, paramTypes).getAnnotation("Override"));
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getBody(),
               "throw new UnsupportedOperationException(\"Not supported yet.\");");

   }

   @Test
   public void testDifferentReturnSuperClassMethodExists() throws Exception
   {
      createTargetClass("public class subTest extends Test {private String simpleString; public subTest setSimpleString(String simple){simpleString=simple;}}");
      createTargetSuperClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;} public int methodName(){int a;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setReturnType("long");

      Result result = commandController.execute();

      Assert.assertTrue(result instanceof Failed);
      Assert.assertEquals(result.getMessage(),
               "Method was already present and had a different return type in the super class");

   }

   @Test
   public void testPrivateAndFinalSuperClassMethodExists() throws Exception
   {
      createTargetClass("public class subTest extends Test {private String simpleString; public subTest setSimpleString(String simple){simpleString=simple;}}");
      createTargetSuperClass("public class Test{private String simpleString; public Test setSimpleString(String simple){simpleString=simple;} private final String methodName(int a,String s,int c,int d){int a;}}");

      createCommandController();
      commandController.initialize();

      String methodName = "methodName";
      setMethodName(methodName);
      setParameters("int a,String s,int c,int d");

      commandController.execute();
      reloadTargetClass();

      String[] paramTypes = { "int", "String", "int", "int" };

      Assert.assertNotNull(targetClass.getMethod(methodName, paramTypes));
      Assert.assertTrue(targetClass.getMethod(methodName, paramTypes).isPrivate());
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getReturnType().toString(), "String");
      Assert.assertNull(targetClass.getMethod(methodName, paramTypes).getAnnotation("Override"));
      Assert.assertEquals(targetClass.getMethod(methodName, paramTypes).getBody(),
               "throw new UnsupportedOperationException(\"Not supported yet.\");");

   }

   private void createTempProject()
   {
      project = projectFactory.createTempProject();
      facetFactory.install(project, JavaSourceFacet.class);
   }

   private void createTargetClass(String classString) throws FileNotFoundException
   {
      targetClass = Roaster.parse(JavaClassSource.class, classString);
      project.getFacet(JavaSourceFacet.class).saveJavaSource(targetClass);
   }

   private void createTargetSuperClass(String classString) throws FileNotFoundException
   {
      targetSuperClass = Roaster.parse(JavaClassSource.class, classString);
      project.getFacet(JavaSourceFacet.class).saveJavaSource(targetSuperClass);
   }

   private void createCommandController() throws Exception
   {

      commandController = testHarness.createCommandController(JavaNewMethodCommand.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass));
   }

   private void reloadTargetClass() throws FileNotFoundException
   {
      targetClass = Roaster.parse(JavaClassSource.class,
               project.getFacet(JavaSourceFacet.class).getJavaResource(targetClass)
                        .getUnderlyingResourceObject());
   }

   private void setMethodName(String methodName)
   {
      commandController.setValueFor("methodName", methodName);
   }

   private void setReturnType(String returnType)
   {
      commandController.setValueFor("returnType", returnType);
   }

   private void setAccessType(Visibility accessType)
   {
      commandController.setValueFor("accessType", accessType);
   }

   private void setParameters(String parameters)
   {
      commandController.setValueFor("parameters", parameters);

   }

}
