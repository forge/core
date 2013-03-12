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
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_1_1_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_1_NODE;
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
public class RelativeGetSingleQueryTestCase
{

   @Test
   public void shouldBeAbleToFindAChildWithTextValueUsingRelativeQuery() throws Exception
   {
      // given
      Node root = createTree();
      System.out.println(root.toString(true));

      // when
      Node found = RelativeGetSingleQuery.INSTANCE.execute(root, Patterns.from(CHILD_3_NODE + "=" + CHILD_3_TEXT));

      // then
      Assert.assertEquals("Verify correct node found", CHILD_3_NODE, found.getName());
      Assert.assertEquals("Verify correct node value", CHILD_3_TEXT, found.getText());
   }

   @Test
   public void shouldBeAbleToFindANodeUsingRelativeQuery() throws Exception
   {
      // given
      Node root = createTree();
      Pattern pattern = new Pattern(CHILD_2_1_1_NODE);
      pattern.attribute(ATTR_NAME, ATTR_VALUE_2);

      // when
      Node found = RelativeGetSingleQuery.INSTANCE.execute(root, pattern);

      // then
      NodeAssert.assertEqualsByName(found, CHILD_2_1_1_NODE);
   }

   @Test
   public void shouldBeAbleToFindANodeUsingMultiPatternRelativeQuery() throws Exception
   {
      // given
      Node root = createTree();

      // when
      Node found = RelativeGetSingleQuery.INSTANCE.execute(root,
               Patterns.from("/" + CHILD_2_1_NODE + "/" + CHILD_2_1_1_NODE + "@" + ATTR_NAME + "=" + ATTR_VALUE_1));

      // then
      NodeAssert.assertEqualsByName(found, CHILD_2_1_1_NODE);
   }

   @Test
   public void shouldNotFindNonExistingNode() throws Exception
   {
      // given
      Node root = createTree();

      // when
      Node found = RelativeGetSingleQuery.INSTANCE.execute(root, new Pattern("Non existing node"));

      // then
      Assert.assertNull(found);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionWhenMoreThanOneNodeFound() throws Exception
   {
      // given
      Node root = new Node(ROOT_NODE).createChild(Patterns.from("/A/B/A/B")).getRoot();

      System.out.println(root.toString(true));
      // when
      Node found = RelativeGetSingleQuery.INSTANCE.execute(root, new Pattern("A"));

      // then
      Assert.assertNull(found);
   }

}
