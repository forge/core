/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.scaffold.faces.util;

import java.io.FileNotFoundException;

import org.jboss.forge.parser.java.Field;
import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.java.Member;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.metawidget.util.simple.StringUtils;

/**
 * utility for easy lookup of fields or properties in related entities of the domain model
 * @author Thomas Frühbeck
 */
public class AnnotationLookup {

    public static final String JAVA_EXTENSION = ".java";

    private Project project;

    public AnnotationLookup(Project project) {
       this.project = project;
    }

    /**
     * lookup the annotated member of the class
     * @param annotation
     * @param qualifiedType
     * @return
     * @throws FileNotFoundException
     */
    public  Member<?,?> lookup (Class<?> annotation, String qualifiedType) throws FileNotFoundException {
        JavaSourceFacet java = this.project.getFacet(JavaSourceFacet.class);
        JavaSource javaSource = java.getJavaResource(qualifiedType).getJavaSource();

        Member member = lookup(javaSource, annotation);
        return member;
    }

    /**
     * convert the member to a field name, assumes JavaBeans notation
     * @param member
     * @return
     */
    public String getFieldName(Member member) {
        if (null == member)
      {
         return null;
      }
        if (member instanceof Method) {
            String methodName = member.getName();
            return StringUtils.decapitalize(methodName.substring(3));
        } else if (member instanceof Field) {
            return member.getName();
        }
        return null;
    }

    /**
     * get the field name of the annotated member of the class
     * @param annotation
     * @param qualifiedType
     * @return
     * @throws FileNotFoundException
     */
    public String getFieldName (Class<?> annotation, String qualifiedType) throws FileNotFoundException {
       Member member = lookup(annotation, qualifiedType);
       return getFieldName(member);
    }

    /**
     * find a member annotated with this annotation.
     * @param javaSource
     * @param ann
     * @return
     */
    public Member lookup(JavaSource<? extends JavaSource> javaSource, Class ann) {
        //@TODO Is not prepared for multiple PrimKeys
        for (Member<? extends JavaSource,?> member : javaSource.getMembers()) {
            if (member.hasAnnotation(ann)) {
                return member;
            }
        }
        return null;
    }

}
