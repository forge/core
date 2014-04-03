/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.resource.FileResourceImpl;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.furnace.util.Streams;

/**
 * @author Dan Allen
 */
public class GitIgnoreResource extends FileResourceImpl
{

   public static final String RESOURCE_NAME = ".gitignore";

//   @Inject
   public GitIgnoreResource(ResourceFactory factory)
   {
      this(factory, null);
   }

   public GitIgnoreResource(ResourceFactory factory, File file)
   {
      super(factory, file);
   }

   @Override
   public FileResourceImpl createFrom(File file)
   {
      return new GitIgnoreResource(getResourceFactory(), file);
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

   public List<GitIgnoreEntry> getEntries()
   {
      List<GitIgnoreEntry> lines = new ArrayList<>();
      BufferedReader reader = null;
      try
      {
         reader = new BufferedReader(new InputStreamReader(getResourceInputStream()));
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
      finally
      {
         Streams.closeQuietly(reader);
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
