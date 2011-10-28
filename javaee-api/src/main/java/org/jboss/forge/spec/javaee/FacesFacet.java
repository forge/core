/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.spec.javaee;

import java.util.List;

import org.jboss.forge.project.Facet;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.FacesProjectStage;

/**
 * If installed, this {@link Project} supports features from the JSF specification.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface FacesFacet extends Facet
{
   public static final String FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet";
   /**
    * Get a reference to this {@link Project}'s configured WEB-INF/faces-config.xml file.
    * <p>
    * Note: This method does not take into account any configuration files that may have been specified by the
    * javax.faces.CONFIG_FILES context-parameter.
    */
   FileResource<?> getConfigFile();

   /**
    * Get this application's currently configured {@link FacesProjectStage}.
    */
   FacesProjectStage getProjectStage();

   /**
    * Get this application's currently configured FacesServlet mappings from the web.xml
    */
    List<String> getFacesServletMappings();

   /**
    * Get this application's currently configured FacesServlet mappings.
    */
   List<String> getEffectiveFacesServletMappings();

   /**
    * Set this application's FacesServlet mapping.
    */
   void setFacesMapping(String mapping);

   /**
    * Set this application's FacesServlet mapping to the default mappings.
    */
   void setDefaultFacesMapping();

   /**
    * For a given {@link Resource}, if the resource is a web-resource, return all known context-relative URLs with which
    * that resource may be accessed.
    * <p>
    * E.g: If the Faces Servlet were mapped to *.jsf, given the resource "$PROJECT_HOME/src/main/webapp/example.xhtml",
    * this method would return "/example.jsf"
    */
   List<String> getWebPaths(Resource<?> r);

   /**
    * For a given JSF view-ID, if the view-ID is valid, return all known context-relative URLs with which that resource
    * may be accessed.
    * <p>
    * E.g: If the Faces Servlet were mapped to *.jsf, given the view-ID "/example.xhtml", this method would return
    * "/example.jsf"
    */
   List<String> getWebPaths(String viewId);

   /**
    * Given a web path, return the corresponding resource to which that path would resolve when the application is
    * deployed.
    * <p>
    * E.g: If the Faces Servlet were mapped to *.jsf, given a web path of, "/example.jsf", this method would return a
    * {@link Resource} reference to "$PROJECT_HOME/src/main/webapp/example.xhtml"
    */
   Resource<?> getResourceForWebPath(String path);

   /**
    * Return all configured JSP or Facelets default suffixes.
    * 
    * @see {@link #getFacesDefaultSuffixes()}, {@link #getFaceletsDefaultSuffixes()}
    */
   List<String> getFacesSuffixes();

   /**
    * Return all Faces default suffixes for JSP files as configured by the javax.faces.DEFAULT_SUFFIX context-parameter.
    */
   List<String> getFacesDefaultSuffixes();

   /**
    * Return all Faces default suffixes for JSP files as configured by the javax.faces.FACELETS_SUFFIX
    * context-parameter.
    */
   List<String> getFaceletsDefaultSuffixes();

   /**
    * Return all Facelets view mappings as configured by the javax.faces.FACELETS_VIEW_MAPPINGS context-parameter.
    */
   List<String> getFaceletsViewMapping();
}
