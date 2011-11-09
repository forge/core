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
package org.jboss.forge.scaffold.faces;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.events.AddedDependencies;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.ResourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyle;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyleConfig;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.spi.TemplateResolver;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.ClassLoaderTemplateResolver;
import org.jboss.shrinkwrap.descriptor.api.spec.cdi.beans.BeansDescriptor;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.iface.Inspector;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.jpa.JpaInspector;
import org.metawidget.inspector.jpa.JpaInspectorConfig;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.ReadOnlyWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.richfaces.RichFacesWidgetBuilder;
import org.metawidget.statically.layout.SimpleLayout;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.IOUtils;
import org.metawidget.util.XmlUtils;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilder;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilderConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Alias("faces")
@RequiresFacet({ WebResourceFacet.class,
         DependencyFacet.class,
         PersistenceFacet.class,
         CDIFacet.class,
         FacesFacet.class })
public class FacesScaffold extends BaseFacet implements ScaffoldProvider
{
   private static final String XMLNS_PREFIX = "xmlns:";

   private static final String SEAM_PERSIST_INTERCEPTOR = "org.jboss.seam.transaction.TransactionInterceptor";

   private static final String REWRITE_CONFIG_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/URLRewriteConfiguration.jv";
   private static final String BACKING_BEAN_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/BackingBean.jv";
   private static final String VIEW_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/view.xhtml";
   private static final String CREATE_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/create.xhtml";
   private static final String LIST_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/list.xhtml";
   private static final String E404_TEMPLATE = "org/jboss/forge/scaffold/faces/templates/404.xhtml";
   private static final String E500_TEMPLATE = "org/jboss/forge/scaffold/faces/templates/500.xhtml";
   private static final String INDEX_TEMPLATE = "org/jboss/forge/scaffold/faces/templates/index.xhtml";

   private final Dependency richfaces3UI = DependencyBuilder.create("org.richfaces.ui:richfaces-ui");
   private final Dependency richfaces3Impl = DependencyBuilder.create("org.richfaces.framework:richfaces-impl");
   private final Dependency richfaces4UI = DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui");
   private final Dependency richfaces4Impl = DependencyBuilder.create("org.richfaces.core:richfaces-core-impl");

   private CompiledTemplateResource backingBeanTemplate;
   private CompiledTemplateResource rewriteConfigTemplate;
   private CompiledTemplateResource viewTemplate;
   private Map<String, String> viewTemplateNamespaces;
   private int viewTemplateMetawidgetIndent;

   private CompiledTemplateResource createTemplate;
   private Map<String, String> createTemplateNamespaces;
   private int createTemplateMetawidgetIndent;

   private CompiledTemplateResource listTemplate;
   private Map<String, String> listTemplateNamespaces;
   private int listTemplateMetawidgetIndent;

   private CompiledTemplateResource e404Template;
   private CompiledTemplateResource e500Template;
   private CompiledTemplateResource indexTemplate;
   TemplateResolver<ClassLoader> resolver;

   private final ShellPrompt prompt;
   private final TemplateCompiler compiler;
   private final Event<InstallFacets> install;
   private final StaticHtmlMetawidget metawidget;

   @Inject
   public FacesScaffold(final ShellPrompt prompt,
            final TemplateCompiler compiler,
            final Event<InstallFacets> install)
   {
      this.prompt = prompt;
      this.compiler = compiler;
      this.install = install;

      this.resolver = new ClassLoaderTemplateResolver(FacesScaffold.class.getClassLoader());
      compiler.getTemplateResolverFactory().addResolver(this.resolver);

      // Initialise Metawidget

      this.metawidget = new StaticHtmlMetawidget();
   }

