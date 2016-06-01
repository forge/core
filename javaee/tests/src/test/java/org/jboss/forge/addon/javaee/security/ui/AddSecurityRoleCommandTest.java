/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.security.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_2_5;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AddSecurityRoleCommandTest extends AbstractSecurityCommandTest
{

   @Test
   public void testAddRoleWithServlet25() throws Exception
   {
      ServletFacet_2_5 servletFacet = installServlet(ServletFacet_2_5.class);
      addSecurityRole("developer");
      addSecurityRole("manager");

      org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor webXml = servletFacet.getConfig();
      List<org.jboss.shrinkwrap.descriptor.api.javaee5.SecurityRoleType<org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor>> securityRoles = webXml
               .getAllSecurityRole();
      assertEquals(2, securityRoles.size());
      assertEquals("developer", securityRoles.get(0).getRoleName());
      assertEquals("manager", securityRoles.get(1).getRoleName());
   }

   @Test
   public void testAddRoleWithServlet30() throws Exception
   {
      ServletFacet_3_0 servletFacet = installServlet(ServletFacet_3_0.class);
      addSecurityRole("developer");

      org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor webXml = servletFacet.getConfig();
      List<org.jboss.shrinkwrap.descriptor.api.javaee6.SecurityRoleType<org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor>> securityRoles = webXml
               .getAllSecurityRole();
      assertEquals(1, securityRoles.size());
      assertEquals("developer", securityRoles.get(0).getRoleName());
   }

   @Test
   public void testAddRoleWithServlet31() throws Exception
   {
      ServletFacet_3_1 servletFacet = installServlet(ServletFacet_3_1.class);
      addSecurityRole("developer");

      org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor webXml = servletFacet.getConfig();
      List<org.jboss.shrinkwrap.descriptor.api.javaee7.SecurityRoleType<org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor>> securityRoles = webXml
               .getAllSecurityRole();
      assertEquals(1, securityRoles.size());
      assertEquals("developer", securityRoles.get(0).getRoleName());
   }

   private void addSecurityRole(String roleName) throws Exception
   {
      try (CommandController commandController = testHarness
               .createCommandController(AddSecurityRoleCommand.class, project.getRoot()))
      {
         commandController.initialize();
         commandController.setValueFor("named", roleName);
         Result result = commandController.execute();
         assertFalse(result instanceof Failed);
      }
   }
}
