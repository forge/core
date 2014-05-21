/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.parser.java.ui;

import org.jboss.forge.roaster.model.source.JavaAnnotationSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class JavaAnnotationCommandImpl extends AbstractJavaSourceCommand<JavaAnnotationSource> implements JavaAnnotationCommand
{
   @Override
   protected Class<JavaAnnotationSource> getSourceType()
   {
      return JavaAnnotationSource.class;
   }

   @Override
   protected String getType()
   {
      return "Annotation";
   }
}
