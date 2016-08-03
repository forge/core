/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script;

import java.io.PrintStream;

import javax.script.ScriptContext;
import javax.script.SimpleScriptContext;

import org.jboss.forge.addon.resource.Resource;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ScriptContextBuilder
{
   public static final String OUTPUT_PRINTSTREAM_ATTRIBUTE = "output";
   public static final String ERROR_PRINTSTREAM_ATTRIBUTE = "error";
   public static final String CURRENT_RESOURCE_ATTRIBUTE = "current_resource";
   public static final String TIMEOUT_ATTRIBUTE = "timeout";

   private final SimpleScriptContext context = new SimpleScriptContext();

   private ScriptContextBuilder()
   {
   }

   public static ScriptContextBuilder create()
   {
      return new ScriptContextBuilder();
   }

   public ScriptContextBuilder stdout(PrintStream stdout)
   {
      context.setAttribute(OUTPUT_PRINTSTREAM_ATTRIBUTE, stdout, ScriptContext.ENGINE_SCOPE);
      return this;
   }

   public ScriptContextBuilder stderr(PrintStream stderr)
   {
      context.setAttribute(ERROR_PRINTSTREAM_ATTRIBUTE, stderr, ScriptContext.ENGINE_SCOPE);
      return this;
   }

   public ScriptContextBuilder currentResource(Resource<?> currentResource)
   {
      context.setAttribute(CURRENT_RESOURCE_ATTRIBUTE, currentResource, ScriptContext.ENGINE_SCOPE);
      return this;
   }

   public ScriptContextBuilder timeout(Integer value)
   {
      context.setAttribute(TIMEOUT_ATTRIBUTE, value, ScriptContext.ENGINE_SCOPE);
      return this;
   }

   public ScriptContext build()
   {
      return context;
   }

}
