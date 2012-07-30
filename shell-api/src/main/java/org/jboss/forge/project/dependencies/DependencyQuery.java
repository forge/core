package org.jboss.forge.project.dependencies;

import java.util.List;

/**
 * A parameter object which is used to search dependencies
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 *
 */
public interface DependencyQuery
{

   public abstract Dependency getDependency();

   public abstract List<DependencyRepository> getDependencyRepositories();

   public abstract DependencyFilter getDependencyFilter();

}