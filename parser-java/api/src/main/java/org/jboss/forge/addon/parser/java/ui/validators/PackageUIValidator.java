/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui.validators;

import org.jboss.forge.addon.parser.java.utils.JLSValidator;
import org.jboss.forge.addon.parser.java.utils.ValidationResult;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class PackageUIValidator extends AbstractJLSUIValidator
{

   @Override
   protected ValidationResult validate(String value)
   {
      return JLSValidator.validatePackageName(value);
   }
}
