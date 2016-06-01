/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui;

import javax.validation.Payload;

import org.jboss.forge.addon.javaee.validation.ValidationFacet;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@StackConstraint(ValidationFacet.class)
public class ValidationNewPayloadCommandImpl extends AbstractValidationCommand<JavaClassSource>
         implements ValidationNewPayloadCommand
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
   protected boolean supportsExtends()
   {
      return false;
   }

   @Override
   protected boolean supportsImplements()
   {
      return false;
   }
   
   @Override
   public JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
            throws Exception
   {
      source.addInterface(Payload.class);
      return source;
   }
}
