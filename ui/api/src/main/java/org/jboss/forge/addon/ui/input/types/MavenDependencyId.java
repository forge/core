package org.jboss.forge.addon.ui.input.types;


public class MavenDependencyId extends PatternedInput
{

   private String dependencyId;

   protected String getDependencyId()
   {
      return dependencyId;
   }

   protected void setDependencyId(String dependencyId)
   {
      this.dependencyId = dependencyId;
   }

}
