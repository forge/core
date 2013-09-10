package org.jboss.forge.addon.ui.input.types;

public class JavaPackageName extends PatternedInput
{

   private String packageName;

   protected String getPackageName()
   {
      return packageName;
   }

   protected void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

}
