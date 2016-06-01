/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.ui.input.mock;

import javax.inject.Inject;

import org.jboss.forge.addon.ui.impl.mock.Gender;
import org.jboss.forge.addon.ui.input.AbstractUISelectOneDecorator;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class GenderInputImpl extends AbstractUISelectOneDecorator<Gender>implements GenderInput
{

   @Inject
   @WithAttributes(label = "Gender", required = true)
   private UISelectOne<Gender> gender;

   @Override
   protected UISelectOne<Gender> createDelegate()
   {
      return gender;
   }

}
