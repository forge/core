/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.git.gitignore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.ResourceHandles;
import org.jboss.forge.shell.util.Streams;

/**
 * @author Dan Allen
 */
@ResourceHandles(GitIgnoreResource.RESOURCE_NAME)
public class GitIgnoreResource extends FileResource<GitIgnoreResource>
{

   public static final String RESOURCE_NAME = ".gitignore";

   @Inject
   public GitIgnoreResource(ResourceFactory factory)
   {
      this(factory, null);
   }

   protected GitIgnoreResource(ResourceFactory factory, File file)
   {
      super(factory, file);
      setFlag(ResourceFlag.File);
   }

   @Override
   public Resource<File> createFrom(File file)
   {
      return new GitIgnoreResource(getResourceFactory(), file);
   }

   @Override
   protected List<Resource<?>> doListResources()
   {
      List<Resource<?>> patterns = new ArrayList<Resource<?>>();
      for (GitIgnoreEntry entry : getEntries())
      {
         if (entry.isPattern())
         {
            patterns.add(new GitIgnorePatternResource(this, entry.getContent()));
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
      List<String> patterns = new ArrayList<String>();
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
      List<GitIgnoreEntry> lines = new ArrayList<GitIgnoreEntry>();
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
