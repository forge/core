/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import static org.jboss.forge.parser.xml.TestTreeBuilder.ATTR_NAME;
import static org.jboss.forge.parser.xml.TestTreeBuilder.ATTR_VALUE_1;
import static org.jboss.forge.parser.xml.TestTreeBuilder.ATTR_VALUE_2;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_1_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_1_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.OTHER_NAME;
import static org.jboss.forge.parser.xml.TestTreeBuilder.ROOT_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.createTree;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class RelativeGetQueryTestCase
{

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowNullNode()
   {
      // given
      new Node(ROOT_NODE);

      // when
      RelativeGetQuery.INSTANCE.execute(null, new Pattern(ROOT_NODE));

      // then
      // exception should be thrown
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowNullPatternSequencToBeUsedForMatching()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      RelativeGetQuery.INSTANCE.execute(root, (Pattern[]) null);

      // then
      // exception should be thrown
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowEmptyPatternSequencToBeUsedForMatching()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      RelativeGetQuery.INSTANCE.execute(root, new Pattern[] {});

      // then
      // exception should be thrown
   }

   @Test
   public void shouldReturnRootFromSingleNodeTreeWhenMatchingNodeName()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      List<Node> matchingNodes = RelativeGetQuery.INSTANCE.execute(root, new Pattern(ROOT_NODE));

      // then
      Assert.assertEquals("Should return only one node", 1, matchingNodes.size());
      Assert.assertEquals("Should find only root node", root, matchingNodes.get(0));
   }

   @Test
   public void shouldReturnNullIfNoMatch()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      List<Node> matchingNodes = RelativeGetQuery.INSTANCE.execute(root, new Pattern(CHILD_2_NODE));

      // then
      Assert.assertTrue("Should not find node", matchingNodes.isEmpty());
   }

   @Test
   public void shouldReturnTwoNodesMatchingGivenNamePattern()
   {
      // given
      Node root = createTree();

      // when
      List<Node> matchingNodes = RelativeGetQuery.INSTANCE.execute(root, new Pattern(CHILD_2_1_NODE));

      // then
      Assert.assertEquals("Should return two nodes", 2, matchingNodes.size());
      NodeAssert.assertEqualsByName(matchingNodes, CHILD_2_1_NODE);
   }

   @Test
   public void shouldReturnOneLeafNodesMatchingGivenNamePattern()
   {
      // given
      Node root = createTree();

      // when
      List<Node> matchingNodes = RelativeGetQuery.INSTANCE.execute(root, new Pattern(CHILD_1_1_NODE));

      // then
      Assert.assertEquals("Should find exactly one node.", 1, matchingNodes.size());
      NodeAssert.assertEqualsByName(matchingNodes, CHILD_1_1_NODE);
   }

   @Test
   public void shouldReturnTwoNodesMatchingGivenNameAndAttributePattern()
   {
      // given
      Node root = createTree();
      Pattern pattern = new Pattern(CHILD_2_1_1_NODE);
      pattern.attribute(OTHER_NAME, ATTR_VALUE_1);

      // when
      List<Node> matchingNodes = RelativeGetQuery.INSTANCE.execute(root, pattern);

      // then
      Assert.assertEquals("Should return two nodes", 2, matchingNodes.size());
      NodeAssert.assertEqualsByName(matchingNodes, CHILD_2_1_1_NODE);
      NodeAssert.assertContainsAttribute(matchingNodes, OTHER_NAME, ATTR_VALUE_1);
   }

   @Test
   public void shouldReturnTwoNodesMatchingGivenTwoPatterns()
   {
      // given
      Node root = createTree();
      Pattern endPatternWithAttribute = new Pattern(CHILD_2_1_1_NODE);
      endPatternWithAttribute.attribute(OTHER_NAME, ATTR_VALUE_1);

      // when
      List<Node> matchingNodes = RelativeGetQuery.INSTANCE.execute(root, new Pattern(CHILD_2_1_NODE),
               endPatternWithAttribute);

      // then
      Assert.assertEquals("Should return two nodes", 2, matchingNodes.size());
      NodeAssert.assertEqualsByName(matchingNodes, CHILD_2_1_1_NODE);
      NodeAssert.assertContainsAttribute(matchingNodes, OTHER_NAME, ATTR_VALUE_1);
   }

   @Test
   public void shouldReturnOneNodeMatchingUniquePattern()
   {
      // given
      Node root = createTree();

      // when
      List<Node> matchingNodes = RelativeGetQuery.INSTANCE.execute(root,
               new Pattern(CHILD_2_1_NODE).attribute(ATTR_NAME, ATTR_VALUE_2),
               new Pattern(CHILD_2_1_1_NODE).attribute(OTHER_NAME, ATTR_VALUE_1));

      // then
      Assert.assertEquals("Should return two nodes", 1, matchingNodes.size());
      NodeAssert.assertEqualsByName(matchingNodes, CHILD_2_1_1_NODE);
      NodeAssert.assertContainsAttribute(matchingNodes, OTHER_NAME, ATTR_VALUE_1);
   }

   @Test
   public void shouldFindOnlyOnePathWithThreeNodesForGivenQuery()
   {
      // given
      String queryExpression = "/A/B/C";

      Node root = new Node(ROOT_NODE);
      root.getOrCreate(Patterns.from("/A/C/B/D"));
      root.getOrCreate(Patterns.from("/A/B/C"));
      root.getOrCreate(Patterns.from("/A/C/B/C"));
      root.getOrCreate(Patterns.from("/B/C"));
      root.getOrCreate(Patterns.from("/B/A"));
      root.getOrCreate(Patterns.from("/B/D"));

      // when
      List<Node> foundNodes = RelativeGetQuery.INSTANCE.execute(root, Patterns.from(queryExpression));

      // then
      Assert.assertEquals("Should find only one node for query " + queryExpression, 1, foundNodes.size());

      NodeAssert.assertEqualsByName(foundNodes.get(0), "C");
   }

   @Test
   public void shouldFindAllFourNodesWithGivenName()
   {
      // given
      Node root = new Node(ROOT_NODE);
      root.getOrCreate(Patterns.from("/A/C/B/D"));
      root.getOrCreate(Patterns.from("/A/B/C"));
      root.getOrCreate(Patterns.from("/A/C/B/C"));
      root.getOrCreate(Patterns.from("/B/C"));
      root.getOrCreate(Patterns.from("/B/A"));
      root.getOrCreate(Patterns.from("/B/D"));

      // when
      List<Node> foundNodes = RelativeGetQuery.INSTANCE.execute(root, new Pattern("C"));

      // then
      Assert.assertEquals("Should find all four nodes", 4, foundNodes.size());

      NodeAssert.assertEqualsByName(foundNodes, "C");
   }

}
