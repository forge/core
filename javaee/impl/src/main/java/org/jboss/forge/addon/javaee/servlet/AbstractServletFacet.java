/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.servlet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.AbstractJavaEEFacet;
import org.jboss.forge.addon.javaee.security.TransportGuarantee;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.dependencies.DependencyInstaller;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.furnace.util.Lists;
import org.jboss.forge.furnace.util.Strings;
import org.jboss.shrinkwrap.descriptor.api.javaee.SecurityRoleCommonType;
import org.jboss.shrinkwrap.descriptor.api.webapp.WebAppCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon.AuthConstraintCommonType;
import org.jboss.shrinkwrap.descriptor.api.webcommon.LoginConfigCommonType;
import org.jboss.shrinkwrap.descriptor.api.webcommon.SecurityConstraintCommonType;
import org.jboss.shrinkwrap.descriptor.api.webcommon.UserDataConstraintCommonType;
import org.jboss.shrinkwrap.descriptor.api.webcommon.WebResourceCollectionCommonType;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractServletFacet<DESCRIPTOR extends WebAppCommonDescriptor> extends AbstractJavaEEFacet
         implements ServletFacet<DESCRIPTOR>
{
   @Inject
   public AbstractServletFacet(DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public String getSpecName()
   {
      return "Servlet";
   }

   @Override
   public boolean isInstalled()
   {
      Project project = getFaceted();
      DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
      return super.isInstalled() && webRoot.exists();
   }

   @Override
   public boolean install()
   {
      if (!isInstalled())
      {
         Project project = getFaceted();
         DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
         if (!webRoot.exists())
         {
            webRoot.mkdirs();
         }
      }
      return super.install();
   }

   @Override
   public FileResource<?> getConfigFile()
   {
      Project project = getFaceted();
      DirectoryResource webRoot = project.getFacet(WebResourcesFacet.class).getWebRootDirectory();
      return (FileResource<?>) webRoot.getChild("WEB-INF" + File.separator + "web.xml");
   }

   private List<Resource<?>> listChildrenRecursively(final DirectoryResource webRoot)
   {
      return listChildrenRecursively(webRoot, new ResourceFilter()
      {
         @Override
         public boolean accept(final Resource<?> resource)
         {
            return true;
         }
      });
   }

   @Override
   public List<Resource<?>> getResources(final ResourceFilter filter)
   {
      DirectoryResource webRoot = getFaceted().getFacet(WebResourcesFacet.class).getWebRootDirectory();
      return listChildrenRecursively(webRoot, filter);
   }

   @Override
   public DirectoryResource getWebInfDirectory()
   {
      return getFaceted().getFacet(WebResourcesFacet.class).getWebRootDirectory().getChildDirectory("WEB-INF");
   }

   /**
    * List all servlet resource files.
    */
   @Override
   public List<Resource<?>> getResources()
   {
      DirectoryResource webRoot = getFaceted().getFacet(WebResourcesFacet.class).getWebRootDirectory();
      return listChildrenRecursively(webRoot);
   }

   private List<Resource<?>> listChildrenRecursively(final DirectoryResource current, final ResourceFilter filter)
   {
      List<Resource<?>> result = new ArrayList<>();
      List<Resource<?>> list = current.listResources();
      if (list != null)
      {
         for (Resource<?> file : list)
         {
            if (file instanceof DirectoryResource)
            {
               result.addAll(listChildrenRecursively((DirectoryResource) file, filter));
            }
            if (filter.accept(file))
               result.add(file);
         }
      }
      return result;
   }

   @Override
   public void addLoginConfig(String authMethod, String realmName)
   {
      DESCRIPTOR webXml = getConfig();
      LoginConfigCommonType loginConfig = webXml.getOrCreateLoginConfig();
      if (!Strings.isNullOrEmpty(authMethod))
      {
         loginConfig.authMethod(authMethod);
      }
      if (!Strings.isNullOrEmpty(realmName))
      {
         loginConfig.realmName(realmName);
      }
      saveConfig(webXml);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void addSecurityRole(String roleName)
   {
      DESCRIPTOR webXml = getConfig();
      List<SecurityRoleCommonType<?, ?>> allSecurityRole = webXml.getAllSecurityRole();
      Optional<SecurityRoleCommonType<?, ?>> existingRole = allSecurityRole.stream()
               .filter(role -> roleName.equals(role.getRoleName())).findFirst();
      if (!existingRole.isPresent())
      {
         webXml.createSecurityRole().roleName(roleName);
         saveConfig(webXml);
      }
   }

   @SuppressWarnings("unchecked")
   @Override
   public List<String> getSecurityRoles()
   {
      List<SecurityRoleCommonType<?, ?>> securityRoles = getConfig().getAllSecurityRole();
      return securityRoles
               .stream()
               .map(securityRole -> securityRole.getRoleName())
               .collect(Collectors.toList());
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean removeSecurityRole(String roleName)
   {
      boolean roleRemoved = false;
      DESCRIPTOR webXml = getConfig();

      List<SecurityRoleCommonType> initialRoles = new ArrayList<>(webXml.getAllSecurityRole());
      webXml.removeAllSecurityRole();

      for (SecurityRoleCommonType role : initialRoles)
      {
         if (!role.getRoleName().equals(roleName))
         {
            webXml.createSecurityRole().roleName(role.getRoleName());
         }
         else
         {
            roleRemoved = true;
         }
      }

      saveConfig(webXml);
      return roleRemoved;
   }

   @Override
   public void addSecurityConstraint(String displayName, String webResourceName, String webResourceDescription,
            Iterable<String> httpMethods, Iterable<String> urlPatterns, Iterable<String> securityRoles,
            TransportGuarantee transportGuarantee)
   {
      DESCRIPTOR webXml = getConfig();

      SecurityConstraintCommonType securityConstraint = webXml.createSecurityConstraint();
      List<String> httpMethodsList = new ArrayList<>();
      if (httpMethods != null)
      {
         httpMethodsList.addAll(Lists.toList(httpMethods));
      }
      List<String> urlPatternsList = new ArrayList<>();
      if (urlPatterns != null)
      {
         urlPatternsList.addAll(Lists.toList(urlPatterns));
      }
      List<String> securityRolesList = new ArrayList<>();
      if (securityRoles != null)
      {
         securityRolesList.addAll(Lists.toList(securityRoles));
      }
      if (displayName != null)
      {
         securityConstraint.displayName(displayName);
      }

      WebResourceCollectionCommonType resourceCollection = securityConstraint.createWebResourceCollection();
      resourceCollection.webResourceName(webResourceName);

      if (webResourceDescription != null)
      {
         resourceCollection.description(webResourceDescription);
      }

      if (!httpMethodsList.isEmpty())
      {
         resourceCollection.httpMethod(httpMethodsList.toArray(new String[httpMethodsList.size()]));
      }

      if (!urlPatternsList.isEmpty())
      {
         resourceCollection.urlPattern(urlPatternsList.toArray(new String[urlPatternsList.size()]));
      }

      if (!securityRolesList.isEmpty())
      {
         AuthConstraintCommonType authConstraint = securityConstraint.getOrCreateAuthConstraint();
         authConstraint.roleName(securityRolesList.toArray(new String[securityRolesList.size()]));
      }
      if (transportGuarantee != null)
      {
         UserDataConstraintCommonType userDataConstraint = securityConstraint.getOrCreateUserDataConstraint();
         userDataConstraint.transportGuarantee(transportGuarantee.name());
      }

      saveConfig(webXml);
   }
}
