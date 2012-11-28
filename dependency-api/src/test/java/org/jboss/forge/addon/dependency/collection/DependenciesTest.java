/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.dependency.collection;

import java.util.Iterator;

import org.jboss.forge.addon.dependency.DependencyNode;
import org.jboss.forge.addon.dependency.builder.DependencyBuilder;
import org.jboss.forge.addon.dependency.builder.DependencyNodeBuilder;
import org.junit.Assert;
import org.junit.Test;

public class DependenciesTest
{
   @Test
   public void testDepthFirstIterator()
   {
      DependencyNodeBuilder root = DependencyNodeBuilder.create(DependencyBuilder
               .create("org.jboss.forge:example1:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childOne = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childTwo = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-two:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildOne = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one-grandchild-one:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildTwo = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one-grandchild-two:2.0.0-SNAPSHOT"));
      childOne.getChildren().add(childOneGrandChildOne);
      childOne.getChildren().add(childOneGrandChildTwo);

      root.getChildren().add(childOne);
      root.getChildren().add(childTwo);

      // Execute SUT
      Iterator<DependencyNode> iterator = Dependencies.depthFirstIterator(root);

      // Check SUT outcome
      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOneGrandChildOne, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOneGrandChildTwo, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOne, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childTwo, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(root, iterator.next());

      Assert.assertFalse("Iterator is not empty", iterator.hasNext());
   }

   @Test
   public void testBreadthFirstIterator()
   {
      DependencyNodeBuilder root = DependencyNodeBuilder.create(DependencyBuilder
               .create("org.jboss.forge:example1:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childOne = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childTwo = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-two:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildOne = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one-grandchild-one:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildTwo = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one-grandchild-two:2.0.0-SNAPSHOT"));
      childOne.getChildren().add(childOneGrandChildOne);
      childOne.getChildren().add(childOneGrandChildTwo);

      root.getChildren().add(childOne);
      root.getChildren().add(childTwo);

      // Execute SUT
      Iterator<DependencyNode> iterator = Dependencies.breadthFirstIterator(root);

      // Check SUT outcome
      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(root, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOne, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childTwo, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOneGrandChildOne, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOneGrandChildTwo, iterator.next());

      Assert.assertFalse("Iterator is not empty", iterator.hasNext());
   }

   @Test
   public void testPreorderIterator()
   {
      DependencyNodeBuilder root = DependencyNodeBuilder.create(DependencyBuilder
               .create("org.jboss.forge:example1:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childOne = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childTwo = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-two:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildOne = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one-grandchild-one:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildTwo = DependencyNodeBuilder.create(
               DependencyBuilder.create("org.jboss.forge:example1-child-one-grandchild-two:2.0.0-SNAPSHOT"));
      childOne.getChildren().add(childOneGrandChildOne);
      childOne.getChildren().add(childOneGrandChildTwo);

      root.getChildren().add(childOne);
      root.getChildren().add(childTwo);

      // Execute SUT
      Iterator<DependencyNode> iterator = Dependencies.preorderIterator(root);

      // Check SUT outcome
      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(root, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOne, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOneGrandChildOne, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childOneGrandChildTwo, iterator.next());

      Assert.assertTrue("Iterator is empty", iterator.hasNext());
      Assert.assertEquals(childTwo, iterator.next());

      Assert.assertFalse("Iterator is not empty", iterator.hasNext());
   }

}
