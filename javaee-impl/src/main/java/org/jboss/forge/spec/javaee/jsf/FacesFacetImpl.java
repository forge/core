/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.jsf;

import java.io.File;
import java.io.InputStream;
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

import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.UnknownFileResource;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.forge.spec.javaee.util.ServletUtil;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.FacesProjectStage;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.ServletDef;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.ServletMappingDef;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.jsf")
@RequiresFacet(ServletFacet.class)
public class FacesFacetImpl extends BaseJavaEEFacet implements FacesFacet
{

   @Inject
   public FacesFacetImpl(final DependencyInstaller installer, final ShellPrintWriter out)
   {
      super(installer);
      this.out = out;
   }

   private ShellPrintWriter out;

   private ServletMappingHelper servletMappingHelper = new ServletMappingHelper();

   @Override
   public boolean isInstalled()
   {
      String version = project.getFacet(ServletFacet.class).getConfig().getVersion();
      return super.isInstalled() && (version == null || version.trim().startsWith("3"));
   }

   @Override
   public boolean install()
   {
      if (!getConfigFile().exists() && !getConfigFile().createNewFile())
      {
         throw new RuntimeException("Failed to create required [" + getConfigFile().getFullyQualifiedName() + "]");
      }
      else
      {
         getConfigFile().setContents(getClass()
                  .getResourceAsStream("/org/jboss/forge/web/faces-config.xml"));
      }

      return super.install();
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList();
   }

   /*
    * Facet Methods
    */

   @Override
   public FileResource<?> getConfigFile()
   {
      DirectoryResource webRoot = project.getFacet(WebResourceFacet.class).getWebRootDirectory();
      FileResource<?> child = (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "faces-config.xml");

      if (!child.exists())
      {
         return new UnknownFileResource(child.getResourceFactory(), child.getUnderlyingResourceObject())
         {
            @Override
            public InputStream getResourceInputStream()
            {
               if (!exists())
               {
                  String projectName = project.getFacet(MetadataFacet.class).getProjectName();
                  WebAppDescriptor unit = Descriptors.create(WebAppDescriptor.class)
                           .displayName(projectName)
                           .sessionTimeout(30);
                  return Streams.fromString(unit.exportAsString());
               }
               else
               {
                  return super.getResourceInputStream();
               }
            }

            @Override
            public UnknownFileResource setContents(InputStream data)
            {
               if (!exists())
               {
                  createNewFile();
               }
               return super.setContents(data);
            }

            @Override
            public UnknownFileResource setContents(char[] data)
            {
               if (!exists())
               {
                  createNewFile();
               }
               return super.setContents(data);
            }

            @Override
            public UnknownFileResource setContents(String data)
            {
               if (!exists())
               {
                  createNewFile();
               }
               return super.setContents(data);
            }
         };
      }

      return child;
   }

