/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.FieldHolder;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.java.Parameter;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.facets.FacetNotFoundException;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.facets.WebResourceFacet;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.scaffold.AccessStrategy;
import org.jboss.forge.scaffold.ScaffoldProvider;
import org.jboss.forge.scaffold.TemplateStrategy;
import org.jboss.forge.scaffold.faces.metawidget.config.ForgeConfigReader;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.shell.ShellMessages;
import org.jboss.forge.shell.ShellPrintWriter;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.shell.util.Streams;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.EJBFacet;
import org.jboss.forge.spec.javaee.FacesAPIFacet;
import org.jboss.forge.spec.javaee.FacesFacet;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.ServletFacet;
import org.jboss.seam.render.TemplateCompiler;
import org.jboss.seam.render.spi.TemplateResolver;
import org.jboss.seam.render.template.CompiledTemplateResource;
import org.jboss.seam.render.template.resolver.ClassLoaderTemplateResolver;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.metawidget.statically.StaticMetawidget;
import org.metawidget.statically.StaticUtils.IndentedWriter;
import org.metawidget.statically.StaticWidget;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlOutcomeTargetLink;
import org.metawidget.statically.faces.component.html.widgetbuilder.ReadOnlyWidgetBuilder;
import org.metawidget.statically.faces.component.html.widgetbuilder.richfaces.RichFacesWidgetBuilder;
import org.metawidget.statically.html.widgetbuilder.HtmlTag;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.util.ArrayUtils;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.XmlUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilder;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilderConfig;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;
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
 * @author Richard Kennard
 */
@Alias("faces")
@Help("JavaServer Faces scaffolding")
@RequiresFacet({ WebResourceFacet.class,
         DependencyFacet.class,
         PersistenceFacet.class,
         EJBFacet.class,
         CDIFacet.class,
         FacesAPIFacet.class })
public class FacesScaffold extends BaseFacet implements ScaffoldProvider
{
   //
   // Private statics
   //

   private static final String XMLNS_PREFIX = "xmlns:";
   private static final String BACKING_BEAN_TEMPLATE = "scaffold/faces/BackingBean.jv";
   private static final String VIEW_UTILS_TEMPLATE = "scaffold/faces/ViewUtils.jv";
   private static final String TAGLIB_TEMPLATE = "scaffold/faces/forge.taglib.xml";
   private static final String VIEW_TEMPLATE = "scaffold/faces/view.xhtml";
   private static final String CREATE_TEMPLATE = "scaffold/faces/create.xhtml";
   private static final String SEARCH_TEMPLATE = "scaffold/faces/search.xhtml";
   private static final String NAVIGATION_TEMPLATE = "scaffold/faces/pageTemplate.xhtml";
   private static final String ERROR_TEMPLATE = "scaffold/faces/error.xhtml";
   private static final String INDEX_TEMPLATE = "scaffold/faces/index.xhtml";
   private static final String INDEX_WELCOME_TEMPLATE = "scaffold/faces/index.html";
   private final Dependency richfaces3UI = DependencyBuilder.create("org.richfaces.ui:richfaces-ui");
   private final Dependency richfaces3Impl = DependencyBuilder.create("org.richfaces.framework:richfaces-impl");
   private final Dependency richfaces4UI = DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui");
   private final Dependency richfaces4Impl = DependencyBuilder.create("org.richfaces.core:richfaces-core-impl");
   //
   // Protected members (nothing is private, to help subclassing)
   //
   protected CompiledTemplateResource backingBeanTemplate;
   protected int backingBeanTemplateQbeMetawidgetIndent;
   protected int backingBeanTemplateRmEntityMetawidgetIndent;
   protected CompiledTemplateResource viewUtilsTemplate;
   protected CompiledTemplateResource taglibTemplate;
   protected CompiledTemplateResource viewTemplate;
   protected Map<String, String> viewTemplateNamespaces;
   protected int viewTemplateEntityMetawidgetIndent;
   protected CompiledTemplateResource createTemplate;
   protected Map<String, String> createTemplateNamespaces;
   protected int createTemplateEntityMetawidgetIndent;
   protected CompiledTemplateResource searchTemplate;
   protected Map<String, String> searchTemplateNamespaces;
   protected int searchTemplateSearchMetawidgetIndent;
   protected int searchTemplateBeanMetawidgetIndent;
   protected CompiledTemplateResource navigationTemplate;
   protected int navigationTemplateIndent;
   protected CompiledTemplateResource errorTemplate;
   protected CompiledTemplateResource indexWelcomeTemplate;
   protected CompiledTemplateResource indexTemplate;
   protected TemplateResolver<ClassLoader> resolver;
   protected final ShellPrompt prompt;
   protected final TemplateCompiler compiler;
   protected final Event<InstallFacets> install;
   protected StaticHtmlMetawidget entityMetawidget;
   protected StaticHtmlMetawidget searchMetawidget;
   protected StaticHtmlMetawidget beanMetawidget;
   protected StaticJavaMetawidget qbeMetawidget;
   protected StaticJavaMetawidget rmEntityMetawidget;

