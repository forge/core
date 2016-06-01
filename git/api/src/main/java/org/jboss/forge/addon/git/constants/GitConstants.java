/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.constants;

/**
 * 
 * @author <a href="mailto:danielsoro@gmail.com">Daniel Cunha (soro)</a>
 * 
 */
public interface GitConstants
{
   String GIT_DIRECTORY = ".git";
   String GITIGNORE = ".gitignore";
   String GLOBAL_TEMPLATES = "Global";
   String GIT_REMOTE_ORIGIN = "origin";
   String GIT_REFS_HEADS = "refs/heads";
   String CLONE_LOCATION_KEY = "gitignore.plugin.clone";
   String REPOSITORY_KEY = "gitignore.plugin.repo";
   String BOILERPLATE_FILE = ".gitignore_boilerplate";
   String REPOSITORY = "https://github.com/github/gitignore.git";
}
