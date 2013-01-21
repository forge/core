/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import javax.inject.Inject;

import org.jboss.forge.ForgeEnvironment;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ForgeEnvironmentImplTest extends AbstractShellTest
{
   @Inject
   private ForgeEnvironment environment;

   @Test
   public void testCreateProject() throws Exception
   {
      Shell shell = getShell();

      assertTrue(environment.isOnline());
      shell.execute("set OFFLINE true");
      assertFalse(environment.isOnline());
   }

   @Test
   public void testGetRuntimeVersion() throws Exception
   {
      String version = getClass().getPackage().getImplementationVersion();
      Assert.assertEquals(version, environment.getRuntimeVersion());
   }

   @Test
   public void testIsEmbedded() throws Exception
   {
      boolean expected = Boolean.getBoolean("forge.compatibility.IDE");
      Assert.assertEquals(expected, environment.isEmbedded());
   }

   @Test
   public void testIsNotEmbeddedExplicitlySet() throws Exception
   {
      System.setProperty("forge.compatibility.IDE", "false");
      Assert.assertFalse(environment.isEmbedded());
   }

   @Test
   public void testIsEmbeddedExplicitlySet() throws Exception
   {
      System.setProperty("forge.compatibility.IDE", "true");
      Assert.assertTrue(environment.isEmbedded());
   }
}
