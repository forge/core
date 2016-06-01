/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraints({
         @FacetConstraint(value = WebResourcesFacet.class, type = FacetConstraintType.REQUIRED)
})
public interface ServletFacet_2_5 extends ServletFacet<WebAppDescriptor>
{

}
