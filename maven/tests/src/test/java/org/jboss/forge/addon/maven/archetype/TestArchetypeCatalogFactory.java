/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.archetype;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class TestArchetypeCatalogFactory implements ArchetypeCatalogFactory
{

   @Override
   public String getName()
   {
      return "Test";
   }

   @Override
   public ArchetypeCatalog getArchetypeCatalog()
   {
      ArchetypeCatalog archetypes = new ArchetypeCatalog();
      Archetype archetype = new Archetype();
      archetype.setGroupId("groupId");
      archetype.setArtifactId("artifactId");
      archetype.setVersion("1.0.0");
      archetype.setDescription("Description");
      archetypes.addArchetype(archetype);
      return archetypes;
   }
   
   @Override
   public String toString()
   {
      return "A Test Archetype";
   }

}
