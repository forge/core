package org.jboss.forge.addon.templates;

import java.io.IOException;
import java.io.Writer;

import org.jboss.forge.addon.resource.Resource;

/**
 * Process a template
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface TemplateGenerator
{
   /**
    * Returns true if the given resource instance is handled by this {@link TemplateGenerator}
    */
   public boolean handles(Resource<?> template);

   /**
    * Processes the template specified by the {@link Resource} parameter and writes the output to the {@link Writer}
    * parameter
    */
   public void process(Object dataModel, Resource<?> template, Writer writer) throws IOException;
}
