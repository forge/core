/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.metawidget.forge;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.events.AddedDependencies;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.shell.ShellColor;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.shrinkwrap.descriptor.api.spec.cdi.beans.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.impl.spec.servlet.web.WebAppDescriptorImpl;
import org.jboss.shrinkwrap.descriptor.spi.Node;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
@Alias("metawidget")
@RequiresFacet({ WebResourceFacet.class,
         DependencyFacet.class,
         PersistenceFacet.class,
         CDIFacet.class,
         FacesFacet.class })
public class MetawidgetScaffold extends BaseFacet implements ScaffoldProvider
{
   private static final String PARTIAL_STATE_SAVING = "javax.faces.PARTIAL_STATE_SAVING";
   private static final String SEAM_PERSIST_TRANSACTIONAL_ANNO = "org.jboss.seam.transaction.Transactional";
   private static final String SEAM_PERSIST_INTERCEPTOR = "org.jboss.seam.transaction.TransactionInterceptor";
   private static final String METAWIDGET_DISABLE_EVENT = "org.metawidget.faces.component.DONT_USE_PRERENDER_VIEW_EVENT";

   private static final String BACKING_BEAN_TEMPLATE = "org/metawidget/scaffold/BackingBean.jv";
   private static final String VIEW_TEMPLATE = "org/metawidget/scaffold/view.xhtml";
   private static final String CREATE_TEMPLATE = "org/metawidget/scaffold/create.xhtml";
   private static final String LIST_TEMPLATE = "org/metawidget/scaffold/list.xhtml";
   private static final String CONFIG_TEMPLATE = "org/metawidget/metawidget.xml";

   private final Dependency metawidgetAll = DependencyBuilder
            .create("org.metawidget.modules:metawidget-all");

   private final Dependency seamPersist = DependencyBuilder
            .create("org.jboss.seam.persistence:seam-persistence:[3.0.0.Final,)");

   private final Dependency richfaces3UI = DependencyBuilder.create("org.richfaces.ui:richfaces-ui");
   private final Dependency richfaces3Impl = DependencyBuilder.create("org.richfaces.framework:richfaces-impl");
   private final Dependency richfaces4UI = DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui");
   private final Dependency richfaces4Impl = DependencyBuilder.create("org.richfaces.core:richfaces-core-impl");

   private final CompiledTemplateResource viewTemplate;
   private final CompiledTemplateResource createTemplate;
   private final CompiledTemplateResource listTemplate;
   private final CompiledTemplateResource configTemplate;

   private final ShellPrompt prompt;
   private final ShellPrintWriter writer;
   private final TemplateCompiler compiler;
   private final Event<InstallFacets> install;

   @Inject
   public MetawidgetScaffold(final ShellPrompt prompt, final ShellPrintWriter writer, final TemplateCompiler compiler,
            final Event<InstallFacets> install)
   {
      this.prompt = prompt;
      this.writer = writer;
      this.compiler = compiler;
      this.install = install;
      viewTemplate = compiler.compile(VIEW_TEMPLATE);
      createTemplate = compiler.compile(CREATE_TEMPLATE);
      listTemplate = compiler.compile(LIST_TEMPLATE);
      configTemplate = compiler.compile(CONFIG_TEMPLATE);
   }

   @Override
   public List<Resource<?>> setup(final boolean overwrite)
   {
      createPersistenceUtils(false);
      createFacesUtils(false);
      List<Resource<?>> resources = generateIndex(overwrite);
      setupRichFaces(project);
      return resources;
   }

   public void handleAddedDependencies(@Observes final AddedDependencies event)
   {
      Project project = event.getProject();
      if (project.hasFacet(MetawidgetScaffold.class))
      {
         boolean richFacesUI = false;
         boolean richFacesImpl = false;
         for (Dependency d : event.getDependencies())
         {
            if (DependencyBuilder.areEquivalent(richfaces3UI, d))
            {
               richFacesUI = true;
            }
            if (DependencyBuilder.areEquivalent(richfaces3Impl, d))
            {
               richFacesImpl = true;
            }
            if (DependencyBuilder.areEquivalent(richfaces4UI, d))
            {
               richFacesUI = true;
            }
            if (DependencyBuilder.areEquivalent(richfaces4Impl, d))
            {
               richFacesImpl = true;
            }
         }

         if (richFacesImpl || richFacesUI)
         {
            setupRichFaces(project);
         }
      }
   }

