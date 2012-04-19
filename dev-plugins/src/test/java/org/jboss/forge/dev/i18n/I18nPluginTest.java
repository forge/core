package org.jboss.forge.dev.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.PropertiesFileResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;

public class I18nPluginTest extends AbstractShellTest
{

   @Test
   public void testSetup() throws Exception
   {
      Project project = initializeJavaProject();
      Shell shell = getShell();
      shell.execute("i18n setup");
      PropertiesFileResource resource = (PropertiesFileResource) project.getFacet(ResourceFacet.class).getResource(
               "messages.properties");
      assertTrue(resource.exists());
   }

   @Test
   public void testPutProperty() throws Exception
   {
      Project project = initializeJavaProject();
      Shell shell = getShell();
      shell.execute("i18n setup");
      PropertiesFileResource resource = (PropertiesFileResource) project.getFacet(ResourceFacet.class).getResource(
               "messages.properties");
      shell.execute("i18n put --key mykey --value myvalue");
      assertEquals("myvalue", resource.getProperty("mykey"));
      shell.execute("i18n put --key mykey --value Ol\u00e1");
      assertEquals("Ol\u00e1", resource.getProperty("mykey"));
   }

   @Test
   public void testGetProperty() throws Exception
   {
      Project project = initializeJavaProject();
      Shell shell = getShell();
      shell.execute("i18n setup");
      PropertiesFileResource resource = (PropertiesFileResource) project.getFacet(ResourceFacet.class).getResource(
               "messages.properties");
      shell.execute("i18n put --key mykey --value myvalue");
      assertEquals("myvalue", resource.getProperty("mykey"));
      shell.execute("i18n get --key mykey");
      // TODO: Test output
   }

   @Test
   public void testAddLocale() throws Exception
   {
      Project project = initializeJavaProject();
      Shell shell = getShell();
      shell.execute("i18n setup");
      shell.execute("i18n put --key mykey --value myvalue");
      shell.execute("i18n add-locale --locale pt_BR");
      shell.execute("i18n put --key mykey --value meuvalor");
      ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
      PropertiesFileResource resource = (PropertiesFileResource) resourceFacet.getResource("messages.properties");
      PropertiesFileResource resource_ptBR = (PropertiesFileResource) resourceFacet
               .getResource("messages_pt_BR.properties");
      assertTrue(resource.exists());
      assertTrue(resource_ptBR.exists());
      assertEquals("myvalue", resource.getProperty("mykey"));
      assertEquals("meuvalor", resource_ptBR.getProperty("mykey"));
   }

   @Test
   public void testRemoveProperty() throws Exception
   {
      Project project = initializeJavaProject();
      Shell shell = getShell();
      shell.execute("i18n setup");
      shell.execute("i18n put --key mykey --value myvalue");
      shell.execute("i18n add-locale --locale pt_BR");
      shell.execute("i18n put --key mykey --value meuvalor");
      shell.execute("i18n remove --key mykey");
      ResourceFacet resourceFacet = project.getFacet(ResourceFacet.class);
      PropertiesFileResource resource = (PropertiesFileResource) resourceFacet.getResource("messages.properties");
      PropertiesFileResource resource_ptBR = (PropertiesFileResource) resourceFacet
               .getResource("messages_pt_BR.properties");
      assertNull(resource.getProperty("mykey"));
      assertNull(resource_ptBR.getProperty("mykey"));
   }


   @Test
   public void testFacesConfig() throws Exception
   {
      Project project = initializeJavaProject();
      queueInputLines("Y", "Y", "Y");
      getShell().execute("faces setup");
      getShell().execute("i18n setup");
      FileResource<?> configFile = project.getFacet(FacesFacet.class).getConfigFile();
      assertTrue(configFile.exists());
      Node facesConfig = XMLParser.parse(configFile.getResourceInputStream());
      assertEquals("messages",facesConfig.getTextValueForPatternName("application/resource-bundle/base-name"));
      assertEquals("msg",facesConfig.getTextValueForPatternName("application/resource-bundle/var"));
   }


   @Test
   public void testGetBaseBundleName() throws Exception
   {
      assertEquals("ApplicationResources", I18nPlugin.getBaseBundleName("ApplicationResources_pt_BR.properties"));

   }
}
