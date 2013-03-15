/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;


/**
 * Utility class for building sample tree used in tests.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 * 
 * @version $Revision: $
 */
public final class TestTreeBuilder
{

   public static final String ATTR_NAME = "attr_name";
   public static final String OTHER_NAME = "other_name";
   public static final String ATTR_VALUE_1 = "attr_value_1";
   public static final String ATTR_VALUE_2 = "attr_value_2";
   public static final String ROOT_NODE = "root";
   public static final String CHILD_1_NODE = "child-1";
   public static final String CHILD_1_1_NODE = "child-1.1";
   public static final String CHILD_1_2_NODE = "child-1.2";
   public static final String CHILD_2_NODE = "child-2";
   public static final String CHILD_2_1_NODE = "child-2.1";
   public static final String CHILD_2_2_NODE = "child-2.2";
   public static final String CHILD_2_1_1_NODE = "child-2.1.1";
   public static final String CHILD_3_TEXT = "child-3-text";
   public static final String CHILD_3_NODE = "child-3";

   private TestTreeBuilder()
   {
   }

   public static Node createTree()
   {
      Node root = new Node(ROOT_NODE);
      Node child1 = root.createChild(CHILD_1_NODE);

      child1.createChild(CHILD_1_1_NODE).attribute(ATTR_NAME, ATTR_VALUE_1);
      child1.createChild(CHILD_1_2_NODE).attribute(ATTR_NAME, ATTR_VALUE_1);

      Node child2 = root.createChild(CHILD_2_NODE);
      child2.createChild(CHILD_2_1_NODE).attribute(ATTR_NAME, ATTR_VALUE_1)
               .createChild(CHILD_2_1_1_NODE).attribute(OTHER_NAME, ATTR_VALUE_1)
               .attribute(ATTR_NAME, ATTR_VALUE_2);

      // same node name, but different attribute value
      child2.createChild(CHILD_2_1_NODE).attribute(ATTR_NAME, ATTR_VALUE_2)
               .createChild(CHILD_2_1_1_NODE).attribute(OTHER_NAME, ATTR_VALUE_1)
               .attribute(ATTR_NAME, ATTR_VALUE_1);

      root.createChild(CHILD_3_NODE).text(CHILD_3_TEXT);
      return root;
   }
}
