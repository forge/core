/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
package org.jboss.forge.addon.scaffold.faces;

import static org.jboss.forge.addon.javaee.JavaEEPackageConstants.DEFAULT_FACES_PACKAGE;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.jboss.forge.addon.configuration.Configuration;
import org.jboss.forge.addon.facets.FacetNotFoundException;
import org.jboss.forge.addon.javaee.cdi.CDIFacet;
import org.jboss.forge.addon.javaee.cdi.ui.CDISetupCommand;
import org.jboss.forge.addon.javaee.ejb.EJBFacet;
import org.jboss.forge.addon.javaee.ejb.ui.EJBSetupWizard;
import org.jboss.forge.addon.javaee.faces.FacesFacet;
import org.jboss.forge.addon.javaee.faces.ui.FacesSetupWizard;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.javaee.jpa.ui.setup.JPASetupWizard;
import org.jboss.forge.addon.javaee.servlet.ServletFacet;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_0;
import org.jboss.forge.addon.javaee.servlet.ServletFacet_3_1;
import org.jboss.forge.addon.javaee.servlet.ui.ServletSetupWizard;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFilter;
import org.jboss.forge.addon.scaffold.faces.freemarker.FreemarkerTemplateProcessor;
import org.jboss.forge.addon.scaffold.faces.metawidget.config.ForgeConfigReader;
import org.jboss.forge.addon.scaffold.spi.AccessStrategy;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.scaffold.ui.ScaffoldSetupWizard;
import org.jboss.forge.addon.scaffold.util.ScaffoldUtil;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.Field;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.JavaSource;
import org.jboss.forge.roaster.model.source.MemberSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.util.Types;
import org.jboss.shrinkwrap.descriptor.api.javaee.ParamValueCommonType;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp.WebAppCommonDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webcommon.ErrorPageCommonType;
import org.jboss.shrinkwrap.descriptor.api.webcommon30.WelcomeFileListType;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;
import org.jboss.shrinkwrap.descriptor.spi.node.NodeDescriptor;
import org.metawidget.statically.StaticUtils.IndentedWriter;
import org.metawidget.statically.faces.StaticFacesUtils;
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
public class FacesScaffoldProvider implements ScaffoldProvider
{
   private static final String JBOSS_COMMUNITY_PNG = "/resources/jboss-community.png";
   private static final String SCAFFOLD_JBOSS_COMMUNITY_PNG = "/scaffold/faces/jboss-community.png";
   private static final String TRUE_PNG = "/resources/true.png";
   private static final String SCAFFOLD_TRUE_PNG = "/scaffold/faces/true.png";
   private static final String SEARCH_PNG = "/resources/search.png";
   private static final String SCAFFOLD_SEARCH_PNG = "/scaffold/faces/search.png";
   private static final String REMOVE_PNG = "/resources/remove.png";
   private static final String SCAFFOLD_REMOVE_PNG = "/scaffold/faces/remove.png";
   private static final String FORGE_STYLE_CSS = "/resources/forge-style.css";
   private static final String SCAFFOLD_FORGE_STYLE_CSS = "/scaffold/faces/forge-style.css";
   private static final String FORGE_LOGO_PNG = "/resources/forge-logo.png";
   private static final String SCAFFOLD_FORGE_LOGO_PNG = "/scaffold/faces/forge-logo.png";
   private static final String FAVICON_ICO = "/resources/favicon.ico";
   private static final String SCAFFOLD_FAVICON_ICO = "/scaffold/faces/favicon.ico";
   private static final String FALSE_PNG = "/resources/false.png";
   private static final String SCAFFOLD_FALSE_PNG = "/scaffold/faces/false.png";
   private static final String BOOTSTRAP_CSS = "/resources/bootstrap.css";
   private static final String SCAFFOLD_BOOTSTRAP_CSS = "/scaffold/faces/bootstrap.css";
   private static final String ADD_PNG = "/resources/add.png";
   private static final String SCAFFOLD_ADD_PNG = "/scaffold/faces/add.png";
   private static final String ERROR_XHTML = "error.xhtml";
   private static final String XMLNS_PREFIX = "xmlns:";
   private static final String BACKING_BEAN_TEMPLATE = "scaffold/faces/BackingBean.jv";
   private static final String VIEW_UTILS_TEMPLATE = "scaffold/faces/ViewUtils.jv";
   private static final String TAGLIB_TEMPLATE = "scaffold/faces/forge.taglib.xml";
   private static final String VIEW_TEMPLATE = "scaffold/faces/view.xhtml";
   private static final String CREATE_TEMPLATE = "scaffold/faces/create.xhtml";
   private static final String SEARCH_TEMPLATE = "scaffold/faces/search.xhtml";
   private static final String NAVIGATION_TEMPLATE = "scaffold/faces/pageTemplate.xhtml";
   private static final String SCAFFOLD_NAVIGATION_TEMPLATE = "/resources/scaffold/pageTemplate.xhtml";
   private static final String ERROR_TEMPLATE = "scaffold/faces/error.xhtml";
   private static final String INDEX_TEMPLATE = "scaffold/faces/index.xhtml";
   private static final String INDEX_HTML_TEMPLATE = "scaffold/faces/index.html";
   private static final String INDEX_XHTML = "/index.xhtml";
   private static final String INDEX_HTML = "/index.html";
   private static final String SCAFFOLD_META_WIDGET_REMOVE_ENTITY = "scaffold/faces/metawidget-remove-entity.xml";
   private static final String SCAFFOLD_META_WIDGET_ENTITY = "scaffold/faces/metawidget-entity.xml";
   private static final String SCAFFOLD_META_WIDGET_SEARCH = "scaffold/faces/metawidget-search.xml";
   private static final String SCAFFOLD_META_WIDGET_BEAN = "scaffold/faces/metawidget-bean.xml";
   private static final String SCAFFOLD_META_WIDGET_QBE = "scaffold/faces/metawidget-qbe.xml";
   private static final String PAGINATOR = "/resources/scaffold/paginator.xhtml";
   private static final String SCAFFOLD_PAGINATOR = "/scaffold/faces/paginator.xhtml";

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

