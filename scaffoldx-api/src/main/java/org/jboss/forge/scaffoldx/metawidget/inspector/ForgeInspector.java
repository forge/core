/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.metawidget.inspector;

import static org.jboss.forge.scaffoldx.metawidget.inspector.ForgeInspectionResultConstants.MANY_TO_ONE;
import static org.jboss.forge.scaffoldx.metawidget.inspector.ForgeInspectionResultConstants.N_TO_MANY;
import static org.jboss.forge.scaffoldx.metawidget.inspector.ForgeInspectionResultConstants.ONE_TO_ONE;
import static org.jboss.forge.scaffoldx.metawidget.inspector.ForgeInspectionResultConstants.PRIMARY_KEY;
import static org.jboss.forge.scaffoldx.metawidget.inspector.ForgeInspectionResultConstants.REVERSE_PRIMARY_KEY;
import static org.metawidget.inspector.InspectionResultConstants.LOOKUP;
import static org.metawidget.inspector.InspectionResultConstants.TRUE;
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
import org.jboss.forge.scaffoldx.metawidget.inspector.propertystyle.ForgePropertyStyle.ForgeProperty;
import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.util.CollectionUtils;

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
         getReversePrimaryKey(property, attributes);
      }

      // ManyToOne

      if (property.isAnnotationPresent(ManyToOne.class))
      {
         attributes.put(MANY_TO_ONE, TRUE);
         getReversePrimaryKey(property, attributes);
      }

      // OneToMany and ManyToMany

      if (property.isAnnotationPresent(OneToMany.class) || property.isAnnotationPresent(ManyToMany.class))
      {
         attributes.put(N_TO_MANY, TRUE);
         getCollectionReversePrimaryKey(property, attributes);
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

   private void getReversePrimaryKey(Property property, Map<String, String> attributes)
   {
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
   
   private void getCollectionReversePrimaryKey(Property property, Map<String, String> attributes)
   {
      // Reverse primary key

      for (Property reverseProperty : getProperties(property.getGenericType()).values())
      {
         if (reverseProperty.isAnnotationPresent(Id.class))
         {
            attributes.put(REVERSE_PRIMARY_KEY, reverseProperty.getName());
            break;
         }
      }
   }
}
