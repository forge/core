/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.constants;

/**
 * 
 * @author <a href="danielsoro@gmail.com">Daniel Cunha (soro)</a>
 * 
 */
public interface GitConstants
{
   static final String GIT_DIRECTORY = ".git";
   static final String GITIGNORE = ".gitignore";
   static final String GLOBAL_TEMPLATES = "Global";
   static final String GIT_REMOTE_ORIGIN = "origin";
   static final String GIT_REFS_HEADS = "refs/heads";
   static final String CLONE_LOCATION_KEY = "gitignore.plugin.clone";
   static final String REPOSITORY_KEY = "gitignore.plugin.repo";
   static final String BOILERPLATE_FILE = ".gitignore_boilerplate";
   static final String REPOSITORY = "https://github.com/github/gitignore.git";
}
