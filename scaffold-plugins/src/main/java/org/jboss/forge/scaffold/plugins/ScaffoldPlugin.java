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

package org.jboss.forge.scaffold.plugins;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.Entity;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.events.ScaffoldGeneratedResources;
import org.jboss.forge.scaffold.plugins.shell.ScaffoldProviderCompleter;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.Current;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.plugins.RequiresProject;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.plugins.Topic;
import org.jboss.forge.shell.util.ConstraintInspector;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
@Alias("scaffold")
@Topic("UI Generation & Scaffolding")
@Help("Faces UI scaffolding")
@RequiresProject
@RequiresFacet(ScaffoldProvider.class)
public class ScaffoldPlugin implements Plugin
{
   @Inject
   @Current
   private Resource<?> currentResource;

   @Inject
   private Project project;
   @Inject
   private ShellPrompt prompt;

   @Inject
   private Instance<ScaffoldProvider> impls;

   @Inject
   private Event<ScaffoldGeneratedResources> generatedEvent;

   @Inject
   private Event<InstallFacets> installFacets;

   @SetupCommand
   public void setup(
            final PipeOut out,
            @Option(name = "scaffoldType", required = false,
                     completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite,
            @Option(name = "usingTemplate") final Resource<?> template)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      verifyTemplate(provider, template);
      List<Resource<?>> generatedResources = provider.setup(template, overwrite);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
      {
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
      }
   }

   @Command("indexes")
   public void generateIndex(
            final PipeOut out,
            @Option(name = "scaffoldType", required = false,
                     completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite,
            @Option(name = "usingTemplate") final Resource<?> template)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      verifyTemplate(provider, template);
      List<Resource<?>> generatedResources = provider.generateIndex(template, overwrite);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
      {
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
      }
   }

