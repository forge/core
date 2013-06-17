package org.jboss.forge.scaffold.faces;

/**
 * Used to test scaffold generation of an immutable class - no setters only getters
 */

public class ImmutableClass
{
   //
   // Field with Getter
   //
   
   private String normalField;
   
   public String getNormalField()
   {
      return this.normalField;
   }

}
