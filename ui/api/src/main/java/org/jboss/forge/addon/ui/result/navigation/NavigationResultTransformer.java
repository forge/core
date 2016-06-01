/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.result.navigation;

import org.jboss.forge.addon.ui.context.UINavigationContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * Implementations can enrich a wizard flow by adding steps after a command is navigated
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public interface NavigationResultTransformer
{
   /**
    * If this {@link NavigationResultTransformer} applies to the specified {@link UINavigationContext}
    * 
    * @param context the {@link UINavigationContext} to be used
    * @return <code>true</code> if {@link NavigationResultTransformer#transform(UINavigationContext, NavigationResult)}
    *         should be called
    */
   boolean handles(UINavigationContext context);

   /**
    * Transforms {@link NavigationResult} objects during a {@link UIWizard} navigation flow
    * 
    * @param context the current {@link UINavigationContext} for the given flow
    * @param original the current {@link NavigationResult} returned from the given flow
    * @return a {@link NavigationResult} object with the proposed navigation flow
    */
   NavigationResult transform(UINavigationContext context, NavigationResult original);

   /**
    * The priority for this {@link NavigationResultTransformer} compared to other instances.
    * 
    * The higher the priority, the sooner this {@link NavigationResultTransformer} will be called (ordered by priority
    * desc).
    */
   int priority();
}
