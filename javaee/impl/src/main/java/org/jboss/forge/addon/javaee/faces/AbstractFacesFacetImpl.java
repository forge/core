/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.faces;

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

import javax.faces.application.ProjectStage;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_2_5;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.util.Streams;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.DescriptorImporter;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.javaee5.ParamValueType;
import org.jboss.shrinkwrap.descriptor.api.webapp25.ServletMappingType;
import org.jboss.shrinkwrap.descriptor.api.webapp25.WebAppDescriptor;

/**
 * Common Implementation for all JSF versions
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractFacesFacetImpl<DESCRIPTOR extends Descriptor> extends AbstractJavaEEFacet implements
         FacesFacet<DESCRIPTOR>
{
   public AbstractFacesFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public boolean isInstalled()
   {
      boolean active = false;
      String packagingType = getFaceted().getFacet(PackagingFacet.class).getPackagingType();

      if (packagingType.equalsIgnoreCase("war"))
      {
         active = (getConfigFile().exists() || getFaceted().hasFacet(ServletFacet_3_0.class) || hasServletMapping());
      }
      else
         active = true;

      return super.isInstalled() && active;
   }

   private boolean hasServletMapping()
   {
      if (getFaceted().hasFacet(ServletFacet_2_5.class))
      {
         ServletFacet_2_5 servlet = getFaceted().getFacet(ServletFacet_2_5.class);
         List<ServletMappingType<WebAppDescriptor>> mappings = servlet.getConfig().getAllServletMapping();
         for (ServletMappingType<WebAppDescriptor> mapping : mappings)
         {

         }
      }
      return false;
   }

   @Override
   public boolean install()
   {
      if (!getFaceted().hasFacet(ServletFacet_3_0.class) && getFaceted().hasFacet(ServletFacet_2_5.class)
               && !hasServletMapping())
      {
         setFacesMapping("*.xhtml");
      }
      return super.install();
   }

   @Override
   public DESCRIPTOR getConfig()
   {
      DESCRIPTOR descriptor;
      FileResource<?> configFile = getConfigFile();
      if (configFile.exists())
      {
         DescriptorImporter<DESCRIPTOR> importer = Descriptors.importAs(getDescriptorClass());
         descriptor = importer.fromStream(configFile.getResourceInputStream());
      }
      else
      {
         descriptor = Descriptors.create(getDescriptorClass());
      }
      return (DESCRIPTOR) descriptor;
   }

   protected abstract Class<DESCRIPTOR> getDescriptorClass();

   @Override
   public FileResource<?> getConfigFile()
   {
      Project project = getFaceted();
      PackagingFacet packaging = project.getFacet(PackagingFacet.class);
      if ("war".equals(packaging.getPackagingType()))
      {
         DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
         return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "faces-config.xml");
      }
      else
      {
         DirectoryResource root = project.getFacet(ResourcesFacet.class).getResourceFolder();
         return (FileResource<?>) root.getChild("META-INF" + File.separator + "faces-config.xml");
      }
   }

   @Override
   public ProjectStage getProjectStage()
   {
      if (getFaceted().hasFacet(ServletFacet_2_5.class))
      {
         ServletFacet_2_5 facet = getFaceted().getFacet(ServletFacet_2_5.class);
         WebAppDescriptor config = facet.getConfig();
         List<ParamValueType<WebAppDescriptor>> params = config.getAllContextParam();
         for (ParamValueType<WebAppDescriptor> param : params)
         {
            if ("javax.faces.PROJECT_STAGE".equals(param.getParamName()))
            {
               return ProjectStage.valueOf(ProjectStage.class, param.getParamValue());
            }
         }
      }
      else if (getFaceted().hasFacet(ServletFacet_3_0.class))
      {
         ServletFacet_3_0 facet = getFaceted().getFacet(ServletFacet_3_0.class);
         org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor config = facet.getConfig();
         List<org.jboss.shrinkwrap.descriptor.api.javaee6.ParamValueType<org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor>> params = config
                  .getAllContextParam();
         for (org.jboss.shrinkwrap.descriptor.api.javaee6.ParamValueType<org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor> param : params)
         {
            if ("javax.faces.PROJECT_STAGE".equals(param.getParamName()))
            {
               return ProjectStage.valueOf(ProjectStage.class, param.getParamValue());
            }
         }
      }

      return ProjectStage.Production;
   }

   @Override
   public List<String> getFacesServletMappings()
   {
      return getExplicitFacesServletMappings();
   }

   @Override
   public List<String> getEffectiveFacesServletMappings()
   {
      List<String> results = new ArrayList<String>();
      ServletFacet facet = getFaceted().getFacet(ServletFacet.class);
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

   private List<String> getExplicitFacesServletMappings()
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
      ServletFacet facet = getFaceted().getFacet(ServletFacet.class);
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
         WebResourceFacet web = getFaceted().getFacet(WebResourceFacet.class);
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
         WebResourceFacet web = getFaceted().getFacet(WebResourceFacet.class);
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
      ServletFacet facet = getFaceted().getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFacesDefaultSuffixes();
   }

   @Override
   public List<String> getFaceletsDefaultSuffixes()
   {
      ServletFacet facet = getFaceted().getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFaceletsDefaultSuffixes();
   }

   @Override
   public List<String> getFaceletsViewMapping()
   {
      ServletFacet facet = getFaceted().getFacet(ServletFacet.class);
      WebAppDescriptor webXml = facet.getConfig();
      return webXml.getFaceletsViewMappings();
   }

   private ServletMappingHelper helper = new ServletMappingHelper();

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
