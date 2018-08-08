package org.jboss.forge.addon.script;

import java.io.File;
import java.io.PrintStream;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.script.impl.ForgeScriptEngineFactory;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.addons.AddonRegistry;

/**
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ScriptOperations
{
   private ResourceFactory resourceFactory;
   private ForgeScriptEngineFactory engineFactory;

   public ScriptOperations()
   {
   }

   public Result evaluate(File currentDir, String script, Integer timeout, PrintStream stdout, PrintStream stderr)
            throws ScriptException
   {
      ScriptEngine scriptEngine = getEngineFactory().getScriptEngine();
      DirectoryResource resource = getResourceFactory().create(DirectoryResource.class, currentDir);
      ScriptContext context = ScriptContextBuilder.create()
               .currentResource(resource)
               .timeout(timeout)
               .stdout(stdout)
               .stderr(stderr)
               .build();
      return (Result) scriptEngine.eval(script, context);
   }

   private ForgeScriptEngineFactory getEngineFactory()
   {
      if (engineFactory == null)
      {
         AddonRegistry addonRegistry = Furnace.instance(getClass().getClassLoader()).getAddonRegistry();
         this.engineFactory = addonRegistry.getServices(ForgeScriptEngineFactory.class).get();
      }
      return engineFactory;
   }

   private ResourceFactory getResourceFactory()
   {
      if (resourceFactory == null)
      {
         AddonRegistry addonRegistry = Furnace.instance(getClass().getClassLoader()).getAddonRegistry();
         this.resourceFactory = addonRegistry.getServices(ResourceFactory.class).get();
      }
      return resourceFactory;
   }
}
