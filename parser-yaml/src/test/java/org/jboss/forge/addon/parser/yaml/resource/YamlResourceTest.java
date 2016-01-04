/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.yaml.resource;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class YamlResourceTest
{
   private ResourceFactory resourceFactory;
   private File yamlFile;

   @Before
   public void setUp() throws Exception
   {
      resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
      yamlFile = File.createTempFile("file", ".yaml");
   }

   @Test
   public void testEmptyYamlShouldNotBePresent() throws Exception
   {
      YamlResource resource = resourceFactory.create(YamlResource.class, yamlFile);
      Assert.assertThat(resource.getModel().isPresent(), is(false));
   }

   @Test
   public void testYamlResource() throws Exception
   {
      Resource<File> resource = resourceFactory.create(yamlFile);
      Assert.assertThat(resource, instanceOf(YamlResource.class));
   }

   @Test
   public void testLoadModel() throws Exception
   {
      YamlResource resource = resourceFactory.create(YamlResource.class, yamlFile);
      resource.setContents("name: George\nnumber: 42");
      Map<String, Object> model = resource.getModel().get();
      Assert.assertThat(model.get("name"), is("George"));
      Assert.assertThat(model.get("number"), is(42));
   }

   @Test
   public void testLoadAllModel() throws Exception
   {
      YamlResource resource = resourceFactory.create(YamlResource.class, yamlFile);
      resource.setContents("name: George\nnumber: 42\n---\nname: Lincoln\nnumber: 24");
      List<Map<String, Object>> allModel = resource.getAllModel();
      Assert.assertThat(allModel.size(), is(2));
      {
         Map<String, Object> model = allModel.get(0);
         Assert.assertThat(model.get("name"), is("George"));
         Assert.assertThat(model.get("number"), is(42));
      }
      {
         Map<String, Object> model = allModel.get(1);
         Assert.assertThat(model.get("name"), is("Lincoln"));
         Assert.assertThat(model.get("number"), is(24));
      }
   }

   @Test
   public void testSaveModel() throws Exception
   {
      YamlResource resource = resourceFactory.create(YamlResource.class, yamlFile);
      Map<String, Object> model = new LinkedHashMap<>();
      model.put("name", "George");
      model.put("number", 42);
      resource.setContents(model);
      Assert.assertEquals("name: George\nnumber: 42\n", resource.getContents());
   }

   @Test
   public void testSaveAllModel() throws Exception
   {
      YamlResource resource = resourceFactory.create(YamlResource.class, yamlFile);
      List<Map<String, Object>> allModel = new ArrayList<>();
      {
         Map<String, Object> model = new LinkedHashMap<>();
         model.put("name", "George");
         model.put("number", 42);
         allModel.add(model);
      }
      {
         Map<String, Object> model = new LinkedHashMap<>();
         model.put("name", "Lincoln");
         model.put("number", 24);
         allModel.add(model);
      }
      resource.setContents(allModel);
      Assert.assertEquals("name: George\nnumber: 42\n---\nname: Lincoln\nnumber: 24\n", resource.getContents());
   }

   @Test
   public void testAnyFileAsYamlResource() throws Exception
   {
      File tmpFile = null;
      try
      {
         tmpFile = File.createTempFile("tmp", "");
         Resource<File> resource = resourceFactory.create(YamlResource.class, tmpFile);
         Assert.assertThat(resource, instanceOf(YamlResource.class));
      }
      finally
      {
         tmpFile.delete();
      }
   }

   @After
   public void tearDown()
   {
      yamlFile.delete();
   }
}