   private void loadTemplates()
   {
      if (this.backingBeanTemplate == null)
      {
         this.backingBeanTemplate = this.compiler.compile(BACKING_BEAN_TEMPLATE);
      }
      if (this.rewriteConfigTemplate == null)
      {
         this.rewriteConfigTemplate = this.compiler.compile(REWRITE_CONFIG_TEMPLATE);
      }
      if (this.viewTemplate == null)
      {
         this.viewTemplate = this.compiler.compile(VIEW_TEMPLATE);
         NamespacesAndIndent namespacesAndIndent = parseNamespacesAndIndent(this.compiler
                  .getTemplateResolverFactory()
                  .resolve(VIEW_TEMPLATE).getInputStream());
         this.viewTemplateNamespaces = namespacesAndIndent.getNamespaces();
         this.viewTemplateMetawidgetIndent = namespacesAndIndent.getIndent();
      }
      if (this.createTemplate == null)
      {
         this.createTemplate = this.compiler.compile(CREATE_TEMPLATE);
         NamespacesAndIndent namespacesAndIndent = parseNamespacesAndIndent(this.compiler
                  .getTemplateResolverFactory()
                  .resolve(CREATE_TEMPLATE).getInputStream());
         this.createTemplateNamespaces = namespacesAndIndent.getNamespaces();
         this.createTemplateMetawidgetIndent = namespacesAndIndent.getIndent();
      }
      if (this.listTemplate == null)
      {
         this.listTemplate = this.compiler.compile(LIST_TEMPLATE);
         NamespacesAndIndent namespacesAndIndent = parseNamespacesAndIndent(this.compiler
                  .getTemplateResolverFactory()
                  .resolve(LIST_TEMPLATE).getInputStream());
         this.listTemplateNamespaces = namespacesAndIndent.getNamespaces();
         this.listTemplateMetawidgetIndent = namespacesAndIndent.getIndent();
      }
      if (this.e404Template == null)
      {
         this.e404Template = this.compiler.compile(E404_TEMPLATE);
      }
      if (this.e500Template == null)
      {
         this.e500Template = this.compiler.compile(E500_TEMPLATE);
      }
      if (this.indexTemplate == null)
      {
         this.indexTemplate = this.compiler.compile(INDEX_TEMPLATE);
      }
   }

   @Override
   public List<Resource<?>> setup(final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> resources = generateIndex(template, overwrite);
      setupRichFaces();
      setupWebXML();
      setupRewrite();
      setupMetawidget();

      CDIFacet cdi = this.project.getFacet(CDIFacet.class);

      if (!this.project.getFacet(CDIFacet.class).getConfig().getInterceptors().contains(SEAM_PERSIST_INTERCEPTOR))
      {
         BeansDescriptor config = cdi.getConfig();
         config.interceptor(SEAM_PERSIST_INTERCEPTOR);
         cdi.saveConfig(config);
      }

      return resources;
   }

   public void handleAddedDependencies(@Observes final AddedDependencies event)
   {
      if (this.project.hasFacet(FacesScaffold.class))
      {
         boolean richFacesUI = false;
         boolean richFacesImpl = false;
         for (Dependency d : event.getDependencies())
         {
            if (DependencyBuilder.areEquivalent(this.richfaces3UI, d))
            {
               richFacesUI = true;
            }
            if (DependencyBuilder.areEquivalent(this.richfaces3Impl, d))
            {
               richFacesImpl = true;
            }
            if (DependencyBuilder.areEquivalent(this.richfaces4UI, d))
            {
               richFacesUI = true;
            }
            if (DependencyBuilder.areEquivalent(this.richfaces4Impl, d))
            {
               richFacesImpl = true;
            }
         }

         if (richFacesImpl || richFacesUI)
         {
            setupRichFaces();
         }
      }
   }

   private void setupMetawidget()
   {
      ForgePropertyStyle forgePropertyStyle = new ForgePropertyStyle(
               new ForgePropertyStyleConfig().setProject(this.project));

      Inspector inspector = new CompositeInspector(new CompositeInspectorConfig()
               .setInspectors(
                        new PropertyTypeInspector(new BaseObjectInspectorConfig()
                                 .setPropertyStyle(forgePropertyStyle)),
                        new JpaInspector(new JpaInspectorConfig()
                                 .setPropertyStyle(forgePropertyStyle))));

      this.metawidget.setInspector(inspector);
   }

