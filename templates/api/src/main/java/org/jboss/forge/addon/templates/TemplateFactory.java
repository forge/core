/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates;

import org.jboss.forge.addon.resource.Resource;

/**
 * Creates a {@link TemplateProcessor} based on a {@link Template}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface TemplateFactory
{
   /**
    * Create a {@link TemplateProcessor} for the supplied {@link Resource} and {@link Template} type.
    */
   Template create(Resource<?> template, Class<? extends Template> type);
}
