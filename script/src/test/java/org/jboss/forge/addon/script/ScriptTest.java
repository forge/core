/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.io.File;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.script.impl.ForgeScriptEngineFactory;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.OperatingSystemUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class ScriptTest
{
   protected ForgeScriptEngineFactory engineFactory;
   protected ResourceFactory resourceFactory;

   @Before
   public void setUp()
   {
      engineFactory = SimpleContainer.getServices(getClass().getClassLoader(), ForgeScriptEngineFactory.class).get();
      resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class).get();
   }

   @Test
   public void testLookupScriptEngineFactoryThroughContainer() throws Exception
   {
      Assert.assertThat(engineFactory, notNullValue());
   }

   @Test
   public void testLookupScriptEngineFactoryThroughScriptEngineManager() throws Exception
   {
      ScriptEngineManager manager = new ScriptEngineManager(getClass().getClassLoader());
      ScriptEngine engine = manager.getEngineByExtension("fsh");
      Assert.assertThat(engine, notNullValue());
   }

   @Test
   public void testScriptExecution() throws Exception
   {
      Resource<File> tmpDir = resourceFactory.create(OperatingSystemUtils.createTempDir());
      ScriptContext context = ScriptContextBuilder.create().currentResource(tmpDir).build();
      ScriptEngine scriptEngine = engineFactory.getScriptEngine();
      Assert.assertThat(tmpDir.getChild("newfile.txt").exists(), is(false));
      scriptEngine.eval("touch newfile.txt", context);
      Assert.assertThat(tmpDir.getChild("newfile.txt").exists(), is(true));
   }

}
