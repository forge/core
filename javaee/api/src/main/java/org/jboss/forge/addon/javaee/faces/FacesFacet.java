/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.faces;

import java.util.List;

import javax.faces.application.ProjectStage;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.facets.constraints.FacetConstraintType;
import org.jboss.forge.addon.facets.constraints.FacetConstraints;
import org.jboss.forge.addon.javaee.Configurable;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.resource.Resource;

/**
 * If installed, this {@link Project} supports features from the JSF specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraints({
         @FacetConstraint(value = { ServletFacet.class }, type = FacetConstraintType.OPTIONAL),
         @FacetConstraint(value = { ResourcesFacet.class }, type = FacetConstraintType.REQUIRED)
})
public interface FacesFacet<DESCRIPTOR> extends JavaEEFacet, Configurable<DESCRIPTOR>
{
   ProjectStage getProjectStage();

   void setProjectStage(ProjectStage projectStage);

   List<String> getFaceletsViewMappings();

   List<String> getFaceletsDefaultSuffixes();

   List<String> getFacesSuffixes();

   Resource<?> getResourceForWebPath(String path);

   List<String> getWebPaths(String path);

   List<String> getWebPaths(Resource<?> r);

   void setFacesMapping(String mapping);

   List<String> getEffectiveFacesServletMappings();

   List<String> getFacesServletMappings();

}
