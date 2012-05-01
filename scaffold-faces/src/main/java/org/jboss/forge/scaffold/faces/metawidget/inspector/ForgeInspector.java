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
package org.jboss.forge.scaffold.faces.metawidget.inspector;

import org.jboss.forge.scaffold.faces.util.AnnotationLookup;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.*;
import static org.metawidget.inspector.InspectionResultConstants.*;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.*;

import java.util.List;
import java.util.Map;

import javax.persistence.*;

import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyle.ForgeProperty;
import org.jboss.solder.logging.Logger;
import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.StringUtils;

/**
 * Inspects Forge-specific metadata.
 *
 * @author Richard Kennard
 */
public class ForgeInspector
        extends BaseObjectInspector {

   Logger log = Logger.getLogger(getClass());
   private AnnotationLookup annotationLookup;

   //
   // Constructor
   //

   public ForgeInspector(ForgeInspectorConfig config) {
      super(config);
      annotationLookup = config.getAnnotationLookup();
   }

   //
   // Protected methods
   //
   @Override
   protected Map<String, String> inspectProperty(Property property)
           throws Exception {
      Map<String, String> attributes = CollectionUtils.newHashMap();

      // OneToOne

      if (property.isAnnotationPresent(OneToOne.class) || property.isAnnotationPresent(Embedded.class)) {

         attributes.put(ONE_TO_ONE, TRUE);
      }

      // ManyToOne

      if (property.isAnnotationPresent(ManyToOne.class)) {
         attributes.put(FACES_LOOKUP,
                 StaticFacesUtils.wrapExpression(StringUtils.decapitalize(ClassUtils.getSimpleName(property.getType())) + "Bean.all"));

         attributes.put(FACES_CONVERTER_ID,
                 StaticFacesUtils.wrapExpression(StringUtils.decapitalize(ClassUtils.getSimpleName(property.getType())) + "Bean.converter"));
      }

      // OneToMany and ManyToMany

      if (property.isAnnotationPresent(OneToMany.class) || property.isAnnotationPresent(ManyToMany.class)) {
         attributes.put(N_TO_MANY, TRUE);
      }

      // Enums

      if (property instanceof ForgeProperty) {

         List<EnumConstant<JavaEnum>> enumConstants = ((ForgeProperty) property).getEnumConstants();

         if (enumConstants != null) {
            List<String> lookup = CollectionUtils.newArrayList();

            for (EnumConstant<JavaEnum> anEnum : enumConstants) {
               lookup.add(anEnum.getName());
            }

            attributes.put(LOOKUP, CollectionUtils.toString(lookup));
         }
      }

      // do @Id specific handling
      if (null != property.getAnnotation(Id.class)) {
         attributes.put(PRIMARY_KEY, property.getName());

         if (null != property.getAnnotation(GeneratedValue.class)) {
            attributes.put(PRIMARY_KEY_NOT_GENERATED, FALSE);
         } else {
            attributes.put(PRIMARY_KEY_NOT_GENERATED, TRUE);
         }
      }

      if (null != property.getAnnotation(ManyToOne.class)) {
         attributes.put(REVERSE_PRIMARY_KEY_TYPE, property.getType());
      }

      if (attributes.containsKey(PRIMARY_KEY) && !TRUE.equals(attributes.get(PRIMARY_KEY_NOT_GENERATED))) {
         // if primary key is not generated it cannot be hidden in view
         attributes.remove(HIDDEN);
         attributes.put(REQUIRED, TRUE);
      }

      if (null != annotationLookup) {
         if (attributes.containsKey(REVERSE_PRIMARY_KEY_TYPE) && null != annotationLookup) {
            try {
               final String reverseKey = annotationLookup.getFieldName(Id.class, attributes.get(REVERSE_PRIMARY_KEY_TYPE));
               attributes.put(REVERSE_PRIMARY_KEY, reverseKey);
            } catch (Exception e) {
               throw new RuntimeException("cannot resolve reverse primary key", e);
            }
         }
      }
      return attributes;
   }

   @Override
   protected Map<String, String> inspectEntity(String declaredClass, String actualClass) throws Exception {
      Map<String,String> attributes = CollectionUtils.newHashMap();
      Map<String,String> superMap = super.inspectEntity(declaredClass, actualClass);
      if (superMap != null)
         attributes.putAll(superMap);
      
      if (null != annotationLookup) {
         try {
            final String primaryKey = annotationLookup.getFieldName(Id.class, declaredClass);
            attributes.put(PRIMARY_KEY, primaryKey);
         } catch (Exception e) {
            log.debug("cannot resolve primary key for class "+declaredClass, e);
         }
      }
      return attributes;
   }
   
}