   @Override
   public FacesProjectStage getProjectStage()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor config = facet.getConfig();
      return config.getFacesProjectStage();
   }

   @Override
   public List<String> getFacesServletMappings()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return getExplicitFacesServletMappings(webXml);
   }

   @Override
   public List<String> getEffectiveFacesServletMappings()
   {
      List<String> results = new ArrayList<String>();
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();

      // TODO should probably take into account facelets view mappings
      // facelets.VIEW_MAPPINGS

      if (webXml.hasFacesServlet())
      {
         results.addAll(getExplicitFacesServletMappings(webXml));
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

   private List<String> getExplicitFacesServletMappings(final WebAppDescriptor webXml)
   {
      List<ServletDef> servlets = webXml.getServlets();
      List<String> results = new ArrayList<String>();
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
      return results;
   }

   @SuppressWarnings("resource")
   @Override
   public void setFacesMapping(final String mapping)
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      InputStream webXml = facet.getConfigFile().getResourceInputStream();
      InputStream newWebXml = servletMappingHelper.addFacesServletMapping(webXml, mapping);
      if (webXml != newWebXml)
      {
         facet.getConfigFile().setContents(newWebXml);
      }
      Streams.closeQuietly(webXml);
      Streams.closeQuietly(newWebXml);
   }

   @Override
   public List<String> getWebPaths(final Resource<?> r)
   {
      if (r != null)
      {
         WebResourceFacet web = project.getFacet(WebResourceFacet.class);
         List<DirectoryResource> webRootDirectories = web.getWebRootDirectories();
         for (DirectoryResource d : webRootDirectories)
         {
            if (r.getFullyQualifiedName().startsWith(d.getFullyQualifiedName()))
            {
               String path = r.getFullyQualifiedName().substring(d.getFullyQualifiedName().length());
               path = path.replace(File.separator, "/");
               return getWebPaths(path);
            }
         }
      }
      return new ArrayList<String>();
   }

   @Override
   public List<String> getWebPaths(final String path)
   {
      List<String> results = new ArrayList<String>();
      if (getResourceForWebPath(path) == null)
      {
         List<String> mappings = getEffectiveFacesServletMappings();
         for (String mapping : mappings)
         {
            String viewId = buildFacesViewId(mapping, path);
            if (!results.contains(viewId))
               results.add(viewId);
         }
      }
      return results;
   }

   @Override
   public Resource<?> getResourceForWebPath(String path)
   {
      if (path != null)
      {
         WebResourceFacet web = project.getFacet(WebResourceFacet.class);
         List<DirectoryResource> webRootDirectories = web.getWebRootDirectories();

         boolean matches = false;
         for (String mapping : getEffectiveFacesServletMappings())
         {
            Matcher matcher = ServletUtil.mappingToRegex(mapping).matcher(path);
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
               if ((child != null) && child.exists())
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
                  if ((child != null) && child.exists())
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
            while ((matched == false) && iterator.hasNext())
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

   @Override
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

   @Override
   public List<String> getFacesDefaultSuffixes()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFacesDefaultSuffixes();
   }

   @Override
   public List<String> getFaceletsDefaultSuffixes()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFaceletsDefaultSuffixes();
   }

   @Override
   public List<String> getFaceletsViewMapping()
   {
      ServletFacet facet = project.getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFaceletsViewMappings();
   }

   public static class ServletMappingHelper
   {
      public static final String FACES_SERVLET_CLASS = "javax.faces.webapp.FacesServlet";

      public InputStream addFacesServletMapping(final InputStream webXmlStream, final String mapping)
      {
         Node root = XMLParser.parse(webXmlStream);
         Node facesServlet = getOrCreateFacesServlet(root);
         boolean mappingCreated = createMappingIfNotExists(root, facesServlet, mapping);
         if (mappingCreated)
         {
            return XMLParser.toXMLInputStream(root);
         }
         else
         {
            return XMLParser.toXMLInputStream(root);
         }
      }

      public Node getOrCreateFacesServlet(final Node root)
      {
         List<Node> servlets = root.get("servlet");
         for (Node servlet : servlets)
         {
            if (FACES_SERVLET_CLASS.equals(servlet.getSingle("servlet-class").getText()))
            {
               return servlet;
            }
         }
         Node servlet = root.createChild("servlet");
         servlet.createChild("servlet-name").text("Faces Servlet");
         servlet.createChild("servlet-class").text(FACES_SERVLET_CLASS);
         servlet.createChild("load-on-startup").text("1");
         return servlet;
      }

      boolean createMappingIfNotExists(final Node root, final Node servlet, final String mapping)
      {
         List<Node> servletMappings = root.get("servlet-mapping");
         Node servletMappingNode = null;
         String servletName = servlet.getSingle("servlet-name").getText();
         for (Node servletMapping : servletMappings)
         {
            if (servletName.equals(servletMapping.getSingle("servlet-name").getText()))
            {
               servletMappingNode = servletMapping;
               List<Node> urlPatterns = servletMapping.get("url-pattern");
               for (Node urlPattern : urlPatterns)
               {
                  if (mapping.equals(urlPattern.getText()))
                  {
                     return false; // mapping already exists; not created
                  }

               }
            }
         }
         // Mapping does not exist, create it and add the url-pattern
         if (servletMappingNode == null)
         {
            servletMappingNode = root.createChild("servlet-mapping");
            servletMappingNode.createChild("servlet-name").text(servletName);
         }
         servletMappingNode.createChild("url-pattern").text(mapping);
         return true;
      }
   }

}