   private Configuration config;
   private ShellPrintWriter writer;

   //
   // Constructor
   //

   @Inject
   public FacesScaffold(final Configuration config,
            final ShellPrompt prompt,
            final TemplateCompiler compiler,
            final Event<InstallFacets> install,
            final ShellPrintWriter writer)
   {
      this.config = config;
      this.prompt = prompt;
      this.compiler = compiler;
      this.install = install;
      this.writer = writer;

      this.resolver = new ClassLoaderTemplateResolver(FacesScaffold.class.getClassLoader());

      if (this.compiler != null)
      {
         this.compiler.getTemplateResolverFactory().addResolver(this.resolver);
      }
   }

   //
   // Public methods
   //
   @Override
   public List<Resource<?>> setup(String targetDir, final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> resources = generateIndex(targetDir, template, overwrite);
      setupWebXML();

      return resources;
   }

   /**
    * Overridden to setup the Metawidgets.
    * <p>
    * Metawidgets must be configured per project <em>and per Forge invocation</em>. It is not sufficient to simply
    * configure them in <code>setup</code> because the user may restart Forge and not run <code>scaffold setup</code> a
    * second time.
    */
   @Override
   public void setProject(Project project)
   {
      super.setProject(project);

      resetMetaWidgets();

   }

   private void resetMetaWidgets()
   {
      ForgeConfigReader configReader = new ForgeConfigReader(this.config, this.project);

      this.entityMetawidget = new StaticHtmlMetawidget();
      this.entityMetawidget.setConfigReader(configReader);
      this.entityMetawidget.setConfig("scaffold/faces/metawidget-entity.xml");

      this.searchMetawidget = new StaticHtmlMetawidget();
      this.searchMetawidget.setConfigReader(configReader);
      this.searchMetawidget.setConfig("scaffold/faces/metawidget-search.xml");

      this.beanMetawidget = new StaticHtmlMetawidget();
      this.beanMetawidget.setConfigReader(configReader);
      this.beanMetawidget.setConfig("scaffold/faces/metawidget-bean.xml");

      this.qbeMetawidget = new StaticJavaMetawidget();
      this.qbeMetawidget.setConfigReader(configReader);
      this.qbeMetawidget.setConfig("scaffold/faces/metawidget-qbe.xml");
      
      this.rmEntityMetawidget = new StaticJavaMetawidget();
      this.rmEntityMetawidget.setConfigReader(configReader);
      this.rmEntityMetawidget.setConfig("scaffold/faces/metawidget-remove-entity.xml");
   }

