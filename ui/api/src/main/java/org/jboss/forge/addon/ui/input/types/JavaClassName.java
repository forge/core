package org.jboss.forge.addon.ui.input.types;

public class JavaClassName extends PatternedStringInput
{

   private String className;

   public String getClassName()
   {
      return className;
   }

   public void setClassName(String className)
   {
      this.className = className;
   }

   @Override
   public String getValidatableValue()
   {
      return className;
   }

}
