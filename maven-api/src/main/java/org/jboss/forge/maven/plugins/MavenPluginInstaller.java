package org.jboss.forge.maven.plugins;

import org.jboss.forge.project.Project;

/**
 * Responsible for installing a given {@link MavenPlugin} into the current project. Resolves available plugins.
 * 
 * @author <a href="mailto:salmon_charles@gmail.com">charless</a>
 * 
 */
public interface MavenPluginInstaller
{
	   /**
	    * Install given {@link MavenPlugin}. 
	    */
      MavenPlugin install(Project project, MavenPlugin plugin );

	   /**
	    * Install given managed {@link MavenPlugin}. 
	    */
      MavenPlugin installManaged(Project project, MavenPlugin plugin);
      
	   /**
	    * Returns whether or not the given {@link MavenPlugin} is installed.
	    */
	   boolean isInstalled(Project project, MavenPlugin plugin);
	   
	   /**
	    * When installing a new plugin, should we merge it's definition with existing configuration ?
	    * Defaulted to true
	    */
	   public void setMergeWithExisting(boolean mergeWithExisting);
	   public boolean isMergeWithExisting();
	   
	   /**
       * When installing a new plugin, should we filter it's definition with existing hierarchy configuration ?
       * All properties having equivalent counterparts in the hierarchy (in plugin or plugin mamnagement sections of the parent)
       * will be removed from the new plugin definition (to preserve hierarchy precedence)
       * 
       * Defaulted to true
       */
	   public void setPreserveHierarchyPrecedence(boolean preserveHierarchyPrecedence);
	   public boolean isPreserveHierarchyPrecedence();
	   
	   
	}
