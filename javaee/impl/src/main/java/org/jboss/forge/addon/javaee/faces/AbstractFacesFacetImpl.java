/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import org.jboss.forge.addon.javaee.Descriptors;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_2_5;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.javaee.util.ServletUtil;
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
import org.jboss.shrinkwrap.descriptor.api.javaee.ParamValueCommonType;
import org.jboss.shrinkwrap.descriptor.api.webapp.WebAppCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon.ServletMappingCommonType;

/**
 * Common Implementation for all JSF versions
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractFacesFacetImpl<DESCRIPTOR extends Descriptor> extends AbstractJavaEEFacet implements
         FacesFacet<DESCRIPTOR>
{
   private static final String FACELETS_VIEW_MAPPINGS = "javax.faces.FACELETS_VIEW_MAPPINGS";
   private static final String FACES_SERVLET_NAME = "Faces Facet";
   private static final String JAVAX_FACES_DEFAULT_SUFFIX = "javax.faces.DEFAULT_SUFFIX";

   public AbstractFacesFacetImpl(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "JavaServer Faces";
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

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private boolean hasServletMapping()
   {
      if (getFaceted().hasFacet(ServletFacet.class))
      {
         ServletFacet servlet = getFaceted().getFacet(ServletFacet.class);
         List<ServletMappingCommonType> mappings = ((WebAppCommonDescriptor) servlet.getConfig())
                  .getAllServletMapping();
         for (ServletMappingCommonType mapping : mappings)
         {
            if (FACES_SERVLET_NAME.equals(mapping.getServletName()))
            {
               return true;
            }
         }
      }
      return false;
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         // Add a faces-config.xml for WARs. Note - the facet can be activated even for JSF components.
         String packagingType = getFaceted().getFacet(PackagingFacet.class).getPackagingType();
         if (packagingType.equalsIgnoreCase("war"))
         {
            FileResource<?> descriptor = getConfigFile();
            if (!descriptor.exists())
            {
               createDefaultConfig(descriptor);
            }
         }
         if (!getFaceted().hasFacet(ServletFacet_3_0.class) && getFaceted().hasFacet(ServletFacet_2_5.class)
                  && !hasServletMapping())
         {
            setFacesMapping("*.xhtml");
         }
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
      return descriptor;
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
         DirectoryResource root = project.getFacet(ResourcesFacet.class).getResourceDirectory();
         return (FileResource<?>) root.getChild("META-INF" + File.separator + "faces-config.xml");
      }
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public ProjectStage getProjectStage()
   {
      if (getFaceted().hasFacet(ServletFacet.class))
      {
         ServletFacet<?> servlet = getFaceted().getFacet(ServletFacet.class);
         WebAppCommonDescriptor config = servlet.getConfig();
         List<ParamValueCommonType> params = config.getAllContextParam();
         for (ParamValueCommonType param : params)
         {
            if (ProjectStage.PROJECT_STAGE_PARAM_NAME.equals(param.getParamName()))
            {
               return ProjectStage.valueOf(ProjectStage.class, param.getParamValue());
            }
         }
      }

      return ProjectStage.Production;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public void setProjectStage(ProjectStage projectStage)
   {
      if (getFaceted().hasFacet(ServletFacet.class))
      {
         ServletFacet servlet = getFaceted().getFacet(ServletFacet.class);
         WebAppCommonDescriptor config = (WebAppCommonDescriptor) servlet.getConfig();
         List<ParamValueCommonType> params = config.getAllContextParam();
         ParamValueCommonType projectStageParam = null;
         for (ParamValueCommonType param : params)
         {
            if (ProjectStage.PROJECT_STAGE_PARAM_NAME.equals(param.getParamName()))
            {
               projectStageParam = param;
               break;
            }
         }
         if (projectStageParam == null)
         {
            projectStageParam = config.createContextParam();
            projectStageParam.paramName(ProjectStage.PROJECT_STAGE_PARAM_NAME);
         }
         projectStageParam.paramValue(projectStage.toString());
         servlet.saveConfig(config);
      }
   }

   @Override
   public List<String> getFacesServletMappings()
   {
      return getExplicitFacesServletMappings();
   }

   @Override
   @SuppressWarnings("rawtypes")
   public List<String> getEffectiveFacesServletMappings()
   {
      List<String> results = new ArrayList<>();
      ServletFacet<?> servlet = getFaceted().getFacet(ServletFacet.class);
      WebAppCommonDescriptor webXml = servlet.getConfig();

      // TODO should probably take into account facelets view mappings
      // facelets.VIEW_MAPPINGS

      results.addAll(getExplicitFacesServletMappings());
      if (results.isEmpty() && (webXml instanceof WebAppDescriptor
               || webXml instanceof org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor))
      {
         results.add("*.jsf");
         results.add("/faces/*");
      }
      return results;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private List<String> getExplicitFacesServletMappings()
   {
      ServletFacet<?> servletFacet = getFaceted().getFacet(ServletFacet.class);
      List<String> results = new ArrayList<>();
      for (ServletMappingCommonType mapping : (List<ServletMappingCommonType>) servletFacet.getConfig()
               .getAllServletMapping())
      {
         if (mapping.getServletName().equals(FACES_SERVLET_NAME))
         {
            results.addAll(mapping.getAllUrlPattern());
         }
      }
      return results;
   }

   @Override
   public void setFacesMapping(final String mapping)
   {
      ServletFacet<?> facet = getFaceted().getFacet(ServletFacet.class);
      InputStream webXml = facet.getConfigFile().getResourceInputStream();
      InputStream newWebXml = helper.addFacesServletMapping(webXml, mapping);
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
         WebResourcesFacet web = getFaceted().getFacet(WebResourcesFacet.class);
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
      return new ArrayList<>();
   }

   @Override
   public List<String> getWebPaths(final String path)
   {
      List<String> results = new ArrayList<>();
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
         WebResourcesFacet web = getFaceted().getFacet(WebResourcesFacet.class);
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
            Queue<String> queue = new LinkedList<>();
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

            Map<Pattern, String> patterns = new HashMap<>();

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
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public List<String> getFacesSuffixes()
   {
      List<String> suffixes = new ArrayList<>();
      if (getFaceted().hasFacet(ServletFacet.class))
      {
         ServletFacet<?> servlet = getFaceted().getFacet(ServletFacet.class);
         WebAppCommonDescriptor config = servlet.getConfig();
         List<ParamValueCommonType> params = config.getAllContextParam();
         for (ParamValueCommonType param : params)
         {
            if (JAVAX_FACES_DEFAULT_SUFFIX.equals(param.getParamName()))
            {
               suffixes.add(param.getParamValue());
            }
         }
      }

      if (suffixes.isEmpty())
         suffixes.add(".xhtml");

      return suffixes;
   }

   @Override
   public List<String> getFaceletsDefaultSuffixes()
   {
      return Arrays.asList(".xhtml");
   }

   @Override
   @SuppressWarnings({ "rawtypes", "unchecked" })
   public List<String> getFaceletsViewMappings()
   {
      List<String> suffixes = new ArrayList<>();
      if (getFaceted().hasFacet(ServletFacet.class))
      {
         ServletFacet<?> servlet = getFaceted().getFacet(ServletFacet.class);
         WebAppCommonDescriptor config = servlet.getConfig();
         List<ParamValueCommonType> params = config.getAllContextParam();
         for (ParamValueCommonType param : params)
         {
            if (FACELETS_VIEW_MAPPINGS.equals(param.getParamName()))
            {
               suffixes.add(param.getParamValue());
            }
         }
      }

      if (suffixes.isEmpty())
         suffixes.add(".xhtml");

      return suffixes;
   }

   protected abstract void createDefaultConfig(FileResource<?> descriptor);

   private final ServletMappingHelper helper = new ServletMappingHelper();

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
