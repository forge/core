/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.util;

import java.util.Comparator;

/**
 * This comparator should be used when a list of related classes should be ordered from the most abstract to the most
 * specialized one
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 * 
 */
public class RelatedClassComparator implements Comparator<Class<?>>
{
   @Override
   public int compare(Class<?> o1, Class<?> o2)
   {
      if (o1 == o2)
      {
         return 0;
      }
      else if (o1.isAssignableFrom(o2))
      {
         return -1;
      }
      else
      {
         return 1;
      }
   }

}
