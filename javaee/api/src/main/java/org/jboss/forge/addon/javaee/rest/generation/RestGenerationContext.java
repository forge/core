/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.rest.generation;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.text.Inflector;
import org.jboss.forge.parser.java.JavaClass;

/**
 * Parameters for REST resource generation
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface RestGenerationContext
{
   public Project getProject();

   public JavaClass getEntity();

   public String getTargetPackageName();

   public String getContentType();

   public String getPersistenceUnitName();
   
   public Inflector getInflector();
}