package org.jboss.forge.addon.parser.xml;

/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.parser.xml.Node;
import org.jboss.forge.addon.parser.xml.XMLParser;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class XMLParserTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.furnace.container:cdi"),
            @AddonDependency(name = "org.jboss.forge.addon:parser-xml")
   })
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap
               .create(ForgeArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-xml")
               );

      return archive;
   }

   private final static String XML_STRING = 
      "<parent attr1='1' attr2='2'> " +
      "  <child num='1'/>           " +
      "  <child num='2'/>           " +
      "</parent>                    ";

//   @Inject
//   private XMLParser xmlParser;
   
   @Test
   public void testXMLParser() throws Exception
   {
      System.out.println("**** testXMLParser ****");
      Node node = helloService.parse(XML_STRING);
      Assert.assertEquals("parent", node.getName());
   }
   
   @Inject
   private HelloService helloService;
   
   @Test
   public void testHelloService() throws Exception
   {
      helloService.sayHello();
   }
}
