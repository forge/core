/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.parser.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 */
public class XMLParser
{
   public static InputStream toXMLInputStream(final Node node)
   {
      return new ByteArrayInputStream(toXMLByteArray(node));
   }

   public static String toXMLString(final Node node)
   {
      return new String(toXMLByteArray(node));
   }

   public static byte[] toXMLByteArray(final Node node)
   {
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document root = builder.newDocument();

         writeRecursive(root, node);

         Transformer transformer = TransformerFactory.newInstance().newTransformer();
         transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
         transformer.setOutputProperty(OutputKeys.INDENT, "yes");

         ByteArrayOutputStream stream = new ByteArrayOutputStream();
         StreamResult result = new StreamResult(stream);
         transformer.transform(new DOMSource(root), result);

         return stream.toByteArray();
      }
      catch (Exception e)
      {
         throw new XMLParserException("Could not export Node strcuture to XML", e);
      }
   }

   public static Node parse(final byte[] xml)
   {
      return parse(new ByteArrayInputStream(xml));
   }

   public static Node parse(final String xml)
   {
      return parse(xml.getBytes());
   }

   public static Node parse(final InputStream stream) throws XMLParserException
   {
      try
      {
         if (stream.available() == 0)
         {
            return null;
         }

         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document doc = builder.parse(stream);

         Node root = new Node(doc.getDocumentElement().getNodeName());
         readRecursive(root, doc.getDocumentElement());
         return root;

      }
      catch (Exception e)
      {
         throw new XMLParserException("Could not import XML from stream", e);
      }
   }

   private static void readRecursive(final Node target, final org.w3c.dom.Node source)
   {
      readAttributes(target, source);
      final NodeList sourceChildren = source.getChildNodes();
      if (sourceChildren != null)
      {
         for (int i = 0; i < sourceChildren.getLength(); i++)
         {
            final org.w3c.dom.Node child = sourceChildren.item(i);
            if (child.getNodeType() != org.w3c.dom.Node.TEXT_NODE)
            {
               // Create our representation of the Node
               final Node newTarget = target.createChild(child.getNodeName());

               if (onlyTextChildren(child))
               {
                  // See if we're dealing with a comment and mark specifically
                  if (child.getNodeType() == org.w3c.dom.Node.COMMENT_NODE)
                  {
                     newTarget.setComment(true);
                  }

                  // Set text
                  newTarget.text(child.getTextContent());
                  readAttributes(newTarget, child);
               }
               else
               {
                  readRecursive(newTarget, child);
               }
            }
         }
      }
   }

   private static void writeRecursive(final org.w3c.dom.Node target, final Node source)
   {
      Document owned = target.getOwnerDocument();
      if (owned == null)
      {
         owned = (Document) target;
      }

      org.w3c.dom.Node targetChild;
      if (NodeType.COMMENT.getNodeName().equals(source.getName()))
      {
         targetChild = owned.createComment(source.getText());
      }
      else if (NodeType.CDATA_SECTION.getNodeName().equals(source.getName()))
      {
         targetChild = owned.createCDATASection(source.getText());
      }
      else
      {
         targetChild = owned.createElement(source.getName());
         if (source.getText() != null)
         {
            targetChild.appendChild(owned.createTextNode(source.getText()));
         }
      }

      target.appendChild(targetChild);

      for (Map.Entry<String, String> attribute : source.getAttributes().entrySet())
      {
         Attr attr = owned.createAttribute(attribute.getKey());
         attr.setValue(attribute.getValue());

         targetChild.getAttributes().setNamedItem(attr);
      }
      for (Node sourceChild : source.getChildren())
      {
         writeRecursive(targetChild, sourceChild);
      }
   }

   private static void readAttributes(final Node target, final org.w3c.dom.Node source)
   {
      final NamedNodeMap attributes = source.getAttributes();
      if (attributes != null)
      {
         for (int i = 0; i < attributes.getLength(); i++)
         {
            final org.w3c.dom.Node attribute = attributes.item(i);
            target.attribute(attribute.getNodeName(), attribute.getNodeValue());
         }
      }
   }

   private static boolean onlyTextChildren(final org.w3c.dom.Node source)
   {
      final NodeList children = source.getChildNodes();
      for (int i = 0; i < children.getLength(); i++)
      {
         final org.w3c.dom.Node child = children.item(i);
         if (child.getNodeType() != org.w3c.dom.Node.TEXT_NODE)
         {
            return false;
         }
      }
      return true;
   }

   public enum NodeType
   {
      COMMENT("#comment"),
      CDATA_SECTION("#cdata-section");

      private final String nodeName;

      private NodeType(final String nodeName)
      {
         this.nodeName = nodeName;
      }

      public String getNodeName()
      {
         return nodeName;
      }
   }

}
