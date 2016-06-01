/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.script.resource;

import javax.script.ScriptContext;
import javax.script.ScriptException;

import org.jboss.forge.addon.resource.FileResource;

/**
 * Handles a script file and provides operations related to it
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface ScriptFileResource extends FileResource<ScriptFileResource>
{
   /**
    * Executes this script
    * 
    * @return The value returned from the execution of the script.
    */
   Object eval() throws ScriptException;

   /**
    * Executes this script using the provided {@link ScriptContext}
    * 
    * @return The value returned from the execution of the script.
    */
   Object eval(ScriptContext context) throws ScriptException;

   /**
    * @return the short name of the scripting language
    */
   String getEngineName();

   /**
    * @return the scripting language
    */
   String getEngineLanguageName();

   /**
    * @return the scripting language version
    */
   String getEngineLanguageVersion();
}
