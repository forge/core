/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.resources;

import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Introduces an ordered {@link Properties#keySet()}
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
class SortedProperties extends Properties
{
   private static final long serialVersionUID = 1L;

   @Override
   public Set<Object> keySet()
   {
      return new TreeSet<Object>(super.keySet());
   }
}
