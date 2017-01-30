/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.resource.URLResource;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * Parser of UNIX-style pathspec. The parser accepts a resource, and provides a result set of resources based on the
 * relative path provided.
 * <p/>
 * 
 * Example:<br/>
 * <code>
 *    List<Resource<?>> res = new ResourcePathResolver(factoryInstance, relativeResource, "../../foobar").resolve();
 * </code>
 * 
 * Where <tt>factoryInstance</tt> is an instance of {@link ResourceFactory}, <tt>relativeResource</tt> is a resource,
 * such as a file or directory, for which the relative result for <tt>../../foobar</tt> will be calculated.
 * <p/>
 * 
 * Wildcards <tt>*</tt> and <tt>?</tt> are accepted.
 * 
 * @author Mike Brock
 */
public class ResourcePathResolver
{
   private static final Pattern WILDCARDS_PATTERN = Pattern.compile(".*(\\?|\\*)+.*");
   private static final Pattern WINDOWS_DRIVE_PATTERN = Pattern.compile("^[a-zA-Z]{1,1}:(/|\\\\).*");

   private int cursor;
   private final int length;

   private final ResourceFactory factory;
   private final Resource<?> res;
   private final String path;

   private final boolean isWindows = OperatingSystemUtils.isWindows();

   List<Resource<?>> results = new LinkedList<>();

   public ResourcePathResolver(final ResourceFactory factory, final Resource<?> res, final String path)
   {
      this.factory = factory;
      this.res = res;
      this.path = path;
      this.length = path.length();
   }

   private ResourcePathResolver(final ResourceFactory factory, final Resource<?> res, final String path, int cursor)
   {
      this.factory = factory;
      this.res = res;
      this.path = path;
      this.length = path.length();
      this.cursor = cursor;
   }

   /**
    * Resolve the results.
    * 
    * @return A list of resources that match the path. Empty if there are no matches.
    */
   public List<Resource<?>> resolve()
   {
      Resource<?> r = res;
      String tk;

      char slashChar = File.separatorChar;
      String slashString = File.separator;

      if (".".equals(path))
      {
         return singleResult(r);
      }
      else if (path.startsWith("~"))
      {
         File homeDir = OperatingSystemUtils.getUserHomeDir();

         if (path.length() == 1)
         {
            return singleResult(factory.create(DirectoryResource.class, homeDir));
         }
         else
         {
            cursor++;
            r = factory.create(DirectoryResource.class, homeDir);
         }
      }
      // for windows, support drive letter prefixes here.
      else if (isWindows && WINDOWS_DRIVE_PATTERN.matcher(path).matches())
      {
         int idx = path.lastIndexOf(slashChar) + 1;
         r = factory.create(DirectoryResource.class, new File(path.substring(0, idx)).getAbsoluteFile());
         cursor = idx;
      }
      // Is an URL ?
      else if (path.matches(".*://.*"))
      {
         int idx = path.indexOf(" ");
         if (idx == -1)
         {
            idx = length;
         }
         try
         {
            r = factory.create(URLResource.class, new URL(path.substring(0, idx)));
         }
         catch (MalformedURLException e)
         {
            throw new RuntimeException(e);
         }
         cursor = idx + 1;
      }

      while (cursor < length)
      {
         SW: switch (path.charAt(cursor++))
         {
         case '\\':
         case '/':
            if (cursor - 1 == 0)
            {
               r = factory.create(new File(slashString).getAbsoluteFile());
            }
            continue;

         case '.':
            switch (read())
            {
            case '.':
               cursor++;
               Resource<?> parent = r.getParent();
               if (parent != null)
               {
                  r = parent;
               }
               break SW;

            default:
               if (cursor < length && path.charAt(cursor) == slashChar)
               {
                  cursor++;
                  break SW;
               }
            }

         default:
            boolean first = --cursor == 0;
            tk = capture();

            if (WILDCARDS_PATTERN.matcher(tk).matches())
            {
               boolean startDot = tk.startsWith(".");
               String regex = pathspecToRegEx(tk.startsWith(slashString) ? tk.substring(1) : tk);
               Pattern p;
               try
               {
                  p = Pattern.compile(regex);
               }
               catch (PatternSyntaxException pe)
               {
                  // Regex might be incomplete, trying again quoted
                  p = Pattern.compile(Pattern.quote(regex));
               }

               List<Resource<?>> res = new ArrayList<>();

               for (Resource<?> child : r.listResources())
               {
                  if (p.matcher(child.getName()).matches())
                  {
                     // child.setFlag(ResourceFlag.AmbiguouslyQualified);

                     if (child.getName().startsWith("."))
                     {
                        if (startDot)
                        {
                           res.add(child);
                        }
                     }
                     else
                     {
                        res.add(child);
                     }
                  }
               }

               if (cursor != length)
               {
                  for (Resource<?> child : res)
                  {
                     results.addAll(new ResourcePathResolver(factory, child, path, cursor).resolve());
                  }
               }
               else
               {
                  results.addAll(res);
               }

               return results;
            }

            if (tk.startsWith(slashString))
            {
               if (first)
               {
                  r = factory.create(new File(tk));
                  cursor++;
                  continue;
               }
               else
               {
                  tk = tk.substring(1);
               }
            }

            Resource<?> child = r.getChild(tk);
            if (child == null)
            {
               throw new RuntimeException("no such child: " + tk);
            }
            r = child;
            break;
         }
      }

      return singleResult(r);
   }

