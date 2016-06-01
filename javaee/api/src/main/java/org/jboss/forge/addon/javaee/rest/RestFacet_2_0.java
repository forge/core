/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.rest;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;

/**
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraints({
         @FacetConstraint(JavaSourceFacet.class),
         @FacetConstraint(ServletFacet_3_1.class)
})
public interface RestFacet_2_0 extends RestFacet
{

}
