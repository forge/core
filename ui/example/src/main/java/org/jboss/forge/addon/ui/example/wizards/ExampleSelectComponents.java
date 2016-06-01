/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.example.wizards;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;

public class ExampleSelectComponents implements UIWizardStep
{

   @Inject
   private UISelectOne<Bean> radioBean;

   @Inject
   private UISelectOne<Bean> radioBeanTwo;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Step 2").description("Select a folder");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      radioBean.getFacet(HintsFacet.class).setInputType(InputType.RADIO);
      radioBeanTwo.getFacet(HintsFacet.class).setInputType(InputType.RADIO);
      radioBean.setValueChoices(Arrays.asList(new Bean("One"), new Bean("Two"), new Bean("Three")));
      radioBeanTwo.setValueChoices(Arrays.asList(new Bean("A"), new Bean("B"), new Bean("C")));

      Converter<Bean, String> converter = new Converter<Bean, String>()
      {
         @Override
         public String convert(Bean source)
         {
            return source.getName();
         }
      };
      radioBean.setItemLabelConverter(converter);
      radioBeanTwo.setItemLabelConverter(converter);
      builder.add(radioBean).add(radioBeanTwo);
   }

   @Override
   public void validate(UIValidationContext context)
   {
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      return Results.success();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      UISelection<?> selection = context.getInitialSelection();
      return !selection.isEmpty();
   }

   @Override
   public NavigationResult next(UINavigationContext context) throws Exception
   {
      return null;
   }
}
