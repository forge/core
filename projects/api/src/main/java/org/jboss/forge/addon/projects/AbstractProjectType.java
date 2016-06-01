/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects;

import org.jboss.forge.addon.ui.context.UIContext;

/**
 * Skeleton class for {@link ProjectType} implementations
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractProjectType implements ProjectType {

    @Override
    public boolean isEnabled(UIContext context)
    {
        return true;
    }

    @Override
    public String toString()
    {
        return getType();
    }

}
