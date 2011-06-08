package org.jboss.forge.project.facets;

import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;

/**
 * A base convenience {@link Facet} abstract class.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public abstract class BaseFacet implements Facet
{
   protected Project project;

   @Override
   public Project getProject()
   {
      return this.project;
   }

   @Override
   public void setProject(final Project project)
   {
      this.project = project;
   }

   @Override
   public boolean uninstall()
   {
      return false;
   }

   /*
    * Facet instances are the same if they are registered to the same project.
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((project == null) ? 0 : project.hashCode());
      return result;
   }

   @Override
   public boolean equals(final Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      BaseFacet other = (BaseFacet) obj;
      if (project == null)
      {
         if (other.project != null)
            return false;
      }
      else if (!project.equals(other.project))
         return false;
      return true;
   }

}