   private void setupRichFaces()
   {
      if ((this.project.getFacet(DependencyFacet.class).hasDependency(this.richfaces3UI)
               && this.project.getFacet(DependencyFacet.class).hasDependency(this.richfaces3Impl))
               || (this.project.getFacet(DependencyFacet.class).hasDependency(this.richfaces4UI)
               && this.project.getFacet(DependencyFacet.class).hasDependency(this.richfaces4Impl)))
      {
         @SuppressWarnings("unchecked")
         CompositeWidgetBuilder<StaticWidget, StaticMetawidget> compositeWidgetBuider = new CompositeWidgetBuilder<StaticWidget, StaticMetawidget>(
                  new CompositeWidgetBuilderConfig<StaticWidget, StaticMetawidget>().setWidgetBuilders(
                           new ReadOnlyWidgetBuilder(),
                           new RichFacesWidgetBuilder(), new HtmlWidgetBuilder()));
         this.metawidget.setWidgetBuilder(compositeWidgetBuider);
      }
   }

   @Override
   public List<Resource<?>> generateFromEntity(final Resource<?> template, final JavaClass entity,
            final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      try
      {
         JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
         WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

         loadTemplates();
         Map<Object, Object> context = CollectionUtils.newHashMap();
         context.put("entity", entity);
         context.put("ccEntity", entity.getName().substring(0, 1).toLowerCase() + entity.getName().substring(1));

         // Create the Backing Bean for this entity
         JavaClass viewBean = JavaParser.parse(JavaClass.class, this.backingBeanTemplate.render(context));
         viewBean.setPackage(java.getBasePackage() + ".view");
         // viewBean.addAnnotation(SEAM_PERSIST_TRANSACTIONAL_ANNO);
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(viewBean), viewBean.toString(),
                  overwrite));

         // Set new context for view generation
         context = getTemplateContext(template);
         String beanName = viewBean.getName().substring(0, 1).toLowerCase() + viewBean.getName().substring(1);
         context.put("beanName", beanName);
         String ccEntity = entity.getName().substring(0, 1).toLowerCase() + entity.getName().substring(1);
         context.put("ccEntity", ccEntity);
         context.put("entity", entity);

         // Prepare Metawidget
         this.metawidget.setValueExpression("value", StaticFacesUtils.wrapExpression(beanName + "." + ccEntity));
         this.metawidget.setPath(entity.getQualifiedName());

         String type = entity.getName().toLowerCase();

