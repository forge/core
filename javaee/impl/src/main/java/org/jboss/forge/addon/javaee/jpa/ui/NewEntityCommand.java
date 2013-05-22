/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.jpa.ui;

import javax.inject.Inject;
import javax.persistence.GenerationType;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputTypes;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class NewEntityCommand implements UICommand
{
   @Inject
   @WithAttributes(label = "Entity named", required = true, requiredMessage = "Entity named must be specified.")
   private UIInput<String> named;

   @Inject
   @WithAttributes(label = "Target package", required = true, requiredMessage = "Target package must be specified.")
   private UIInput<String> targetPackage;

   @Inject
   @WithAttributes(label = "ID Column Generation Strategy", required = true, requiredMessage = "ID Column Generation Strategy must be specified.")
   private UISelectOne<GenerationType> idStrategy;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("JPA: New Entity").description("Create a new JPA Entity");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      targetPackage.getFacet(HintsFacet.class).setInputType(InputTypes.JAVA_PACKAGE_PICKER);
      idStrategy.setDefaultValue(GenerationType.AUTO);
      builder.add(named).add(targetPackage).add(idStrategy);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      // TODO Auto-generated method stub

   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

}
