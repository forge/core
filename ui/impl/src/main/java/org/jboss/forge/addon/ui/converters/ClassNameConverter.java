package org.jboss.forge.addon.ui.converters;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.types.JavaClassName;

public class ClassNameConverter implements Converter<String, JavaClassName>
{

   @Override
   public JavaClassName convert(String source)
   {
      JavaClassName className = new JavaClassName();
      className.setClassName(source);
      return className;
   }

}
