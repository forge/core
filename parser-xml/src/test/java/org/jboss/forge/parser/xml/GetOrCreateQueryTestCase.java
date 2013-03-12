/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import static org.jboss.forge.parser.xml.TestTreeBuilder.ATTR_NAME;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_2_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_3_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_3_TEXT;
import static org.jboss.forge.parser.xml.TestTreeBuilder.ROOT_NODE;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @version $Revision: $
 */
public class GetOrCreateQueryTestCase
{

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowNullNode()
   {
      // given
      new Node(ROOT_NODE);

      // when
      GetOrCreateQuery.INSTANCE.execute(null, new Pattern(ROOT_NODE));

      // then
      // exception should be thrown
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowNullPatternSequencToBeUsedForMatching()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      GetOrCreateQuery.INSTANCE.execute(root, (Pattern[]) null);

      // then
      // exception should be thrown
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowEmptyPatternSequencToBeUsedForMatching()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      GetOrCreateQuery.INSTANCE.execute(root, new Pattern[] {});

      // then
      // exception should be thrown
   }

   @Test
   public void shouldBeAbleToCreateOrGetNodes()
   {
      // given
      Node root = new Node(ROOT_NODE);
      root.createChild(CHILD_2_NODE);

      // when
      Node created = GetOrCreateQuery.INSTANCE.execute(
               root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_2_NODE + "/" + CHILD_2_1_NODE + "@" + ATTR_NAME + "="
                        + CHILD_2_2_NODE));

      // then
      Assert.assertNotNull("Verify a node was created", created);

      Assert.assertEquals("Verify correct node created", CHILD_2_1_NODE, created.getName());

      Assert.assertEquals("Verify correct node created", CHILD_2_2_NODE, created.getAttribute(ATTR_NAME));

      Assert.assertEquals("Verify node created has correct parent", CHILD_2_NODE, created.getParent().getName());

      Assert.assertEquals("Verify root only has one child node", 1, root.getChildren().size());
   }

   @Test
   public void shouldBeAbleToCreateOrGetNodesWithTextValues()
   {
      // given
      Node root = new Node(ROOT_NODE);
      root.createChild(CHILD_3_NODE);

      // when
      Node created = GetOrCreateQuery.INSTANCE.execute(root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_3_NODE + "=" + CHILD_3_TEXT));

      // then
      Assert.assertNotNull("Verify a node was created", created);

      Assert.assertEquals("Verify correct node created", CHILD_3_NODE, created.getName());

      Assert.assertEquals("Verify correct value set", CHILD_3_TEXT, created.getText());

      Assert.assertEquals("Verify root only has two child nodes", 2, root.getChildren().size());

      Assert.assertEquals("Created node has wrong parent", root, created.getParent());
   }

   @Test
   public void shouldBeAbleToGetNodesWithTextValues()
   {
      // given
      Node root = new Node(ROOT_NODE);
      GetOrCreateQuery.INSTANCE.execute(root, Patterns.from("/" + ROOT_NODE + "/" + CHILD_2_NODE));
      GetOrCreateQuery.INSTANCE.execute(root, Patterns.from("/" + ROOT_NODE + "/" + CHILD_3_NODE));
      GetOrCreateQuery.INSTANCE.execute(root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_3_NODE + "=" + CHILD_3_TEXT));
      GetOrCreateQuery.INSTANCE.execute(root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_3_NODE + "=" + CHILD_3_TEXT + "diff"));

      // when
      List<Node> nodes = root.get("/" + CHILD_3_NODE + "=" + CHILD_3_TEXT);

      // then
      Assert.assertNotNull("Verify nodes were found", nodes);

      Assert.assertEquals("Verify found a single node", 1, nodes.size());

      Node found = nodes.get(0);
      Assert.assertEquals("Verify correct node created", CHILD_3_NODE, found.getName());

      Assert.assertEquals("Verify correct value set", CHILD_3_TEXT, found.getText());

      Assert.assertEquals("Verify root only has four children", 4, root.getChildren().size());
   }

   @Test
   public void shouldCreateTwoChildrenUsingGetOrCreateQueryWhenOnlyRootDefined()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      GetOrCreateQuery.INSTANCE.execute(root, Patterns.from("/" + CHILD_1_NODE));
      GetOrCreateQuery.INSTANCE.execute(root, Patterns.from("/" + CHILD_2_NODE));

      // then
      Assert.assertEquals("Should have two children created", 2, root.getChildren().size());

   }

}