   private Project project;

   private void setProject(Project project)
   {
      this.project = project;
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
   public List<Resource<?>> setup(ScaffoldSetupContext setupContext)
   {
      setProject(setupContext.getProject());
      String targetDir = setupContext.getTargetDirectory();
      Resource<?> template = null;
      List<Resource<?>> resources = generateIndex(targetDir, template);
      setupWebXML();

      return resources;
   }

   @SuppressWarnings("rawtypes")
   @Override
   public boolean isSetup(ScaffoldSetupContext setupContext)
   {
      Project project = setupContext.getProject();
      setProject(project);
      String targetDir = setupContext.getTargetDirectory();
      targetDir = targetDir == null ? "" : targetDir;
      if (project.hasAllFacets(
               Arrays.asList(WebResourcesFacet.class, DependencyFacet.class, JPAFacet.class, EJBFacet.class,
                        CDIFacet.class, ServletFacet.class, FacesFacet.class)))
      {
         WebResourcesFacet web = project.getFacet(WebResourcesFacet.class);
         boolean areResourcesInstalled = web.getWebResource(targetDir + INDEX_HTML).exists()
                  && web.getWebResource(targetDir + INDEX_XHTML).exists()
                  && web.getWebResource(ERROR_XHTML).exists()
                  && web.getWebResource(ADD_PNG).exists()
                  && web.getWebResource(BOOTSTRAP_CSS).exists()
                  && web.getWebResource(FALSE_PNG).exists()
                  && web.getWebResource(FAVICON_ICO).exists()
                  && web.getWebResource(FORGE_LOGO_PNG).exists()
                  && web.getWebResource(FORGE_STYLE_CSS).exists()
                  && web.getWebResource(REMOVE_PNG).exists()
                  && web.getWebResource(SEARCH_PNG).exists()
                  && web.getWebResource(TRUE_PNG).exists()
                  && web.getWebResource(JBOSS_COMMUNITY_PNG).exists()
                  && web.getWebResource(PAGINATOR).exists()
                  && getTemplateStrategy().getDefaultTemplate().exists();
         ServletFacet servlet = project.getFacet(ServletFacet.class);
         boolean isWelcomeFileListed = false;
         if (servlet instanceof ServletFacet_3_0)
         {
            WebAppDescriptor servletConfig = (WebAppDescriptor) servlet.getConfig();
            for (WelcomeFileListType<WebAppDescriptor> welcomeFileList : servletConfig.getAllWelcomeFileList())
            {
               for (String welcomeFile : welcomeFileList.getAllWelcomeFile())
               {
                  if (welcomeFile.equals(INDEX_HTML))
                  {
                     isWelcomeFileListed = true;
                     break;
                  }
               }
            }
         }
         else if (servlet instanceof ServletFacet_3_1)
         {
            org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor servletConfig = (org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor) servlet
                     .getConfig();
            for (org.jboss.shrinkwrap.descriptor.api.webcommon31.WelcomeFileListType<org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor> welcomeFileList : servletConfig
                     .getAllWelcomeFileList())
            {
               for (String welcomeFile : welcomeFileList.getAllWelcomeFile())
               {
                  if (welcomeFile.equals(INDEX_HTML))
                  {
                     isWelcomeFileListed = true;
                     break;
                  }
               }

            }
         }
         return areResourcesInstalled && isWelcomeFileListed;
      }
      return false;
   }

   @Override
   public List<Resource<?>> generateFrom(ScaffoldGenerationContext generationContext)
   {
      setProject(generationContext.getProject());
      List<Resource<?>> generatedResources = new ArrayList<>();
      Collection<?> resources = generationContext.getResources();
      for (Object resource : resources)
      {
         JavaSource<?> javaSource = null;
         if (resource instanceof JavaResource)
         {
            JavaResource javaResource = (JavaResource) resource;
            try
            {
               javaSource = javaResource.getJavaType();
            }
            catch (FileNotFoundException fileEx)
            {
               throw new IllegalStateException(fileEx);
            }
         }
         else
         {
            continue;
         }

         JavaClassSource entity = (JavaClassSource) javaSource;
         String targetDir = generationContext.getTargetDirectory();
         targetDir = (targetDir == null) ? "" : targetDir;
         getConfig().setProperty(FacesScaffoldProvider.class.getName() + "_targetDir", targetDir);
         Resource<?> template = (Resource<?>) generationContext.getAttribute("pageTemplate");
         List<Resource<?>> generatedResourcesForEntity = this.generateFromEntity(targetDir, template, entity);

         // TODO give plugins a chance to react to generated resources, use event bus?
         // if (!generatedResources.isEmpty())
         // {
         // generatedEvent.fire(new ScaffoldGeneratedResources(provider, prepareResources(generatedResources)));
         // }
         generatedResources.addAll(generatedResourcesForEntity);
      }
      return generatedResources;
   }

   @Override
   public NavigationResult getSetupFlow(ScaffoldSetupContext setupContext)
   {
      Project project = setupContext.getProject();
      setProject(setupContext.getProject());
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      List<Class<? extends UICommand>> setupCommands = new ArrayList<>();
      if (!project.hasFacet(JPAFacet.class))
      {
         builder.add(JPASetupWizard.class);
      }
      if (!project.hasFacet(CDIFacet.class))
      {
         setupCommands.add(CDISetupCommand.class);
      }
      if (!project.hasFacet(EJBFacet.class))
      {
         setupCommands.add(EJBSetupWizard.class);
      }
      if (!project.hasFacet(ServletFacet.class))
      {
         // TODO: FORGE-1296. Ensure that this wizard only sets up Servlet 3.0+
         setupCommands.add(ServletSetupWizard.class);
      }
      if (!project.hasFacet(FacesFacet.class))
      {
         setupCommands.add(FacesSetupWizard.class);
      }

      Metadata compositeSetupMetadata = Metadata.forCommand(ScaffoldSetupWizard.class)
               .name("Setup Facets")
               .description("Setup all dependent facets for the Faces scaffold.");
      builder.add(compositeSetupMetadata, setupCommands);
      return builder.build();
   }

   @Override
   public NavigationResult getGenerationFlow(ScaffoldGenerationContext generationContext)
   {
      NavigationResultBuilder builder = NavigationResultBuilder.create();
      builder.add(ScaffoldableEntitySelectionWizard.class);
      return builder.build();
   }

   @SuppressWarnings("rawtypes")
   protected List<Resource<?>> generateIndex(String targetDir, final Resource<?> template)
   {
      List<Resource<?>> result = new ArrayList<>();
      WebResourcesFacet web = this.project.getFacet(WebResourcesFacet.class);

      ServletFacet servlet = this.project.getFacet(ServletFacet.class);
      if (servlet instanceof ServletFacet_3_0)
      {
         WebAppDescriptor servletConfig = (WebAppDescriptor) servlet.getConfig();
         servletConfig.getOrCreateWelcomeFileList().welcomeFile(INDEX_HTML);
      }
      else if (servlet instanceof ServletFacet_3_1)
      {
         org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor servletConfig = (org.jboss.shrinkwrap.descriptor.api.webapp31.WebAppDescriptor) servlet
                  .getConfig();
         servletConfig.getOrCreateWelcomeFileList().welcomeFile(INDEX_HTML);
      }
      loadTemplates();

      generateTemplates(targetDir);
      HashMap<Object, Object> context = getTemplateContext(targetDir, template);

      // Basic pages

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(targetDir + INDEX_HTML),
               FreemarkerTemplateProcessor.processTemplate(context, indexWelcomeTemplate)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(targetDir + INDEX_XHTML),
               FreemarkerTemplateProcessor.processTemplate(context, indexTemplate)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(ERROR_XHTML),
               FreemarkerTemplateProcessor.processTemplate(context, errorTemplate)));

