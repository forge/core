/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.ejb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.ejb.Stateless;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.MethodHolder;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author fiorenzo pizza - fiorenzo.pizza@gmail.com
 *
 */
@RunWith(Arquillian.class)
public class EJBPluginTest extends SingletonAbstractShellTest
{

   @Before
   public void initializeTest() throws Exception
   {
      initializeJavaProject();
   }

   @Test
   public void testSetup() throws Exception
   {
      assertFalse(getProject().hasFacet(EJBFacet.class));
      queueInputLines("");
      getShell().execute("setup ejb");
      assertTrue(getProject().hasFacet(EJBFacet.class));
      assertTrue(getProject().getFacet(DependencyFacet.class).hasEffectiveDependency(
               DependencyBuilder.create("org.jboss.spec.javax.ejb:jboss-ejb-api_3.1_spec")));
   }

   @Test
   public void testNewEJbStateless() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package com.test --named TestEjb --type STATELESS");
      JavaSourceFacet javaClass = getProject().getFacet(JavaSourceFacet.class);
      JavaResource resource = javaClass.getJavaResource("com.test.TestEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      Assert.assertNotNull(source);
      Assert.assertEquals("com.test", source.getPackage());
      assertTrue(source.hasImport(Stateless.class));
   }

   @Test
   public void testNewEJbStatelessWithoutPackage() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --named TestEjb --type STATELESS");
      JavaSourceFacet javaClass = getProject().getFacet(JavaSourceFacet.class);
      JavaResource resource = javaClass.getJavaResource("com.test.TestEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      Assert.assertNotNull(source);
      Assert.assertEquals("com.test", source.getPackage());
      assertTrue(source.hasImport(Stateless.class));
   }

   @Test
   public void testNewEJbStateful() throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package com.test --named TestEjb --type STATEFUL");
      JavaSourceFacet java = getProject().getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("com.test.TestEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      Assert.assertNotNull(source);
      Assert.assertEquals("com.test", source.getPackage());
   }

   @Test
   public void testNewEJbStatelessAndAddTransactionAttribute()
            throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package com.test --named TestEjb --type STATELESS");
      queueInputLines("", "");
      getShell().execute("ejb add-transaction-attribute --type NOT_SUPPORTED");
      JavaSourceFacet javaClass = getProject().getFacet(JavaSourceFacet.class);
      JavaResource resource = javaClass.getJavaResource("com.test.TestEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      assertTrue(source.hasAnnotation("javax.ejb.TransactionAttribute"));
   }

   @Test
   public void testNewEJbStatelessAndAddTransactionAttributeOnMethod()
            throws Exception
   {
      queueInputLines("", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package com.test --named TestEjb --type STATELESS");
      queueInputLines("", "");
      JavaSourceFacet javaClass = getProject().getFacet(JavaSourceFacet.class);
      JavaResource resource = javaClass.getJavaResource("com.test.TestEjb");
      JavaSource<?> source = resource.getJavaSource();
      assertTrue(source instanceof MethodHolder);
      MethodHolder<?> clazz = ((MethodHolder<?>) source);
      clazz.addMethod("public void test(){}");
      javaClass.saveJavaSource(source);
      getShell().execute(" cd test()::void");
      getShell().execute("ejb add-transaction-attribute --type SUPPORTS");
      Assert.assertTrue(resource.exists());
      assertTrue(resource.getJavaSource().toString().contains("javax.ejb.TransactionAttribute"));
   }

   @Test
   public void testNewMDB() throws Exception
   {
      queueInputLines("", "", "", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package com.test --named TestEjb --type MESSAGEDRIVEN");
      queueInputLines("", "");
      JavaSourceFacet javaClass = getProject().getFacet(JavaSourceFacet.class);
      JavaResource resource = javaClass.getJavaResource("com.test.TestEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      assertTrue(source.hasAnnotation("javax.ejb.MessageDriven"));
   }
}
