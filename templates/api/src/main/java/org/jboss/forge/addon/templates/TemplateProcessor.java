package org.jboss.forge.addon.templates;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * Process a template
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface TemplateProcessor
{
   /**
    * Returns a {@link String}
    */
   String process(Map<?, ?> map) throws IOException;

   /**
    * Writes the output to the {@link Writer}
    */
   void process(Map<?, ?> map, Writer output) throws IOException;
}
