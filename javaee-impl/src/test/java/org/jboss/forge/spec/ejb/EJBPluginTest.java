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

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author fiorenzo pizza - fiorenzo.pizza@gmail.com
 * 
 */
@RunWith(Arquillian.class)
public class EJBPluginTest extends AbstractShellTest
{

   @Test
   public void testSetup() throws Exception
   {
      Project project = initializeJavaProject();
      assertFalse(project.hasFacet(EJBFacet.class));
      queueInputLines("");
      getShell().execute("setup ejb");
      assertTrue(project.hasFacet(EJBFacet.class));
   }

   @Test
   public void testNewEJbStateless() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package by.giava --name FlowerEjb --type STATELESS");
      JavaSourceFacet javaClass = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = javaClass.getJavaResource("by.giava.FlowerEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      Assert.assertNotNull(source);
      Assert.assertEquals("by.giava", source.getPackage());
      System.out.println(source);
      assertTrue(source.hasImport(Stateless.class));
   }

   @Test
   public void testNewEJbStateful() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package by.giava --name FlowerEjb --type STATEFUL");
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = java.getJavaResource("by.giava.FlowerEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      Assert.assertNotNull(source);
      System.out.println(source);
      Assert.assertEquals("by.giava", source.getPackage());
   }

   @Test
   public void testNewEJbStatelessAndAddTransactionAttribute()
            throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package by.giava --name FlowerEjb --type STATELESS");
      queueInputLines("", "");
      getShell().execute("ejb add-transaction-attribute --type NOT_SUPPORTED");
      JavaSourceFacet javaClass = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = javaClass.getJavaResource("by.giava.FlowerEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      String content = source.toString();
      System.out.println(content);
      assertTrue(source.hasAnnotation("javax.ejb.TransactionAttribute"));
   }

   @Test
   public void testNewMDB() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("", "", "", "");
      getShell().execute("setup ejb");
      getShell()
               .execute(
                        "ejb new-ejb --package by.giava --name FlowerEjb --type MESSAGEDRIVEN");
      queueInputLines("", "");
      JavaSourceFacet javaClass = project.getFacet(JavaSourceFacet.class);
      JavaResource resource = javaClass.getJavaResource("by.giava.FlowerEjb");
      Assert.assertTrue(resource.exists());
      JavaSource<?> source = resource.getJavaSource();
      String content = source.toString();
      System.out.println(content);
      assertTrue(source.hasAnnotation("javax.ejb.MessageDriven"));
   }
}
