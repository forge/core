/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.parser.xml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

   public static Node parse(final File file) throws XMLParserException, FileNotFoundException
   {
      FileInputStream fis = null;
      try
      {
         fis = new FileInputStream(file);
         return parse(new BufferedInputStream(fis, 2048));
      }
      finally
      {
         try
         {
            if (fis != null)
               fis.close();
         }
         catch (IOException ignored)
         {
         }
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
