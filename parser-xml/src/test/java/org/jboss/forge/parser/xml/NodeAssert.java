/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.util.Arrays;

import org.junit.Assert;


/**
 * {@link Node} related assertions.
 * 
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 * 
 */
public final class NodeAssert
{

   private NodeAssert()
   {
   }

   /**
    * Verifies if all nodes have expected name.
    * 
    * @param nodes
    * @param expectedName
    * 
    * @throws Exception Assertion error when one of node does not match
    */
   public static void assertEqualsByName(Iterable<Node> nodes, String expectedName)
   {
      for (Node node : nodes)
      {
         Assert.assertEquals("Name of node " + node + " does not match!", expectedName, node.getName());
      }
   }

   /**
    * Verifies if node has expected name.
    * 
    * @param nodes
    * @param expectedName
    * 
    * @throws Exception Assertion error when one of node does not match
    */
   public static void assertEqualsByName(Node node, String expectedName)
   {
      Assert.assertNotNull(node);
      assertEqualsByName(Arrays.asList(node), expectedName);
   }

   /**
    * Verifies if all nodes contain attribute with given value.
    * 
    * @param nodes
    * @param name Name of the node attribute
    * @param expectedValue
    * 
    * @throws Exception Assertion error when one of node does not match
    */
   public static void assertContainsAttribute(Iterable<Node> nodes, String name, String expectedValue)
   {
      for (Node node : nodes)
      {
         Assert.assertEquals("Attribute [" + name + "] value not matching for node " + node, expectedValue,
                  node.getAttribute(name));
      }
   }

}
