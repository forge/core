/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import static org.jboss.forge.parser.xml.TestTreeBuilder.ATTR_NAME;
import static org.jboss.forge.parser.xml.TestTreeBuilder.ATTR_VALUE_1;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_1_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_3_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_3_TEXT;
import static org.jboss.forge.parser.xml.TestTreeBuilder.ROOT_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.createTree;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @version $Revision: $
 */
public class AbsoluteGetSingleQueryTestCase
{

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowNullNode()
   {
      // given
      new Node(ROOT_NODE);

      // when
      AbsoluteGetSingleQuery.INSTANCE.execute(null, new Pattern(ROOT_NODE));

      // then
      // exception should be thrown
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowNullPatternSequencToBeUsedForMatching()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      AbsoluteGetSingleQuery.INSTANCE.execute(root, (Pattern[]) null);

      // then
      // exception should be thrown
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowEmptyPatternSequencToBeUsedForMatching()
   {
      // given
      Node root = new Node(ROOT_NODE);

      // when
      AbsoluteGetSingleQuery.INSTANCE.execute(root, new Pattern[] {});

      // then
      // exception should be thrown
   }

   @Test
   public void shouldBeAbleToFindAChildWithTextValueUsingAbsoluteQuery() throws Exception
   {
      // given
      Node root = createTree();

      // when
      Node found = AbsoluteGetSingleQuery.INSTANCE.execute(root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_3_NODE + "=" + CHILD_3_TEXT));

      // then
      Assert.assertEquals("Verify correct node found", CHILD_3_NODE, found.getName());
      Assert.assertEquals("Verify correct node value", CHILD_3_TEXT, found.getText());
   }

   @Test
   public void shouldBeAbleToFindAExpressedChild() throws Exception
   {
      Node root = createTree();
      System.out.println(root.toString(true));
      Node found = AbsoluteGetSingleQuery.INSTANCE.execute(root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_1_NODE + "/" + CHILD_1_1_NODE));

      Assert.assertNotNull("Verify a node as found", found);

      Assert.assertEquals("Verify correct node found", CHILD_1_1_NODE, found.getName());
   }

   @Test
   public void shouldBeAbleToFindAExpressedFromRoot() throws Exception
   {
      Node root = createTree();
      System.out.println(root.toString(true));
      Node found = AbsoluteGetSingleQuery.INSTANCE.execute(root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_1_NODE + "/" + CHILD_1_1_NODE));

      Assert.assertNotNull("Verify a node was found", found);

      Assert.assertEquals("Verify correct node found", CHILD_1_1_NODE, found.getName());
   }

   @Test
   public void shouldBeAbleToFindAExpressedFromRootWithExpression() throws Exception
   {
      Node root = createTree();
      Node found = AbsoluteGetSingleQuery.INSTANCE.execute(
               root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_2_NODE + "/" + CHILD_2_1_NODE + "@" + ATTR_NAME + "="
                        + ATTR_VALUE_1));

      System.out.println(root.toString(true));
      Assert.assertNotNull("Verify a node was found", found);

      Assert.assertEquals("Verify correct node found", CHILD_2_1_NODE, found.getName());

      Assert.assertEquals("Verify correct node found", ATTR_VALUE_1, found.getAttribute(ATTR_NAME));
   }

   @Test
   public void shouldBeAbleToGetNodeWithTextValues()
   {
      Node root = new Node(ROOT_NODE);
      root.getOrCreate(("/" + CHILD_2_NODE));
      root.getOrCreate(("/" + CHILD_3_NODE));
      root.getOrCreate(("/" + CHILD_3_NODE + "=" + CHILD_3_TEXT));
      root.getOrCreate(("/" + CHILD_3_NODE + "=" + CHILD_3_TEXT + "diff"));

      Node found = AbsoluteGetSingleQuery.INSTANCE.execute(root,
               Patterns.from("/" + ROOT_NODE + "/" + CHILD_3_NODE + "=" + CHILD_3_TEXT));

      Assert.assertNotNull("Verify node was found", found);

      Assert.assertEquals("Verify correct node created", CHILD_3_NODE, found.getName());

      Assert.assertEquals("Verify correct value set", CHILD_3_TEXT, found.getText());

      Assert.assertEquals("Verify root only has four children", 4, root.getChildren().size());
   }

}
