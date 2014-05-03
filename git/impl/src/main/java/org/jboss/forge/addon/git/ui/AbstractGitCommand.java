package org.jboss.forge.addon.git.ui;

import static org.jboss.forge.addon.git.constants.GitConstants.GITIGNORE;

import javax.inject.Inject;

import org.jboss.forge.addon.git.gitignore.resources.GitIgnoreResource;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

abstract class AbstractGitCommand extends AbstractProjectCommand
{

   @Inject
   private ProjectFactory projectFactory;

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).category(Categories.create("SCM / GIT"));
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

   protected GitIgnoreResource gitIgnoreResource(UIContext context)
   {
      GitIgnoreResource resource = getSelectedProject(context).getRootDirectory().getChildOfType(
               GitIgnoreResource.class,
               GITIGNORE);
      if (resource == null || !resource.exists())
      {
         resource.createNewFile();
      }
      return resource;
   }

   protected boolean isGitIgnoreSelected(UIContext context)
   {
      return context.getInitialSelection().get() instanceof GitIgnoreResource;
   }
}
