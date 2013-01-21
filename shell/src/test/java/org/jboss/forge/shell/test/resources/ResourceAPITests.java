/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.resources;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.util.OSUtils;
import org.jboss.forge.shell.util.PathspecParser;
import org.jboss.forge.shell.util.ResourceUtil;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@Singleton
@RunWith(Arquillian.class)
public class ResourceAPITests extends AbstractShellTest
{
   @Inject
   private ResourceFactory factory;

   @Test
   public void testPathNavigation()
   {
      DirectoryResource expect = new DirectoryResource(factory, new File("").getAbsoluteFile().getParentFile()
               .getParentFile());
      DirectoryResource r = new DirectoryResource(factory, new File("").getAbsoluteFile());

      Assert.assertEquals(expect, ResourceUtil.parsePathspec(factory, r, "../..").iterator().next());
   }

   @Test
   public void testPathNavigation2()
   {
      DirectoryResource expect = new DirectoryResource(factory, new File("").getAbsoluteFile());
      DirectoryResource r = new DirectoryResource(factory, new File("").getAbsoluteFile());

      Assert.assertEquals(expect, ResourceUtil.parsePathspec(factory, r, ".").iterator().next());
   }

   @Test
   public void testPathParser()
   {
      DirectoryResource expect = new DirectoryResource(factory, new File("/"));
      DirectoryResource root = new DirectoryResource(factory, new File("").getAbsoluteFile());

      List<Resource<?>> r = ResourceUtil.parsePathspec(factory, root, "/");
      Resource<?> actual = r.iterator().next();

      Assert.assertEquals(expect.getFullyQualifiedName(), actual.getFullyQualifiedName());
   }

   @Test
   public void testWindowsPaths()
   {
      OSUtils.setPretendWindows(true);

      DirectoryResource expect = new DirectoryResource(factory, new File("").getAbsoluteFile().getParentFile());

      String name = expect.getName();

      DirectoryResource r = new DirectoryResource(factory, new File("").getAbsoluteFile());

      Assert.assertEquals(expect, ResourceUtil.parsePathspec(factory, r, "..\\..\\" + name).iterator().next());

      OSUtils.setPretendWindows(false);
   }

   @Test
   public void testUnixPathsOnWindows()
   {
      OSUtils.setPretendWindows(true);

      DirectoryResource expect = new DirectoryResource(factory, new File("").getAbsoluteFile().getParentFile());

      String name = expect.getName();

      DirectoryResource r = new DirectoryResource(factory, new File("").getAbsoluteFile());

      Assert.assertEquals(expect, ResourceUtil.parsePathspec(factory, r, "../../" + name).iterator().next());

      OSUtils.setPretendWindows(false);
   }

   @Test
   public void testWildCards()
   {

      DirectoryResource r = new DirectoryResource(factory, new File("").getAbsoluteFile());

      for (Resource<?> res : ResourceUtil.parsePathspec(factory, r, "*"))
      {
         System.out.println(res);
      }
   }

   @Test
   public void testWildCards2()
   {

      DirectoryResource r = new DirectoryResource(factory, new File("").getAbsoluteFile());

      for (Resource<?> res : ResourceUtil.parsePathspec(factory, r, "*.java"))
      {
         System.out.println(res);
      }
   }

   @Test
   public void testDeepSearch()
   {
      DirectoryResource root = new DirectoryResource(factory, new File("").getAbsoluteFile());
      List<Resource<?>> results = new PathspecParser(factory, root, "BaseEvent.java").search();

      for (Resource<?> r : results)
      {
         System.out.println(r);
      }
   }

}
