/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.visit;

import org.jboss.forge.addon.resource.Resource;

/**
 * Visits all {@link Resource} instances in the visit controller.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ResourceVisitor
{
   /**
    * Called for each visited {@link Resource}.
    */
   public void visit(VisitContext context, Resource<?> resource);
}
