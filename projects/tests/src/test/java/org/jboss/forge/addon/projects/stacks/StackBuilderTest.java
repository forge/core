/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.stacks;

import org.jboss.forge.addon.projects.mock.facets.ProjectFacetA;
import org.jboss.forge.addon.projects.mock.facets.ProjectFacetB;
import org.jboss.forge.addon.projects.mock.facets.ProjectFacetC;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class StackBuilderTest
{
   @Test
   public void testName()
   {
      Stack stack = StackBuilder.stack("Foo");
      Assert.assertEquals("Foo", stack.getName());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testNullName()
   {
      StackBuilder.stack(null);
   }

   @Test
   public void testIncludesFacet()
   {
      Stack stack = StackBuilder.stack("Test Stack")
               .includes(ProjectFacetA.class)
               .includes(ProjectFacetB.class);
      Assert.assertTrue(stack.supports(ProjectFacetA.class));
      Assert.assertFalse(stack.supports(ProjectFacetC.class));
   }

   @Test
   public void testExcludesFacet()
   {
      Stack stack = StackBuilder.stack("Test Stack")
               .includes(ProjectFacetA.class)
               .includes(ProjectFacetB.class)
               .excludes(ProjectFacetA.class);
      Assert.assertFalse(stack.supports(ProjectFacetA.class));
   }

   @Test
   public void testIncludesStack()
   {
      Stack anotherStack = StackBuilder.stack("Another Test Stack")
               .includes(ProjectFacetC.class);
      Stack stack = StackBuilder.stack("Test Stack")
               .includes(ProjectFacetA.class)
               .includes(anotherStack)
               .includes(ProjectFacetB.class);
      Assert.assertTrue(stack.supports(ProjectFacetA.class));
      Assert.assertTrue(stack.supports(ProjectFacetB.class));
      Assert.assertTrue(stack.supports(ProjectFacetC.class));
   }

   @Test
   public void testExcludesStack()
   {
      Stack anotherStack = StackBuilder.stack("Another Test Stack")
               .includes(ProjectFacetC.class);
      Stack stack = StackBuilder.stack("Test Stack")
               .includes(ProjectFacetA.class)
               .includes(anotherStack)
               .includes(ProjectFacetB.class)
               .excludes(anotherStack);
      Assert.assertTrue(stack.supports(ProjectFacetA.class));
      Assert.assertTrue(stack.supports(ProjectFacetB.class));
      Assert.assertFalse(stack.supports(ProjectFacetC.class));
   }
}
