/**
 * 
 */
package org.jboss.forge.addon.scaffold.faces;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_0;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.javaee.cdi.ui.CDISetupWizard;
import org.jboss.forge.addon.javaee.ejb.EJBFacet;
import org.jboss.forge.addon.javaee.ejb.EJBFacet_3_1;
import org.jboss.forge.addon.javaee.ejb.EJBFacet_3_2;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetupWizard;
import org.jboss.forge.addon.javaee.faces.FacesFacet;
import org.jboss.forge.addon.javaee.faces.FacesFacet_2_0;
import org.jboss.forge.addon.javaee.faces.FacesFacet_2_1;
import org.jboss.forge.addon.javaee.faces.FacesFacet_2_2;
import org.jboss.forge.addon.javaee.faces.ui.FacesSetupWizard;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.JPAFacet_2_0;
import org.jboss.forge.addon.javaee.jpa.JPAFacet_2_1;
import org.jboss.forge.addon.javaee.jpa.ui.setup.JPASetupWizard;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.javaee.servlet.ui.ServletSetupWizard;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.scaffold.faces.freemarker.FreemarkerTemplateProcessor;
import org.jboss.forge.addon.scaffold.faces.metawidget.config.ForgeConfigReader;
import org.jboss.forge.addon.scaffold.faces.util.ScaffoldUtil;
import org.jboss.forge.addon.scaffold.spi.AccessStrategy;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.shrinkwrap.descriptor.api.javaee6.ParamValueType;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;
import org.jboss.shrinkwrap.descriptor.spi.node.NodeDescriptor;
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
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.XmlUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilder;
import org.metawidget.widgetbuilder.composite.CompositeWidgetBuilderConfig;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import freemarker.template.Template;

/**
 * The scaffold provider for JSF 2.0
 */
public class FacesScaffoldProvider extends AbstractFacet<Project> implements ScaffoldProvider
{
   private static final String JBOSS_COMMUNITY_PNG = "/resources/jboss-community.png";
   private static final String TRUE_PNG = "/resources/true.png";
   private static final String SEARCH_PNG = "/resources/search.png";
   private static final String REMOVE_PNG = "/resources/remove.png";
   private static final String FORGE_STYLE_CSS = "/resources/forge-style.css";
   private static final String FORGE_LOGO_PNG = "/resources/forge-logo.png";
   private static final String FAVICON_ICO = "/resources/favicon.ico";
   private static final String FALSE_PNG = "/resources/false.png";
   private static final String BOOTSTRAP_CSS = "/resources/bootstrap.css";
   private static final String ADD_PNG = "/resources/add.png";
   private static final String ERROR_XHTML = "error.xhtml";
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
   
   protected FreemarkerTemplateProcessor templateProcessor;
   protected Template backingBeanTemplate;
   protected int backingBeanTemplateQbeMetawidgetIndent;
   protected int backingBeanTemplateRmEntityMetawidgetIndent;
   protected Template viewUtilsTemplate;
   protected Template taglibTemplate;
   protected Template viewTemplate;
   protected Map<String, String> viewTemplateNamespaces;
   protected int viewTemplateEntityMetawidgetIndent;
   protected Template createTemplate;
   protected Map<String, String> createTemplateNamespaces;
   protected int createTemplateEntityMetawidgetIndent;
   protected Template searchTemplate;
   protected Map<String, String> searchTemplateNamespaces;
   protected int searchTemplateSearchMetawidgetIndent;
   protected int searchTemplateBeanMetawidgetIndent;
   protected Template navigationTemplate;
   protected int navigationTemplateIndent;
   protected Template errorTemplate;
   protected Template indexWelcomeTemplate;
   protected Template indexTemplate;
   protected StaticHtmlMetawidget entityMetawidget;
   protected StaticHtmlMetawidget searchMetawidget;
   protected StaticHtmlMetawidget beanMetawidget;
   protected StaticJavaMetawidget qbeMetawidget;
   protected StaticJavaMetawidget rmEntityMetawidget;

   private Configuration config;
   
   @Inject
   public FacesScaffoldProvider(final Configuration config, final FreemarkerTemplateProcessor templateProcessor)
   {
      this.config = config;
      this.templateProcessor = templateProcessor;
   }

   @Override
   public boolean install()
   {
      // TODO Auto-generated method stub
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      // TODO Auto-generated method stub
      return true;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      // TODO Auto-generated method stub
      
   }
   
