/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.results.navigation;

import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultBuilder;
import org.jboss.forge.addon.ui.result.navigation.NavigationResultTransformer;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class AddLastNameTransformer implements NavigationResultTransformer
{
   @Override
   public boolean handles(UINavigationContext context)
   {
      return context.getCurrentCommand() instanceof FirstNameWizard;
   }

   @Override
   public NavigationResult transform(UINavigationContext context, NavigationResult original)
   {
      return NavigationResultBuilder.create(original).add(LastNameCommand.class).build();
   }

   @Override
   public int priority()
   {
      return 100;
   }

}
