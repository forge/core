/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.projects.shell;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.spi.command.CdTokenHandler;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 */
public class ProjectRootCdTokenHandler implements CdTokenHandler
{
   @Override
   public List<Resource<?>> getNewCurrentResources(UIContext current, String token)
   {
      List<Resource<?>> result = new ArrayList<>();
      if ("~~".equals(token))
      {
         UISelection<Object> selection = current.getInitialSelection();
         if (!selection.isEmpty())
         {
            Object object = selection.get();
            if (object instanceof Resource<?>)
            {
               Resource<?> resource = (Resource<?>) object;
               while (!(resource instanceof FileResource<?>))
               {
                  resource = resource.getParent();

                  if (resource == null)
                     break;
               }

               if (resource instanceof FileResource<?>)
               {
                  ProjectFactory factory = SimpleContainer
                           .getServices(getClass().getClassLoader(), ProjectFactory.class).get();
                  Project project = factory.findProject(resource);
                  if (project != null)
                  {
                     result.add(project.getRoot());
                  }
               }
            }
         }
      }
      return result;
   }

}
