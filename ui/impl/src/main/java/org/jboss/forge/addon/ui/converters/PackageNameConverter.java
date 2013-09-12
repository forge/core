package org.jboss.forge.addon.ui.converters;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.types.JavaPackageName;

public class PackageNameConverter implements Converter<String, JavaPackageName>
{

   @Override
   public JavaPackageName convert(String source)
   {
      JavaPackageName packageName = new JavaPackageName();
      packageName.setPackageName(source);
      return packageName;
   }

}
