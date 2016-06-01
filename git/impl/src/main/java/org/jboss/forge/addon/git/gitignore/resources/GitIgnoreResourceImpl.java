/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.git.gitignore.resources;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.git.gitignore.GitIgnoreEntry;
import org.jboss.forge.addon.resource.AbstractFileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;

/**
 * @author Dan Allen
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class GitIgnoreResourceImpl extends AbstractFileResource<GitIgnoreResource> implements GitIgnoreResource
{
   public GitIgnoreResourceImpl(ResourceFactory factory, File file)
   {
      super(factory, file);
   }

   @Override
   public GitIgnoreResource createFrom(File file)
   {
      return new GitIgnoreResourceImpl(getResourceFactory(), file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      List<Resource<?>> patterns = new ArrayList<>();
      for (GitIgnoreEntry entry : getEntries())
      {
         if (entry.isPattern())
         {
            patterns.add(new GitIgnorePatternResource(getResourceFactory(), this, entry.getContent()));
         }
      }
      return patterns;
   }

   @Override
   public void addPattern(String pattern)
   {
      List<GitIgnoreEntry> entries = getEntries();
      GitIgnoreEntry entry = new GitIgnoreEntry(pattern);
      if (!entries.contains(entry))
      {
         entries.add(entry);
         storeEntries(entries);
      }
   }

   @Override
   public void addPatterns(String[] newPatterns)
   {
      List<GitIgnoreEntry> entries = getEntries();
      boolean modified = false;
      for (String pattern : newPatterns)
      {
         GitIgnoreEntry entry = new GitIgnoreEntry(pattern);
         if (entries.contains(entry))
         {
            entries.add(entry);
            modified = true;
         }
      }
      if (modified)
      {
         storeEntries(entries);
      }
   }

   @Override
   public void removePattern(String pattern)
   {
      List<GitIgnoreEntry> entries = getEntries();
      GitIgnoreEntry entry = new GitIgnoreEntry(pattern);
      if (entries.contains(entry))
      {
         entries.remove(entry);
         storeEntries(entries);
      }
   }

   @Override
   public List<String> getPatterns()
   {
      List<String> patterns = new ArrayList<>();
      for (GitIgnoreEntry entry : getEntries())
      {
         if (entry.isPattern())
         {
            patterns.add(entry.toString());
         }
      }
      return patterns;
   }

   @Override
   public List<GitIgnoreEntry> getEntries()
   {
      List<GitIgnoreEntry> lines = new ArrayList<>();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(getResourceInputStream())))
      {
         String line = null;
         while ((line = reader.readLine()) != null)
         {
            lines.add(new GitIgnoreEntry(line));
         }
         return lines;
      }
      catch (IOException e)
      {
         throw new RuntimeException(
                  "Error while reading .gitignore patterns", e);
      }
   }

   protected void storeEntries(List<GitIgnoreEntry> entries)
   {
      StringBuilder contents = new StringBuilder();
      for (GitIgnoreEntry entry : entries)
      {
         contents.append(entry.toString()).append("\n");
      }
      setContents(contents.toString());
   }
}
