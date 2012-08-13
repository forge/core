/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.events;

import java.util.List;

import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.ScaffoldProvider;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ScaffoldGeneratedResources
{
   private final List<Resource<?>> generatedViews;
   private final ScaffoldProvider provider;

   public ScaffoldGeneratedResources(ScaffoldProvider provider, List<Resource<?>> generatedViews)
   {
      this.generatedViews = generatedViews;
      this.provider = provider;
   }

   public List<Resource<?>> getResources()
   {
      return generatedViews;
   }

   public ScaffoldProvider getProvider()
   {
      return provider;
   }
}
