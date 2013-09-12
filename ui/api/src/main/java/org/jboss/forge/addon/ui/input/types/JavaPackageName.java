package org.jboss.forge.addon.ui.input.types;

public class JavaPackageName extends PatternedStringInput
{

   private String packageName;

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   @Override
   public String getValidatableValue()
   {
      return packageName;
   }

}
