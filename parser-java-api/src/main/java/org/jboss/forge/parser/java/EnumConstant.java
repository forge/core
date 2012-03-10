package org.jboss.forge.parser.java;

import org.jboss.forge.parser.Internal;
import org.jboss.forge.parser.Origin;

public interface EnumConstant<O extends JavaSource<O>> extends Internal, Origin<O>
{
   /**
    * Get this enum constant name.
    */
   String getName();
   
   /**
    * Set this enum constant name.
    */
   EnumConstant<O> setName(String name);
}