   @Override
   public List<Resource<?>> generateFromEntity(String targetDir, final Resource<?> template, final JavaClass entity,
            final boolean overwrite)
   {
      if(!isScaffoldable(entity))
      {
         ShellMessages.info(writer, "Skipping @Entity Java resource [" + entity.getQualifiedName() + "]");
         return Collections.emptyList();
      }
      
      resetMetaWidgets();

      // FORGE-460: setupRichFaces during generateFromEntity, not during setup, as generally 'richfaces setup' is called
      // *after* 'scaffold setup'
      setupRichFaces();

      // Track the list of resources generated

      List<Resource<?>> result = new ArrayList<Resource<?>>();
      try
      {
         JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
         WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

         loadTemplates();
         Map<Object, Object> context = CollectionUtils.newHashMap();
         context.put("entity", entity);
         String ccEntity = StringUtils.decapitalize(entity.getName());
         context.put("ccEntity", ccEntity);
         context.put("rmEntity", ccEntity + "ToDelete");
         setPrimaryKeyMetaData(context, entity);

         // Prepare qbeMetawidget
         this.qbeMetawidget.setPath(entity.getQualifiedName());
         StringWriter stringWriter = new StringWriter();
         this.qbeMetawidget.write(stringWriter, this.backingBeanTemplateQbeMetawidgetIndent);
         context.put("qbeMetawidget", stringWriter.toString().trim());
         
         // Prepare removeEntityMetawidget
         this.rmEntityMetawidget.setPath(entity.getQualifiedName());
         stringWriter = new StringWriter();
         this.rmEntityMetawidget.write(stringWriter, this.backingBeanTemplateRmEntityMetawidgetIndent);
         context.put("rmEntityMetawidget", stringWriter.toString().trim());
         
         // Prepare Java imports
         Set<String> qbeMetawidgetImports = this.qbeMetawidget.getImports();
         Set<String> rmEntityMetawidgetImports = this.rmEntityMetawidget.getImports();
         Set<String> metawidgetImports = CollectionUtils.newHashSet();
         metawidgetImports.addAll(qbeMetawidgetImports);
         metawidgetImports.addAll(rmEntityMetawidgetImports);
         metawidgetImports.remove(entity.getQualifiedName());
         context.put("metawidgetImports",
                  CollectionUtils.toString(metawidgetImports, ";\r\nimport ", true, false));

         // Create the Backing Bean for this entity
         JavaClass viewBean = JavaParser.parse(JavaClass.class, this.backingBeanTemplate.render(context));
         viewBean.setPackage(java.getBasePackage() + ".view");
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(viewBean), viewBean.toString(),
                  overwrite));

         // Set new context for view generation
         context = getTemplateContext(targetDir, template);
         String beanName = StringUtils.decapitalize(viewBean.getName());
         context.put("beanName", beanName);
         context.put("ccEntity", ccEntity);
         context.put("entityName", StringUtils.uncamelCase(entity.getName()));
         setPrimaryKeyMetaData(context, entity);