   private void setupRichFaces(final Project project)
   {
      if ((project.getFacet(DependencyFacet.class).hasDependency(richfaces3UI)
               && project.getFacet(DependencyFacet.class).hasDependency(richfaces3Impl))
               || (project.getFacet(DependencyFacet.class).hasDependency(richfaces4UI)
                        && project.getFacet(DependencyFacet.class).hasDependency(richfaces4Impl)))
      {
         if (prompt
                  .promptBoolean(writer.renderColor(ShellColor.YELLOW, "Metawidget")
                           + " has detected RichFaces installed in this project. Would you like to configure Metawidget to use RichFaces?"))
         {

            WebResourceFacet web = project.getFacet(WebResourceFacet.class);

            ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("WEB-INF/metawidget.xml"),
                     getClass().getResourceAsStream("/org/metawidget/metawidget-richfaces.xml"), true);
         }
      }
   }

   @Override
   public List<Resource<?>> generateFromEntity(final JavaClass entity, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      try
      {
         JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
         WebResourceFacet web = project.getFacet(WebResourceFacet.class);

         CompiledTemplateResource backingBeanTemplate = compiler.compile(BACKING_BEAN_TEMPLATE);
         HashMap<Object, Object> context = new HashMap<Object, Object>();
         context.put("entity", entity);

         // Create the Backing Bean for this entity
         JavaClass viewBean = JavaParser.parse(JavaClass.class, backingBeanTemplate.render(context));
         viewBean.setPackage(java.getBasePackage() + ".view");
         viewBean.addAnnotation(SEAM_PERSIST_TRANSACTIONAL_ANNO);
         result.add(ScaffoldUtil.createOrOverwrite(prompt, java.getJavaResource(viewBean), viewBean.toString(),
                  overwrite));

         // Set context for view generation
         context = new HashMap<Object, Object>();
         String name = viewBean.getName();
         name = name.substring(0, 1).toLowerCase() + name.substring(1);
         context.put("beanName", name);
         context.put("entity", entity);

         // Generate views
         String type = entity.getName().toLowerCase();
         result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("scaffold/" + type + "/view.xhtml"),
                  viewTemplate.render(context), overwrite));
         result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("scaffold/" + type + "/create.xhtml"),
                  createTemplate.render(context),
                  overwrite));
         result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("scaffold/" + type + "/list.xhtml"),
                  listTemplate.render(context), overwrite));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default scaffolding.", e);
      }
      return result;
   }

   public void createMetawidgetConfig(final boolean overwrite)
   {
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("WEB-INF/metawidget.xml"),
               configTemplate.render(new HashMap<Object, Object>()), overwrite);
   }

   public void createPersistenceUtils(final boolean overwrite)
   {
      JavaClass util = JavaParser.parse(JavaClass.class,
               getClass().getResourceAsStream("/org/metawidget/persistence/PersistenceUtil.jv"));
      JavaClass producer = JavaParser.parse(JavaClass.class,
               getClass().getResourceAsStream("/org/metawidget/persistence/DatasourceProducer.jv"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      try
      {
         JavaResource producerResource = java.getJavaResource(producer);
         JavaResource utilResource = java.getJavaResource(util);

         ScaffoldUtil.createOrOverwrite(prompt, producerResource, producer.toString(), overwrite);
         ScaffoldUtil.createOrOverwrite(prompt, utilResource, util.toString(), overwrite);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void createFacesUtils(final boolean overwrite)
   {
      JavaClass util = JavaParser.parse(JavaClass.class,
               getClass().getResourceAsStream("/org/metawidget/persistence/PaginationHelper.jv"));
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      try
      {         
         JavaResource utilResource = java.getJavaResource(util);
         ScaffoldUtil.createOrOverwrite(prompt, utilResource, util.toString(), overwrite);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }
   
   @Override
   @SuppressWarnings("unchecked")
   public boolean install()
   {
      if (!(project.hasFacet(WebResourceFacet.class) && project.hasFacet(PersistenceFacet.class)
               && project.hasFacet(CDIFacet.class) && project.hasFacet(FacesFacet.class)))
      {
         install.fire(new InstallFacets(WebResourceFacet.class, PersistenceFacet.class, CDIFacet.class,
                  FacesFacet.class));
      }

      DependencyFacet df = project.getFacet(DependencyFacet.class);
      CDIFacet cdi = project.getFacet(CDIFacet.class);
      ServletFacet servlet = project.getFacet(ServletFacet.class);
      if (!df.hasDependency(metawidgetAll))
      {
         Dependency dependency = prompt.promptChoiceTyped("Install which version of Metawidget?",
                  df.resolveAvailableVersions(metawidgetAll));
         df.addDependency(dependency);
      }

      // fixme this needs to be fixed in SHRINKDESC
      WebAppDescriptorImpl webxml = (WebAppDescriptorImpl) servlet.getConfig();

      List<Node> list = webxml.getRootNode().get("context-param/param-name");

      // Hack to support JSF2 and metawidget
      ShellMessages.info(writer, "JSF2 and Metawidget currently require Partial State Saving to be disabled.");
      boolean pssUpdated = false;
      boolean mweUpdated = false;
      for (Node node : list)
      {
         if (PARTIAL_STATE_SAVING.equals(node.text()))
         {
            node.parent().getOrCreate("param-value").text("false");
            pssUpdated = true;
            continue;
         }
         if (METAWIDGET_DISABLE_EVENT.equals(node.text()))
         {
            node.parent().getOrCreate("param-value").text("true");
            continue;
         }
      }
      if (!mweUpdated)
      {
         webxml.contextParam(METAWIDGET_DISABLE_EVENT, "true");
      }
      if (!pssUpdated)
      {
         webxml.contextParam(PARTIAL_STATE_SAVING, "false");
      }
      servlet.saveConfig(webxml);

      if (!df.hasDependency(seamPersist))
      {
         df.addDependency(prompt.promptChoiceTyped("Install which version of Seam Persistence?",
                  df.resolveAvailableVersions(seamPersist)));

         BeansDescriptor config = cdi.getConfig();
         config.interceptor(SEAM_PERSIST_INTERCEPTOR);
         cdi.saveConfig(config);
      }
      createMetawidgetConfig(false);

      return true;
   }

   @Override
   public boolean isInstalled()
   {
      final DependencyFacet df = project.getFacet(DependencyFacet.class);
      return df.hasDependency(metawidgetAll)
               && df.hasDependency(seamPersist)
               && project.getFacet(CDIFacet.class).getConfig().getInterceptors().contains(SEAM_PERSIST_INTERCEPTOR)
               && project.getFacet(WebResourceFacet.class).getWebResource("/WEB-INF/metawidget.xml").exists();
   }

   @Override
   public List<Resource<?>> generateIndex(final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      project.getFacet(ServletFacet.class).getConfig().welcomeFile("index.html");

      result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("index.html"), getClass()
               .getResourceAsStream("/org/metawidget/templates/index.html"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("index.xhtml"),
               getClass().getResourceAsStream("/org/metawidget/templates/index.xhtml"), overwrite));

      generateTemplates(overwrite);
      return result;
   }

   @Override
   public List<Resource<?>> getGeneratedResources()
   {
      throw new RuntimeException("Not yet implemented!");
   }

   @Override
   public AccessStrategy getAccessStrategy()
   {
      final FacesFacet faces = project.getFacet(FacesFacet.class);

      return new AccessStrategy()
      {
         @Override
         public List<String> getWebPaths(final Resource<?> r)
         {
            return faces.getWebPaths(r);
         }

         @Override
         public Resource<?> fromWebPath(final String path)
         {
            return faces.getResourceForWebPath(path);
         }
      };
   }

   @Override
   public List<Resource<?>> generateTemplates(final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourceFacet web = project.getFacet(WebResourceFacet.class);

      result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("/resources/forge-template.xhtml"),
               getClass().getResourceAsStream("/org/metawidget/templates/forge-template.xhtml"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(prompt, web.getWebResource("/resources/forge.css"),
               getClass().getResourceAsStream("/org/metawidget/templates/forge.css"), overwrite));

      return result;
   }

}
