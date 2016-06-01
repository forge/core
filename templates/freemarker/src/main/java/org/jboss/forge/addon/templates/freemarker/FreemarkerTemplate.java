/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
