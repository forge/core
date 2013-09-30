/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
