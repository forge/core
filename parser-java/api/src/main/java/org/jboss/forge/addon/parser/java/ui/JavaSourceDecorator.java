/*
  Copyright 2017 Red Hat, Inc. and/or its affiliates.

  Licensed under the Eclipse Public License version 1.0, available at
  http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.parser.java.ui;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.roaster.model.source.JavaSource;

/**
 * A decorator facility to externalize code generation in the context of a command working on Java sources. Serves the
 * same purpose as {@link AbstractJavaSourceCommand#decorateSource(UIExecutionContext, Project, JavaSource)}.
 *
 * @author <a href="claprun@redhat.com">Christophe Laprun</a>
 */
@FunctionalInterface
public interface JavaSourceDecorator<T extends JavaSource<?>>
{
   /**
    * Customizes the specified source and returns it.
    *
    * @param context the {@link UIExecutionContext} in which the decorator is called
    * @param project the associated {@link Project}
    * @param source the source to be customized, a sub-class of {@link JavaSource}
    * @return the modified source
    * @throws Exception if anything unexpected happened during the processing of the source
    */
   T decorateSource(UIExecutionContext context, Project project, T source) throws Exception;
}
