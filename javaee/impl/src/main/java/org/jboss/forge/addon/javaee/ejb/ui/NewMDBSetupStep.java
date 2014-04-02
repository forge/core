/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.ejb.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.javaee.ejb.EJBOperations;
import org.jboss.forge.addon.javaee.ejb.JMSDestinationType;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class NewMDBSetupStep implements UIWizardStep
{

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("MDB Configuration")
               .description("Specify Message Driven Bean attributes for new EJB.")
               .category(Categories.create("EJB", "Message Driven Beans"));
   }

   @Inject
   EJBOperations operations;

   @Inject
   @WithAttributes(label = "JMS Destination Type", required = true)
   private UISelectOne<JMSDestinationType> destType;

   @Inject
   @WithAttributes(label = "JMS Destination Name", required = true)
   private UIInput<String> destName;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      destType.setDefaultValue(JMSDestinationType.QUEUE);

      builder.add(destName).add(destType);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      JavaResource ejbResource = (JavaResource) context.getUIContext().getAttributeMap().get(JavaResource.class);
      JavaClassSource ejb = operations.setupMessageDrivenBean((JavaClassSource) ejbResource.getJavaType(),
               destType.getValue(),
               destName.getValue());
      ejbResource.setContents(ejb);
      return Results.success("Configured Message Driven EJB.");
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return true;
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }
}
