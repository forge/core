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

import java.io.StringWriter;
import java.io.Writer;
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
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.events.AddedDependencies;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspector;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyle;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyleConfig;
import org.jboss.forge.scaffold.faces.metawidget.widgetbuilder.Anchor;
import org.jboss.forge.scaffold.faces.metawidget.widgetbuilder.ForgeWidgetBuilder;
import org.jboss.forge.scaffold.faces.metawidget.widgetbuilder.ListItem;
import org.jboss.forge.scaffold.faces.metawidget.widgetbuilder.UnorderedList;
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
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.iface.Inspector;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.java5.Java5Inspector;
import org.metawidget.inspector.jpa.JpaInspector;
import org.metawidget.inspector.jpa.JpaInspectorConfig;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.statically.StaticUtils.IndentedWriter;
import org.metawidget.statically.StaticXmlMetawidget;
import org.metawidget.statically.StaticXmlWidget;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.statically.faces.component.html.layout.HtmlTableLayout;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.ReadOnlyWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.richfaces.RichFacesWidgetBuilder;
import org.metawidget.statically.layout.SimpleLayout;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.XmlUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilder;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilderConfig;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * Facet to generate a Java Server Faces UI.
 * <p>
 * This facet utilizes <a href="http://metawidget.org">Metawidget</a> internally. This enables the use of the Metawidget
 * SPI (pluggable WidgetBuilders, Layouts etc) for customizing the generated User Interface. For more information on
 * writing Metawidget plugins, see <a href="http://metawidget.org/documentation.php">the Metawidget documentation</a>.
 * <p>
 * This Facet does <em>not</em> require Metawidget to be in the final project.
 *
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

   private static final String REWRITE_CONFIG_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/URLRewriteConfiguration.jv";
   private static final String BACKING_BEAN_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/BackingBean.jv";
   private static final String VIEW_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/view.xhtml";
   private static final String CREATE_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/create.xhtml";
   private static final String LIST_TEMPLATE = "org/jboss/forge/scaffold/faces/scaffold/list.xhtml";
   private static final String NAVIGATION_TEMPLATE = "org/jboss/forge/scaffold/faces/templates/page.xhtml";

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

   private CompiledTemplateResource navigationTemplate;
   private int navigationTemplateIndent;

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

      this.metawidget = new StaticHtmlMetawidget();

      @SuppressWarnings("unchecked")
      CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget> compositeWidgetBuider = new CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget>(
               new CompositeWidgetBuilderConfig<StaticXmlWidget, StaticXmlMetawidget>().setWidgetBuilders(
                        new ForgeWidgetBuilder(), new ReadOnlyWidgetBuilder(), new HtmlWidgetBuilder()));

      this.metawidget.setWidgetBuilder(compositeWidgetBuider);
   }

   /**
    * Overridden to configure the Metawidget to inspect classes from the given Project.
    */

   @Override
   public void setProject(Project project)
   {
      super.setProject(project);

      ForgePropertyStyle forgePropertyStyle = new ForgePropertyStyle(
               new ForgePropertyStyleConfig().setProject(this.project));

      Inspector inspector = new CompositeInspector(new CompositeInspectorConfig()
               .setInspectors(
                        new PropertyTypeInspector(new BaseObjectInspectorConfig()
                                 .setPropertyStyle(forgePropertyStyle)),
                        new Java5Inspector(new BaseObjectInspectorConfig()
                                 .setPropertyStyle(forgePropertyStyle)),
                        new ForgeInspector(new BaseObjectInspectorConfig()
                                 .setPropertyStyle(forgePropertyStyle)),
                        new JpaInspector(new JpaInspectorConfig()
                                 .setPropertyStyle(forgePropertyStyle))));

      this.metawidget.setInspector(inspector);
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
         String template = String.valueOf(this.viewTemplate
                  .getCompiledTemplate().getTemplate());
         this.viewTemplateNamespaces = parseNamespaces(template);
         this.viewTemplateMetawidgetIndent = parseIndent(template, "@{metawidget}");
      }
      if (this.createTemplate == null)
      {
         this.createTemplate = this.compiler.compile(CREATE_TEMPLATE);
         String template = String.valueOf(this.createTemplate
                  .getCompiledTemplate().getTemplate());
         this.createTemplateNamespaces = parseNamespaces(template);
         this.createTemplateMetawidgetIndent = parseIndent(template, "@{metawidget}");
      }
      if (this.listTemplate == null)
      {
         this.listTemplate = this.compiler.compile(LIST_TEMPLATE);
         String template = String.valueOf(this.listTemplate
                  .getCompiledTemplate().getTemplate());
         this.listTemplateNamespaces = parseNamespaces(template);
         this.listTemplateMetawidgetIndent = parseIndent(template, "@{metawidget}");
      }
      if (this.navigationTemplate == null)
      {
         this.navigationTemplate = this.compiler.compile(NAVIGATION_TEMPLATE);
         String template = String.valueOf(this.navigationTemplate
                  .getCompiledTemplate().getTemplate());
         this.navigationTemplateIndent = parseIndent(template, "@{navigation}");
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

   private void setupRichFaces()
   {
      if ((this.project.getFacet(DependencyFacet.class).hasDependency(this.richfaces3UI)
               && this.project.getFacet(DependencyFacet.class).hasDependency(this.richfaces3Impl))
               || (this.project.getFacet(DependencyFacet.class).hasDependency(this.richfaces4UI)
               && this.project.getFacet(DependencyFacet.class).hasDependency(this.richfaces4Impl)))
      {
         @SuppressWarnings("unchecked")
         CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget> compositeWidgetBuider = new CompositeWidgetBuilder<StaticXmlWidget, StaticXmlMetawidget>(
                  new CompositeWidgetBuilderConfig<StaticXmlWidget, StaticXmlMetawidget>().setWidgetBuilders(
                           new ReadOnlyWidgetBuilder(),
                           new RichFacesWidgetBuilder(), new ForgeWidgetBuilder()));
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
         this.metawidget.setValue(StaticFacesUtils.wrapExpression(beanName + "." + ccEntity));
         this.metawidget.setPath(entity.getQualifiedName());
         this.metawidget.setReadOnly(false);
         this.metawidget.setLayout(new HtmlTableLayout());
         this.metawidget.setStyle(null);

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
         this.metawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".pageItems"));
         this.metawidget.setPath(viewBean.getQualifiedName() + "/pageItems");
         this.metawidget.setLayout(new SimpleLayout());
         writeMetawidget(context, this.listTemplateMetawidgetIndent, this.listTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("scaffold/" + type + "/list.xhtml"),
                  this.listTemplate.render(context), overwrite));

         // Generate navigation
         UnorderedList unorderedList = new UnorderedList();

         for (Resource<?> resource : web.getWebResource("scaffold").listResources())
         {
            Anchor anchor = new Anchor();
            anchor.putAttribute("href", "/faces/scaffold/" + resource.getName() + "/list.xhtml");
            anchor.setTextContent(StringUtils.uncamelCase(resource.getName()));

            ListItem listItem = new ListItem();
            listItem.getChildren().add(anchor);
            unorderedList.getChildren().add(listItem);
         }

         Writer writer = new IndentedWriter(new StringWriter(), this.navigationTemplateIndent);
         unorderedList.write(writer);
         context.put("navigation", writer.toString().trim());
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, (FileResource<?>) getTemplateStrategy()
                  .getDefaultTemplate(),
                  this.navigationTemplate.render(context),
                  overwrite));
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

   /*
    * LINCOLN TO RE-ENABLE?
    *
    * private void setupRewrite() { JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class); FacesFacet
    * faces = this.project.getFacet(FacesFacet.class);
    *
    * loadTemplates();
    *
    * Map<Object, Object> context = new HashMap<Object, Object>(); context.put("indexPage",
    * faces.getWebPaths("/index.xhtml").get(0)); context.put("notFoundPage", faces.getWebPaths("/404.xhtml").get(0));
    * context.put("errorPage", faces.getWebPaths("/500.xhtml").get(0)); context.put("listPage",
    * faces.getWebPaths("/scaffold/{domain}/list.xhtml").get(0)); context.put("createPage",
    * faces.getWebPaths("/scaffold/{domain}/create.xhtml").get(0)); context.put("viewPage",
    * faces.getWebPaths("/scaffold/{domain}/view.xhtml").get(0));
    *
    * JavaSource<?> rewriteConfig = JavaParser.parse(this.rewriteConfigTemplate.render(context));
    * rewriteConfig.setPackage(java.getBasePackage() + ".rewrite");
    *
    * try { ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(rewriteConfig), rewriteConfig.toString(),
    * false); } catch (FileNotFoundException e) { throw new
    * RuntimeException("Could not save Rewrite Configuration source file", e); }
    *
    * ResourceFacet resources = this.project.getFacet(ResourceFacet.class); DirectoryResource services =
    * resources.getResourceFolder().getOrCreateChildDirectory("META-INF") .getOrCreateChildDirectory("services");
    *
    * // Register the configuration provider ScaffoldUtil.createOrOverwrite(this.prompt, (FileResource<?>)
    * services.getChild("com.ocpsoft.rewrite.config.ConfigurationProvider"), rewriteConfig.getQualifiedName(), false); }
    */

   @Override
   public boolean isInstalled()
   {
      return true;
   }

   @Override
   public List<Resource<?>> generateIndex(final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

      this.project.getFacet(ServletFacet.class).getConfig().welcomeFile("index.html");
      loadTemplates();

      generateTemplates(overwrite);
      HashMap<Object, Object> context = getTemplateContext(template);

      // Basic pages

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("index.html"), getClass()
               .getResourceAsStream("/org/jboss/forge/scaffold/faces/templates/index.html"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("index.xhtml"),
               this.indexTemplate.render(context), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("404.xhtml"),
               this.e404Template.render(context), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("500.xhtml"),
               this.e500Template.render(context), overwrite));

      // Static resources

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/background.gif"),
               getClass().getResourceAsStream("/org/jboss/forge/scaffold/faces/static/background.gif"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/favicon.ico"),
               getClass().getResourceAsStream("/org/jboss/forge/scaffold/faces/static/favicon.ico"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-logo.png"),
               getClass().getResourceAsStream("/org/jboss/forge/scaffold/faces/static/forge-logo.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-style.css"),
               getClass().getResourceAsStream("/org/jboss/forge/scaffold/faces/static/forge-style.css"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/jboss-community.png"),
               getClass().getResourceAsStream("/org/jboss/forge/scaffold/faces/static/jboss-community.png"), overwrite));

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

      try
      {
         WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource("/resources/scaffold/paginator.xhtml"),
                  getClass().getResourceAsStream("/org/jboss/forge/scaffold/faces/templates/paginator.xhtml"),
                  overwrite));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default templates", e);
      }

      return result;
   }

   /**
    * Parses the given XML and determines what namespaces it already declares. These are later removed from the list of
    * namespaces that Metawidget introduces.
    */

   private Map<String, String> parseNamespaces(String template)
   {
      Map<String, String> namespaces = CollectionUtils.newHashMap();
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

      return namespaces;
   }

   /**
    * Parses the given XML and determines the indent of the given String namespaces that Metawidget introduces.
    */

   private int parseIndent(String template, String indentOf)
   {
      int indent = 0;
      int indexOf = template.indexOf(indentOf);

      while ((indexOf >= 0) && (template.charAt(indexOf) != '\n'))
      {
         if (template.charAt(indexOf) == '\t')
         {
            indent++;
         }

         indexOf--;
      }

      return indent;
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
}
