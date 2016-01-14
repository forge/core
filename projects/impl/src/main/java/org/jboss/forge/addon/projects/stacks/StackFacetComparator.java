/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.projects.stacks;

import java.util.Comparator;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class StackFacetComparator implements Comparator<StackFacet>
{
   @Override
   public int compare(StackFacet o1, StackFacet o2)
   {
      return o2.getStack().getName().compareTo(o1.getStack().getName());
   }

}
