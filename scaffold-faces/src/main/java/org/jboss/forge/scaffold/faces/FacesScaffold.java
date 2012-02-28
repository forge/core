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

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.OneToOne;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.jboss.forge.project.dependencies.Dependency;
import org.jboss.forge.project.dependencies.DependencyBuilder;
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
import org.jboss.forge.scaffold.faces.metawidget.config.ForgeConfigReader;
import org.jboss.forge.scaffold.util.ScaffoldUtil;
import org.jboss.forge.shell.ShellPrompt;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.RequiresFacet;
import org.jboss.forge.spec.javaee.CDIFacet;
import org.jboss.forge.spec.javaee.EJBFacet;
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
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.statically.jsp.html.widgetbuilder.HtmlTag;
import org.metawidget.util.ArrayUtils;
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
@RequiresFacet({ WebResourceFacet.class,
         DependencyFacet.class,
         PersistenceFacet.class,
         EJBFacet.class,
         CDIFacet.class,
         FacesFacet.class })
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
   private static final String NAVIGATION_TEMPLATE = "scaffold/faces/page.xhtml";

   private static final String ERROR_TEMPLATE = "scaffold/faces/error.xhtml";
   private static final String INDEX_TEMPLATE = "scaffold/faces/index.xhtml";

   private final Dependency richfaces3UI = DependencyBuilder.create("org.richfaces.ui:richfaces-ui");
   private final Dependency richfaces3Impl = DependencyBuilder.create("org.richfaces.framework:richfaces-impl");
   private final Dependency richfaces4UI = DependencyBuilder.create("org.richfaces.ui:richfaces-components-ui");
   private final Dependency richfaces4Impl = DependencyBuilder.create("org.richfaces.core:richfaces-core-impl");

   //
   // Protected members (nothing is private, to help subclassing)
   //

   protected CompiledTemplateResource backingBeanTemplate;
   protected int backingBeanTemplateQbeMetawidgetIndent;

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
   protected CompiledTemplateResource indexTemplate;
   protected TemplateResolver<ClassLoader> resolver;

   protected final ShellPrompt prompt;
   protected final TemplateCompiler compiler;
   protected final Event<InstallFacets> install;
   protected StaticHtmlMetawidget entityMetawidget;
   protected StaticHtmlMetawidget searchMetawidget;
   protected StaticHtmlMetawidget beanMetawidget;
   protected StaticJavaMetawidget qbeMetawidget;

   //
   // Constructor
   //

   @Inject
   public FacesScaffold(final ShellPrompt prompt,
            final TemplateCompiler compiler,
            final Event<InstallFacets> install)
   {
      this.prompt = prompt;
      this.compiler = compiler;
      this.install = install;

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
   public List<Resource<?>> setup(final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> resources = generateIndex(template, overwrite);
      setupMetawidget();
      setupRichFaces();
      setupWebXML();

      return resources;
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
         String ccEntity = StringUtils.decapitalize(entity.getName());
         context.put("ccEntity", ccEntity);

         // Prepare qbeMetawidget
         this.qbeMetawidget.setPath(entity.getQualifiedName());
         StringWriter stringWriter = new StringWriter();
         this.qbeMetawidget.write(stringWriter, this.backingBeanTemplateQbeMetawidgetIndent);
         context.put("qbeMetawidget", stringWriter.toString().trim());
         context.put("qbeMetawidgetImports",
                  CollectionUtils.toString(this.qbeMetawidget.getImports(), ";\r\nimport ", true, false));

         // Create the Backing Bean for this entity
         JavaClass viewBean = JavaParser.parse(JavaClass.class, this.backingBeanTemplate.render(context));
         viewBean.setPackage(java.getBasePackage() + ".view");
         result.add(ScaffoldUtil.createOrOverwrite(this.prompt, java.getJavaResource(viewBean), viewBean.toString(),
                  overwrite));

         // Set new context for view generation
         context = getTemplateContext(template);
         String beanName = StringUtils.decapitalize(viewBean.getName());
         context.put("beanName", beanName);
         context.put("ccEntity", ccEntity);
         context.put("entityName", StringUtils.uncamelCase(entity.getName()));

         // Prepare entityMetawidget
         this.entityMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + "." + ccEntity));
         this.entityMetawidget.setPath(entity.getQualifiedName());
         this.entityMetawidget.setReadOnly(false);
         this.entityMetawidget.setStyle(null);

         // Generate create
         writeEntityMetawidget(context, this.createTemplateEntityMetawidgetIndent, this.createTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource("scaffold/" + ccEntity + "/create.xhtml"),
                  this.createTemplate.render(context),
                  overwrite));

         // Generate view
         this.entityMetawidget.setReadOnly(true);
         writeEntityMetawidget(context, this.viewTemplateEntityMetawidgetIndent, this.viewTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource("scaffold/" + ccEntity + "/view.xhtml"),
                  this.viewTemplate.render(context), overwrite));

         // Generate search
         this.searchMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".search"));
         this.searchMetawidget.setPath(entity.getQualifiedName());
         this.beanMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".pageItems"));
         this.beanMetawidget.setPath(viewBean.getQualifiedName() + "/pageItems");
         writeSearchAndBeanMetawidget(context, this.searchTemplateSearchMetawidgetIndent,
                  this.searchTemplateBeanMetawidgetIndent, this.searchTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(this.prompt,
                  web.getWebResource("scaffold/" + ccEntity + "/search.xhtml"),
                  this.searchTemplate.render(context), overwrite));

         // Generate navigation
         result.add(generateNavigation(overwrite));

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
         this.project.getFacet(JavaSourceFacet.class).saveJavaSource(entity);

      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default scaffolding.", e);
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
               .getResourceAsStream("/scaffold/faces/index.html"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("index.xhtml"),
               this.indexTemplate.render(context), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("error.xhtml"),
               this.errorTemplate.render(context), overwrite));

      // Static resources

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/add.png"),
               getClass().getResourceAsStream("/scaffold/faces/add.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/background.gif"),
               getClass().getResourceAsStream("/scaffold/faces/background.gif"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/false.png"),
               getClass().getResourceAsStream("/scaffold/faces/false.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/favicon.ico"),
               getClass().getResourceAsStream("/scaffold/faces/favicon.ico"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-logo.png"),
               getClass().getResourceAsStream("/scaffold/faces/forge-logo.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/forge-style.css"),
               getClass().getResourceAsStream("/scaffold/faces/forge-style.css"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/jboss-community.png"),
               getClass().getResourceAsStream("/scaffold/faces/jboss-community.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/remove.png"),
               getClass().getResourceAsStream("/scaffold/faces/remove.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/search.png"),
               getClass().getResourceAsStream("/scaffold/faces/search.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(this.prompt, web.getWebResource("/resources/true.png"),
               getClass().getResourceAsStream("/scaffold/faces/true.png"), overwrite));

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
                  getClass().getResourceAsStream("/scaffold/faces/paginator.xhtml"),
                  overwrite));

         result.add(generateNavigation(overwrite));
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
         String template = String.valueOf(this.backingBeanTemplate
                  .getCompiledTemplate().getTemplate());
         this.backingBeanTemplateQbeMetawidgetIndent = parseIndent(template, "@{qbeMetawidget}");
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
         String template = String.valueOf(this.viewTemplate
                  .getCompiledTemplate().getTemplate());
         this.viewTemplateNamespaces = parseNamespaces(template);
         this.viewTemplateEntityMetawidgetIndent = parseIndent(template, "@{metawidget}");
      }
      if (this.createTemplate == null)
      {
         this.createTemplate = this.compiler.compile(CREATE_TEMPLATE);
         String template = String.valueOf(this.createTemplate
                  .getCompiledTemplate().getTemplate());
         this.createTemplateNamespaces = parseNamespaces(template);
         this.createTemplateEntityMetawidgetIndent = parseIndent(template, "@{metawidget}");
      }
      if (this.searchTemplate == null)
      {
         this.searchTemplate = this.compiler.compile(SEARCH_TEMPLATE);
         String template = String.valueOf(this.searchTemplate
                  .getCompiledTemplate().getTemplate());
         this.searchTemplateNamespaces = parseNamespaces(template);
         this.searchTemplateSearchMetawidgetIndent = parseIndent(template, "@{searchMetawidget}");
         this.searchTemplateBeanMetawidgetIndent = parseIndent(template, "@{beanMetawidget}");
      }
      if (this.navigationTemplate == null)
      {
         this.navigationTemplate = this.compiler.compile(NAVIGATION_TEMPLATE);
         String template = String.valueOf(this.navigationTemplate
                  .getCompiledTemplate().getTemplate());
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
   }

   protected void setupMetawidget()
   {
      ForgeConfigReader configReader = new ForgeConfigReader(this.project);

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
      WidgetBuilder<W, M>[] newWidgetBuilders = (WidgetBuilder<W, M>[]) ArrayUtils
               .addAt(existingWidgetBuilders, addAt,
                        new RichFacesWidgetBuilder());

      return new CompositeWidgetBuilder<W, M>(
               new CompositeWidgetBuilderConfig<W, M>()
                        .setWidgetBuilders(newWidgetBuilders));
   }

   protected void createInitializers(final JavaClass entity)
   {
      for (Field<JavaClass> field : entity.getFields())
      {
         if (field.hasAnnotation(OneToOne.class))
         {
            Annotation<JavaClass> oneToOne = field.getAnnotation(OneToOne.class);
            if (oneToOne.getStringValue("mappedBy") == null)
            {
               oneToOne.setEnumValue("cascade", CascadeType.ALL);
            }
            String methodName = "new" + field.getTypeInspector().getName();
            if (!entity.hasMethodSignature(methodName))
            {
               entity.addMethod().setName(methodName).setReturnTypeVoid().setPublic()
                        .setBody("this." + field.getName() + " = new " + field.getType() + "();");
            }
         }
      }
      for (Method<JavaClass> method : entity.getMethods())
      {
         if (method.hasAnnotation(OneToOne.class))
         {
            Annotation<JavaClass> oneToOne = method.getAnnotation(OneToOne.class);
            if (oneToOne.getStringValue("mappedBy") == null)
            {
               oneToOne.setEnumValue("cascade", CascadeType.ALL);
            }
            String methodName = "new" + method.getReturnTypeInspector().getName();
            if (!entity.hasMethodSignature(methodName))
            {
               entity.addMethod().setName(methodName).setReturnTypeVoid().setPublic()
                        .setBody("this." + method.getName() + " = new " + method.getReturnType() + "();");
            }
         }
      }
   }

   protected HashMap<Object, Object> getTemplateContext(final Resource<?> template)
   {
      HashMap<Object, Object> context;
      context = new HashMap<Object, Object>();
      context.put("template", template);
      context.put("templateStrategy", getTemplateStrategy());
      return context;
   }

   protected void setupWebXML()
   {
      ServletFacet servlet = this.project.getFacet(ServletFacet.class);

      Node webXML = removeConflictingErrorPages(servlet);
      servlet.getConfigFile().setContents(XMLParser.toXMLInputStream(webXML));

      WebAppDescriptor config = servlet.getConfig();
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);

      // (prefer /faces/error.xhtml)

      String errorLocation = getAccessStrategy().getWebPaths(web.getWebResource("error.xhtml")).get(1);
      config.errorPage(404, errorLocation);
      config.errorPage(500, errorLocation);

      servlet.saveConfig(config);
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

   protected Resource<?> generateNavigation(final boolean overwrite)
            throws IOException
   {
      WebResourceFacet web = this.project.getFacet(WebResourceFacet.class);
      HtmlTag unorderedList = new HtmlTag("ul");

      for (Resource<?> resource : web.getWebResource("scaffold").listResources())
      {
         HtmlOutcomeTargetLink outcomeTargetLink = new HtmlOutcomeTargetLink();
         outcomeTargetLink.putAttribute("outcome", "/scaffold/" + resource.getName() + "/search");
         outcomeTargetLink.setValue(StringUtils.uncamelCase(resource.getName()));

         HtmlTag listItem = new HtmlTag("li");
         listItem.getChildren().add(outcomeTargetLink);
         unorderedList.getChildren().add(listItem);
      }

      Writer writer = new IndentedWriter(new StringWriter(), this.navigationTemplateIndent);
      unorderedList.write(writer);
      Map<Object, Object> context = CollectionUtils.newHashMap();
      context.put("navigation", writer.toString().trim());

      if (this.navigationTemplate == null)
      {
         loadTemplates();
      }

      return ScaffoldUtil.createOrOverwrite(this.prompt, (FileResource<?>) getTemplateStrategy()
               .getDefaultTemplate(),
               this.navigationTemplate.render(context),
               overwrite);
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
}
