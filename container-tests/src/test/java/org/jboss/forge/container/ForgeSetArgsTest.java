/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.container;

import java.io.File;
import java.io.IOException;

import org.jboss.forge.container.repositories.AddonRepositoryMode;
import org.jboss.forge.container.util.Files;
import org.jboss.forge.se.init.ForgeFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ForgeSetArgsTest
{
   static File repodir1;

   @Before
   public void init() throws IOException
   {
      repodir1 = File.createTempFile("forge", "repo1");
   }

   @After
   public void teardown()
   {
      Files.delete(repodir1, true);
   }

   @Test
   public void testAddonsCanReferenceDependenciesInOtherRepositories() throws IOException
   {
      String[] args = new String[] { "arg1", "arg2" };

      Forge forge = ForgeFactory.getInstance(Forge.class.getClassLoader());
      forge.setArgs(args);
      forge.addRepository(AddonRepositoryMode.MUTABLE, repodir1);
      forge.startAsync();

      Assert.assertSame(args, forge.getArgs());

      forge.stop();
   }

}
