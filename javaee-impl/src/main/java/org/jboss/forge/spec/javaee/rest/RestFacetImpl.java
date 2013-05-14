/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.javaee.rest;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.java.util.Strings;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.BaseJavaEEFacet;
import org.jboss.forge.spec.javaee.RestActivatorType;
import org.jboss.forge.spec.javaee.RestApplicationFacet;
import org.jboss.forge.spec.javaee.RestFacet;
import org.jboss.forge.spec.javaee.RestWebXmlFacet;
import org.jboss.forge.spec.javaee.ServletFacet;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("forge.spec.jaxrs")
@RequiresFacet(ServletFacet.class)
public class RestFacetImpl extends BaseJavaEEFacet implements RestFacet
{

   @Inject
   private Configuration configuration;
   
   @Inject
   private ShellPrompt prompt;
   
   @Inject
   private Event<InstallFacets> request;

   @Inject
   public RestFacetImpl(final DependencyInstaller installer)
   {
      super(installer);
   }

   @Override
   public boolean install()
   {
      RestActivatorType activatorType = null;
      String activatorChoice = configuration.getString(RestFacet.ACTIVATOR_CHOICE);
      if (activatorChoice == null || activatorChoice.equals(""))
      {
         activatorType = prompt.promptEnum("How do you want to activate REST resources in your application ?", RestActivatorType.class,
                  RestActivatorType.WEB_XML);
      }
      else
      {
         activatorType = RestActivatorType.valueOf(activatorChoice);
      }
      
      String rootpath = prompt.prompt("What root path do you want to use for your resources?", "/rest");
      configuration.addProperty(RestFacet.ROOTPATH, rootpath);
      
      if (activatorType == null || activatorType == RestActivatorType.WEB_XML
               && !project.hasFacet(RestWebXmlFacetImpl.class))
      {
         request.fire(new InstallFacets(RestWebXmlFacetImpl.class));
      }
      else if (activatorType == RestActivatorType.APP_CLASS && !project.hasFacet(RestApplicationFacet.class))
      {
         String pkg = prompt.promptCommon("In what package do you want to store the Application class?",
                  PromptType.JAVA_PACKAGE);
         String restApplication = prompt.prompt("How do you want to name the Application class?", "RestApplication");
         configuration.addProperty(RestApplicationFacet.REST_APPLICATIONCLASS_PACKAGE, pkg);
         configuration.addProperty(RestApplicationFacet.REST_APPLICATIONCLASS_NAME, restApplication);
         request.fire(new InstallFacets(RestApplicationFacet.class));
      }
      return super.install();
   }

   @Override
   public boolean isInstalled()
   {
      if (!project.hasFacet(RestWebXmlFacet.class) && !project.hasFacet(RestApplicationFacet.class))
      {
         return false;
      }
      return super.isInstalled();
   }

   @Override
   protected List<Dependency> getRequiredDependencies()
   {
      return Arrays.asList(
              (Dependency) DependencyBuilder.create("org.jboss.spec.javax.ws.rs:jboss-jaxrs-api_1.1_spec")
      );
   }

   @Override
   public String getApplicationPath()
   {
      return configuration.getString(RestFacet.ROOTPATH);
   }
}
