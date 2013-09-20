/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces;

import javax.faces.application.ProjectStage;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.javaee.ConfigurableFacet;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.projects.Project;

/**
 * If installed, this {@link Project} supports features from the JSF specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint(value = { ServletFacet.class }, type = FacetConstraintType.OPTIONAL)
public interface FacesFacet<DESCRIPTOR> extends JavaEEFacet, ConfigurableFacet<DESCRIPTOR>
{
   ProjectStage getProjectStage();
}
