package org.jboss.forge.parser.java.binary;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;
import javax.xml.bind.annotation.XmlAccessorType;

@MyMarkerAnnotation
@MySingleAnnotation(FIELD)
@MyNormalAnnotation(value="single", other="double")
public class TestClass {
	int existingField;
	
	int existingMethod() {
		return 0;
	}
	int existingMethod(Integer i) {
		return i;
	}
}


