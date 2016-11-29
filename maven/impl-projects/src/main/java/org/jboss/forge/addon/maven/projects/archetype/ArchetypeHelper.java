/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype;

import java.io.File;
import java.io.InputStream;

/**
 * This class is a replacement for <code>mvn archetype:generate</code> without dependencies to maven-archetype related
 * libraries.
 * 
 * @author fabric8
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @deprecated use {@link org.jboss.forge.addon.maven.archetype.ArchetypeHelper} instead
 */
@Deprecated
public class ArchetypeHelper extends org.jboss.forge.addon.maven.archetype.ArchetypeHelper
{
   public ArchetypeHelper(InputStream archetypeIn, File outputDir, String groupId, String artifactId)
   {
      super(archetypeIn, outputDir, groupId, artifactId, "1.0-SNAPSHOT");
   }

   public ArchetypeHelper(InputStream archetypeIn, File outputDir, String groupId, String artifactId, String version)
   {
      super(archetypeIn, outputDir, groupId, artifactId, version);
   }
}
