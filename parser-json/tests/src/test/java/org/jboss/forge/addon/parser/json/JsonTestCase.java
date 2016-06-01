/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.json;

import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParser.Event;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.parser.json.resource.JsonResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceException;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.arquillian.AddonDeployment;
import org.jboss.forge.arquillian.AddonDeployments;
import org.jboss.forge.arquillian.archive.AddonArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class JsonTestCase
{

   @Deployment
   @AddonDeployments({
            @AddonDeployment(name = "org.jboss.forge.furnace.container:cdi"),
            @AddonDeployment(name = "org.jboss.forge.addon:resources"),
            @AddonDeployment(name = "org.jboss.forge.addon:parser-json")
   })
   public static AddonArchive getDeployment()
   {
      AddonArchive archive = ShrinkWrap
               .create(AddonArchive.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:parser-json"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:resources"));
      return archive;
   }

   @Inject
   private ResourceFactory resourceFactory;

   @Test
   public void testJsonBuilder() throws Exception
   {
      JsonObject model = Json.createObjectBuilder()
               .add("firstName", "Duke")
               .add("lastName", "Java")
               .add("age", 18)
               .add("streetAddress", "100 Internet Dr")
               .add("city", "JavaTown")
               .add("state", "JA")
               .add("postalCode", "12345")
               .add("phoneNumbers", Json.createArrayBuilder()
                        .add(Json.createObjectBuilder()
                                 .add("type", "mobile")
                                 .add("number", "111-111-1111"))
                        .add(Json.createObjectBuilder()
                                 .add("type", "home")
                                 .add("number", "222-222-2222")))
               .build();
      Assert.assertNotNull(model);
   }

   @Test
   public void testJsonRead()
   {
      String jsonData = "{\"firstName\": \"George\", \"lastName\": \"Gastaldi\"}";
      JsonParser parser = Json.createParser(new StringReader(jsonData));
      Assert.assertTrue(parser.hasNext());
      Assert.assertEquals(Event.START_OBJECT, parser.next());
      Assert.assertTrue(parser.hasNext());
      Assert.assertEquals(Event.KEY_NAME, parser.next());
      Assert.assertEquals("firstName", parser.getString());
      Assert.assertTrue(parser.hasNext());
      Assert.assertEquals(Event.VALUE_STRING, parser.next());
      Assert.assertEquals("George", parser.getString());
      Assert.assertTrue(parser.hasNext());
      Assert.assertEquals(Event.KEY_NAME, parser.next());
      Assert.assertEquals("lastName", parser.getString());
      Assert.assertTrue(parser.hasNext());
      Assert.assertEquals(Event.VALUE_STRING, parser.next());
      Assert.assertEquals("Gastaldi", parser.getString());
      Assert.assertTrue(parser.hasNext());
      Assert.assertEquals(Event.END_OBJECT, parser.next());
      Assert.assertFalse(parser.hasNext());
   }

   @Test
   public void testResourceFactoryLookup() throws Exception
   {
      File tmpFile = File.createTempFile("parser_json_test", ".json");
      tmpFile.deleteOnExit();
      Resource<File> resource = resourceFactory.create(tmpFile);
      Assert.assertThat(resource, CoreMatchers.instanceOf(JsonResource.class));
   }

   @Test
   public void testJsonResourceDataRead() throws Exception
   {
      File tmpFile = File.createTempFile("parser_json_test", ".json");
      tmpFile.deleteOnExit();
      String jsonData = "{\"firstName\": \"George\", \"lastName\": \"Gastaldi\"}";
      Files.write(tmpFile.toPath(), jsonData.getBytes());
      Resource<File> resource = resourceFactory.create(tmpFile);
      Assert.assertThat(resource, CoreMatchers.instanceOf(JsonResource.class));
      JsonResource jsonResource = resource.reify(JsonResource.class);
      JsonObject jsonObject = jsonResource.getJsonObject();
      Assert.assertNotNull(jsonObject);
      Assert.assertEquals("George", jsonObject.getString("firstName"));
      Assert.assertEquals("Gastaldi", jsonObject.getString("lastName"));
   }

   @Test(expected = ResourceException.class)
   public void testJsonResourceDataReadEmptyFile() throws Exception
   {
      File tmpFile = File.createTempFile("parser_json_test", ".json");
      tmpFile.deleteOnExit();
      Resource<File> resource = resourceFactory.create(tmpFile);
      Assert.assertThat(resource, CoreMatchers.instanceOf(JsonResource.class));
      JsonResource jsonResource = resource.reify(JsonResource.class);
      jsonResource.getJsonObject();
   }

   @Test
   public void testJsonResourceDataWriteJsonObject() throws Exception
   {
      File tmpFile = File.createTempFile("parser_json_test", ".json");
      tmpFile.deleteOnExit();
      JsonObject jsonObject = Json.createObjectBuilder().add("firstName", "George").add("lastName", "Gastaldi").build();
      Resource<File> resource = resourceFactory.create(tmpFile);
      Assert.assertThat(resource, CoreMatchers.instanceOf(JsonResource.class));
      JsonResource jsonResource = resource.reify(JsonResource.class);
      jsonResource.setContents(jsonObject);
      jsonObject = jsonResource.getJsonObject();
      Assert.assertNotNull(jsonObject);
      Assert.assertEquals("George", jsonObject.getString("firstName"));
      Assert.assertEquals("Gastaldi", jsonObject.getString("lastName"));
   }

   // FORGE-2500
   @Test
   public void testJsonResourceDataWriteEmptyArrayString() throws Exception
   {
      File tmpFile = File.createTempFile("parser_json_test", ".json");
      tmpFile.deleteOnExit();
      Resource<File> resource = resourceFactory.create(tmpFile);
      Assert.assertThat(resource, CoreMatchers.instanceOf(JsonResource.class));
      JsonResource jsonResource = resource.reify(JsonResource.class);
      jsonResource.setContents("[]"); // Json.createArrayBuilder().build().toString()
      Assert.assertTrue(jsonResource.getJsonArray().isEmpty());
   }
}