   @Override
   public void setFaceted(Project origin)
   {
      super.setFaceted(origin);
      resetMetaWidgets();
   }

   @Override
   public String getName()
   {
      return "Faces";
   }

   @Override
   public String getDescription()
   {
      return "Scaffold a Faces project from JPA entities";
   }

   @Override
   public List<Resource<?>> setup(Project project, ScaffoldSetupContext scaffoldContext)
   {
      if(this.origin == null)
      {
         origin = project;
      }
      String targetDir = scaffoldContext.getTargetDirectory();
      boolean overwrite = scaffoldContext.isOverwrite();
      Resource<?> template = null;
      List<Resource<?>> resources = generateIndex(targetDir, template, overwrite);
      setupWebXML();

      return resources;
   }

   @Override
   public List<Resource<?>> generateFrom(Project project, ScaffoldGenerationContext scaffoldContext)
   {
      List<Resource<?>> generatedResources = new ArrayList<Resource<?>>();
      Collection<?> resources = scaffoldContext.getResources();
      for (Object resource : resources)
      {
         JavaClass entity  = (JavaClass) resource;
         String targetDir = scaffoldContext.getTargetDirectory();
         Resource<?> template = null;
         boolean overwrite = scaffoldContext.isOverwrite();
         List<Resource<?>> generatedResourcesForEntity = this.generateFromEntity(targetDir , template, entity, overwrite );

         // TODO give plugins a chance to react to generated resources, use event bus?
         // if (!generatedResources.isEmpty())
         // {
         //    generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
         // }
         generatedResources.addAll(generatedResourcesForEntity);
      }
      return generatedResources;
   }

   @Override
   public List<Class<? extends UICommand>> getSetupFlow()
   {
      List<Class<? extends UICommand>> setupCommands = new ArrayList<Class<? extends UICommand>>();
      // FORGE-1304 detect all facet subtypes that we support
      if(!origin.hasFacet(JPAFacet_2_0.class) && !origin.hasFacet(JPAFacet_2_1.class))
      {
         setupCommands.add(JPASetupWizard.class);
      }
      if(!origin.hasFacet(CDIFacet_1_0.class) && !origin.hasFacet(CDIFacet_1_1.class))
      {
         setupCommands.add(CDISetupWizard.class);
      }
      if(!origin.hasFacet(EJBFacet_3_1.class) && !origin.hasFacet(EJBFacet_3_2.class))
      {
         setupCommands.add(EJBSetupWizard.class);
      }
      if(!origin.hasFacet(ServletFacet_3_0.class) && !origin.hasFacet(ServletFacet_3_1.class))
      {
         //TODO: FORGE-1296. Ensure that this wizard only sets up Servlet 3.0+
         setupCommands.add(ServletSetupWizard.class);
      }
      if(!origin.hasFacet(FacesFacet_2_0.class) && !origin.hasFacet(FacesFacet_2_1.class) && !origin.hasFacet(FacesFacet_2_2.class))
      {
         setupCommands.add(FacesSetupWizard.class);
      }
      return setupCommands;
   }
   
   @Override
   public List<Class<? extends UICommand>> getGenerationFlow()
   {
      List<Class<? extends UICommand>> generationCommands = new ArrayList<Class<? extends UICommand>>();
      generationCommands.add(ScaffoldableEntitySelectionWizard.class);
      return generationCommands;
   }
   