         // Prepare entityMetawidget
         this.entityMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + "." + ccEntity));
         this.entityMetawidget.setPath(entity.getQualifiedName());
         this.entityMetawidget.setReadOnly(false);
         this.entityMetawidget.setStyle(null);

         // Generate create
         writeEntityMetawidget(context, this.createTemplateEntityMetawidgetIndent, this.createTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource(targetDir + "/" + ccEntity + "/create.xhtml"),
                  this.createTemplate.render(context),
                  overwrite));

         // Generate view
         this.entityMetawidget.setReadOnly(true);
         writeEntityMetawidget(context, this.viewTemplateEntityMetawidgetIndent, this.viewTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource(targetDir + "/" + ccEntity + "/view.xhtml"),
                  this.viewTemplate.render(context), overwrite));

         // Generate search
         this.searchMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".example"));
         this.searchMetawidget.setPath(entity.getQualifiedName());
         this.beanMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".pageItems"));
         this.beanMetawidget.setPath(viewBean.getQualifiedName() + "/pageItems");
         writeSearchAndBeanMetawidget(context, this.searchTemplateSearchMetawidgetIndent,
                  this.searchTemplateBeanMetawidgetIndent, this.searchTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource(targetDir + "/" + ccEntity + "/search.xhtml"),
                  this.searchTemplate.render(context), overwrite));

         // Generate navigation
         result.add(generateNavigation(targetDir, overwrite));

         // Need ViewUtils and forge.taglib.xml for forgeview:asList
         JavaClass viewUtils = JavaParser.parse(JavaClass.class, this.viewUtilsTemplate.render(context));
         viewUtils.setPackage(viewBean.getPackage());
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(viewUtils), viewUtils.toString(),
                  true));

         context.put("viewPackage", viewBean.getPackage());
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource("WEB-INF/classes/META-INF/forge.taglib.xml"),
                  this.taglibTemplate.render(context), true));

         createInitializers(entity);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default scaffolding: " + e.getMessage(), e);
      }
      return result;
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

   @Override
   public boolean isInstalled()
   {
      return true;
   }

   @Override
   public List<Resource<?>> generateIndex(String targetDir, final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

      this.project.getFacet(ServletFacet.class).getConfig().welcomeFile("/index.html");
      loadTemplates();

      generateTemplates(targetDir, overwrite);
      HashMap<Object, Object> context = getTemplateContext(targetDir, template);

      // Basic pages

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource(targetDir + "/index.html"),
               this.indexWelcomeTemplate.render(context), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource(targetDir + "/index.xhtml"),
               this.indexTemplate.render(context), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("error.xhtml"),
               this.errorTemplate.render(context), overwrite));

      // Static resources

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/add.png"),
               getClass().getResourceAsStream("/scaffold/faces/add.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/bootstrap.css"),
               getClass().getResourceAsStream("/scaffold/faces/bootstrap.css"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/false.png"),
               getClass().getResourceAsStream("/scaffold/faces/false.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/favicon.ico"),
               getClass().getResourceAsStream("/scaffold/faces/favicon.ico"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-logo.png"),
               getClass().getResourceAsStream("/scaffold/faces/forge-logo.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-style.css"),
               getClass().getResourceAsStream("/scaffold/faces/forge-style.css"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/remove.png"),
               getClass().getResourceAsStream("/scaffold/faces/remove.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/search.png"),
               getClass().getResourceAsStream("/scaffold/faces/search.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/true.png"),
               getClass().getResourceAsStream("/scaffold/faces/true.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/jboss-community.png"),
               getClass().getResourceAsStream("/scaffold/faces/jboss-community.png"), overwrite));

      return result;
   }

   @Override
   public List<Resource<?>> getGeneratedResources(String targetDir)
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
   public List<Resource<?>> generateTemplates(String targetDir, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();

      try
      {
         WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource("/resources/scaffold/paginator.xhtml"),
                  getClass().getResourceAsStream("/scaffold/faces/paginator.xhtml"),
                  overwrite));

         result.add(generateNavigation(targetDir, overwrite));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default templates", e);
      }

      return result;
   }

   //
   // Protected methods (nothing is private, to help subclassing)
   //
   protected void loadTemplates()
   {
      if (this.backingBeanTemplate == null)
      {
         this.backingBeanTemplate = this.compiler.compile(BACKING_BEAN_TEMPLATE);
         String template = Streams.toString(this.backingBeanTemplate.getSourceTemplateResource().getInputStream());
         this.backingBeanTemplateQbeMetawidgetIndent = parseIndent(template, "@{qbeMetawidget}");
         this.backingBeanTemplateRmEntityMetawidgetIndent = parseIndent(template, "@{rmEntityMetawidget}");
      }
      if (this.viewUtilsTemplate == null)
      {
         this.viewUtilsTemplate = this.compiler.compile(VIEW_UTILS_TEMPLATE);
      }
      if (this.taglibTemplate == null)
      {
         this.taglibTemplate = this.compiler.compile(TAGLIB_TEMPLATE);
      }
      if (this.viewTemplate == null)
      {
         this.viewTemplate = this.compiler.compile(VIEW_TEMPLATE);
         String template = Streams.toString(this.viewTemplate.getSourceTemplateResource().getInputStream());
         this.viewTemplateNamespaces = parseNamespaces(template);
         this.viewTemplateEntityMetawidgetIndent = parseIndent(template, "@{metawidget}");
      }
      if (this.createTemplate == null)
      {
         this.createTemplate = this.compiler.compile(CREATE_TEMPLATE);
         String template = Streams.toString(this.createTemplate.getSourceTemplateResource().getInputStream());
         this.createTemplateNamespaces = parseNamespaces(template);
         this.createTemplateEntityMetawidgetIndent = parseIndent(template, "@{metawidget}");
      }
      if (this.searchTemplate == null)
      {
         this.searchTemplate = this.compiler.compile(SEARCH_TEMPLATE);
         String template = Streams.toString(this.searchTemplate.getSourceTemplateResource().getInputStream());
         this.searchTemplateNamespaces = parseNamespaces(template);
         this.searchTemplateSearchMetawidgetIndent = parseIndent(template, "@{searchMetawidget}");
         this.searchTemplateBeanMetawidgetIndent = parseIndent(template, "@{beanMetawidget}");
      }
      if (this.navigationTemplate == null)
      {
         this.navigationTemplate = this.compiler.compile(NAVIGATION_TEMPLATE);
         String template = Streams.toString(this.navigationTemplate.getSourceTemplateResource().getInputStream());
         this.navigationTemplateIndent = parseIndent(template, "@{navigation}");
      }
      if (this.errorTemplate == null)
      {
         this.errorTemplate = this.compiler.compile(ERROR_TEMPLATE);
      }
      if (this.indexTemplate == null)
      {
         this.indexTemplate = this.compiler.compile(INDEX_TEMPLATE);
      }
      if (this.indexWelcomeTemplate == null)
      {
         this.indexWelcomeTemplate = this.compiler.compile(INDEX_WELCOME_TEMPLATE);
      }
   }

   protected void setupRichFaces()
   {
      if ((this.project.getFacet(DependencyFacet.class).hasEffectiveDependency(this.richfaces3UI)
               && this.project.getFacet(DependencyFacet.class).hasEffectiveDependency(this.richfaces3Impl))
               || (this.project.getFacet(DependencyFacet.class).hasEffectiveDependency(this.richfaces4UI)
               && this.project.getFacet(DependencyFacet.class).hasEffectiveDependency(this.richfaces4Impl)))
      {
         this.entityMetawidget
                  .setWidgetBuilder(insertRichFacesWidgetBuilder((CompositeWidgetBuilder<StaticWidget, StaticMetawidget>) this.entityMetawidget
                           .getWidgetBuilder()));

         this.searchMetawidget
                  .setWidgetBuilder(insertRichFacesWidgetBuilder((CompositeWidgetBuilder<StaticWidget, StaticMetawidget>) this.searchMetawidget
                           .getWidgetBuilder()));

         this.beanMetawidget
                  .setWidgetBuilder(insertRichFacesWidgetBuilder((CompositeWidgetBuilder<StaticWidget, StaticMetawidget>) this.beanMetawidget
                           .getWidgetBuilder()));
      }
   }

   /**
    * Locates a <code>ReadOnlyWidgetBuilder</code> in the list of WidgetBuilders, and inserts a
    * <code>RichFacesWidgetBuilder</code> after it (unless there's a <code>RichFacesWidgetBuilder</code> in there
    * already).
    */
   protected <W extends StaticWidget, M extends W> CompositeWidgetBuilder<W, M> insertRichFacesWidgetBuilder(
            final CompositeWidgetBuilder<W, M> compositeWidgetBuilder)
   {
      // Get the current WidgetBuilders...

      WidgetBuilder<W, M>[] existingWidgetBuilders = compositeWidgetBuilder.getWidgetBuilders();

      // ...find the ReadOnlyWidgetBuilder (if any)...

      int addAt = 0;

      for (int loop = 0; loop < existingWidgetBuilders.length; loop++)
      {
         // ...(abort if there's already a RichFacesWidgetBuilder)...

         // Use an Object loop variable here to avoid a nasty Java/Generics compiler bug
         Object widgetBuilder = existingWidgetBuilders[loop];
         if (widgetBuilder instanceof RichFacesWidgetBuilder)
         {
            return compositeWidgetBuilder;
         }

         if (widgetBuilder instanceof ReadOnlyWidgetBuilder)
         {
            addAt = loop + 1;
         }
      }

      // ...and insert our RichFacesWidgetBuilder just after it

      @SuppressWarnings("unchecked")
      WidgetBuilder<W, M>[] newWidgetBuilders = (WidgetBuilder<W, M>[]) ArrayUtils.addAt(existingWidgetBuilders, addAt,
               new RichFacesWidgetBuilder());

      return new CompositeWidgetBuilder<W, M>(
               new CompositeWidgetBuilderConfig<W, M>().setWidgetBuilders(newWidgetBuilders));
   }

   protected void createInitializers(final JavaClass entity) throws FacetNotFoundException, FileNotFoundException
   {
      boolean dirtyBit = false;
      for (Field<JavaClass> field : entity.getFields())
      {
         if (field.hasAnnotation(OneToOne.class))
         {
            Annotation<JavaClass> oneToOne = field.getAnnotation(OneToOne.class);
            if (oneToOne.getStringValue("mappedBy") == null && oneToOne.getStringValue("cascade") == null)
            {
               oneToOne.setEnumValue("cascade", CascadeType.ALL);
               dirtyBit = true;
            }
            String methodName = "new" + StringUtils.capitalize(field.getName());
            if (!entity.hasMethodSignature(methodName))
            {
               entity.addMethod().setName(methodName).setReturnTypeVoid().setPublic()
                        .setBody("this." + field.getName() + " = new " + field.getType() + "();");
               dirtyBit = true;
            }
         }
      }
      for (Method<JavaClass> method : entity.getMethods())
      {
         if (method.hasAnnotation(OneToOne.class))
         {
            Annotation<JavaClass> oneToOne = method.getAnnotation(OneToOne.class);
            if (oneToOne.getStringValue("mappedBy") == null && oneToOne.getStringValue("cascade") == null)
            {
               oneToOne.setEnumValue("cascade", CascadeType.ALL);
               dirtyBit = true;
            }
            String fieldName = StringUtils.camelCase(method.getName().substring(3));
            String methodName = "new" + StringUtils.capitalize(fieldName);
            if (!entity.hasMethodSignature(methodName))
            {
               entity.addMethod().setName(methodName).setReturnTypeVoid().setPublic()
                        .setBody("this." + fieldName + " = new " + method.getReturnType() + "();");
               dirtyBit = true;
            }
         }
      }
      if(dirtyBit)
      {
         this.project.getFacet(JavaSourceFacet.class).saveJavaSource(entity);
      }
   }

   protected HashMap<Object, Object> getTemplateContext(String targetDir, final Resource<?> template)
   {
      HashMap<Object, Object> context;
      context = new HashMap<Object, Object>();
      context.put("template", template);
      context.put("templateStrategy", getTemplateStrategy());
      context.put("targetDir", targetDir);
      return context;
   }

   protected void setupWebXML()
   {
      ServletFacet servlet = this.project.getFacet(ServletFacet.class);

      Node webXML = removeConflictingErrorPages(servlet);
      servlet.getConfigFile().setContents(XMLParser.toXMLInputStream(webXML));

      WebAppDescriptor servletConfig = servlet.getConfig();
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

      // (prefer /faces/error.xhtml)

      String errorLocation = getAccessStrategy().getWebPaths(web.getWebResource("error.xhtml")).get(1);
      servletConfig.errorPage(404, errorLocation);
      servletConfig.errorPage(500, errorLocation);
      
      // Use the server timezone since we accept dates in that timezone, and it makes sense to display them in the same
      if (servletConfig.getContextParam("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE") == null)
      {
         servletConfig.contextParam("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE", "true");
      }

      servlet.saveConfig(servletConfig);
   }

   protected Node removeConflictingErrorPages(final ServletFacet servlet)
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

   /**
    * Generates the navigation menu based on scaffolded entities.
    */

   protected Resource<?> generateNavigation(final String targetDir, final boolean overwrite)
            throws IOException
   {
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
      HtmlTag unorderedList = new HtmlTag("ul");

      ResourceFilter filter = new ResourceFilter()
      {
         @Override
         public boolean accept(Resource<?> resource)
         {
            FileResource<?> file = (FileResource<?>) resource;

            if (!file.isDirectory()
                     || file.getName().equals("resources")
                     || file.getName().equals("WEB-INF")
                     || file.getName().equals("META-INF"))
            {
               return false;
            }

            return true;
         }
      };

      for (Resource<?> resource : web.getWebResource(targetDir + "/").listResources(filter))
      {
         HtmlOutcomeTargetLink outcomeTargetLink = new HtmlOutcomeTargetLink();
         outcomeTargetLink.putAttribute("outcome", targetDir + "/" + resource.getName() + "/search");
         outcomeTargetLink.setValue(StringUtils.uncamelCase(resource.getName()));

         HtmlTag listItem = new HtmlTag("li");
         listItem.getChildren().add(outcomeTargetLink);
         unorderedList.getChildren().add(listItem);
      }

      Writer writer = new IndentedWriter(new StringWriter(), this.navigationTemplateIndent);
      unorderedList.write(writer);

      Map<Object, Object> context = CollectionUtils.newHashMap();
      context.put("appName", StringUtils.uncamelCase(this.project.getProjectRoot().getName()));
      context.put("navigation", writer.toString().trim());
      context.put("targetDir", targetDir);

      if (this.navigationTemplate == null)
      {
         loadTemplates();
      }

      try
      {
         return ScaffoldUtil.createOrOverwrite(this.prompt, (FileResource<?>) getTemplateStrategy()
                  .getDefaultTemplate(),
                  this.navigationTemplate.render(context),
                  overwrite);
      }
      finally
      {
         writer.close();
      }
   }

   /**
    * Parses the given XML and determines what namespaces it already declares. These are later removed from the list of
    * namespaces that Metawidget introduces.
    */
   protected Map<String, String> parseNamespaces(final String template)
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
   protected int parseIndent(final String template, final String indentOf)
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
    * Writes the entity Metawidget and its namespaces into the given context.
    */
   protected void writeEntityMetawidget(final Map<Object, Object> context, final int entityMetawidgetIndent,
            final Map<String, String> existingNamespaces)
   {
      StringWriter stringWriter = new StringWriter();
      this.entityMetawidget.write(stringWriter, entityMetawidgetIndent);
      context.put("metawidget", stringWriter.toString().trim());

      Map<String, String> namespaces = this.entityMetawidget.getNamespaces();
      namespaces.keySet().removeAll(existingNamespaces.keySet());
      context.put("metawidgetNamespaces", namespacesToString(namespaces));
   }

   /**
    * Writes the search Metawidget, the bean Metawidget and their namespaces into the given context.
    */
   protected void writeSearchAndBeanMetawidget(final Map<Object, Object> context, final int searchMetawidgetIndent,
            final int beanMetawidgetIndent,
            final Map<String, String> existingNamespaces)
   {
      StringWriter stringWriter = new StringWriter();
      this.searchMetawidget.write(stringWriter, searchMetawidgetIndent);
      context.put("searchMetawidget", stringWriter.toString().trim());

      stringWriter = new StringWriter();
      this.beanMetawidget.write(stringWriter, beanMetawidgetIndent);
      context.put("beanMetawidget", stringWriter.toString().trim());

      Map<String, String> namespaces = this.searchMetawidget.getNamespaces();
      namespaces.putAll(this.beanMetawidget.getNamespaces());
      namespaces.keySet().removeAll(existingNamespaces.keySet());
      context.put("metawidgetNamespaces", namespacesToString(namespaces));
   }

   protected String namespacesToString(final Map<String, String> namespaces)
   {
      StringBuilder builder = new StringBuilder();

      for (Map.Entry<String, String> entry : namespaces.entrySet())
      {
         // At the start, break out of the current quote. Field must be in quotes so that we're valid XML

         builder.append("\"\r\n\txmlns:");
         builder.append(entry.getKey());
         builder.append("=\"");
         builder.append(entry.getValue());
      }

      return builder.toString();
   }

   private void setPrimaryKeyMetaData(Map<Object, Object> context, final JavaClass entity)
   {
      String pkName = "id";
      String pkType = "Long";
      String nullablePkType = "Long";
      for (Member<JavaClass, ?> m : entity.getMembers())
      {
         if (m.hasAnnotation(Id.class))
         {
            if (m instanceof Field)
            {
               Field<?> field = (Field<?>) m;
               pkName = field.getName();
               pkType = field.getType();
               nullablePkType = pkType;
               break;
            }

            Method<?> method = (Method<?>) m;
            pkName = method.getName().substring(3);
            if (method.getName().startsWith("get"))
            {
               pkType = method.getReturnType();
            }
            else
            {
               pkType = method.getParameters().get(0).getType();
            }
            nullablePkType = pkType;
            break;
         }
      }

      if ("int".equals(pkType))
      {
         nullablePkType = Integer.class.getSimpleName();
      }
      else if ("short".equals(pkType))
      {
         nullablePkType = Short.class.getSimpleName();
      }
      else if ("byte".equals(pkType))
      {
         nullablePkType = Byte.class.getSimpleName();
      }
      else if ("long".equals(pkType))
      {
         nullablePkType = Long.class.getSimpleName();
      }

      context.put("primaryKey", pkName);
      context.put("primaryKeyCC", StringUtils.capitalize(pkName));
      context.put("primaryKeyType", pkType);
      context.put("nullablePrimaryKeyType", nullablePkType);
   }
   
   private boolean isScaffoldable(JavaClass entity)
   {
      int setterCount = 0;
      for (Method<JavaClass> method : entity.getMethods())
      {
         // Exclude static methods

         if (method.isStatic())
         {
            continue;
         }

         // Get type

         List<Parameter<JavaClass>> parameters = method.getParameters();

         if (parameters.size() != 1)
         {
            continue;
         }

         // Get name

         String setterPropertyName = getSetterProperty(method);

         if (setterPropertyName == null)
         {
            continue;
         }

         Field<?> privateField = getPrivateField((FieldHolder<?>) entity, setterPropertyName);

         if (privateField != null)
         {
            // TODO Verify other aspects like whether the property is transient or has a generated value etc.
            setterCount++;
         }
      }
      if (setterCount > 0)
      {
         return true;
      }
      else
      {
         return false;
      }
   }
   
   /**
    * Returns the property name corresponding to the given 'setter' method.
    * Returns null if the supplied method is not a setter. 
    *
    * @param method a single-parametered method. May return non-void (ie. for Fluent interfaces)
    * @return the property name
    */
   private String getSetterProperty(final Method<?> method)
   {
      String methodName = method.getName();

      if (!methodName.startsWith(ClassUtils.JAVABEAN_SET_PREFIX))
      {
         return null;
      }

      String propertyName = methodName.substring(ClassUtils.JAVABEAN_SET_PREFIX.length());

      return StringUtils.decapitalize(propertyName);
   }
   
   /**
    * Gets the private field representing the given <code>propertyName</code> within the given class.
    *
    * @return the private Field for this propertyName, or null if no such field (should not throw NoSuchFieldException)
    */
   private Field<?> getPrivateField(final FieldHolder<?> fieldHolder, final String propertyName)
   {
      Field<?> field = fieldHolder.getField(propertyName);

      // FORGE-402: support fields starting with capital letter

      if (field == null && !Character.isUpperCase(propertyName.charAt( 0 )))
      {
         field = fieldHolder.getField(StringUtils.capitalize(propertyName));
      }

      return field;
   }
}
