/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.security.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_2_5;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.shrinkwrap.descriptor.api.javaee.SecurityRoleCommonType;
import org.jboss.shrinkwrap.descriptor.api.webapp.WebAppCommonDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class RemoveSecurityRoleCommandTest extends AbstractSecurityCommandTest
{

   @Test
   public void testRemoveRoleWithServlet25() throws Exception
   {
      ServletFacet_2_5 servletFacet = installServlet(ServletFacet_2_5.class);
      addInitialRoles(servletFacet);
      removeManager();
      assertDeveloperRoleIsTheOnlyOne(servletFacet);
   }

   @Test
   public void testRemoveRoleWithServlet30() throws Exception
   {
      ServletFacet_3_0 servletFacet = installServlet(ServletFacet_3_0.class);
      addInitialRoles(servletFacet);
      removeManager();
      assertDeveloperRoleIsTheOnlyOne(servletFacet);
   }

   @Test
   public void testRemoveRoleWithServlet31() throws Exception
   {
      ServletFacet_3_1 servletFacet = installServlet(ServletFacet_3_1.class);
      addInitialRoles(servletFacet);
      removeManager();
      assertDeveloperRoleIsTheOnlyOne(servletFacet);
   }

   @Test(expected = IllegalStateException.class)
   public void testRemovingNonExistingRole() throws Exception
   {
      ServletFacet_3_1 servletFacet = installServlet(ServletFacet_3_1.class);
      addInitialRoles(servletFacet);
      removeRole("blah", false);
      assertEquals(2, getSecurityRoles(servletFacet).size());
   }

   private void addInitialRoles(ServletFacet<?> servletFacet)
   {
      servletFacet.addSecurityRole("manager");
      servletFacet.addSecurityRole("developer");
   }

   private void removeManager() throws Exception
   {
      removeRole("manager", true);
   }

   private void removeRole(String roleName, boolean isResultSuccess) throws Exception
   {
      try (CommandController commandController = testHarness
               .createCommandController(RemoveSecurityRoleCommand.class, project.getRoot()))
      {
         commandController.initialize();
         commandController.setValueFor("named", roleName);
         Result result = commandController.execute();
         if (isResultSuccess)
            assertFalse(result instanceof Failed);
         else
            assertTrue(result instanceof Failed);
      }
   }

   @SuppressWarnings("rawtypes")
   private void assertDeveloperRoleIsTheOnlyOne(ServletFacet<?> servletFacet)
   {
      List<SecurityRoleCommonType> securityRoles = getSecurityRoles(servletFacet);
      assertEquals(1, securityRoles.size());
      assertEquals("developer", securityRoles.get(0).getRoleName());
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private List<SecurityRoleCommonType> getSecurityRoles(ServletFacet<?> servletFacet)
   {
      WebAppCommonDescriptor webXml = servletFacet.getConfig();
      return webXml.getAllSecurityRole();
   }

}
