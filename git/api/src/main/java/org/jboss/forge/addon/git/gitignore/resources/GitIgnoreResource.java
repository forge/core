/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.gitignore.resources;

import java.util.List;

import org.jboss.forge.addon.git.gitignore.GitIgnoreEntry;
import org.jboss.forge.addon.resource.FileResource;

/**
 * A {@link FileResource} type representing a .gitignore file.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface GitIgnoreResource extends FileResource<GitIgnoreResource>
{

   void addPattern(String pattern);

   void addPatterns(String[] newPatterns);

   void removePattern(String pattern);

   List<String> getPatterns();

   List<GitIgnoreEntry> getEntries();
}
