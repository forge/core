/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.addons.facets;

import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.furnace.versions.Version;
import org.jboss.forge.furnace.versions.Versions;

/**
 * Adds the Forge version property in the pom.xml.
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
@FacetConstraint(MetadataFacet.class)
public class ForgeVersionFacet extends AbstractFacet<Project> implements ProjectFacet
{
   public static final String VERSION_PROPERTY = "${version.forge}";

   private static final String VERSION_PROPERTY_NAME = "version.forge";

   @Override
   public boolean install()
   {
      Version forgeVersion = Versions.getImplementationVersionFor(getClass());
      getFaceted().getFacet(MetadataFacet.class).setDirectProperty(VERSION_PROPERTY_NAME,
               forgeVersion.toString());
      return isInstalled();
   }

   @Override
   public boolean isInstalled()
   {
      return getVersion() != null;
   }

   public String getVersion()
   {
      return getFaceted().getFacet(MetadataFacet.class).getEffectiveProperty(VERSION_PROPERTY_NAME);
   }

   public void setVersion(String version)
   {
      getFaceted().getFacet(MetadataFacet.class).setDirectProperty(VERSION_PROPERTY_NAME, version);
   }

}
