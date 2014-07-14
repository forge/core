package org.jboss.forge.addon.scaffold.faces;

import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.Projects;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.util.ResourceUtil;
import org.jboss.forge.addon.scaffold.spi.ResourceCollection;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.util.Refactory;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;

public class ScaffoldableEntitySelectionWizard implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "Facelet Template", description = "The Facelets template file to be used in the generated facelets.")
   private UIInput<FileResource<?>> pageTemplate;

   @Inject
   @WithAttributes(label = "Targets", required = true, description = "The JPA entities to use as the basis for generating the scaffold.")
   private UISelectMany<JavaClassSource> targets;

   @Inject
   @WithAttributes(label = "Use custom template when generating pages", required = false, description = "Enabling this will allow the generated scaffold to use the specified Facelet template.")
   private UIInput<Boolean> useCustomTemplate;

   @Inject
   @WithAttributes(label = "Generate missing .equals() and .hashCode() methods", required = false, description = "If enabled, entities missing an .equals() or .hashCode() method will be updated to provide them")
   private UIInput<Boolean> generateEqualsAndHashCode;

   @Inject
   private ProjectFactory projectFactory;

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      UIContext uiContext = context.getUIContext();
      Map<Object, Object> attributeMap = uiContext.getAttributeMap();
      ResourceCollection resourceCollection = new ResourceCollection();
      if (targets.getValue() != null)
      {
         for (JavaClassSource klass : targets.getValue())
         {
            Project project = getSelectedProject(uiContext);
            JavaSourceFacet javaSource = project.getFacet(JavaSourceFacet.class);
            Resource<?> resource = javaSource.getJavaResource(klass);
            if (resource != null)
            {
               resourceCollection.addToCollection(resource);
            }
         }
      }

      attributeMap.put(ResourceCollection.class, resourceCollection);
      ScaffoldGenerationContext genCtx = (ScaffoldGenerationContext) attributeMap.get(ScaffoldGenerationContext.class);
      if (uiContext.getProvider().isGUI())
      {
         if (useCustomTemplate.getValue())
         {
            genCtx.addAttribute("pageTemplate", pageTemplate.getValue());
         }
      }
      else
      {
         genCtx.addAttribute("pageTemplate", pageTemplate.getValue());
      }
      return null;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Select JPA entities")
               .description("Select the JPA entities to be used for scaffolding.");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   @SuppressWarnings({ "unchecked", "rawtypes" })
   public void initializeUI(final UIBuilder builder) throws Exception
   {
      UIContext uiContext = builder.getUIContext();
      Project project = getSelectedProject(uiContext);

      JPAFacet<PersistenceCommonDescriptor> persistenceFacet = project.getFacet(JPAFacet.class);
      targets.setValueChoices(persistenceFacet.getAllEntities());
      targets.setItemLabelConverter(new Converter<JavaClassSource, String>()
      {
         @Override
         public String convert(JavaClassSource source)
         {
            return source == null ? null : source.getQualifiedName();
         }
      });

      builder.add(targets);
      if (uiContext.getProvider().isGUI())
      {
         useCustomTemplate.setDefaultValue(false);
         pageTemplate.setEnabled(new Callable<Boolean>()
         {
            @Override
            public Boolean call() throws Exception
            {
               return useCustomTemplate.getValue();
            }
         });
         builder.add(useCustomTemplate).add(pageTemplate);
      }
      else
      {
         builder.add(pageTemplate);
      }
      generateEqualsAndHashCode.setEnabled(new Callable<Boolean>()
      {
         @Override
         public Boolean call() throws Exception
         {
            for (JavaClassSource javaSource : targets.getValue())
            {
               if (!javaSource.hasMethodSignature("hashCode") || !javaSource.hasMethodSignature("equals", Object.class))
               {
                  return true;
               }
            }
            return false;
         }
      });
      builder.add(generateEqualsAndHashCode);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      for (JavaClassSource javaSource : targets.getValue())
      {
         UIContext uiContext = context.getUIContext();
         Project project = getSelectedProject(uiContext);
         JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
         if (!javaSource.hasMethodSignature("hashCode"))
         {
            if (generateEqualsAndHashCode.getValue())
            {
               if (javaSource.getField("id") != null)
               {
                  Refactory.createHashCode(javaSource, javaSource.getField("id"));
               }
               else
               {
                  Refactory.createHashCode(javaSource, (FieldSource<?>[]) javaSource.getFields().toArray(new FieldSource[javaSource.getFields().size()]));
               }
              
            }
         }
         
         if(!javaSource.hasMethodSignature("equals", Object.class)) {
            if (generateEqualsAndHashCode.getValue())
            {
               if (javaSource.getField("id") != null)
               {
                  Refactory.createEquals(javaSource, javaSource.getField("id"));
               }
               else
               {
                  Refactory.createEquals(javaSource, (FieldSource<?>[]) javaSource.getFields().toArray(new FieldSource[javaSource.getFields().size()]));
               }
            }
         }
         javaSourceFacet.saveJavaSource(javaSource);
         
         
      }

      return null;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      UIContext uiContext = context.getUIContext();
      if (uiContext.getProvider().isGUI())
      {
         boolean useTemplate = useCustomTemplate.getValue();
         if (useTemplate)
         {
            validateTemplate(context);
         }
      }
      else
      {
         validateTemplate(context);
      }
   }

   private void validateTemplate(UIValidationContext context)
   {
      Resource<?> template = pageTemplate.getValue();
      if (template != null)
      {
         if (template.exists())
         {
            Map<Object, Object> attributeMap = context.getUIContext().getAttributeMap();
            Project project = (Project) attributeMap.get(Project.class);
            WebResourcesFacet web = project.getFacet(WebResourcesFacet.class);
            boolean isValidTemplate = false;
            for (DirectoryResource dir : web.getWebRootDirectories())
            {
               if (ResourceUtil.isChildOf(dir, template))
               {
                  isValidTemplate = true;
               }
            }
            if (!isValidTemplate)
            {
               context.addValidationError(pageTemplate, "Not a valid template resource. "
                        + "The template should be located under a web root directory for the project.");
            }
         }
         else
         {
            context.addValidationError(pageTemplate, "The template [" + template.getName()
                     + "] does not exist. You must select a template that exists.");
         }
      }
   }

   private Project getSelectedProject(UIContext uiContext)
   {
      return Projects.getSelectedProject(projectFactory, uiContext);
   }

}
