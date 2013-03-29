package org.jboss.forge.parser.java.binary;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.Assert;

import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.Annotation;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.JavaSource;
import org.junit.BeforeClass;
import org.junit.Test;

public class WrappedJavaClassTest {

	private static JavaSource<JavaClass> expected;
	private static JavaSource<JavaClass> actual;
	
	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void beforeClass() throws FileNotFoundException {
		File file = new File("src/test/java/" + asPath(WrappedJavaSource.class.getPackage()) + "TestClass.java");
		expected = (JavaSource<JavaClass>) JavaParser.parse(file);
		
		actual = new WrappedJavaClass(TestClass.class);
		
	}
	@Test
	public void testName() {
		Assert.assertEquals(expected.getName(), actual.getName());
	}
	
	@Test
	public void testAnnotations() {
		Assert.assertEquals(expected.getAnnotations().size(), actual.getAnnotations().size());
		Assert.assertEquals(3, actual.getAnnotations().size());
		assertEquals(expected, actual);
	}
	
	private void assertEquals(JavaSource<JavaClass> expected, JavaSource<JavaClass> actual) {
		contains(expected, actual);
		contains(actual, expected);

		for(Annotation<JavaClass> ea: expected.getAnnotations()) {
			for(Annotation<JavaClass> aa: actual.getAnnotations()) {
				if(ea.getQualifiedName().equals(aa.getQualifiedName())) {
					assertEquals(ea,aa);
				}
			}
		}

	}
	private void contains(JavaSource<JavaClass> xx, JavaSource<JavaClass> yy) {
		boolean contained = true;
		for(Annotation<JavaClass> a1: xx.getAnnotations()) {
			boolean found = false;
			for(Annotation<JavaClass> a2: yy.getAnnotations()) {
				if(a1.getQualifiedName().equals(a2.getQualifiedName())) {
					found = true;
				}
			}
			if(! found) {
				contained = false;
			}
		}
		Assert.assertTrue(contained);

	}
	private void assertEquals(Annotation<JavaClass> ea, Annotation<JavaClass> aa) {
		Assert.assertEquals(ea.getQualifiedName(), aa.getQualifiedName());
		Assert.assertEquals(ea.getLiteralValue(), aa.getLiteralValue());
		Assert.assertEquals(ea.getStringValue(), aa.getStringValue());
		Assert.assertEquals(ea.isMarker(), aa.isMarker());
		Assert.assertEquals(ea.isNormal(), ea.isNormal());
		Assert.assertEquals(ea.isSingleValue(), aa.isSingleValue());
	}
	
	
	
	private static String asPath(Package p) {
		return p.getName().replace(".", "/") + "/";
	}
}
