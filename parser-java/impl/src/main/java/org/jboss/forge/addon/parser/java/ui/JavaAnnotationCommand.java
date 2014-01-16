/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.ui;

import org.jboss.forge.parser.java.JavaAnnotation;
import org.jboss.forge.parser.java.JavaSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaAnnotationCommand extends AbstractJavaSourceCommand
{
   @Override
   protected Class<? extends JavaSource<?>> getSourceType()
   {
      return JavaAnnotation.class;
   }

   @Override
   protected String getType()
   {
      return "Annotation";
   }
}
