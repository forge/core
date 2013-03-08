package org.jboss.forge.parser.java.binary;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
 
@Retention(RetentionPolicy.RUNTIME)
public @interface MyNormalAnnotation {
    String value();
	String other();
}