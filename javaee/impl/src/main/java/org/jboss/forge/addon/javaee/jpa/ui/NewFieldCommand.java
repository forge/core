/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.jpa.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ui.AbstractProjectUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

public class NewFieldCommand extends AbstractProjectUICommand
{

   @Inject
   @WithAttributes(label = "Field Name:", required = true)
   private UIInput<String> fieldName;

   @Inject
   @WithAttributes(label = "Type:", type = InputType.JAVA_CLASS_PICKER)
   private UIInput<String> typeName;

   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata().name("JPA: New Field").description("Create a new field");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(fieldName).add(typeName);
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return Results.success("Field " + fieldName.getName() + " created");
   }

}