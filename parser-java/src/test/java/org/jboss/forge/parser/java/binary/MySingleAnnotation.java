package org.jboss.forge.parser.java.binary;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.xml.bind.annotation.XmlAccessType;

@Retention(RetentionPolicy.RUNTIME)
public @interface MySingleAnnotation {
    XmlAccessType value();
}