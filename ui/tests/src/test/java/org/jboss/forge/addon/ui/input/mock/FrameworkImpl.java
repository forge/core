/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input.mock;

import java.util.Arrays;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.input.AbstractUIInputManyDecorator;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class FrameworkImpl extends AbstractUIInputManyDecorator<String>implements Framework
{

   @Inject
   @WithAttributes(label = "Framework")
   private UIInputMany<String> framework;

   @Override
   protected UIInputMany<String> createDelegate()
   {
      framework.setValue(Arrays.asList("Java EE", "Furnace"));
      return framework;
   }

}
