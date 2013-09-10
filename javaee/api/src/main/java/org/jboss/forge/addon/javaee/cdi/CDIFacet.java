/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi;

import org.jboss.forge.addon.javaee.ConfigurableFacet;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

/**
 * If installed, this {@link Project} supports features from the CDI specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface CDIFacet<T extends Descriptor> extends JavaEEFacet, ConfigurableFacet<T>
{
}
