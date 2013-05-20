package org.jboss.forge.furnace.util;

import org.jboss.forge.furnace.services.Exported;
import org.jboss.forge.furnace.util.Annotations;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class AnnotationsTest
{
   @Test
   public void testInheritFromInterface() throws Exception
   {
      Assert.assertTrue(Annotations.isAnnotationPresent(InheritsRemote.class, Exported.class));
   }

   @Test
   public void testInheritFromInterfaceInheritingRemote() throws Exception
   {
      Assert.assertTrue(Annotations.isAnnotationPresent(InheritsRemoteFromExtendedInterface.class, Exported.class));
   }

   @Test
   public void testInheritFromSuperclass() throws Exception
   {
      Assert.assertTrue(Annotations.isAnnotationPresent(SuperClassAnnotatedWithRemote.class, Exported.class));
   }

   @Test
   public void testInheritFromSuperclassInheritingRemote() throws Exception
   {
      Assert.assertTrue(Annotations.isAnnotationPresent(InheritsRemoteFromSuperClassInheriting.class, Exported.class));
   }

   public class InheritsRemote implements AnnotatedWithRemote
   {

   }

   @Exported
   public interface AnnotatedWithRemote
   {

   }

   public class InheritsRemoteFromExtendedInterface implements ExtendsRemoteInterface
   {

   }

   public interface ExtendsRemoteInterface extends AnnotatedWithRemote
   {

   }

   @Exported
   public class SuperClassAnnotatedWithRemote
   {

   }

   public class InheritsRemoteFromSuperClassInheriting extends SuperClassInheritsFromInterface
   {

   }

   public class SuperClassInheritsFromInterface implements AnnotatedWithRemote
   {

   }
}