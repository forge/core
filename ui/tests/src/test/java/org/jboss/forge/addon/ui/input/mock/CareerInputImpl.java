/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input.mock;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.impl.mock.Career;
import org.jboss.forge.addon.ui.input.AbstractUISelectManyDecorator;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class CareerInputImpl extends AbstractUISelectManyDecorator<Career>implements CareerInput
{
   @Inject
   @WithAttributes(label = "Careers", required = true)
   private UISelectMany<Career> careers;

   @Override
   protected UISelectMany<Career> createDelegate()
   {
      return careers;
   }
}
