/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation.ui;

import org.jboss.forge.addon.javaee.validation.ValidationFacet;
import org.jboss.forge.addon.projects.stacks.annotations.StackConstraint;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.roaster.model.source.JavaInterfaceSource;

/**
 *
 * @author <a href="antonio.goncalves@gmail.com">Antonio Goncalves</a>
 */
@StackConstraint(ValidationFacet.class)
public class ValidationNewGroupCommandImpl extends AbstractValidationCommand<JavaInterfaceSource> implements
         ValidationNewGroupCommand
{

   @Override
   public Metadata getMetadata(UIContext context)
   {
      return Metadata.from(super.getMetadata(context), getClass())
               .name("Constraint: New Group")
               .description("Create a Bean Validation group");
   }

   @Override
   protected String getType()
   {
      return "Constraint Group";
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
   protected Class<JavaInterfaceSource> getSourceType()
   {
      return JavaInterfaceSource.class;
   }
}
