package org.jboss.forge.addon.parser.java.ui.methods;

import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.jboss.forge.roaster.model.source.MethodSource;
import org.jboss.forge.roaster.model.source.PropertySource;

public interface GetSetMethodGenerator
{

   MethodSource<JavaClassSource> createAccessor(PropertySource<JavaClassSource> property);
   MethodSource<JavaClassSource> createMutator(PropertySource<JavaClassSource> property);
  
   boolean isCorrectAccessor(MethodSource<JavaClassSource> method, PropertySource<JavaClassSource> property);
   boolean isCorrectMutator(MethodSource<JavaClassSource> method, PropertySource<JavaClassSource> property);
}
