package org.jboss.forge.furnace;

import javax.inject.Inject;
import javax.xml.xpath.XPathFactory;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.addons.Addon;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class XPathLookupTest
{
   @Deployment
   public static ForgeArchive getDeployment()
   {
      ForgeArchive archive = ShrinkWrap.create(ForgeArchive.class)
               .addBeansXML();

      return archive;
   }

   @Inject
   private Addon addon;

   @Test
   public void testGetJDKProvidedXPathImpl() throws Exception
   {
      try
      {
         addon.getClassLoader().loadClass("com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.org.apache.xpath.internal.jaxp.XPathFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.xml.internal.stream.events.XMLEventFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.xml.internal.stream.XMLInputFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.xml.internal.stream.XMLOutputFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.org.apache.xerces.internal.jaxp.datatype.DatatypeFactoryImpl");
         addon.getClassLoader().loadClass("com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory");
         addon.getClassLoader().loadClass("com.sun.org.apache.xerces.internal.parsers.SAXParser");
      }
      catch (Exception e)
      {
         Assert.fail("Could not load required Factory class." + e.getMessage());
      }
   }


   @Test
   public void testXPathFactoryLookup()
   {
      Assert.assertNotNull(XPathFactory.newInstance().newXPath());
   }
   
}
