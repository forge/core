/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.containers;

import org.jboss.forge.addon.javaee.facets.JavaEE7Facet;
import org.jboss.forge.addon.projects.stacks.Stack;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class JavaEE7Container extends JavaEEDefaultContainer
{
   @Override
   public boolean supports(Stack stack)
   {
      return stack.supports(JavaEE7Facet.class);
   }
}
