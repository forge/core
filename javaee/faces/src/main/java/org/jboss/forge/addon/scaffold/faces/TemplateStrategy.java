/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces;

import org.jboss.forge.addon.resource.Resource;

/**
 * A strategy defining the manner in which template resources interact with generated resources.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public interface TemplateStrategy
{
   /**
    * Return true if this {@link TemplateStrategy} is compatible with the given template {@link Resource}.
    */
   boolean compatibleWith(Resource<?> template);

   /**
    * Return the path by which the given {@link Resource} template should be referenced when constructing generated
    * resources.
    */
   String getReferencePath(Resource<?> template);

   /**
    * Return the default template to be used when generating resource, or null if none should be used.
    */
   Resource<?> getDefaultTemplate();
}
