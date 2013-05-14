/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;

/**
 * If installed, this {@link Project} supports features from the JAX-RS specification.
 *
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface RestFacet extends Facet
{
    public static final String ROOTPATH = "rootpath";
    public static final String ACTIVATOR_CHOICE = "activatorChoice";
    public String getApplicationPath();
}
