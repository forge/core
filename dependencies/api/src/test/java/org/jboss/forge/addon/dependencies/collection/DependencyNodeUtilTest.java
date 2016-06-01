/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.dependencies.collection;

import java.util.Iterator;

import org.jboss.forge.addon.dependencies.DependencyNode;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyNodeBuilder;
import org.junit.Assert;
import org.junit.Test;

public class DependencyNodeUtilTest
{
   @Test
   public void testDepthFirstIterator()
   {
      DependencyNodeBuilder root = DependencyNodeBuilder.create(null, DependencyBuilder
               .create("org.jboss.forge.addon:example1:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childOne = DependencyNodeBuilder.create(root,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childTwo = DependencyNodeBuilder.create(root,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-two:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildOne = DependencyNodeBuilder.create(childOne,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one-grandchild-one:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildTwo = DependencyNodeBuilder.create(childOne,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one-grandchild-two:2.0.0-SNAPSHOT"));
      childOne.getChildren().add(childOneGrandChildOne);
      childOne.getChildren().add(childOneGrandChildTwo);

      root.getChildren().add(childOne);
      root.getChildren().add(childTwo);

      // Execute SUT
      Iterator<DependencyNode> iterator = DependencyNodeUtil.depthFirstIterator(root);

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
      DependencyNodeBuilder root = DependencyNodeBuilder.create(null, DependencyBuilder
               .create("org.jboss.forge.addon:example1:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childOne = DependencyNodeBuilder.create(root,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childTwo = DependencyNodeBuilder.create(root,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-two:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildOne = DependencyNodeBuilder.create(childOne,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one-grandchild-one:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildTwo = DependencyNodeBuilder.create(childOne,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one-grandchild-two:2.0.0-SNAPSHOT"));
      childOne.getChildren().add(childOneGrandChildOne);
      childOne.getChildren().add(childOneGrandChildTwo);

      root.getChildren().add(childOne);
      root.getChildren().add(childTwo);

      // Execute SUT
      Iterator<DependencyNode> iterator = DependencyNodeUtil.breadthFirstIterator(root);

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
      DependencyNodeBuilder root = DependencyNodeBuilder.create(null, DependencyBuilder
               .create("org.jboss.forge.addon:example1:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childOne = DependencyNodeBuilder.create(root,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one:2.0.0-SNAPSHOT"));
      DependencyNodeBuilder childTwo = DependencyNodeBuilder.create(root,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-two:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildOne = DependencyNodeBuilder.create(childOne,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one-grandchild-one:2.0.0-SNAPSHOT"));

      DependencyNodeBuilder childOneGrandChildTwo = DependencyNodeBuilder.create(childOne,
               DependencyBuilder.create("org.jboss.forge.addon:example1-child-one-grandchild-two:2.0.0-SNAPSHOT"));
      childOne.getChildren().add(childOneGrandChildOne);
      childOne.getChildren().add(childOneGrandChildTwo);

      root.getChildren().add(childOne);
      root.getChildren().add(childTwo);

      // Execute SUT
      Iterator<DependencyNode> iterator = DependencyNodeUtil.preorderIterator(root);

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
