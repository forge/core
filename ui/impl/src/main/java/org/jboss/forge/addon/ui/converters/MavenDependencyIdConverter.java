package org.jboss.forge.addon.ui.converters;

import org.jboss.forge.addon.convert.Converter;
import org.jboss.forge.addon.ui.input.types.MavenDependencyId;

public class MavenDependencyIdConverter implements Converter<String, MavenDependencyId>
{

   @Override
   public MavenDependencyId convert(String source)
   {
      MavenDependencyId dependencyId = new MavenDependencyId();
      dependencyId.setDependencyId(source);
      return dependencyId;
   }

}
