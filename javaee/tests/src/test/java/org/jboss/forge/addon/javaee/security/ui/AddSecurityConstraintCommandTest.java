/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.security.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.security.TransportGuarantee;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_2_5;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddSecurityConstraintCommandTest extends AbstractSecurityCommandTest
{

   @Test
   public void testRequiredFieldsPerSpec() throws Exception
   {
      installServlet(ServletFacet_2_5.class);
      try (CommandController commandController = testHarness
               .createCommandController(AddSecurityConstraintCommand.class, project.getRoot()))
      {
         commandController.initialize();
         assertTrue(commandController.getInput("webResourceName").isRequired());
         assertTrue(commandController.getInput("urlPatterns").isRequired());
      }
   }

   @Test
   public void testAddSecurityConstraintWithTheMinimumConfiguration() throws Exception
   {
      ServletFacet_2_5 servletFacet = installServlet(ServletFacet_2_5.class);

      executeAddSecurityConstraintCommand(Arrays.asList(
               new InputPair("webResourceName", "testResource"),
               new InputPair("urlPatterns", Arrays.asList("/admin/*", "/manager/*"))));

      List<org.jboss.shrinkwrap.descriptor.api.webapp25.SecurityConstraintType<org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor>> securityConstraints = servletFacet
               .getConfig()
               .getAllSecurityConstraint();
      assertEquals(1, securityConstraints.size());
      org.jboss.shrinkwrap.descriptor.api.webapp25.WebResourceCollectionType<org.jboss.shrinkwrap.descriptor.api.webapp25.SecurityConstraintType<org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor>> resourceCollection = securityConstraints
               .get(0).getOrCreateWebResourceCollection();
      assertEquals("testResource", resourceCollection.getWebResourceName());
      assertEquals(2, resourceCollection.getAllUrlPattern().size());
      assertEquals(0, resourceCollection.getAllHttpMethod().size());
      assertEquals(0, resourceCollection.getAllDescription().size());
      assertEquals(0, securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().size());
      assertNull(securityConstraints.get(0).getOrCreateUserDataConstraint().getTransportGuarantee());
   }

   @Test
   public void testAddSecurityConstraintWithAllConfigurations25() throws Exception
   {
      ServletFacet_2_5 servletFacet = installServlet(ServletFacet_2_5.class);

      executeAddSecurityConstraintCommand(Arrays.asList(
               new InputPair("webResourceName", "testResource"),
               new InputPair("urlPatterns", "/admin/*"),
               new InputPair("displayName", "myTestResource"),
               new InputPair("description", "This is a security constraint"),
               new InputPair("httpMethods", Arrays.asList("GET", "POST")),
               new InputPair("securityRoles", Arrays.asList("MANAGER", "ADMIN")),
               new InputPair("enableUserDataConstraint", true),
               new InputPair("transportGuarantee", TransportGuarantee.CONFIDENTIAL)));

      List<org.jboss.shrinkwrap.descriptor.api.webapp25.SecurityConstraintType<org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor>> securityConstraints = servletFacet
               .getConfig()
               .getAllSecurityConstraint();
      org.jboss.shrinkwrap.descriptor.api.webapp25.WebResourceCollectionType<org.jboss.shrinkwrap.descriptor.api.webapp25.SecurityConstraintType<org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor>> resourceCollection = securityConstraints
               .get(0).getOrCreateWebResourceCollection();
      assertEquals("testResource", resourceCollection.getWebResourceName());
      assertEquals("/admin/*", resourceCollection.getAllUrlPattern().get(0));
      assertEquals(2, resourceCollection.getAllHttpMethod().size());
      assertEquals("GET", resourceCollection.getAllHttpMethod().get(0));
      assertEquals("POST", resourceCollection.getAllHttpMethod().get(1));
      assertEquals("This is a security constraint", resourceCollection.getAllDescription().get(0));
      assertEquals(2, securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().size());
      assertEquals("MANAGER", securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().get(0));
      assertEquals("ADMIN", securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().get(1));
      assertEquals("CONFIDENTIAL",
               securityConstraints.get(0).getOrCreateUserDataConstraint().getTransportGuarantee().toString());
   }

   @Test
   public void testAddSecurityConstraintWithAllConfigurations30() throws Exception
   {
      ServletFacet_3_0 servletFacet = installServlet(ServletFacet_3_0.class);

      executeAddSecurityConstraintCommand(Arrays.asList(
               new InputPair("webResourceName", "testResource"), new InputPair("urlPatterns", "/admin/*"),
               new InputPair("displayName", "myTestResource"),
               new InputPair("description", "This is a security constraint"),
               new InputPair("httpMethods", Arrays.asList("GET", "POST")),
               new InputPair("securityRoles", Arrays.asList("MANAGER", "ADMIN")),
               new InputPair("enableUserDataConstraint", true),
               new InputPair("transportGuarantee", TransportGuarantee.CONFIDENTIAL)));

      List<org.jboss.shrinkwrap.descriptor.api.webcommon30.SecurityConstraintType<org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor>> securityConstraints = servletFacet
               .getConfig()
               .getAllSecurityConstraint();
      org.jboss.shrinkwrap.descriptor.api.webcommon30.WebResourceCollectionType<org.jboss.shrinkwrap.descriptor.api.webcommon30.SecurityConstraintType<org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor>> resourceCollection = securityConstraints
               .get(0).getOrCreateWebResourceCollection();
      assertEquals("testResource", resourceCollection.getWebResourceName());
      assertEquals("/admin/*", resourceCollection.getAllUrlPattern().get(0));
      assertEquals(2, resourceCollection.getAllHttpMethod().size());
      assertEquals("GET", resourceCollection.getAllHttpMethod().get(0));
      assertEquals("POST", resourceCollection.getAllHttpMethod().get(1));
      assertEquals("This is a security constraint", resourceCollection.getAllDescription().get(0));
      assertEquals(2, securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().size());
      assertEquals("MANAGER", securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().get(0));
      assertEquals("ADMIN", securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().get(1));
      assertEquals("CONFIDENTIAL",
               securityConstraints.get(0).getOrCreateUserDataConstraint().getTransportGuarantee().toString());
   }

   @Test
   public void testAddSecurityConstraintWithAllConfigurations31() throws Exception
   {
      ServletFacet_3_1 servletFacet = installServlet(ServletFacet_3_1.class);

      executeAddSecurityConstraintCommand(Arrays.asList(
               new InputPair("webResourceName", "testResource"),
               new InputPair("urlPatterns", "/admin/*"),
               new InputPair("displayName", "myTestResource"),
               new InputPair("description", "This is a security constraint"),
               new InputPair("httpMethods", Arrays.asList("GET", "POST")),
               new InputPair("securityRoles", Arrays.asList("MANAGER", "ADMIN")),
               new InputPair("enableUserDataConstraint", true),
               new InputPair("transportGuarantee", TransportGuarantee.CONFIDENTIAL)));

      List<org.jboss.shrinkwrap.descriptor.api.webcommon31.SecurityConstraintType<org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor>> securityConstraints = servletFacet
               .getConfig()
               .getAllSecurityConstraint();
      org.jboss.shrinkwrap.descriptor.api.webcommon31.WebResourceCollectionType<org.jboss.shrinkwrap.descriptor.api.webcommon31.SecurityConstraintType<org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor>> resourceCollection = securityConstraints
               .get(0).getOrCreateWebResourceCollection();
      assertEquals("testResource", resourceCollection.getWebResourceName());
      assertEquals("/admin/*", resourceCollection.getAllUrlPattern().get(0));
      assertEquals(2, resourceCollection.getAllHttpMethod().size());
      assertEquals("GET", resourceCollection.getAllHttpMethod().get(0));
      assertEquals("POST", resourceCollection.getAllHttpMethod().get(1));
      assertEquals("This is a security constraint", resourceCollection.getAllDescription().get(0));
      assertEquals(2, securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().size());
      assertEquals("MANAGER", securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().get(0));
      assertEquals("ADMIN", securityConstraints.get(0).getOrCreateAuthConstraint().getAllRoleName().get(1));
      assertEquals("CONFIDENTIAL",
               securityConstraints.get(0).getOrCreateUserDataConstraint().getTransportGuarantee().toString());
   }

   @Test
   public void testAddTwoConstraints() throws Exception
   {
      ServletFacet_3_0 servletFacet = installServlet(ServletFacet_3_0.class);

      executeAddSecurityConstraintCommand(Arrays.asList(new InputPair("webResourceName", "testResource1"),
               new InputPair("urlPatterns", Arrays.asList("/admin/*", "/manager/*"))));
      executeAddSecurityConstraintCommand(Arrays.asList(
               new InputPair("webResourceName", "testResource2"),
               new InputPair("urlPatterns", "/user/*")));

      assertEquals(2, servletFacet.getConfig().getAllSecurityConstraint().size());
   }

   @Test
   public void testAddConstraintCreatesNonExistingRole() throws Exception
   {
      ServletFacet_3_1 servletFacet = installServlet(ServletFacet_3_1.class);
      servletFacet.addSecurityRole("ADMIN");

      executeAddSecurityConstraintCommand(Arrays.asList(
               new InputPair("webResourceName", "testResource1"),
               new InputPair("urlPatterns", Arrays.asList("/admin/*", "/manager/*")),
               new InputPair("securityRoles", Arrays.asList("MANAGER", "ADMIN"))));

      assertEquals(2, servletFacet.getSecurityRoles().size());
      assertEquals("ADMIN", servletFacet.getSecurityRoles().get(0));
      assertEquals("MANAGER", servletFacet.getSecurityRoles().get(1));
   }

   private void executeAddSecurityConstraintCommand(List<InputPair> inputs) throws Exception
   {
      try (CommandController commandController = testHarness
               .createCommandController(AddSecurityConstraintCommand.class, project.getRoot()))
      {
         commandController.initialize();
         for (InputPair input : inputs)
         {
            commandController.setValueFor(input.inputName, input.inputValue);
         }
         Result result = commandController.execute();
         assertFalse(result instanceof Failed);
      }
   }

   private static class InputPair
   {
      private final String inputName;
      private final Object inputValue;

      private InputPair(String inputName, Object inputValue)
      {
         this.inputName = inputName;
         this.inputValue = inputValue;
      }
   }
}
