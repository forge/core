/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.faces.metawidget.inspector;

import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.N_TO_MANY;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.ONE_TO_ONE;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.PRIMARY_KEY;
import static org.jboss.forge.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.REVERSE_PRIMARY_KEY;
import static org.metawidget.inspector.InspectionResultConstants.LOOKUP;
import static org.metawidget.inspector.InspectionResultConstants.TRUE;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_CONVERTER_ID;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_LOOKUP;

import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyle.ForgeProperty;
import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
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
         extends BaseObjectInspector
{
   //
   // Constructor
   //

   public ForgeInspector()
   {
      super(new BaseObjectInspectorConfig());
   }

   public ForgeInspector(BaseObjectInspectorConfig config)
   {
      super(config);
   }

   //
   // Protected methods
   //

   @Override
   protected Map<String, String> inspectProperty(Property property)
            throws Exception
   {
      Map<String, String> attributes = CollectionUtils.newHashMap();

      // OneToOne

      if (property.isAnnotationPresent(OneToOne.class) || property.isAnnotationPresent(Embedded.class))
      {
         attributes.put(ONE_TO_ONE, TRUE);
      }

      // ManyToOne

      if (property.isAnnotationPresent(ManyToOne.class))
      {
         attributes
                  .put(FACES_LOOKUP,
                           StaticFacesUtils.wrapExpression(StringUtils.decapitalize(ClassUtils.getSimpleName(property
                                    .getType())) + "Bean.all"));

         attributes
                  .put(FACES_CONVERTER_ID,
                           StaticFacesUtils.wrapExpression(StringUtils.decapitalize(ClassUtils.getSimpleName(property
                                    .getType())) + "Bean.converter"));

         // Reverse primary key

         for (Property reverseProperty : getProperties(property.getType()).values())
         {
            if (reverseProperty.isAnnotationPresent(Id.class))
            {
               attributes.put(REVERSE_PRIMARY_KEY, reverseProperty.getName());
               break;
            }
         }
      }

      // OneToMany and ManyToMany

      if (property.isAnnotationPresent(OneToMany.class) || property.isAnnotationPresent(ManyToMany.class))
      {
         attributes.put(N_TO_MANY, TRUE);
      }

      // Enums

      if (property instanceof ForgeProperty)
      {
         List<EnumConstant<JavaEnum>> enumConstants = ((ForgeProperty) property).getEnumConstants();

         if (enumConstants != null)
         {
            List<String> lookup = CollectionUtils.newArrayList();

            for (EnumConstant<JavaEnum> anEnum : enumConstants)
            {
               lookup.add(anEnum.getName());
            }

            attributes.put(LOOKUP, CollectionUtils.toString(lookup));
         }
      }

      // Id

      if (property.isAnnotationPresent(Id.class))
      {
         attributes.put(PRIMARY_KEY, TRUE);
      }

      return attributes;
   }
}
