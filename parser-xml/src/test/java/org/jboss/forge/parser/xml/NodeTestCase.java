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
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_2_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_2_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_3_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.CHILD_3_TEXT;
import static org.jboss.forge.parser.xml.TestTreeBuilder.ROOT_NODE;
import static org.jboss.forge.parser.xml.TestTreeBuilder.createTree;

import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class NodeTestCase
{
   private static final String BODY = "test_body";

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfNullNameParamInConstructor() throws Exception
   {
      Node parent = new Node(ROOT_NODE);
      new Node(null, parent);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfSpaceInConstructorNameParam() throws Exception
   {
      Node parent = new Node(ROOT_NODE);
      new Node("a name", parent);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowCreationOfTreeWithEmptyStringAsRootName()
   {
      new Node("");
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotAllowCreationOfChildWithBlankStringAsNodeName()
   {
      new Node(ROOT_NODE).createChild("    ");
   }

   @Test
   public void shouldBeAbleToGetParentNode() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      Node child = root.createChild(CHILD_1_NODE);

      Assert.assertEquals("Verify ability to get parent node", root, child.getParent());
   }

   @Test
   public void shouldBeAbleToGetOrCreateExistingNode() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      Node child1 = root.getOrCreate(CHILD_1_NODE);
      Node child1_ref = root.getOrCreate(CHILD_1_NODE);

      Assert.assertEquals("Verify root only has one child", 1, root.getChildren().size());

      Assert.assertEquals("Verify the previous created node was returned", child1, child1_ref);
   }

   @Test
   public void shouldBeAbleToCreateMultipleEquallyNamedChildren() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      Node child1 = root.createChild(CHILD_1_NODE);
      Node child2 = root.createChild(CHILD_1_NODE);

      Assert.assertEquals("Verify root only has two children", 2, root.getChildren().size());

      Assert.assertNotSame("Verify the children are not the same object", child1, child2);
   }

   @Test
   public void shouldBeAbleToGetChildNodesByName() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      Node child1 = root.createChild(CHILD_1_NODE);
      Node child2 = root.createChild(CHILD_1_NODE);
      root.createChild(CHILD_2_NODE);

      List<Node> found = root.get(CHILD_1_NODE);

      Assert.assertEquals("Verify only the named nodes were found", 2, found.size());

      Assert.assertEquals("Verify the correct node was found", child1, found.get(0));

      Assert.assertEquals("Verify the correct node was found", child2, found.get(1));
   }

   @Test
   public void shouldBeAbleToGetASingleNode() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      Node child = root.createChild(CHILD_1_NODE);

      Node found = root.getSingle(CHILD_1_NODE);

      Assert.assertEquals("Verify correct node was found", child, found);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfMultipleNamedNodesFoundOnGetSingle() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      root.createChild(CHILD_1_NODE);
      root.createChild(CHILD_1_NODE);

      // throws Exception, multiple nodes with same name
      root.getSingle(CHILD_1_NODE);
   }

   @Test
   public void shouldBeAbleToReadAndWriteAttribute() throws Exception
   {
      Node root = new Node(ROOT_NODE).attribute(ATTR_NAME, ATTR_VALUE_1);

      Assert.assertEquals("Verify abillity to store attribues", root.getAttribute(ATTR_NAME), ATTR_VALUE_1);
   }

   @Test
   public void shouldBeAbleToReadAndWriteAttributeObject() throws Exception
   {
      Node root = new Node(ROOT_NODE).attribute(ATTR_NAME, new StringBuilder(ATTR_VALUE_1));

      Assert.assertEquals("Verify abillity to store attribues", root.getAttribute(ATTR_NAME), ATTR_VALUE_1);
   }

   @Test
   public void shouldBeAbleToReadWriteTextBody() throws Exception
   {
      Node root = new Node(ROOT_NODE).text(BODY);

      Assert.assertEquals("Verify abillity to store text body", BODY, root.getText());
   }

   @Test
   public void shouldBeAbleToReadWriteTextBodyObject() throws Exception
   {
      Node root = new Node(ROOT_NODE).text(new StringBuilder(BODY));

      Assert.assertEquals("Verify abillity to store text body", BODY, root.getText());
   }

   @Test
   public void shouldBeAbleToReadAllChildTextBodyValues() throws Exception
   {
      Node root = new Node(ROOT_NODE);

      for (int i = 0; i < 10; i++)
      {
         root.createChild("subject").text(i);
      }

      List<String> textValues = root.getTextValuesForPatternName("subject");
      for (int i = 0; i < 10; i++)
      {
         Assert.assertTrue(textValues.contains(String.valueOf(i)));
      }
   }

   @Test
   public void shouldBeAbleToDetermineTextValue() throws Exception
   {
      String childName = "testval";
      String childText = "textval";

      Node root = new Node(ROOT_NODE);
      Assert.assertNull(root.getTextValueForPatternName(childName));

      root.createChild(childName);
      Assert.assertNull(root.getTextValueForPatternName(childName));

      root.getChildren().get(0).text(childText);
      Assert.assertNotNull(root.getTextValueForPatternName(childName));
      Assert.assertEquals(childText, root.getTextValueForPatternName(childName));
   }

   @Test
   public void shouldReturnEmptyListForMissingTextValues() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      root.createChild("child1");
      root.createChild("child2");
      root.createChild("child3").text(null);
      List<String> textValues = root.getTextValuesForPatternName("textValue");
      Assert.assertNotNull(textValues);
      Assert.assertTrue(textValues.isEmpty());
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfMultipleChildrenWithSameNameOnTextValue() throws Exception
   {
      String childName = "child";
      Node root = new Node(ROOT_NODE);
      Assert.assertNull(root.getTextValueForPatternName(childName));

      root.createChild(childName);
      root.createChild(childName);
      root.getChildren().get(0).text("text");
      root.getChildren().get(1).text("text");

      root.getTextValueForPatternName(childName);
   }

   @Test
   public void shouldFindAllPropertiesInToString() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      Assert.assertTrue(root.toString().contains(root.getClass().getSimpleName()));
      Assert.assertTrue(root.toString().contains("children"));
      Assert.assertTrue(root.toString().contains("attributes"));
      Assert.assertFalse(root.toString().contains("text"));

      root.text("arbitrary cdata");
      Assert.assertTrue(root.toString().contains("Node"));
      Assert.assertTrue(root.toString().contains("children"));
      Assert.assertTrue(root.toString().contains("attributes"));
      Assert.assertTrue(root.toString().contains("text"));
      Assert.assertTrue(root.toString().contains(root.getText()));
   }

   @Test
   public void assertToStringFormat() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      String r = root.toString();
      Assert.assertTrue(r.startsWith(root.getClass().getSimpleName()));
      Assert.assertTrue(r.indexOf("text") < r.indexOf("Node"));
      Assert.assertTrue(r.indexOf("Node") < r.indexOf("children"));
      Assert.assertTrue(r.indexOf("children") < r.indexOf("attributes"));

      root.text("arbitrary cdata");
      r = root.toString();
      Assert.assertTrue(r.indexOf("Node") < r.indexOf("text"));
      Assert.assertTrue(r.indexOf("Node") < r.indexOf("children"));
      Assert.assertTrue(r.indexOf("children") < r.indexOf("attributes"));
      Assert.assertTrue(r.indexOf("attributes") < r.indexOf("text"));

      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("children[0]"));
      root.createChild("testchild1");
      root.createChild("testchild2");
      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("children[2]"));

      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("attributes[{}]"));
      root.attribute("name", "value");
      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString()
               .contains("attributes[{name=value}]"));

      Assert.assertTrue("Unexpected content? " + root.toString(), root.toString().contains("text[arbitrary cdata]"));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void shouldHaveImmutableAttributeMap() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      root.attribute("attribute1", "value");
      root.attribute("attribute2", "value");
      Map<String, String> attributes = root.getAttributes();
      attributes.clear();
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionForNullStringParameter() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      root.removeChildren((String) null);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionForEmptyStringParameter() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      root.removeChildren("");
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionForNullQueryParameter() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      root.removeChildren((Pattern) null);
   }

   @Test
   public void shouldRemoveNodeByString() throws Exception
   {
      String name = "child";
      Node root = new Node(ROOT_NODE);

      Assert.assertTrue(root.getChildren().isEmpty());
      root.createChild(name);
      Assert.assertFalse(root.getChildren().isEmpty());
      Assert.assertEquals(1, root.getChildren().size());

      root.removeChildren(name);
      Assert.assertTrue(root.getChildren().isEmpty());
   }

   @Test
   public void shouldRemoveSingleChildNodeWithNodeParam()
   {
      Node root = new Node(ROOT_NODE);
      Node child = root.createChild("child_node");
      Assert.assertTrue(root.removeChild(child));
   }

   @Test
   public void shouldNotRemoveSingleChildNodeWithNodeParam()
   {
      Node root = new Node(ROOT_NODE);
      Node child = new Node("another_node");
      Assert.assertFalse(root.removeChild((Node) null));
      Assert.assertFalse(root.removeChild(child));
      root.createChild("a_proper_child_node");
      Assert.assertFalse(root.removeChild(child));
   }

   @Test
   public void shouldRemoveSingleChildNodeWithStringParam()
   {
      Node root = new Node(ROOT_NODE);
      String childNodeName = "another_node";
      Assert.assertNull(root.removeChild(childNodeName));
      root.createChild(childNodeName);
      Node removedChild = root.removeChild(childNodeName);
      Assert.assertNotNull(removedChild);
      Assert.assertEquals(childNodeName, removedChild.getName());
   }

   @Test
   public void shouldNotRemoveSingleChildNodeWithStringParam()
   {
      Node root = new Node(ROOT_NODE);
      Assert.assertNull(root.removeChild("node_that_doesn't_exist"));
      root.createChild("a_node");
      Assert.assertNull(root.removeChild("nonexisting_node"));
   }

   @Test
   public void shouldRemoveWithQueryParam() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      Node child = root.createChild("child_node");

      Assert.assertFalse(root.getChildren().isEmpty());
      Assert.assertEquals(child, root.getChildren().get(0));

      Pattern pattern = new Pattern(child.getName());
      List<Node> removedNodes = root.removeChildren(pattern);
      Assert.assertNotNull(removedNodes);
      Assert.assertFalse(removedNodes.isEmpty());
      Assert.assertEquals(1, removedNodes.size());
   }

   @Test
   public void shouldNotRemoveWithQueryParam() throws Exception
   {
      Node root = new Node(ROOT_NODE);
      Node child = root.createChild("child_node");

      Assert.assertFalse(root.getChildren().isEmpty());
      Assert.assertEquals(child, root.getChildren().get(0));

      Pattern pattern = new Pattern("some_other_NODE");
      List<Node> removedNodes = root.removeChildren(pattern);
      Assert.assertTrue(removedNodes.isEmpty());
   }

   @Test
   public void shouldNotBeCommentByDefault()
   {
      final Node node = new Node(ROOT_NODE);
      Assert.assertEquals("A Node should not be a comment by default", false, node.isComment());
   }

   @Test
   public void shouldBeAbleToMarkAsComment()
   {
      final Node node = new Node(ROOT_NODE);
      node.setComment(true);
      Assert.assertEquals("A Node set as comment should report as comment", true, node.isComment());
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotBeAbleToSetNodeWitHChildrenAsComment()
   {
      final Node node = new Node(ROOT_NODE);
      node.createChild(CHILD_1_NODE);
      node.setComment(true);
   }

   @Test
   public void shouldBeAbleToAccessRootFromChildNode()
   {
      // given
      final Node root = new Node(ROOT_NODE);
      final Node child = root.createChild(CHILD_1_NODE);

      // when
      final Node actualRoot = child.getRoot();

      // then
      Assert.assertEquals("Obtained wrong root", root, actualRoot);
   }

   @Test
   public void shouldBeAbleToAccessRootAnyDescendantNode()
   {
      // given
      final Node root = new Node(ROOT_NODE);
      final Node descendantNode = root.createChild(CHILD_1_NODE).createChild(CHILD_1_1_NODE).getParent().getParent()
               .createChild(CHILD_2_NODE).createChild(CHILD_2_1_NODE).getParent().createChild(CHILD_2_2_NODE);

      // when
      final Node actualRoot = descendantNode.getRoot();

      // then
      Assert.assertEquals("Obtained wrong root", root, actualRoot);
   }

   /**
    * Ensures that {@link Node#isRoot()} is working as contracted
    */
   @Test
   public void shouldBeAbleToReportAsRoot()
   {
      final Node root = new Node(ROOT_NODE);
      final Node child = root.createChild(CHILD_1_NODE);
      Assert.assertTrue("Root should report as root", root.isRoot());
      Assert.assertFalse("Child should not report as root", child.isRoot());
   }

   @Test
   public void shouldRemoveAttributeByName() throws Exception
   {
      Node root = new Node(ROOT_NODE).attribute(ATTR_NAME, ATTR_VALUE_1);
      Assert.assertEquals("attribute should exist", root.getAttribute(ATTR_NAME), ATTR_VALUE_1);

      Assert.assertEquals("attribute value should be returned on removal", root.removeAttribute(ATTR_NAME),
               ATTR_VALUE_1);
      Assert.assertNull("attribute should no longer exist", root.getAttribute(ATTR_NAME));
   }

   @Test
   public void shouldReturnNullWhenAttemptingToRemoveAttributeThatDoesNotExist() throws Exception
   {
      String bogusAttribute = "SOME_NONEXISTANT_ATTR";

      Node root = new Node(ROOT_NODE).attribute(ATTR_NAME, ATTR_VALUE_1);
      Assert.assertEquals("attribute should exist", root.getAttribute(ATTR_NAME), ATTR_VALUE_1);
      final String shouldBeNull = root.removeAttribute(bogusAttribute);
      Assert.assertNull("Attempting to remove an attribute which does not exist should return null", shouldBeNull);
   }

   @Test(expected = IllegalArgumentException.class)
   public void removeNullAttributeShouldThrowException()
   {
      Node root = new Node(ROOT_NODE);
      root.removeAttribute(null);
   }

   @Test
   public void shouldNotMatchAChildsChildrenOnGet()
   {
      // given /root/child1/child2
      Node root = new Node(ROOT_NODE).createChild(CHILD_1_NODE).createChild(CHILD_2_NODE).getRoot();

      // when
      List<Node> child = root.get(CHILD_2_NODE);

      // then
      Assert.assertTrue("Should not find matching child", child.isEmpty());
   }

   @Test
   public void shouldNotMatchAChildsChildrenOnGetSingle()
   {
      // given /root/child1/child2
      Node root = new Node(ROOT_NODE).createChild(CHILD_1_NODE).createChild(CHILD_2_NODE).getRoot();
      // when
      Node child = root.getSingle(CHILD_2_NODE);

      // then
      Assert.assertNull(child);
   }

   @Test
   public void shouldNotMatchAChildsChildrenOnGetOrCreate()
   {
      // given /root/child1/child2
      Node root = new Node(ROOT_NODE);
      Node child1 = root.createChild(CHILD_1_NODE);
      Node child2 = child1.createChild(CHILD_2_NODE);

      // when
      Node createdChild = root.getOrCreate(CHILD_2_NODE);

      // then
      Assert.assertNotSame(createdChild, child2);
   }

   @Test
   public void shouldNotMatchAChildsChildrenOnRemoveChild()
   {
      // given /root/child1/child2
      Node root = new Node(ROOT_NODE).createChild(CHILD_1_NODE).createChild(CHILD_2_NODE).getRoot();
      // when
      Node removedChild = root.removeChild(CHILD_2_NODE);

      // then
      Assert.assertNull(removedChild);
   }

   @Test
   public void shouldNotMatchAChildsChildrenOnRemoveChildren()
   {
      // given /root/child1/child2
      Node root = new Node(ROOT_NODE).createChild(CHILD_1_NODE).createChild(CHILD_2_NODE).getRoot();

      // when
      List<Node> removed = root.removeChildren(CHILD_2_NODE);

      // then
      Assert.assertNotNull(removed);
      Assert.assertEquals(0, removed.size());
   }

   @Test
   public void shouldBeAbleToFindAChildWithTextValue() throws Exception
   {
      // given
      Node root = createTree();

      // when
      Node found = root.getSingle(("/" + CHILD_3_NODE + "=" + CHILD_3_TEXT));

      // then
      Assert.assertEquals("Verify correct node found", CHILD_3_NODE, found.getName());
      Assert.assertEquals("Verify correct node value", CHILD_3_TEXT, found.getText());
   }

   @Test
   public void shouldBeAbleToFindAExpressedChild() throws Exception
   {
      // given
      Node root = createTree();

      // when
      Node found = root.getSingle((CHILD_1_NODE + "/" + CHILD_1_1_NODE));

      // when
      Assert.assertNotNull("Verify a node as found", found);

      Assert.assertEquals("Verify correct node found", CHILD_1_1_NODE, found.getName());
   }

   @Test
   public void shouldBeAbleToFindDescendantWithAttribute() throws Exception
   {
      // given
      Node root = createTree();

      // when
      Node found = root.getSingle(("/" + CHILD_2_NODE + "/" + CHILD_2_1_NODE + "@" + ATTR_NAME + "=" + ATTR_VALUE_1));

      // then
      Assert.assertNotNull("Verify a node was found", found);

      Assert.assertEquals("Verify correct node found", CHILD_2_1_NODE, found.getName());

      Assert.assertEquals("Verify correct node found", ATTR_VALUE_1, found.getAttribute(ATTR_NAME));
   }

   @Test
   public void shouldBeAbleToGetNodeWithTextValues()
   {
      // given
      Node root = new Node(ROOT_NODE);
      root.getOrCreate(("/" + CHILD_2_NODE));
      root.getOrCreate(("/" + CHILD_3_NODE));
      root.getOrCreate(("/" + CHILD_3_NODE + "=" + CHILD_3_TEXT));
      root.getOrCreate(("/" + CHILD_3_NODE + "=" + CHILD_3_TEXT + "diff"));

      // when
      Node found = root.getSingle(("/" + CHILD_3_NODE + "=" + CHILD_3_TEXT));

      // then
      Assert.assertNotNull("Verify node was found", found);
      Assert.assertEquals("Verify correct node created", CHILD_3_NODE, found.getName());
      Assert.assertEquals("Verify correct value set", CHILD_3_TEXT, found.getText());
      Assert.assertEquals("Verify root only has four children", 4, root.getChildren().size());
   }

   @Test
   public void shouldBeAbleToCreateNodeWithValueWithEqualsInIt()
   {
      String child3Value = "omg\\/with\\/slashes";
      String child3ValueNotEscaped = "omg/with/slashes";
      // given
      Node root = new Node(ROOT_NODE);
      root.getOrCreate(("/" + CHILD_2_NODE + "/" + CHILD_3_NODE + "=" + child3Value));

      // when
      Node found = root.getSingle(("/" + CHILD_2_NODE + "/" + CHILD_3_NODE));

      // then
      Assert.assertNotNull("Verify node was found", found);
      Assert.assertEquals("Verify correct value set", child3ValueNotEscaped, found.getText());
   }

}