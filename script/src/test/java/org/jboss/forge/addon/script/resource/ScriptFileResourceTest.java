/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.script.resource;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;

import java.io.File;
import java.nio.file.Files;

import javax.inject.Inject;
import javax.script.ScriptContext;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.script.ScriptContextBuilder;
import org.jboss.forge.addon.script.impl.ForgeScriptEngineFactory;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ScriptFileResourceTest
{
   @Inject
   private ResourceFactory resourceFactory;

   @Inject
   private ForgeScriptEngineFactory engineFactory;

   @Test
   public void testScriptFileResource() throws Exception
   {
      File file = File.createTempFile("forgescript", ".fsh");
      file.deleteOnExit();
      Resource<File> resource = resourceFactory.create(file);
      Assert.assertThat(resource, instanceOf(ScriptFileResource.class));
      ScriptFileResource scriptResource = (ScriptFileResource) resource;
      Assert.assertEquals(engineFactory.getEngineName(), scriptResource.getEngineName());
      Assert.assertEquals(engineFactory.getLanguageName(), scriptResource.getEngineLanguageName());
      Assert.assertEquals(engineFactory.getLanguageVersion(), scriptResource.getEngineLanguageVersion());
   }

   @Test
   public void testScriptFileResourceExecution() throws Exception
   {
      File file = File.createTempFile("forgescript", ".fsh");
      Files.write(file.toPath(), "touch foo.txt".getBytes());
      file.deleteOnExit();

      Resource<File> resource = resourceFactory.create(file);
      DirectoryResource tmpDir = resourceFactory.create(OperatingSystemUtils.createTempDir())
               .reify(DirectoryResource.class);
      tmpDir.deleteOnExit();
      Assert.assertThat(resource, instanceOf(ScriptFileResource.class));
      ScriptFileResource scriptResource = (ScriptFileResource) resource;
      ScriptContext context = ScriptContextBuilder.create().currentResource(tmpDir).build();
      Assert.assertThat(tmpDir.getChild("foo.txt").exists(), is(false));
      scriptResource.eval(context);
      Assert.assertThat(tmpDir.getChild("foo.txt").exists(), is(true));
   }

   @Test
   public void testScriptFileResourceJavascript() throws Exception
   {
      File file = File.createTempFile("script", ".js");
      Files.write(file.toPath(), "var a = 1;".getBytes());
      file.deleteOnExit();

      Resource<File> resource = resourceFactory.create(file);
      Assert.assertThat(resource, instanceOf(ScriptFileResource.class));
      ScriptFileResource scriptResource = (ScriptFileResource) resource;
      ScriptContext context = ScriptContextBuilder.create().build();
      scriptResource.eval(context);
      Assert.assertEquals(1.0D, context.getAttribute("a"));
   }

}
