/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.ui.example.wizards;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.forge.convert.Converter;
import org.jboss.forge.ui.context.UIBuilder;
import org.jboss.forge.ui.context.UIContext;
import org.jboss.forge.ui.context.UISelection;
import org.jboss.forge.ui.context.UIValidationContext;
import org.jboss.forge.ui.facets.HintsFacet;
import org.jboss.forge.ui.hints.InputTypes;
import org.jboss.forge.ui.input.UISelectOne;
import org.jboss.forge.ui.metadata.UICommandMetadata;
import org.jboss.forge.ui.result.NavigationResult;
import org.jboss.forge.ui.result.Result;
import org.jboss.forge.ui.result.Results;
import org.jboss.forge.ui.util.Metadata;
import org.jboss.forge.ui.wizard.UIWizardStep;

public class ExampleSelectComponents implements UIWizardStep
{

   @Inject
   private UISelectOne<Bean> radioBean;

   @Inject
   private UISelectOne<Bean> radioBeanTwo;

   @Override
   public UICommandMetadata getMetadata()
   {
      return Metadata.forCommand(getClass()).name("Step 2").description("Select a folder");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      radioBean.getFacet(HintsFacet.class).setInputType(InputTypes.SELECT_ONE_RADIO);
      radioBeanTwo.getFacet(HintsFacet.class).setInputType(InputTypes.SELECT_ONE_RADIO);
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
   public Result execute(UIContext context) throws Exception
   {
      return Results.success();
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      UISelection<?> selection = context.getInitialSelection();
      return selection != null;
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }
}
