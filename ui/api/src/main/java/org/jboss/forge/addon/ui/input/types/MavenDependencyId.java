package org.jboss.forge.addon.ui.input.types;


public class MavenDependencyId extends PatternedStringInput
{

   private String dependencyId;

   public String getDependencyId()
   {
      return dependencyId;
   }

   public void setDependencyId(String dependencyId)
   {
      this.dependencyId = dependencyId;
   }

   @Override
   public String getValidatableValue()
   {
      return dependencyId;
   }

}
