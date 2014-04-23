package org.jboss.forge.addon.templates.freemarker;

import org.jboss.forge.addon.templates.Template;

/**
 * A Freemarker {@link Template}.
 * 
 * @author Vineet Reynolds
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface FreemarkerTemplate extends Template
{
   /**
    * Get the Freemarker engine template Configuration.
    */
   public freemarker.template.Configuration getFreemarkerConfig();
}
