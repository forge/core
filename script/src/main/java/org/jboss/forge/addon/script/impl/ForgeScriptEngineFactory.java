/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.impl;

import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.jboss.forge.furnace.versions.Versions;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ForgeScriptEngineFactory implements ScriptEngineFactory
{

   @Override
   public String getEngineName()
   {
      return "JBoss Forge Script Engine";
   }

   @Override
   public String getEngineVersion()
   {
      return Versions.getImplementationVersionFor(getClass()).toString();
   }

   @Override
   public List<String> getExtensions()
   {
      return Arrays.asList("fsh");
   }

   @Override
   public List<String> getMimeTypes()
   {
      return Arrays.asList("text/forge");
   }

   @Override
   public List<String> getNames()
   {
      return Arrays.asList("JBossForgeScript");
   }

   @Override
   public String getLanguageName()
   {
      return "JBoss Forge";
   }

   @Override
   public String getLanguageVersion()
   {
      return Versions.getImplementationVersionFor(getClass()).toString();
   }

   @Override
   public Object getParameter(String key)
   {
      switch (key)
      {
      case ScriptEngine.ENGINE:
         return getEngineName();
      case ScriptEngine.ENGINE_VERSION:
         return getEngineVersion();
      case ScriptEngine.NAME:
         return getNames().get(0);
      case ScriptEngine.LANGUAGE:
         return getLanguageName();
      case ScriptEngine.LANGUAGE_VERSION:
         return getLanguageVersion();
      default:
         return null;
      }
   }

   @Override
   public String getMethodCallSyntax(String obj, String m, String... args)
   {
      // no methods
      return null;
   }

   @Override
   public String getOutputStatement(String toDisplay)
   {
      return null;
   }

   @Override
   public String getProgram(String... statements)
   {
      return null;
   }

   @Override
   public ScriptEngine getScriptEngine()
   {
      return new ForgeScriptEngine(this);
   }

}