      // Static resources

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(ADD_PNG),
               getClass().getResourceAsStream(SCAFFOLD_ADD_PNG)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(BOOTSTRAP_CSS),
               getClass().getResourceAsStream(SCAFFOLD_BOOTSTRAP_CSS)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(FALSE_PNG),
               getClass().getResourceAsStream(SCAFFOLD_FALSE_PNG)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(FAVICON_ICO),
               getClass().getResourceAsStream(SCAFFOLD_FAVICON_ICO)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(FORGE_LOGO_PNG),
               getClass().getResourceAsStream(SCAFFOLD_FORGE_LOGO_PNG)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(FORGE_STYLE_CSS),
               getClass().getResourceAsStream(SCAFFOLD_FORGE_STYLE_CSS)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(REMOVE_PNG),
               getClass().getResourceAsStream(SCAFFOLD_REMOVE_PNG)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(SEARCH_PNG),
               getClass().getResourceAsStream(SCAFFOLD_SEARCH_PNG)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(TRUE_PNG),
               getClass().getResourceAsStream(SCAFFOLD_TRUE_PNG)));

      result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(JBOSS_COMMUNITY_PNG),
               getClass().getResourceAsStream(SCAFFOLD_JBOSS_COMMUNITY_PNG)));

      return result;
   }

   @Override
   public AccessStrategy getAccessStrategy()
   {
      return new FacesAccessStrategy(this.project);
   }

   public TemplateStrategy getTemplateStrategy()
   {
      return new FacesTemplateStrategy(this.project);
   }

   protected List<Resource<?>> generateTemplates(String targetDir)
   {
      List<Resource<?>> result = new ArrayList<>();

      try
      {
         WebResourcesFacet web = this.project.getFacet(WebResourcesFacet.class);

         result.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(PAGINATOR),
                  getClass().getResourceAsStream(SCAFFOLD_PAGINATOR)));

         result.add(generateNavigation(targetDir));
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
         this.backingBeanTemplate = FreemarkerTemplateProcessor.getTemplate(BACKING_BEAN_TEMPLATE);
         String template = this.backingBeanTemplate.toString();
         this.backingBeanTemplateQbeMetawidgetIndent = parseIndent(template, "${qbeMetawidget}");
         this.backingBeanTemplateRmEntityMetawidgetIndent = parseIndent(template, "${rmEntityMetawidget}");
      }
      if (this.viewUtilsTemplate == null)
      {
         this.viewUtilsTemplate = FreemarkerTemplateProcessor.getTemplate(VIEW_UTILS_TEMPLATE);
      }
      if (this.taglibTemplate == null)
      {
         this.taglibTemplate = FreemarkerTemplateProcessor.getTemplate(TAGLIB_TEMPLATE);
      }
      if (this.viewTemplate == null)
      {
         this.viewTemplate = FreemarkerTemplateProcessor.getTemplate(VIEW_TEMPLATE);
         String template = this.viewTemplate.toString();
         this.viewTemplateNamespaces = parseNamespaces(template);
         this.viewTemplateEntityMetawidgetIndent = parseIndent(template, "${metawidget}");
      }
      if (this.createTemplate == null)
      {
         this.createTemplate = FreemarkerTemplateProcessor.getTemplate(CREATE_TEMPLATE);
         String template = this.createTemplate.toString();
         this.createTemplateNamespaces = parseNamespaces(template);
         this.createTemplateEntityMetawidgetIndent = parseIndent(template, "${metawidget}");
      }
      if (this.searchTemplate == null)
      {
         this.searchTemplate = FreemarkerTemplateProcessor.getTemplate(SEARCH_TEMPLATE);
         String template = this.searchTemplate.toString();
         this.searchTemplateNamespaces = parseNamespaces(template);
         this.searchTemplateSearchMetawidgetIndent = parseIndent(template, "${searchMetawidget}");
         this.searchTemplateBeanMetawidgetIndent = parseIndent(template, "${beanMetawidget}");
      }
      if (this.navigationTemplate == null)
      {
         this.navigationTemplate = FreemarkerTemplateProcessor.getTemplate(NAVIGATION_TEMPLATE);
         String template = navigationTemplate.toString();
         this.navigationTemplateIndent = parseIndent(template, "${navigation}");
      }
      if (this.errorTemplate == null)
      {
         this.errorTemplate = FreemarkerTemplateProcessor.getTemplate(ERROR_TEMPLATE);
      }
      if (this.indexTemplate == null)
      {
         this.indexTemplate = FreemarkerTemplateProcessor.getTemplate(INDEX_TEMPLATE);
      }
      if (this.indexWelcomeTemplate == null)
      {
         this.indexWelcomeTemplate = FreemarkerTemplateProcessor.getTemplate(INDEX_HTML_TEMPLATE);
      }
   }

   protected HashMap<Object, Object> getTemplateContext(String targetDir, final Resource<?> template)
   {
      TemplateStrategy templateStrategy = getTemplateStrategy();

      HashMap<Object, Object> context;
      context = new HashMap<>();
      context.put("template", template);
      context.put("templatePath",
               templateStrategy.getReferencePath(template != null ? template : templateStrategy.getDefaultTemplate()));
      context.put("templatePath", SCAFFOLD_NAVIGATION_TEMPLATE);
      context.put("templateStrategy", templateStrategy);
      context.put("targetDir", targetDir);
      return context;
   }

   @SuppressWarnings({ "rawtypes", "unchecked" })
   protected void setupWebXML()
   {
      WebResourcesFacet web = this.project.getFacet(WebResourcesFacet.class);
      ServletFacet servlet = this.project.getFacet(ServletFacet.class);
      WebAppCommonDescriptor servletConfig = (WebAppCommonDescriptor) servlet.getConfig();
      Node root = ((NodeDescriptor) servletConfig).getRootNode();
      removeConflictingErrorPages(root);

      // (prefer /faces/error.xhtml)

      List<String> webPaths = getAccessStrategy().getWebPaths(web.getWebResource(ERROR_XHTML));
      String errorLocation = webPaths.size() > 1 ? webPaths.get(1) : "/faces/error.xhtml";
      createErrorPageEntry(servletConfig, errorLocation, "404");
      createErrorPageEntry(servletConfig, errorLocation, "500");

      // Use the server timezone since we accept dates in that timezone, and it makes sense to display them in the
      // same
      boolean found = false;
      List<ParamValueCommonType<?>> allContextParam = servletConfig.getAllContextParam();
      for (ParamValueCommonType<?> contextParam : allContextParam)
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

   private void removeConflictingErrorPages(Node root)
   {
      List<Node> nodeList = root.get("error-page");
      for (Node errorPage : nodeList)
      {
         String errorCode = errorPage.getTextValueForPatternName("error-code");
         if (errorCode.equals("404") || errorCode.equals("500"))
         {
            // TODO: Prompt before removing? A prompt existed in Forge 1.
            root.removeChild(errorPage);
         }
      }
   }

   /**
    * Generates the navigation menu based on scaffolded entities.
    */

   protected Resource<?> generateNavigation(final String targetDir)
            throws IOException
   {
      WebResourcesFacet web = this.project.getFacet(WebResourcesFacet.class);
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
         String outcome = targetDir.isEmpty() || targetDir.startsWith("/") ? targetDir : "/" + targetDir;
         outcomeTargetLink.putAttribute("outcome", outcome + "/" + resource.getName() + "/search");
         outcomeTargetLink.setValue(StringUtils.uncamelCase(resource.getName()));

         HtmlTag listItem = new HtmlTag("li");
         listItem.getChildren().add(outcomeTargetLink);
         unorderedList.getChildren().add(listItem);
      }

      Writer writer = new IndentedWriter(new StringWriter(), this.navigationTemplateIndent);
      unorderedList.write(writer);

      Map<Object, Object> context = CollectionUtils.newHashMap();
      context.put("appName", StringUtils.uncamelCase(this.project.getRoot().getName()));
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
                  FreemarkerTemplateProcessor.processTemplate(context, navigationTemplate));
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
      ForgeConfigReader configReader = new ForgeConfigReader(getConfig(), this.project);

      this.entityMetawidget = new StaticHtmlMetawidget();
      this.entityMetawidget.setConfigReader(configReader);
      this.entityMetawidget.setConfig(SCAFFOLD_META_WIDGET_ENTITY);

      this.searchMetawidget = new StaticHtmlMetawidget();
      this.searchMetawidget.setConfigReader(configReader);
      this.searchMetawidget.setConfig(SCAFFOLD_META_WIDGET_SEARCH);

      this.beanMetawidget = new StaticHtmlMetawidget();
      this.beanMetawidget.setConfigReader(configReader);
      this.beanMetawidget.setConfig(SCAFFOLD_META_WIDGET_BEAN);

      this.qbeMetawidget = new StaticJavaMetawidget();
      this.qbeMetawidget.setConfigReader(configReader);
      this.qbeMetawidget.setConfig(SCAFFOLD_META_WIDGET_QBE);

      this.rmEntityMetawidget = new StaticJavaMetawidget();
      this.rmEntityMetawidget.setConfigReader(configReader);
      this.rmEntityMetawidget.setConfig(SCAFFOLD_META_WIDGET_REMOVE_ENTITY);
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   private List<Resource<?>> generateFromEntity(String targetDir, final Resource<?> template,
            final JavaClassSource entity)
   {
      resetMetaWidgets();

      // Track the list of resources generated

      List<Resource<?>> result = new ArrayList<>();
      try
      {
         JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
         ResourcesFacet resources = this.project.getFacet(ResourcesFacet.class);
         WebResourcesFacet web = this.project.getFacet(WebResourcesFacet.class);
         JPAFacet<PersistenceCommonDescriptor> jpa = this.project.getFacet(JPAFacet.class);

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

         // Prepare JPA Persistence Unit
         context.put("persistenceUnitName", jpa.getConfig().getOrCreatePersistenceUnit().getName());

         // Create the Backing Bean for this entity
         JavaClassSource viewBean = Roaster.parse(JavaClassSource.class,
                  FreemarkerTemplateProcessor.processTemplate(context, this.backingBeanTemplate));
         viewBean.setPackage(java.getBasePackage() + "." + DEFAULT_FACES_PACKAGE);
         result.add(ScaffoldUtil.createOrOverwrite(java.getJavaResource(viewBean), viewBean.toString()));

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
                  FreemarkerTemplateProcessor.processTemplate(context, this.createTemplate)));

         // Generate view
         this.entityMetawidget.setReadOnly(true);
         writeEntityMetawidget(context, this.viewTemplateEntityMetawidgetIndent, this.viewTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(
                  web.getWebResource(targetDir + "/" + ccEntity + "/view.xhtml"),
                  FreemarkerTemplateProcessor.processTemplate(context, this.viewTemplate)));

         // Generate search
         this.searchMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".example"));
         this.searchMetawidget.setPath(entity.getQualifiedName());
         this.beanMetawidget.setValue(StaticFacesUtils.wrapExpression(beanName + ".pageItems"));
         this.beanMetawidget.setPath(viewBean.getQualifiedName() + "/pageItems");
         writeSearchAndBeanMetawidget(context, this.searchTemplateSearchMetawidgetIndent,
                  this.searchTemplateBeanMetawidgetIndent, this.searchTemplateNamespaces);

         result.add(ScaffoldUtil.createOrOverwrite(
                  web.getWebResource(targetDir + "/" + ccEntity + "/search.xhtml"),
                  FreemarkerTemplateProcessor.processTemplate(context, this.searchTemplate)));

         // Generate navigation
         result.add(generateNavigation(targetDir));

         // Need ViewUtils and forge.taglib.xml for forgeview:asList
         JavaClassSource viewUtils = Roaster.parse(JavaClassSource.class,
                  FreemarkerTemplateProcessor.processTemplate(context, this.viewUtilsTemplate));
         viewUtils.setPackage(viewBean.getPackage());
         result.add(ScaffoldUtil.createOrOverwrite(java.getJavaResource(viewUtils), viewUtils.toString()));

         context.put("viewPackage", viewBean.getPackage());
         result.add(ScaffoldUtil.createOrOverwrite(
                  resources.getResource("META-INF/forge.taglib.xml"),
                  FreemarkerTemplateProcessor.processTemplate(context, this.taglibTemplate)));

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

   protected void createInitializers(final JavaClassSource entity) throws FacetNotFoundException, FileNotFoundException
   {
      boolean dirtyBit = false;
      for (FieldSource<JavaClassSource> field : entity.getFields())
      {
         if (field.hasAnnotation(OneToOne.class))
         {
            AnnotationSource<JavaClassSource> oneToOne = field.getAnnotation(OneToOne.class);
            if (oneToOne.getStringValue("mappedBy") == null && oneToOne.getStringValue("cascade") == null)
            {
               oneToOne.setEnumValue("cascade", CascadeType.ALL);
               dirtyBit = true;
            }
            String methodName = "new" + StringUtils.capitalize(field.getName());
            if (!entity.hasMethodSignature(methodName))
            {
               entity.addMethod().setName(methodName).setReturnTypeVoid().setPublic()
                        .setBody("this." + field.getName() + " = new " + field.getType().getName() + "();");
               dirtyBit = true;
            }
         }
      }
      for (MethodSource<JavaClassSource> method : entity.getMethods())
      {
         if (method.hasAnnotation(OneToOne.class))
         {
            AnnotationSource<JavaClassSource> oneToOne = method.getAnnotation(OneToOne.class);
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
                        .setBody("this." + fieldName + " = new " + method.getReturnType().getName() + "();");
               dirtyBit = true;
            }
         }
      }
      if (dirtyBit)
      {
         this.project.getFacet(JavaSourceFacet.class).saveJavaSource(entity);
      }
   }

   private void setPrimaryKeyMetaData(Map<Object, Object> context, final JavaClassSource entity)
   {
      String pkName = "id";
      String pkType = "Long";
      String nullablePkType = "Long";
      for (MemberSource<JavaClassSource, ?> m : entity.getMembers())
      {
         if (m.hasAnnotation(Id.class))
         {
            if (m instanceof Field)
            {
               Field<?> field = (Field<?>) m;
               pkName = field.getName();
               pkType = field.getType().getQualifiedName();
               nullablePkType = pkType;
               break;
            }

            MethodSource<?> method = (MethodSource<?>) m;
            pkName = method.getName().substring(3);
            if (method.getName().startsWith("get"))
            {
               pkType = method.getReturnType().getQualifiedName();
            }
            else
            {
               pkType = method.getParameters().get(0).getType().getQualifiedName();
            }
            nullablePkType = pkType;
            break;
         }
      }

      if (Types.isJavaLang(pkType))
      {
         nullablePkType = Types.toSimpleName(pkType);
      }
      else if ("int".equals(pkType))
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

   @SuppressWarnings({ "rawtypes", "unchecked" })
   private void createErrorPageEntry(WebAppCommonDescriptor servletConfig, String errorLocation, String errorCode)
   {
      List<ErrorPageCommonType> allErrorPage = servletConfig.getAllErrorPage();
      for (ErrorPageCommonType errorPageType : allErrorPage)
      {
         if (errorPageType.getErrorCode().equalsIgnoreCase(errorCode))
         {
            return;
         }
      }
      servletConfig.createErrorPage().errorCode(errorCode).location(errorLocation);
   }

   /**
    * @return the config
    */
   private Configuration getConfig()
   {
      return SimpleContainer.getServices(getClass().getClassLoader(), Configuration.class).get();
   }
}