         // Generate create view
         writeMetawidget(context, this.createTemplateMetawidgetIndent, this.createTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource("scaffold/" + type + "/create.xhtml"),
                  this.createTemplate.render(context),
                  overwrite));

         // Generate read-only view
         this.metawidget.setReadOnly(true);
         writeMetawidget(context, this.viewTemplateMetawidgetIndent, this.viewTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("scaffold/" + type + "/view.xhtml"),
                  this.viewTemplate.render(context), overwrite));

         // Generate list view
         this.metawidget.setLayout(new SimpleLayout());
         this.metawidget.setStyle("margin-right: 0.5em");
         writeMetawidget(context, this.listTemplateMetawidgetIndent, this.listTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("scaffold/" + type + "/list.xhtml"),
                  this.listTemplate.render(context), overwrite));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default scaffolding.", e);
      }
      return result;
   }

   private HashMap<Object, Object> getTemplateContext(final Resource<?> template)
   {
      HashMap<Object, Object> context;
      context = new HashMap<Object, Object>();
      context.put("template", template);
      context.put("templateStrategy", getTemplateStrategy());
      return context;
   }

   @Override
   @SuppressWarnings("unchecked")
   public boolean install()
   {
      if (!(this.project.hasFacet(WebResourceFacet.class) && this.project.hasFacet(PersistenceFacet.class)
               && this.project.hasFacet(CDIFacet.class) && this.project.hasFacet(FacesFacet.class)))
      {
         this.install.fire(new InstallFacets(WebResourceFacet.class, PersistenceFacet.class, CDIFacet.class,
                  FacesFacet.class));
      }

      DependencyFacet df = this.project.getFacet(DependencyFacet.class);

      String version = null;
      for (Dependency dependency : getFacesDependencies())
      {
         if (!df.hasDependency(dependency))
         {
            if (version == null)
            {
               dependency = this.prompt.promptChoiceTyped("Install which version of Faces Scaffold?",
                        df.resolveAvailableVersions(dependency));
               version = dependency.getVersion();
            }
            else
            {
               dependency = DependencyBuilder.create(dependency).setVersion(version);
            }

            df.addDependency(dependency);
         }
      }

      return true;
   }

   private void setupWebXML()
   {
      ServletFacet servlet = this.project.getFacet(ServletFacet.class);

      Node webXML = removeConflictingErrorPages(servlet);
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
      servlet.getConfigFile().setContents(XMLParser.toXMLInputStream(webXML));

      WebAppDescriptor config = servlet.getConfig();
      config.errorPage(404, getAccessStrategy().getWebPaths(web.getWebResource("404.xhtml")).get(0));
      config.errorPage(500, getAccessStrategy().getWebPaths(web.getWebResource("500.xhtml")).get(0));

      servlet.saveConfig(config);
   }

   private Node removeConflictingErrorPages(final ServletFacet servlet)
   {
      Node webXML = XMLParser.parse(servlet.getConfigFile().getResourceInputStream());
      Node root = webXML.getRoot();
      List<Node> errorPages = root.get("error-page");

      for (String code : Arrays.asList("404", "500"))
      {
         for (Node errorPage : errorPages)
         {
            if (code.equals(errorPage.getSingle("error-code").getText())
                     && this.prompt.promptBoolean("Your web.xml already contains an error page for " + code
                              + " status codes, replace it?"))
            {
               root.removeChild(errorPage);
            }
         }
      }
      return webXML;
   }

   private void setupRewrite()
   {
      JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
      FacesFacet faces = this.project.getFacet(FacesFacet.class);

      loadTemplates();

      Map<Object, Object> context = new HashMap<Object, Object>();
      context.put("indexPage", faces.getWebPaths("/index.xhtml").get(0));
      context.put("notFoundPage", faces.getWebPaths("/404.xhtml").get(0));
      context.put("errorPage", faces.getWebPaths("/500.xhtml").get(0));
      context.put("listPage", faces.getWebPaths("/scaffold/{domain}/list.xhtml").get(0));
      context.put("createPage", faces.getWebPaths("/scaffold/{domain}/create.xhtml").get(0));
      context.put("viewPage", faces.getWebPaths("/scaffold/{domain}/view.xhtml").get(0));

      JavaSource<?> rewriteConfig = JavaParser.parse(this.rewriteConfigTemplate.render(context));
      rewriteConfig.setPackage(java.getBasePackage() + ".rewrite");

      try
      {
         ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(rewriteConfig),
                  rewriteConfig.toString(), false);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Could not save Rewrite Configuration source file", e);
      }

      ResourceFacet resources = this.project.getFacet(ResourceFacet.class);
      DirectoryResource services = resources.getResourceFolder().getOrCreateChildDirectory("META-INF")
               .getOrCreateChildDirectory("services");

      // Register the configuration provider
      ScaffoldUtil.createOrOverwrite(this.prompt,
               (FileResource<?>) services.getChild("com.ocpsoft.rewrite.config.ConfigurationProvider"),
               rewriteConfig.getQualifiedName(), false);
   }

   @Override
   public boolean isInstalled()
   {
      final DependencyFacet df = this.project.getFacet(DependencyFacet.class);
      boolean hasDependencies = true;
      for (Dependency dependency : getFacesDependencies())
      {
         if (!df.hasDependency(dependency))
         {
            hasDependencies = false;
            break;
         }
      }

      return hasDependencies;
   }

   @Override
   public List<Resource<?>> generateIndex(final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

      this.project.getFacet(ServletFacet.class).getConfig().welcomeFile("index.html");

      generateTemplates(overwrite);
      HashMap<Object, Object> context = getTemplateContext(template);

      loadTemplates();
      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("index.html"), getClass()
               .getResourceAsStream("/org/jboss/forge/scaffold/faces/templates/index.html"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("index.xhtml"),
               this.indexTemplate.render(context), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("404.xhtml"),
               this.e404Template.render(context), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("500.xhtml"),
               this.e500Template.render(context), overwrite));

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
      return new FacesAccessStrategy(this.project);
   }

   @Override
   public TemplateStrategy getTemplateStrategy()
   {
      return new FacesTemplateStrategy(this.project);
   }

   @Override
   public List<Resource<?>> generateTemplates(final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, (FileResource<?>) getTemplateStrategy()
               .getDefaultTemplate(),
               getClass().getResourceAsStream("/org/jboss/forge/scaffold/faces/templates/forge-template.xhtml"),
               overwrite));

      return result;
   }

   private List<Dependency> getFacesDependencies()
   {
      return Arrays.asList(
               (Dependency) DependencyBuilder.create("org.jboss.forge:forge-scaffold-faces-lib"));
   }

   /**
    * Parses XML from the given input stream and determines what namespaces it declares.
    */

   private NamespacesAndIndent parseNamespacesAndIndent(final InputStream streamIn)
   {
      Map<String, String> namespaces = CollectionUtils.newHashMap();

      ByteArrayOutputStream streamOut = new ByteArrayOutputStream();
      IOUtils.streamBetween(streamIn, streamOut);
      String template = streamOut.toString();

      // Determine namespaces

      Document document = XmlUtils.documentFromString(template);
      Element element = document.getDocumentElement();
      NamedNodeMap attributes = element.getAttributes();

      for (int loop = 0, length = attributes.getLength(); loop < length; loop++)
      {
         org.w3c.dom.Node node = attributes.item(loop);
         String nodeName = node.getNodeName();
         int indexOf = nodeName.indexOf(XMLNS_PREFIX);

         if (indexOf == -1)
         {
            continue;
         }

         namespaces.put(nodeName.substring(indexOf + XMLNS_PREFIX.length()), node.getNodeValue());
      }

      // Determine indent

      int indent = 0;
      int indexOf = template.indexOf("@{metawidget}");

      while ((indexOf >= 0) && (template.charAt(indexOf) != '\n'))
      {
         if (template.charAt(indexOf) == '\t')
         {
            indent++;
         }

         indexOf--;
      }

      return new NamespacesAndIndent(namespaces, indent);
   }

   /**
    * Writes the Metawidget into the given context.
    */

   private void writeMetawidget(final Map<Object, Object> context, final int indent,
            final Map<String, String> existingNamespaces)
   {
      StringWriter stringWriter = new StringWriter();
      this.metawidget.write(stringWriter, indent);
      context.put("metawidget", stringWriter.toString().trim());

      Map<String, String> namespaces = this.metawidget.getNamespaces();
      namespaces.keySet().removeAll(existingNamespaces.keySet());

      StringBuilder builder = new StringBuilder();

      for (Map.Entry<String, String> entry : namespaces.entrySet())
      {
         // At the start, break out of the current quote. Field must be in quotes so that we're valid XML

         builder.append("\"\r\n\txmlns:");
         builder.append(entry.getKey());
         builder.append("=\"");
         builder.append(entry.getValue());
      }

      context.put("metawidgetNamespaces", builder.toString());
   }

   //
   // Inner class
   //

   /**
    * Simple immutable structure to store some namespaces and an indent.
    *
    * @author Richard Kennard
    */

   private static class NamespacesAndIndent
   {

      //
      // Private members
      //

      private final Map<String, String> namespaces;

      private final int indent;

      //
      // Constructor
      //

      public NamespacesAndIndent(final Map<String, String> namespaces, final int indent)
      {
         this.namespaces = namespaces;
         this.indent = indent;
      }

      //
      // Public methods
      //

      public Map<String, String> getNamespaces()
      {
         return this.namespaces;
      }

      public int getIndent()
      {
         return this.indent;
      }
   }

}
