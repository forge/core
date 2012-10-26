package org.jboss.forge.container.impl.modules.providers;

import java.util.HashSet;
import java.util.Set;

import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleSpec.Builder;

public class WeldClasspathSpec extends BaseModuleSpecProvider
{
   public static final ModuleIdentifier ID = ModuleIdentifier.create("org.jboss.weld");

   public static Set<String> paths = new HashSet<String>();

   static
   {
      paths.add("javax/enterprise/event");
      paths.add("javax/enterprise/inject");
      paths.add("javax/enterprise/inject/spi");

      paths.add("org/jboss/weld");
      paths.add("org/jboss/weld/bootstrap");
      paths.add("org/jboss/weld/environment");
      paths.add("org/jboss/weld/environment/se");
      paths.add("org/jboss/weld/environment/se/beans");
      paths.add("org/jboss/weld/environment/se/bindings");
      paths.add("org/jboss/weld/environment/se/contexts");
      paths.add("org/jboss/weld/environment/se/discovery");
      paths.add("org/jboss/weld/environment/se/discovery/url");
      paths.add("org/jboss/weld/environment/se/events");
      paths.add("org/jboss/weld/environment/se/threading");
   }

   @Override
   protected void configure(Builder builder)
   {
   }

   @Override
   protected ModuleIdentifier getId()
   {
      return ID;
   }

   @Override
   protected Set<String> getPaths()
   {
      return paths;
   }

}
