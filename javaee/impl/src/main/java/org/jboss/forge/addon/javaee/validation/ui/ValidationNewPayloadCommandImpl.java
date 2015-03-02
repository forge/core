package org.jboss.forge.addon.javaee.validation.ui;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.validation.Payload;

/**
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
public class ValidationNewPayloadCommandImpl extends AbstractValidationCommand<JavaClassSource> implements ValidationNewPayloadCommand
{
   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Constraint: New Payload")
               .description("Create a Bean Validation payload");
   }

   @Override
   protected String getType()
   {
      return "Constraint Payload";
   }

   @Override
   protected Class<JavaClassSource> getSourceType()
   {
      return JavaClassSource.class;
   }

    @Override
    public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source) throws Exception {
        source.addInterface(Payload.class);
        return source;
    }
}
