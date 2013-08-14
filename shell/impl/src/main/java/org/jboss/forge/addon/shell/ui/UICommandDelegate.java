/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.ui;

import javax.enterprise.inject.Vetoed;

import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIValidationContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
@Vetoed
public class UICommandDelegate implements UICommand
{

   private final UICommand delegate;
   private final UICommandMetadata metadata;

   public UICommandDelegate(UICommand delegate)
   {
      this.delegate = delegate;
      this.metadata = Metadata.from(delegate.getMetadata()).name(shellifyName(delegate.getMetadata().getName()));
   }

   @Override
   public UICommandMetadata getMetadata()
   {
      return metadata;
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return delegate.isEnabled(context);
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      delegate.initializeUI(builder);
   }

   @Override
   public void validate(UIValidationContext validator)
   {
      delegate.validate(validator);
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return delegate.execute(context);
   }

   private String shellifyName(String name)
   {
      return name.trim().toLowerCase().replaceAll("\\W+", "-").replaceAll("\\:", "");
   }
}
