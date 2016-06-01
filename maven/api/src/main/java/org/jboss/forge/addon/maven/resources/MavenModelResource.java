/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.resources;

import org.apache.maven.model.Model;
import org.jboss.forge.addon.parser.xml.resources.XMLResource;
import org.jboss.forge.addon.resource.FileResource;

/**
 * Represents a 'pom.xml' {@link FileResource}.
 * <p>
 * May be used to retrieve and modify the underlying Maven {@link Model} and other information.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface MavenModelResource extends XMLResource
{
   /**
    * Get the current project {@link Model}
    */
   Model getCurrentModel();

   /**
    * Sets the current project {@link Model}
    * 
    * @param model the {@link Model} to be written to disk
    * @return this {@link MavenModelResource} instance
    */
   MavenModelResource setCurrentModel(Model model);

}