   @Command("templates")
   public void generateTemplates(
            @Option(name = "scaffoldType", required = false,
                     completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            final PipeOut out,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite)
   {
      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      List<Resource<?>> generatedResources = provider.generateTemplates(overwrite);

      // TODO give plugins a chance to react to generated resources, use event bus?
      if (!generatedResources.isEmpty())
      {
         generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
      }
   }

   @Command("from-entity")
   public void generateFromEntity(
            @Option(required = false) JavaResource[] targets,
            @Option(name = "scaffoldType", required = false,
                     completer = ScaffoldProviderCompleter.class) final String scaffoldType,
            @Option(flagOnly = true, name = "overwrite") final boolean overwrite,
            @Option(name = "usingTemplate") final Resource<?> template,
            final PipeOut out) throws FileNotFoundException
   {
      if (((targets == null) || (targets.length < 1))
               && (currentResource instanceof JavaResource))
      {
         targets = new JavaResource[] { (JavaResource) currentResource };
      }

      List<JavaResource> javaTargets = selectTargets(out, targets);
      if (javaTargets.isEmpty())
      {
         ShellMessages.error(out, "Must specify a domain @Entity on which to operate.");
         return;
      }

      ScaffoldProvider provider = getScaffoldType(scaffoldType);
      verifyTemplate(provider, template);

      for (JavaResource jr : javaTargets)
      {
         JavaClass entity = (JavaClass) (jr).getJavaSource();
         List<Resource<?>> generatedResources = provider.generateFromEntity(template, entity, overwrite);

         // TODO give plugins a chance to react to generated resources, use event bus?
         if (!generatedResources.isEmpty())
         {
            generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
         }

         ShellMessages.success(out, "Generated UI for [" + entity.getQualifiedName() + "]");
      }

   }

   private ScaffoldProvider getScaffoldType(String scaffoldType)
   {
      ScaffoldProvider scaffoldImpl = null;

      Collection<Facet> facets = project.getFacets();
      List<ScaffoldProvider> detected = new ArrayList<ScaffoldProvider>();
      for (Facet facet : facets) {
         if (facet instanceof ScaffoldProvider)
         {
            detected.add((ScaffoldProvider) facet);
            scaffoldImpl = (ScaffoldProvider) facet;
         }
      }

      List<String> typeNames = new ArrayList<String>();
      for (ScaffoldProvider sp : detected) {
         typeNames.add(ConstraintInspector.getName(sp.getClass()));
      }
      if (detected.size() > 1)
      {
         // FIXME This needs to show the facet name!!!
         String name = prompt.promptChoiceTyped("Use which scaffold provider?", typeNames,
                  typeNames.get(typeNames.size() - 1));

         for (ScaffoldProvider sp : detected) {
            if (name.equals(ConstraintInspector.getName(sp.getClass())))
            {
               scaffoldImpl = sp;
               break;
            }
         }
      }

      if ((scaffoldType == null)
               && prompt.promptBoolean("No scaffold type was selected, use default (JSF)?"))
      {
         scaffoldType = "faces";
      }
      else if (scaffoldType == null)
      {
         throw new RuntimeException("Re-run with --scaffoldType {...}");
      }

      for (ScaffoldProvider type : impls)
      {
         if (ConstraintInspector.getName(type.getClass()).equals(scaffoldType))
         {
            scaffoldImpl = type;
         }
      }

      if (!project.hasFacet(scaffoldImpl.getClass())
               && prompt.promptBoolean("Scaffold provider [" + scaffoldType + "] is not installed. Install it?"))
      {
         installFacets.fire(new InstallFacets(scaffoldImpl.getClass()));
      }
      else if (!project.hasFacet(scaffoldImpl.getClass()))
      {
         throw new RuntimeException("Aborted.");
      }

      if (project.hasFacet(WebResourceFacet.class))
      {
         FileResource<?> favicon = project.getFacet(WebResourceFacet.class).getWebResource("/favicon.ico");
         if (!favicon.exists())
         {
            favicon.setContents(getClass().getResourceAsStream("/org/jboss/forge/scaffold/favicon.ico"));
         }
      }

      return project.getFacet(scaffoldImpl.getClass());
   }

   private List<JavaResource> selectTargets(final PipeOut out, Resource<?>[] targets)
            throws FileNotFoundException
   {
      List<JavaResource> results = new ArrayList<JavaResource>();
      if (targets == null)
      {
         targets = new Resource<?>[] {};
      }
      for (Resource<?> r : targets)
      {
         if (r instanceof JavaResource)
         {
            JavaSource<?> entity = ((JavaResource) r).getJavaSource();

            if (entity instanceof JavaClass)
            {
               if (entity.hasAnnotation(Entity.class))
               {
                  results.add((JavaResource) r);
               }
               else
               {
                  displaySkippingResourceMsg(out, entity);
               }
            }
            else
            {
               displaySkippingResourceMsg(out, entity);
            }
         }
      }
      return results;
   }

   private void displaySkippingResourceMsg(final PipeOut out, final JavaSource<?> entity)
   {
      if (!out.isPiped())
      {
         ShellMessages.info(out, "Skipped non-@Entity Java resource ["
                  + entity.getQualifiedName() + "]");
      }
   }

   private List<Resource<?>> prepareResources(final List<Resource<?>> generatedResources)
   {
      List<Integer> nullIndexes = new ArrayList<Integer>();
      for (int i = 0; i < generatedResources.size(); i++)
      {
         Resource<?> r = generatedResources.get(i);
         if (r == null)
         {
            nullIndexes.add(i);
         }
      }

      for (Integer index : nullIndexes)
      {
         generatedResources.remove(index);
      }
      return generatedResources;
   }

   private void verifyTemplate(final ScaffoldProvider provider, final Resource<?> template)
   {
      if (template != null)
      {
         if (!template.exists())
         {
            throw new IllegalArgumentException(
                     "Template [" + template.getName()
                              + "] does not exist. You must select a template that exists, or use " +
                              "the default template (do not specify a template.)");
         }

         if (!provider.getTemplateStrategy().compatibleWith(template))
         {
            throw new IllegalStateException("Template [" + template.getName() + "] is not compatible with provider ["
                     + ConstraintInspector.getName(provider.getClass()) + "]");
         }
      }
   }
}
