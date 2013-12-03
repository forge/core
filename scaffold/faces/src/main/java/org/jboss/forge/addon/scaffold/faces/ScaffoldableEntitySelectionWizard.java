package org.jboss.forge.addon.scaffold.faces;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.javaee.jpa.JPAFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.scaffold.spi.ResourceCollection;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.shrinkwrap.descriptor.api.persistence.PersistenceCommonDescriptor;

public class ScaffoldableEntitySelectionWizard implements UIWizardStep
{
   @Inject
   @WithAttributes(label = "Targets", required = true)
   private UISelectMany<JavaClass> targets;
   
   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      ResourceCollection resourceCollection = new ResourceCollection();
      for (JavaClass klass : targets.getValue())
      {
         resourceCollection.addToCollection(klass);
      }
      context.setAttribute(ResourceCollection.class, resourceCollection );
      return null;
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Select JPA entities").description("Select the JPA entities to be used for scaffolding.");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      Project project = (Project) context.getAttribute(Project.class);
      JPAFacet<PersistenceCommonDescriptor> persistenceFacet = project.getFacet(JPAFacet.class);
      JavaSourceFacet javaSourceFacet = project.getFacet(JavaSourceFacet.class);
      targets.setValueChoices(persistenceFacet.getAllEntities());
      targets.setItemLabelConverter(new Converter<JavaClass, String>()
      {
         @Override
         public String convert(JavaClass source)
         {
            return source == null ? null : source.getQualifiedName();
         }
      });
      builder.add(targets);
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return null;
   }

   @Override
   public void validate(UIValidationContext context)
   {
      
   }

}
