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
import org.jboss.forge.addon.javaee.Configurable;
import org.jboss.forge.addon.javaee.JavaEEFacet;
import org.jboss.forge.addon.javaee.security.TransportGuarantee;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.shrinkwrap.descriptor.api.webapp.WebAppCommonDescriptor;

import java.util.List;

/**
 * If installed, this {@link Project} supports features from the Servlet specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("rawtypes")
@FacetConstraints({
         @FacetConstraint(value = WebResourcesFacet.class, type = FacetConstraintType.REQUIRED)
})
public interface ServletFacet<DESCRIPTOR extends WebAppCommonDescriptor> extends JavaEEFacet, Configurable<DESCRIPTOR>,
         ProjectFacet
{
   /**
    * List all files in this {@link Project}'s WebContent directory, recursively.
    */
   List<Resource<?>> getResources();

   /**
    * List all files in this {@link Project}'s WebContent directory, recursively, only if they match the given
    * {@link ResourceFilter}.
    */
   List<Resource<?>> getResources(final ResourceFilter filter);

   /**
    * Return a reference to the WEB-INF directory of this project.
    */
   DirectoryResource getWebInfDirectory();

   /**
    * Adds a login-config element to the web.xml of the current project.
    *
    * @param authMethod The authentication mechanism for this login config
    * @param realmName The realm name to be used for the authentication scheme chosen for this login config
    */
   void addLoginConfig(String authMethod, String realmName);

   /**
    * Adds a security role to the current project.
    *
    * @param roleName The name of the security role. Must conform to the NMTOKEN lexical rules
    */
   void addSecurityRole(String roleName);

   /**
    * Returns all the security roles defined for the current project.
    */
   List<String> getSecurityRoles();

   /**
    * Removes a security role from the current project.
    *
    * @param roleName The name of the role that should be removed
    * @return Returns <code>true</code> if the role was successfully removed. If for some reason, e.g. the role was not
    * found, the role could not be removed, returns <code>false</code>.
    */
   boolean removeSecurityRole(String roleName);

   /**
    * Adds a security constraint to the current project.
    *
    * @param displayName The display name of the new security constraints
    * @param webResourceName The name of the web resource collection of the new security constraint. Cannot be null
    * @param webResourceDescription The description  of the web resource collection of the new security constraint
    * @param httpMethods The HTTP methods that are protected by the new security constraint. May be empty
    * @param urlPatterns The URL patterns that are protected by the new security constraint. May be empty
    * @param roleNames The role names which are part of the authentication constraint of the new security constraint. May be empty
    * @param transportGuarantee The user data constraint of the new security constraint
    */
   void addSecurityConstraint(String displayName, String webResourceName, String webResourceDescription,
            Iterable<String> httpMethods, Iterable<String> urlPatterns, Iterable<String> roleNames,
            TransportGuarantee transportGuarantee);

}
