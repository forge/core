/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.seam.forge.spec.javaee6.jsf;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.jboss.seam.forge.project.facets.BaseFacet;
import org.jboss.seam.forge.project.facets.WebResourceFacet;
import org.jboss.seam.forge.resources.DirectoryResource;
import org.jboss.seam.forge.resources.FileResource;
import org.jboss.seam.forge.resources.Resource;
import org.jboss.seam.forge.shell.ShellMessages;
import org.jboss.seam.forge.shell.ShellPrintWriter;
import org.jboss.seam.forge.shell.plugins.Alias;
import org.jboss.seam.forge.shell.plugins.RequiresFacet;
import org.jboss.seam.forge.spec.javaee6.servlet.ServletFacet;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.FacesProjectStage;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.ServletDef;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.ServletMappingDef;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.jsf")
@RequiresFacet(ServletFacet.class)
public class FacesFacet extends BaseFacet
{
   @Inject
   private ShellPrintWriter out;

   public FileResource<?> getConfigFile()
   {
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "faces-config.xml");
   }

   /*
    * Facet Methods
    */
   @Override
   public boolean isInstalled()
   {
      return getConfigFile().exists();
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         if (!getConfigFile().createNewFile())
         {
            throw new RuntimeException("Failed to create required [" + getConfigFile().getFullyQualifiedName() + "]");
         }
         getConfigFile().setContents(getClass()
                  .getResourceAsStream("/org/jboss/seam/forge/web/faces-config.xml"));
      }
      return true;
   }

   public FacesProjectStage getProjectStage()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor config = facet.getConfig();
      return config.getFacesProjectStage();
   }

   public List<String> getFacesServletMappings()
   {
      List<String> results = new ArrayList<String>();
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();

      // TODO should probably take into account facelets view mappings
      // facelets.VIEW_MAPPINGS

      if (webXml.hasFacesServlet())
      {
         List<ServletDef> servlets = webXml.getServlets();
         for (ServletDef servlet : servlets)
         {
            if ("javax.faces.webapp.FacesServlet".equals(servlet.getServletClass()))
            {
               List<ServletMappingDef> mappings = servlet.getMappings();
               for (ServletMappingDef mapping : mappings)
               {
                  results.addAll(mapping.getUrlPatterns());
               }
            }
         }
      }
      else
      {
         if (webXml.getVersion().startsWith("3"))
         {
            results.add("*.jsf");
            results.add("/faces/*");
         }
         else
            ShellMessages.info(out, "FacesServlet not found in web.xml and Servlet " +
                      "Version not >= 3.0, could not discover FacesServlet mappings");
      }
      return results;
   }

   public List<String> getWebPaths(Resource<?> r)
   {
      List<String> results = new ArrayList<String>();

      if (r != null)
      {
         WebResourceFacet web = project.getFacet(WebResourceFacet.class);
         List<DirectoryResource> webRootDirectories = web.getWebRootDirectories();
         for (DirectoryResource d : webRootDirectories)
         {
            if (r.getFullyQualifiedName().startsWith(d.getFullyQualifiedName()))
            {
               String path = r.getFullyQualifiedName().substring(d.getFullyQualifiedName().length());

               for (String p : getWebPaths(path))
               {
                  if (!results.contains(p))
                     results.add(p);
               }
               break;
            }
         }
      }
      return results;
   }

   public List<String> getWebPaths(String path)
   {
      List<String> results = new ArrayList<String>();
      if (getResourceForWebPath(path) == null)
      {
         List<String> mappings = getFacesServletMappings();
         for (String mapping : mappings)
         {
            String viewId = buildFacesViewId(mapping, path);
            if (!results.contains(viewId))
               results.add(viewId);
         }
      }
      return results;
   }

   public Resource<?> getResourceForWebPath(String path)
   {
      if (path != null)
      {
         WebResourceFacet web = project.getFacet(WebResourceFacet.class);
         List<DirectoryResource> webRootDirectories = web.getWebRootDirectories();

         boolean matches = false;
         for (String mapping : getFacesServletMappings())
         {
            Matcher matcher = Pattern.compile(ServletFacet.mappingToRegex(mapping)).matcher(path);
            if (matcher.matches())
            {
               path = matcher.group(1);
               matches = true;
               break;
            }
         }

         while (path.startsWith("/"))
         {
            path = path.substring(1);
         }

         if (!matches)
         {
            return null;
         }

         List<String> strings = Arrays.asList(path.split("/"));
         for (DirectoryResource d : webRootDirectories)
         {
            Queue<String> queue = new LinkedList<String>();
            queue.addAll(strings);

            Resource<?> temp = d;
            while (queue.size() > 1)
            {
               Resource<?> child = temp.getChild(queue.remove());
               if (child != null && child.exists())
               {
                  temp = child;
               }
               else
               {
                  break;
               }

               if (queue.isEmpty())
               {
                  return child;
               }
            }

            if (temp != null)
            {
               String name = queue.remove();
               for (String suffix : getFacesSuffixes())
               {
                  Resource<?> child = null;
                  if (name.endsWith(suffix))
                  {
                     child = temp.getChild(name);
                  }
                  else
                  {
                     child = temp.getChild(name + suffix);
                  }
                  if (child != null && child.exists())
                  {
                     return child;
                  }
               }
            }
         }
      }
      return null;
   }

   /**
    * Build a Faces view ID for the given resource path, return null if not mapped by Faces Servlet
    */
   private String buildFacesViewId(final String servletMapping, final String resourcePath)
   {
      for (String suffix : getFacesSuffixes())
      {
         if (resourcePath.endsWith(suffix))
         {
            StringBuffer result = new StringBuffer();

            Map<Pattern, String> patterns = new HashMap<Pattern, String>();

            Pattern pathMapping = Pattern.compile("^(/.*)/\\*$");
            Pattern extensionMapping = Pattern.compile("^\\*(\\..*)$");
            Pattern defaultMapping = Pattern.compile("^/\\*$");

            patterns.put(pathMapping, "$1" + resourcePath);
            patterns.put(extensionMapping, resourcePath.replaceAll("^(.*)(\\.\\w+)$", "$1") + "$1");
            patterns.put(defaultMapping, resourcePath);

            boolean matched = false;
            Iterator<Pattern> iterator = patterns.keySet().iterator();
            while (matched == false && iterator.hasNext())
            {
               Pattern p = iterator.next();
               Matcher m = p.matcher(servletMapping);
               if (m.matches())
               {
                  String replacement = patterns.get(p);
                  m.appendReplacement(result, replacement);
                  matched = true;
               }
            }

            if (matched == false)
            {
               return null;
            }

            return result.toString();
         }
      }
      return resourcePath;
   }

   public List<String> getFacesSuffixes()
   {
      List<String> suffixes = getFacesDefaultSuffixes();
      for (String s : getFaceletsDefaultSuffixes())
      {
         if (!suffixes.contains(s))
            suffixes.add(s);
      }
      return suffixes;
   }

   public List<String> getFacesDefaultSuffixes()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFacesDefaultSuffixes();
   }

   public List<String> getFaceletsDefaultSuffixes()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFaceletsDefaultSuffixes();
   }

   public List<String> getFaceletsViewMapping()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFaceletsViewMappings();
   }
}
