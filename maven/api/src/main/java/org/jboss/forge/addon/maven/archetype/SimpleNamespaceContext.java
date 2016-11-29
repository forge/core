/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.archetype;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
class SimpleNamespaceContext implements NamespaceContext
{
   private Map<String, String> prefix2Ns = new HashMap<>();
   private Map<String, String> ns2Prefix = new HashMap<>();

   public SimpleNamespaceContext()
   {
      prefix2Ns.put(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
      ns2Prefix.put(XMLConstants.XML_NS_URI, XMLConstants.XML_NS_PREFIX);
      prefix2Ns.put(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
      ns2Prefix.put(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, XMLConstants.XMLNS_ATTRIBUTE);
   }

   @Override
   public String getNamespaceURI(String prefix)
   {
      return prefix2Ns.get(prefix);
   }

   @Override
   public String getPrefix(String namespaceURI)
   {
      return ns2Prefix.get(namespaceURI);
   }

   @Override
   public Iterator<String> getPrefixes(String namespaceURI)
   {
      return prefix2Ns.keySet().iterator();
   }

   /**
    * Registers prefix - namespace URI mapping
    *
    * @param prefix
    * @param namespaceURI
    */
   public void registerMapping(String prefix, String namespaceURI)
   {
      prefix2Ns.put(prefix, namespaceURI);
      ns2Prefix.put(namespaceURI, prefix);
   }
}
