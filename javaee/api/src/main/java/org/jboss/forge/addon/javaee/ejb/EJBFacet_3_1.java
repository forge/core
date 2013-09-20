/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.ejb;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.javaee.jta.JTAFacet_1_1;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(JTAFacet_1_1.class)
public interface EJBFacet_3_1 extends EJBFacet
{

}
