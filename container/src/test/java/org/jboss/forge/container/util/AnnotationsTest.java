package org.jboss.forge.container.util;

import org.jboss.forge.container.services.Remote;
import org.jboss.forge.container.util.Annotations;
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
      Assert.assertTrue(Annotations.isAnnotationPresent(InheritsRemote.class, Remote.class));
   }

   @Test
   public void testInheritFromInterfaceInheritingRemote() throws Exception
   {
      Assert.assertTrue(Annotations.isAnnotationPresent(InheritsRemoteFromExtendedInterface.class, Remote.class));
   }

   @Test
   public void testInheritFromSuperclass() throws Exception
   {
      Assert.assertTrue(Annotations.isAnnotationPresent(SuperClassAnnotatedWithRemote.class, Remote.class));
   }

   @Test
   public void testInheritFromSuperclassInheritingRemote() throws Exception
   {
      Assert.assertTrue(Annotations.isAnnotationPresent(InheritsRemoteFromSuperClassInheriting.class, Remote.class));
   }

   public class InheritsRemote implements AnnotatedWithRemote
   {

   }

   @Remote
   public interface AnnotatedWithRemote
   {

   }

   public class InheritsRemoteFromExtendedInterface implements ExtendsRemoteInterface
   {

   }

   public interface ExtendsRemoteInterface extends AnnotatedWithRemote
   {

   }

   @Remote
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