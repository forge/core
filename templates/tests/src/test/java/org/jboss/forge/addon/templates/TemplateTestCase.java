/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.templates.freemarker.FreemarkerTemplate;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.addons.AddonRegistry;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TemplateTestCase
{
   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.addon:templates"),
            @AddonDeployment(name = "org.jboss.forge.addon:resources") })
   public static AddonArchive getDeployment()
   {
      String packagePath = TemplateTestCase.class.getPackage().getName().replace('.', '/');
      AddonArchive archive = ShrinkWrap.create(AddonArchive.class)
               .addBeansXML()
               .addClass(JavaBean.class)
               .addAsResource(TemplateTestCase.class.getResource("template.ftl"), packagePath + "/template.ftl")
               .addAsResource(TemplateTestCase.class.getResource("includes.ftl"), packagePath + "/includes.ftl")
               .addAsServiceProvider("org.jboss.forge.furnace.container.simple.Service",
                        TemplateTestCase.class.getName())
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:simple"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:templates"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"));

      return archive;
   }

   private ResourceFactory resourceFactory;
   private TemplateFactory templateFactory;

   @Before
   public void setUp()
   {
      AddonRegistry addonRegistry = SimpleContainer.getFurnace(getClass().getClassLoader()).getAddonRegistry();
      this.resourceFactory = addonRegistry.getServices(ResourceFactory.class).get();
      this.templateFactory = addonRegistry.getServices(TemplateFactory.class).get();
   }

   @Test
   public void testProcessorFactoryInjection() throws Exception
   {
      Assert.assertNotNull(templateFactory);
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testTemplateProcessor() throws Exception
   {
      String templateContents = "Hello ${name}!";
      String expected = "Hello JBoss Forge!";
      File tempFile = File.createTempFile("template", ".tmp");
      tempFile.deleteOnExit();
      FileResource resource = resourceFactory.create(tempFile).reify(FileResource.class);
      resource.setContents(templateContents);
      Template template = templateFactory.create(resource, FreemarkerTemplate.class);
      String actual = template.process(Collections.singletonMap("name", "JBoss Forge"));
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testClasspathTemplateProcessor() throws Exception
   {
      URL templateURL = getClass().getResource("template.ftl");
      Assert.assertNotNull(templateURL);
      String expected = "Hello JBoss Forge!";
      Resource<?> resource = resourceFactory.create(templateURL);
      Template template = templateFactory.create(resource, FreemarkerTemplate.class);
      String actual = template.process(Collections.singletonMap("name", "JBoss Forge"));
      Assert.assertEquals(expected, actual);
   }

   @Test
   @SuppressWarnings("rawtypes")
   public void testTemplateProcessorJavaBean() throws Exception
   {
      String templateContents = "Hello ${name}!";
      String expected = "Hello JBoss Forge!";
      File tempFile = File.createTempFile("template", ".tmp");
      tempFile.deleteOnExit();
      FileResource resource = resourceFactory.create(tempFile).reify(FileResource.class);
      resource.setContents(templateContents);
      Template template = templateFactory.create(resource, FreemarkerTemplate.class);
      JavaBean dataModel = new JavaBean();
      dataModel.setName("JBoss Forge");
      String actual = template.process(dataModel);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testIncludesTemplateProcessor() throws Exception
   {
      URL templateURL = getClass().getResource("includes.ftl");
      Assert.assertNotNull(templateURL);
      String expected = "Hello JBoss Forge! And Goodbye JBoss Forge!";
      Resource<?> resource = resourceFactory.create(templateURL);
      Template template = templateFactory.create(resource, FreemarkerTemplate.class);
      String actual = template.process(Collections.singletonMap("name", "JBoss Forge"));
      Assert.assertEquals(expected, actual);
   }

}