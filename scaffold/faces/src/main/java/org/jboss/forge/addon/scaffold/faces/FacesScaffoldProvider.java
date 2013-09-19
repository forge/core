/**
 * 
 */
package org.jboss.forge.addon.scaffold.faces;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.facets.AbstractFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.scaffold.faces.freemarker.FreemarkerTemplateProcessor;
import org.jboss.forge.addon.scaffold.faces.metawidget.config.ForgeConfigReader;
import org.jboss.forge.addon.scaffold.faces.util.ScaffoldUtil;
import org.jboss.forge.addon.scaffold.spi.AccessStrategy;
import org.jboss.forge.addon.scaffold.spi.ScaffoldContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.parser.xml.Node;
import org.jboss.forge.parser.xml.XMLParser;
import org.metawidget.statically.StaticUtils.IndentedWriter;
import org.metawidget.statically.faces.component.html.StaticHtmlMetawidget;
import org.metawidget.statically.faces.component.html.widgetbuilder.HtmlOutcomeTargetLink;
import org.metawidget.statically.html.widgetbuilder.HtmlTag;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.XmlUtils;
import org.metawidget.util.simple.StringUtils;
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
   public List<Resource<?>> setup(Project project, ScaffoldContext scaffoldContext)
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
   public List<Resource<?>> generateFrom(Iterable<Resource<?>> resources, ScaffoldContext scaffoldContext)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean needsOverwriteConfirmation(ScaffoldContext scaffoldContext)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Class<? extends UIWizardStep> getSetupFlow()
   {
      // TODO Auto-generated method stub
      return null;
   }
   
   protected List<Resource<?>> generateIndex(String targetDir, final Resource<?> template, final boolean overwrite)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      WebResourcesFacet web = this.origin.getFacet(WebResourcesFacet.class);

      //TODO Fix ServletFacet
      //this.project.getFacet(ServletFacet.class).getConfig().welcomeFile("/index.html");
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
      context.put("templatePath", "/resources/scaffold/paginator.xhtml");
      context.put("templateStrategy", templateStrategy);
      context.put("targetDir", targetDir);
      return context;
   }

   protected void setupWebXML()
   {
      ServletFacet servlet = this.origin.getFacet(ServletFacet.class);

      Node webXML = removeConflictingErrorPages(servlet);
      servlet.getConfigFile().setContents(XMLParser.toXMLInputStream(webXML));

      /*TODO Fix ServletFacet
      WebAppDescriptor servletConfig = servlet.getConfig();
      WebResourcesFacet web = this.project.getFacet(WebResourcesFacet.class);

      // (prefer /faces/error.xhtml)

      String errorLocation = getAccessStrategy().getWebPaths(web.getWebResource(ERROR_XHTML)).get(1);
      servletConfig.errorPage(404, errorLocation);
      servletConfig.errorPage(500, errorLocation);
      
      // Use the server timezone since we accept dates in that timezone, and it makes sense to display them in the same
      if (servletConfig.getContextParam("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE") == null)
      {
         servletConfig.contextParam("javax.faces.DATETIMECONVERTER_DEFAULT_TIMEZONE_IS_SYSTEM_TIMEZONE", "true");
      }

      servlet.saveConfig(servletConfig);
      */
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
                     /* && this.prompt.promptBoolean("Your web.xml already contains an error page for " + code
                              + " status codes, replace it?") */)
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

}