   protected List<Resource<?>> generateIndex(String targetDir, final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourcesFacet web = this.origin.getFacet(WebResourcesFacet.class);

      // TODO: Refactor this and remove the duplication
      // TODO: origin.getFacet(ServletFacet.class) returns null!!
      if (this.origin.hasFacet(ServletFacet_3_0.class))
      {
         ServletFacet servlet = this.origin.getFacet(ServletFacet_3_0.class);
         WebAppDescriptor servletConfig = (WebAppDescriptor) servlet.getConfig();
         servletConfig.getOrCreateWelcomeFileList().welcomeFile("/index.html");
      }
      else if (this.origin.hasFacet(ServletFacet_3_1.class))
      {
         ServletFacet servlet = this.origin.getFacet(ServletFacet_3_1.class);
         org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor servletConfig = (org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor) servlet
                  .getConfig();
         servletConfig.getOrCreateWelcomeFileList().welcomeFile("/index.html");
      }
      loadTemplates();

      generateTemplates(targetDir, overwrite);
      HashMap<Object, Object> context = getTemplateContext(targetDir, template);

      // Basic pages

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(targetDir + "/index.html"),
               this.templateProcessor.processTemplate(context, indexWelcomeTemplate), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(targetDir + "/index.xhtml"),
               this.templateProcessor.processTemplate(context, indexTemplate), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(ERROR_XHTML),
               this.templateProcessor.processTemplate(context, errorTemplate), overwrite));

      // Static resources

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(ADD_PNG),
               getClass().getResourceAsStream("/scaffold/faces/add.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(BOOTSTRAP_CSS),
               getClass().getResourceAsStream("/scaffold/faces/bootstrap.css"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(FALSE_PNG),
               getClass().getResourceAsStream("/scaffold/faces/false.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(FAVICON_ICO),
               getClass().getResourceAsStream("/scaffold/faces/favicon.ico"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(FORGE_LOGO_PNG),
               getClass().getResourceAsStream("/scaffold/faces/forge-logo.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(FORGE_STYLE_CSS),
               getClass().getResourceAsStream("/scaffold/faces/forge-style.css"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(REMOVE_PNG),
               getClass().getResourceAsStream("/scaffold/faces/remove.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(SEARCH_PNG),
               getClass().getResourceAsStream("/scaffold/faces/search.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(TRUE_PNG),
               getClass().getResourceAsStream("/scaffold/faces/true.png"), overwrite));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(JBOSS_COMMUNITY_PNG),
               getClass().getResourceAsStream("/scaffold/faces/jboss-community.png"), overwrite));

      return result;
   }
   
   @Override
   public AccessStrategy getAccessStrategy()
   {
      return new FacesAccessStrategy(this.origin);
   }
   
   public TemplateStrategy getTemplateStrategy()
   {
      return new FacesTemplateStrategy(this.origin);
   }

   protected List<Resource<?>> generateTemplates(String targetDir, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();

      try
      {
         WebResourcesFacet web = this.origin.getFacet(WebResourcesFacet.class);

         result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource("/resources/scaffold/paginator.xhtml"),
                  getClass().getResourceAsStream("/scaffold/faces/paginator.xhtml"), overwrite));

         result.add(generateNavigation(targetDir, overwrite));
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default templates", e);
      }

      return result;
   }
   
   protected void loadTemplates()
   {
      if (this.backingBeanTemplate == null)
      {
         this.backingBeanTemplate = this.templateProcessor.getTemplate(BACKING_BEAN_TEMPLATE);
         String template = this.backingBeanTemplate.toString();
         this.backingBeanTemplateQbeMetawidgetIndent = parseIndent(template, "${qbeMetawidget}");
         this.backingBeanTemplateRmEntityMetawidgetIndent = parseIndent(template, "${rmEntityMetawidget}");
      }
      if (this.viewUtilsTemplate == null)
      {
         this.viewUtilsTemplate = this.templateProcessor.getTemplate(VIEW_UTILS_TEMPLATE);
      }
      if (this.taglibTemplate == null)
      {
         this.taglibTemplate = this.templateProcessor.getTemplate(TAGLIB_TEMPLATE);
      }
      if (this.viewTemplate == null)
      {
         this.viewTemplate = this.templateProcessor.getTemplate(VIEW_TEMPLATE);
         String template = this.viewTemplate.toString();
         this.viewTemplateNamespaces = parseNamespaces(template);
         this.viewTemplateEntityMetawidgetIndent = parseIndent(template, "${metawidget}");
      }
      if (this.createTemplate == null)
      {
         this.createTemplate = this.templateProcessor.getTemplate(CREATE_TEMPLATE);
         String template = this.createTemplate.toString();
         this.createTemplateNamespaces = parseNamespaces(template);
         this.createTemplateEntityMetawidgetIndent = parseIndent(template, "${metawidget}");
      }
      if (this.searchTemplate == null)
      {
         this.searchTemplate = this.templateProcessor.getTemplate(SEARCH_TEMPLATE);
         String template = this.searchTemplate.toString();
         this.searchTemplateNamespaces = parseNamespaces(template);
         this.searchTemplateSearchMetawidgetIndent = parseIndent(template, "${searchMetawidget}");
         this.searchTemplateBeanMetawidgetIndent = parseIndent(template, "${beanMetawidget}");
      }
      if (this.navigationTemplate == null)
      {
         this.navigationTemplate = this.templateProcessor.getTemplate(NAVIGATION_TEMPLATE);
         String template = navigationTemplate.toString();
         this.navigationTemplateIndent = parseIndent(template, "${navigation}");
      }
      if (this.errorTemplate == null)
      {
         this.errorTemplate = this.templateProcessor.getTemplate(ERROR_TEMPLATE);
      }
      if (this.indexTemplate == null)
      {
         this.indexTemplate = this.templateProcessor.getTemplate(INDEX_TEMPLATE);
      }
      if (this.indexWelcomeTemplate == null)
      {
         this.indexWelcomeTemplate = this.templateProcessor.getTemplate(INDEX_WELCOME_TEMPLATE);
      }
   }
   
   protected HashMap<Object, Object> getTemplateContext(String targetDir, final Resource<?> template)
   {
      TemplateStrategy templateStrategy = getTemplateStrategy();

      HashMap<Object, Object> context;
      context = new HashMap<Object, Object>();
      context.put("template", template);
      //TODO Fix ResourceUtil first 
      //context.put("templatePath", templateStrategy.getReferencePath(template != null ? template : templateStrategy.getDefaultTemplate()));
      context.put("templatePath", "/resources/scaffold/pageTemplate.xhtml");
      context.put("templateStrategy", templateStrategy);
      context.put("targetDir", targetDir);
      return context;
   }

   protected void setupWebXML()
   {
      WebResourcesFacet web = this.origin.getFacet(WebResourcesFacet.class);
      // TODO: Refactor this and remove the duplication
      // TODO: origin.getFacet(ServletFacet.class) returns null!!
      if (this.origin.hasFacet(ServletFacet_3_0.class))
      {
         ServletFacet servlet = this.origin.getFacet(ServletFacet_3_0.class);
         WebAppDescriptor servletConfig = (WebAppDescriptor) servlet.getConfig();
         Node root = ((NodeDescriptor)servletConfig).getRootNode();
         removeConflictingErrorPages(root);
         
         // (prefer /faces/error.xhtml)

         String errorLocation = getAccessStrategy().getWebPaths(web.getWebResource(ERROR_XHTML)).get(1);
         servletConfig.createErrorPage().errorCode("404").location(errorLocation);
         servletConfig.createErrorPage().errorCode("500").location(errorLocation);

         // Use the server timezone since we accept dates in that timezone, and it makes sense to display them in the
         // same
         boolean found = false;
         for (ParamValueType<WebAppDescriptor> contextParam : servletConfig.getAllContextParam())
         {
            if (contextParam.getParamName().equals("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE"))
            {
               found = true;
            }
         }
         if (!found)
         {
            servletConfig.createContextParam()
                     .paramName("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE").paramValue("true");
         }
         servlet.saveConfig(servletConfig);
      }
      else if (this.origin.hasFacet(ServletFacet_3_1.class))
      {
         ServletFacet servlet = this.origin.getFacet(ServletFacet_3_1.class);
         org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor servletConfig = (org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor) servlet
                  .getConfig();
         // (prefer /faces/error.xhtml)

         String errorLocation = getAccessStrategy().getWebPaths(web.getWebResource(ERROR_XHTML)).get(1);
         servletConfig.createErrorPage().errorCode("404").location(errorLocation);
         servletConfig.createErrorPage().errorCode("500").location(errorLocation);

         // Use the server timezone since we accept dates in that timezone, and it makes sense to display them in the
         // same
         boolean found = false;
         for (org.jboss.shrinkwrap.descriptor.api.javaee7.ParamValueType<org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor> contextParam : servletConfig
                  .getAllContextParam())
         {
            if (contextParam.getParamName().equals("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE"))
            {
               found = true;
            }
         }
         if (!found)
         {
            servletConfig.createContextParam()
                     .paramName("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE").paramValue("true");
         }
         servlet.saveConfig(servletConfig);
      }
   }

   private void removeConflictingErrorPages(Node root)
   {
      List<Node> nodeList = root.get("error-page");
      for (Node errorPage : nodeList)
      {
         String errorCode = errorPage.getTextValueForPatternName("error-code");
         if(errorCode.equals("404") || errorCode.equals("500"))
         {
            // TODO: Prompt before removing? A prompt existed in Forge 1.
            root.removeChild(errorPage);
         }
      }
   }

   /**
    * Generates the navigation menu based on scaffolded entities.
    */

   protected Resource<?> generateNavigation(final String targetDir, final boolean overwrite)
            throws IOException
   {
      WebResourcesFacet web = this.origin.getFacet(WebResourcesFacet.class);
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
      context.put("appName", StringUtils.uncamelCase(this.origin.getProjectRoot().getName()));
      context.put("navigation", writer.toString().trim());
      context.put("targetDir", targetDir);

      if (this.navigationTemplate == null)
      {
         loadTemplates();
      }

      try
      {
         return ScaffoldUtil.createOrOverwrite((FileResource<?>) getTemplateStrategy()
                  .getDefaultTemplate(),
                  this.templateProcessor.processTemplate(context, navigationTemplate),
                  true);
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
   
   private void resetMetaWidgets()
   {
      ForgeConfigReader configReader = new ForgeConfigReader(this.config, this.origin);

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
   
   private List<Resource<?>> generateFromEntity(String targetDir, final Resource<?> template, final JavaClass entity,
            final boolean overwrite)
   {
      resetMetaWidgets();

      // FORGE-460: setupRichFaces during generateFromEntity, not during setup, as generally 'richfaces setup' is called
      // *after* 'scaffold setup'
      // setupRichFaces();

      // Track the list of resources generated

      List<Resource<?>> result = new ArrayList<Resource<?>>();
      try
      {
         JavaSourceFacet java = this.origin.getFacet(JavaSourceFacet.class);
         WebResourcesFacet web = this.origin.getFacet(WebResourcesFacet.class);

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
         JavaClass viewBean = JavaParser.parse(JavaClass.class, this.templateProcessor.processTemplate(context, this.backingBeanTemplate));
         viewBean.setPackage(java.getBasePackage() + ".view");
         result.add(ScaffoldUtil.createOrOverwrite(java.getJavaResource(viewBean), viewBean.toString(),
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

         result.add(ScaffoldUtil.createOrOverwrite(
                  web.getWebResource(targetDir + "/" + ccEntity + "/create.xhtml"),
                  this.templateProcessor.processTemplate(context, this.createTemplate),
                  overwrite));

         // Generate view
         this.entityMetawidget.setReadOnly(true);
         writeEntityMetawidget(context, this.viewTemplateEntityMetawidgetIndent, this.viewTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(
                  web.getWebResource(targetDir + "/" + ccEntity + "/view.xhtml"),
                  this.templateProcessor.processTemplate(context, this.viewTemplate), overwrite));

         // Generate search
         this.searchMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".example"));
         this.searchMetawidget.setPath(entity.getQualifiedName());
         this.beanMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".pageItems"));
         this.beanMetawidget.setPath(viewBean.getQualifiedName() + "/pageItems");
         writeSearchAndBeanMetawidget(context, this.searchTemplateSearchMetawidgetIndent,
                  this.searchTemplateBeanMetawidgetIndent, this.searchTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(
                  web.getWebResource(targetDir + "/" + ccEntity + "/search.xhtml"),
                  this.templateProcessor.processTemplate(context, this.searchTemplate), overwrite));

         // Generate navigation
         result.add(generateNavigation(targetDir, overwrite));

         // Need ViewUtils and forge.taglib.xml for forgeview:asList
         JavaClass viewUtils = JavaParser.parse(JavaClass.class, this.templateProcessor.processTemplate(context, this.viewUtilsTemplate));
         viewUtils.setPackage(viewBean.getPackage());
         result.add(ScaffoldUtil.createOrOverwrite(java.getJavaResource(viewUtils), viewUtils.toString(),
                  true));

         context.put("viewPackage", viewBean.getPackage());
         result.add(ScaffoldUtil.createOrOverwrite(
                  web.getWebResource("WEB-INF/classes/META-INF/forge.taglib.xml"),
                  this.templateProcessor.processTemplate(context, this.taglibTemplate), true));

         createInitializers(entity);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error generating default scaffolding: " + e.getMessage(), e);
      }
      return result;
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
         this.origin.getFacet(JavaSourceFacet.class).saveJavaSource(entity);
      }
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
   
   protected void setupRichFaces()
   {
      if ((this.origin.getFacet(DependencyFacet.class).hasEffectiveDependency(this.richfaces3UI)
               && this.origin.getFacet(DependencyFacet.class).hasEffectiveDependency(this.richfaces3Impl))
               || (this.origin.getFacet(DependencyFacet.class).hasEffectiveDependency(this.richfaces4UI)
               && this.origin.getFacet(DependencyFacet.class).hasEffectiveDependency(this.richfaces4Impl)))
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

}
