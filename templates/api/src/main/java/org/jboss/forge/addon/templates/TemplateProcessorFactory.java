package org.jboss.forge.addon.templates;

import org.jboss.forge.addon.resource.Resource;

/**
 * Creates a {@link TemplateProcessor} based on a {@link Resource}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface TemplateProcessorFactory
{
   TemplateProcessor fromTemplate(Resource<?> template);
}
