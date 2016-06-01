/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.security.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_2_5;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.LoginConfigType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddLoginConfigCommandTest extends AbstractSecurityCommandTest
{

   @Test
   public void testAddLoginConfigWithServlet25() throws Exception
   {
      ServletFacet_2_5 servletFacet = addLoginConfig("myRealm", "BASIC", ServletFacet_2_5.class);
      org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor webXml = servletFacet.getConfig();
      assertEquals(1, webXml.getAllLoginConfig().size());
      org.jboss.shrinkwrap.descriptor.api.webapp25.LoginConfigType<org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor> loginConfig = webXml
               .getAllLoginConfig().get(
                        0);
      assertEquals("BASIC", loginConfig.getAuthMethod());
      assertEquals("myRealm", loginConfig.getRealmName());
   }

   @Test
   public void testAddLoginConfigWithServlet30() throws Exception
   {
      ServletFacet_3_0 servletFacet = addLoginConfig("myRealm", "FORM", ServletFacet_3_0.class);
      org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor webXml = servletFacet.getConfig();
      assertEquals(1, webXml.getAllLoginConfig().size());
      LoginConfigType<org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor> loginConfig = webXml
               .getAllLoginConfig().get(0);
      assertEquals("FORM", loginConfig.getAuthMethod());
      assertEquals("myRealm", loginConfig.getRealmName());
   }

   @Test
   public void testAddLoginConfigWithServlet31() throws Exception
   {
      ServletFacet_3_1 servletFacet = addLoginConfig("myRealm", "BASIC", ServletFacet_3_1.class);
      org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor webXml = servletFacet.getConfig();
      assertEquals(1, webXml.getAllLoginConfig().size());
      org.jboss.shrinkwrap.descriptor.api.webcommon31.LoginConfigType<org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor> loginConfig = webXml
               .getAllLoginConfig().get(
                        0);
      assertEquals("BASIC", loginConfig.getAuthMethod());
      assertEquals("myRealm", loginConfig.getRealmName());
   }

   @After
   public void tearDown() throws Exception
   {
      project.getRoot().delete(true);
   }

   private <T extends ServletFacet<?>> T addLoginConfig(String realm, String authenticationMethod,
            Class<T> servletFacetClass)
                     throws Exception
   {
      T servletFacet = installServlet(servletFacetClass);
      try (CommandController commandController = testHarness
               .createCommandController(AddLoginConfigCommand.class, project.getRoot()))
      {
         commandController.initialize();
         commandController.setValueFor("securityRealm", realm);
         commandController.setValueFor("authMethod", authenticationMethod);
         Result execute = commandController.execute();
         assertFalse(execute instanceof Failed);
      }
      return servletFacet;
   }
}