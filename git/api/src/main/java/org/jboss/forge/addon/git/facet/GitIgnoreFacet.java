/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.facet;

import java.io.IOException;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.forge.addon.git.gitignore.GitIgnoreTemplateGroup;
import org.jboss.forge.addon.projects.ProjectFacet;

/**
 * Downloads a list of .gitignore templates.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:jevgeni.zelenkov@gmail.com">Jevgeni Zelenkov</a>
 */
public interface GitIgnoreFacet extends ProjectFacet
{
   /**
    * List all available gitignore templates.
    */
   public List<GitIgnoreTemplateGroup> list();

   /**
    * Read the content of a gitignore template
    *
    * @param template Template name.
    * @return Template content as string.
    */
   public String contentOf(String template);
   
   /**
    * Update the templates from the remote repository.
    *
    * @throws IOException Failure reading the git repository.
    * @throws GitAPIException Git failure.
    */
   public void update() throws IOException, GitAPIException;

}