   /**
    * Perform a search, by doing a breadth-first traversal of the resource tree for resources that match the path
    * string.
    * 
    * @return A list of resources that match the path string. Empty if there are no matches.
    */
   public List<Resource<?>> search()
   {
      return match(path.split(Pattern.quote(File.separator)), 0, res, new LinkedList<Resource<?>>());
   }

   private static List<Resource<?>> match(String[] matchLevels,
            int nestStart,
            Resource<?> res,
            List<Resource<?>> candidates)
   {
      String regex = pathspecToRegEx(matchLevels[nestStart]);
      Pattern matchPattern = Pattern.compile(regex);

      if (matchPattern.matcher(res.getName()).matches())
      {
         // if ((nestStart < matchLevels.length) && res.isFlagSet(ResourceFlag.Node))
         if ((nestStart < matchLevels.length) && res instanceof DirectoryResource)
         {
            return match(matchLevels, nestStart + 1, res, candidates);
         }
         else
         {
            candidates.add(res);
            return candidates;
         }
      }

      /**
       * Check to see if this type of node can have children, or if we're exhausted the nest match depth. Otherwise,
       * bail.
       */
      // if (!res.isFlagSet(ResourceFlag.Node) || (nestStart == matchLevels.length))
      if (!(res instanceof DirectoryResource) || (nestStart == matchLevels.length))
      {
         return candidates;
      }

      /**
       * Let's iterate the child nodes.
       */
      for (Resource<?> r : res.listResources())
      {
         match(matchLevels, nestStart, r, candidates);
      }

      return candidates;
   }

   private static List<Resource<?>> singleResult(Resource<?> item)
   {
      return Collections.<Resource<?>> singletonList(item);
   }

   private char read()
   {
      if (cursor != length)
      {
         return path.charAt(cursor);
      }
      return (char) 0;
   }

   private String capture()
   {
      int start = cursor;

      // capture can start with a '/' or '\\' (the latter on Windows only)
      if (path.charAt(cursor) == '/' || (OperatingSystemUtils.isWindows() && path.charAt(cursor) == '\\'))
      {
         cursor++;
      }

      while ((cursor < length))
      {
         if (OperatingSystemUtils.isWindows() && path.charAt(cursor) == '\\')
         {
            break;
         }
         if (path.charAt(cursor) == '/')
         {
            break;
         }
         cursor++;
      }

      return path.substring(start, cursor);
   }

   public static String pathspecToRegEx(final String pathSpec)
   {
      StringBuilder sb = new StringBuilder("^");
      char c;
      for (int i = 0; i < pathSpec.length(); i++)
      {
         switch (c = pathSpec.charAt(i))
         {
         case '.':
            sb.append("\\.");
            break;
         case '*':
            sb.append(".*");
            break;
         case '?':
            sb.append(".");
            break;
         default:
            sb.append(c);
         }
      }

      return sb.append("$").toString();
   }
}
