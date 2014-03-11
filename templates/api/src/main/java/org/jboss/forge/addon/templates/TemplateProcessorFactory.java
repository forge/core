/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
 */
public interface TemplateProcessorFactory
{
   /**
    * Create a {@link TemplateProcessor} for the supplied {@link Template}. The created TemplateProcessor is associated
    * with a template engine, and can be provided with a data model to eventually produce some output.
    * 
    * @param template The template for which the processor is to be created
    * @return A {@link TemplateProcessor} instance
    */
   TemplateProcessor fromTemplate(Template template);

   /**
    * Create a {@link TemplateProcessor} for the supplied {@link Resource}. The created TemplateProcessor is associated
    * with a template engine, and can be provided with a data model to eventually produce some output.
    * 
    * @deprecated Deprecated after Forge 2.1.1. Use the method requiring {@link Template} instances instead.
    * @param template The template for which the processor is to be created
    * @return A {@link TemplateProcessor} instance
    */
   @Deprecated
   TemplateProcessor fromTemplate(Resource template);
}
