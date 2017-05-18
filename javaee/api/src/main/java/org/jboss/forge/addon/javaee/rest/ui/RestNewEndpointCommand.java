/*
(c) 2017 Christophe Laprun
*/
package org.jboss.forge.addon.javaee.rest.ui;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectMany;
import org.jboss.forge.roaster.model.source.JavaClassSource;

/**
 * @author Christophe Laprun
 */
public interface RestNewEndpointCommand extends UICommand {
   UISelectMany<RestMethod> getMethods();

   UIInput<String> getPath();

   JavaClassSource decorateSource(UIExecutionContext context, Project project, JavaClassSource source)
         throws Exception;
}
