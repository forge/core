package org.jboss.forge.parser.java.binary;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;

import junit.framework.Assert;

import org.jboss.forge.parser.java.JavaClass;
import org.junit.Test;

/**
 * 
 * @author jfraney
 *
 */


public class ClassWrapperTest {

	@Test
	public void testClassAccess() {
		JavaClass c = w(TestClass.class);
		Assert.assertEquals(TestClass.class.getName(), c.getQualifiedName());
		Assert.assertEquals(TestClass.class.getSimpleName(), c.getName());
		Assert.assertTrue(c.hasAnnotation(XmlAccessorType.class));
	}
	
	private abstract class TestPrivateAbstractClass {
		
	}
	public class TestPublicNonAbstractClass {
		
	}
	protected class TestProtectedConcreteClass {
		
	}
	
	@Test
	public void testEqualsHashcode() {
		JavaClass w1 = w(TestClass.class);
		JavaClass w2 = w(TestClass.class);
		Assert.assertEquals(w1, w2);
		Assert.assertEquals(w1.hashCode(), w2.hashCode());
	}
	
	@Test
	public void testAbstract() {
		Assert.assertTrue(w(TestPrivateAbstractClass.class).isAbstract());
		Assert.assertFalse(w(TestPublicNonAbstractClass.class).isAbstract());
	}
	
	@Test
	public void testIsAnnotation() {
		Assert.assertFalse(w(TestPrivateAbstractClass.class).isAnnotation());
		Assert.assertTrue(w(XmlAccessorType.class).isAnnotation());
	}
	
	@Test
	public void testIsClass() {
		Assert.assertFalse(w(JavaClass.class).isClass());
		Assert.assertTrue(w(TestPrivateAbstractClass.class).isClass());
	}
	
	@Test
	public void testIsDefaultPackage() {
		Assert.assertFalse(w(JavaClass.class).isDefaultPackage());
		// TODO: need a class in default package
	}
	
	public enum Status { ON, OFF};
	
	@Test
	public void testIsEnum() {
		Assert.assertFalse(w(JavaClass.class).isEnum());
		Assert.assertTrue(w(Status.class).isEnum());
	}
	
	@Test
	public void testIsInterface() {
		Assert.assertFalse(w(TestClass.class).isInterface());
		Assert.assertTrue(w(JavaClass.class).isInterface());
	}
	
	@Test
	public void testIsPrivate() {
		Assert.assertFalse(w(TestClass.class).isPrivate());
		Assert.assertTrue(w(TestPrivateAbstractClass.class).isPrivate());
	}
	@Test
	public void testIsPublic() {
		Assert.assertFalse(w(TestPrivateAbstractClass.class).isPublic());
		Assert.assertTrue(w(TestClass.class).isPublic());
	}
	@Test
	public void testIsProtected() {
		Assert.assertFalse(w(TestPrivateAbstractClass.class).isProtected());
		Assert.assertTrue(w(TestProtectedConcreteClass.class).isProtected());
	}

	@Test
	public void testGetAnnotation() {
		JavaClass w = w(TestClass.class);
		Assert.assertNull(w.getAnnotation(XmlTransient.class));
		Assert.assertNull(w.getAnnotation(XmlTransient.class.getName()));
		Assert.assertNotNull(w.getAnnotation(XmlAccessorType.class));
		Assert.assertNotNull(w.getAnnotation(XmlAccessorType.class.getName()));
		Assert.assertEquals(1, w.getAnnotations().size());
	}
	
	@Test
	public void testGetEnclosingTYpe() {
		JavaClass enclosing = w(this.getClass());
		Assert.assertEquals(enclosing, w(TestPublicNonAbstractClass.class).getEnclosingType());
		Assert.assertEquals(enclosing, enclosing.getEnclosingType());
	}
	
	@Test
	public void testGetField() {
		JavaClass w = w(TestClass.class);
		Assert.assertNull(w.getField("noSuchField"));
		Assert.assertFalse(w.hasField("noSuchField"));
		Assert.assertNotNull(w.getField("existingField"));
		Assert.assertTrue(w.hasField("existingField"));
		Assert.assertEquals(1, w.getFields().size());
	}
	@Test
	public void testGetMethod() {
		JavaClass w = w(TestClass.class);
		Assert.assertNull(w.getMethod("noSuchMethod"));
		Assert.assertFalse(w.hasMethodSignature("noSuchMethod"));
		Assert.assertNotNull(w.getMethod("existingMethod"));
		Assert.assertTrue(w.hasMethodSignature("existingMethod"));
		Assert.assertTrue(w.hasMethodSignature("existingMethod", Integer.class));
		Assert.assertEquals(2, w.getMethods().size());
	}
	
	
	public class ClassWithInterfaces implements Cloneable, Comparable {
		@Override
		public int compareTo(Object o) {
			return 0;
		}
	}
	
	@Test
	public void testGetInterfaces() {
		
		JavaClass w = w(ClassWithInterfaces.class);
		Assert.assertEquals(2, w.getInterfaces().size());
	}
	
	private JavaClass w(Class<?> clzz) {
		return new WrappedJavaClass(clzz);
	}
	
	
	public void junk () {
		JavaClass c = null;
		c.getInterfaces();
		c.getMembers();
	}
	
}
