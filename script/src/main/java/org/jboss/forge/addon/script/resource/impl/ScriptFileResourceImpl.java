/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.resource.impl;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.script.resource.ScriptFileResource;

/**
 * Default implementation of {@link ScriptFileResource}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ScriptFileResourceImpl extends AbstractFileResource<ScriptFileResource>
         implements ScriptFileResource
{
   private final ScriptEngine engine;

   public ScriptFileResourceImpl(ResourceFactory factory, File file, ScriptEngine engine)
   {
      super(factory, file);
      this.engine = engine;
   }

   @Override
   public Object eval() throws ScriptException
   {
      return engine.eval(getContents());
   }

   @Override
   public Object eval(ScriptContext context) throws ScriptException
   {
      return engine.eval(getContents(), context);
   }

   @Override
   public String getEngineName()
   {
      return engine.getFactory().getEngineName();
   }

   @Override
   public String getEngineLanguageName()
   {
      return engine.getFactory().getLanguageName();
   }

   @Override
   public String getEngineLanguageVersion()
   {
      return engine.getFactory().getLanguageVersion();
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new ScriptFileResourceImpl(getResourceFactory(), file, engine);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      return Collections.emptyList();
   }
}
