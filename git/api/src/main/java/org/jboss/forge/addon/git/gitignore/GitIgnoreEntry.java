/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.git.gitignore;

/**
 * @author Dan Allen
 */
public class GitIgnoreEntry
{
   private String content;
   private boolean pattern;
   private boolean comment;
   private boolean negate;

   public GitIgnoreEntry(String line)
   {
      if (line.startsWith("!"))
      {
         this.content = line.substring(1);
         this.pattern = true;
         this.negate = true;
      }
      else if (line.startsWith("#"))
      {
         this.content = line.substring(1).trim();
         this.comment = true;
      }
      else if (line.length() > 0)
      {
         this.content = line;
         this.pattern = true;
      }
   }

   public GitIgnoreEntry(String content, boolean pattern, boolean negate)
   {
      this.content = content;
      this.pattern = pattern;
      this.negate = negate;
   }

   public String getContent()
   {
      return content;
   }

   public boolean isPattern()
   {
      return pattern;
   }

   public boolean isComment()
   {
      return comment;
   }

   public boolean isNegate()
   {
      return negate;
   }

   public boolean isBlank()
   {
      return content != null;
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + (comment ? 1231 : 1237);
      result = prime * result + ((content == null) ? 0 : content.hashCode());
      result = prime * result + (negate ? 1231 : 1237);
      result = prime * result + (pattern ? 1231 : 1237);
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (getClass() != obj.getClass())
      {
         return false;
      }
      GitIgnoreEntry other = (GitIgnoreEntry) obj;
      if (comment != other.comment)
      {
         return false;
      }
      if (content == null)
      {
         if (other.content != null)
         {
            return false;
         }
      }
      else if (!content.equals(other.content))
      {
         return false;
      }
      if (negate != other.negate)
      {
         return false;
      }
      if (pattern != other.pattern)
      {
         return false;
      }
      return true;
   }

   @Override
   public String toString()
   {
      if (content == null)
      {
         return "";
      }
      else if (comment)
      {
         return "# " + content;
      }
      else if (negate)
      {
         return "!" + content;
      }
      else
      {
         return content;
      }
   }

}
