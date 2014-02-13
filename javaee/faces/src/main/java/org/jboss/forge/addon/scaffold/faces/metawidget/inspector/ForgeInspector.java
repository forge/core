/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.inspector;

import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.GENERATED_VALUE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.INVERSE_FIELD;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_MANY_TO_MANY;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_MANY_TO_ONE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_ONE_TO_ONE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.N_TO_MANY;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_ONE_TO_MANY;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.ONE_TO_ONE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.OWNING_FIELD;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.PRIMARY_KEY;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_REL_TYPE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.REVERSE_PRIMARY_KEY;
import static org.metawidget.inspector.InspectionResultConstants.LOOKUP;
import static org.metawidget.inspector.InspectionResultConstants.TRUE;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_CONVERTER_ID;
import static org.metawidget.inspector.faces.StaticFacesInspectionResultConstants.FACES_LOOKUP;

import java.util.List;
import java.util.Map;

import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.jboss.forge.parser.java.EnumConstant;
import org.jboss.forge.parser.java.JavaEnum;
import org.jboss.forge.addon.scaffold.faces.metawidget.inspector.propertystyle.ForgePropertyStyle.ForgeProperty;
import org.metawidget.inspector.impl.BaseObjectInspector;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.impl.propertystyle.Property;
import org.metawidget.statically.faces.StaticFacesUtils;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.CollectionUtils;
import org.metawidget.util.simple.StringUtils;
import org.w3c.dom.Element;

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

   @Override
   public Element inspectAsDom(Object toInspect, String type, String... names)
   {
      this.setTypeUnderInspection(type);
      return super.inspectAsDom(toInspect, type, names);
   }

   private String typeUnderInspection;

   public String getTypeUnderInspection()
   {
      return typeUnderInspection;
   }

   public void setTypeUnderInspection(String typeUnderInspection)
   {
      this.typeUnderInspection = typeUnderInspection;
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
         attributes.put(JPA_REL_TYPE, JPA_ONE_TO_ONE);
         getReversePrimaryKey(property, attributes);
         if (property.isAnnotationPresent(OneToOne.class))
         {
            getOneToOneBidirectionalProperties(property, attributes);
         }
      }

      // ManyToOne

      if (property.isAnnotationPresent(ManyToOne.class))
      {
         attributes.put(JPA_REL_TYPE, JPA_MANY_TO_ONE);
         attributes
                  .put(FACES_LOOKUP,
                           StaticFacesUtils.wrapExpression(StringUtils.decapitalize(ClassUtils.getSimpleName(property
                                    .getType())) + "Bean.all"));

         attributes
                  .put(FACES_CONVERTER_ID,
                           StaticFacesUtils.wrapExpression(StringUtils.decapitalize(ClassUtils.getSimpleName(property
                                    .getType())) + "Bean.converter"));

         // Reverse primary key

         getReversePrimaryKey(property, attributes);
         getManyToOneBidirectionalProperties(property, attributes);
      }

      // OneToMany and ManyToMany

      if (property.isAnnotationPresent(OneToMany.class) || property.isAnnotationPresent(ManyToMany.class))
      {
         attributes.put(N_TO_MANY, TRUE);
         getCollectionReversePrimaryKey(property, attributes);
         if (property.isAnnotationPresent(OneToMany.class))
         {
            attributes.put(JPA_REL_TYPE, JPA_ONE_TO_MANY);
            getOneToManyBidirectionalProperties(property, attributes);
         }
         else
         {
            attributes.put(JPA_REL_TYPE, JPA_MANY_TO_MANY);
            getManyToManyBidirectionalProperties(property, attributes);
         }
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
      if (property.isAnnotationPresent(GeneratedValue.class))
      {
         attributes.put(GENERATED_VALUE, TRUE);
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

   private void getOneToOneBidirectionalProperties(Property property, Map<String, String> attributes)
   {
      String owningProperty = property.getAnnotation(OneToOne.class).mappedBy();
      String propertyName = property.getName();
      /*
       * Set the inverse association only when the mappedBy attribute is available. We do not support the ability to
       * identify the owning and inverse sides through JoinColumn or PrimaryKeyJoinColumn annotations yet.
       */
      if (!owningProperty.isEmpty())
      {
         attributes.put(INVERSE_FIELD, propertyName);
         for (Property reverseProperty : getProperties(property.getType()).values())
         {
            String reversePropertyName = reverseProperty.getName();
            String reversePropertyType = reverseProperty.getType();
            if (reverseProperty.isAnnotationPresent(OneToOne.class) && reversePropertyName.equals(owningProperty)
                     && reversePropertyType.equals(getTypeUnderInspection()))
            {
               attributes.put(OWNING_FIELD, reversePropertyName);
               break;
            }
         }
      }
      else
      {
         for (Property reverseProperty : getProperties(property.getType()).values())
         {
            String reversePropertyName = reverseProperty.getName();
            String reversePropertyType = reverseProperty.getType();
            if (reverseProperty.isAnnotationPresent(OneToOne.class)
                     && !reverseProperty.getAnnotation(OneToOne.class).mappedBy().isEmpty())
            {
               owningProperty = reverseProperty.getAnnotation(OneToOne.class).mappedBy();
               if (propertyName.equals(owningProperty) && reversePropertyType.equals(getTypeUnderInspection()))
               {
                  attributes.put(OWNING_FIELD, propertyName);
                  attributes.put(INVERSE_FIELD, reversePropertyName);
                  break;
               }
            }
         }
      }
   }

   private void getManyToOneBidirectionalProperties(Property property, Map<String, String> attributes)
   {
      String propertyName = property.getName();
      // The ManyToOne side is always the owning side
      attributes.put(OWNING_FIELD, propertyName);
      for (Property reverseProperty : getProperties(property.getType()).values())
      {
         if (reverseProperty.isAnnotationPresent(OneToMany.class))
         {
            String mappedPropertyName = reverseProperty.getAnnotation(OneToMany.class).mappedBy();
            String reversePropertyType = reverseProperty.getGenericType();
            /*
             * Set the inverse association only when the mappedBy attribute is available. This is as per the JPA
             * specification. We'll ignore the ability of JPA providers like Hibernate to automatically treat a
             * OneToMany field as the inverse side of the association.
             */
            if (reversePropertyType.equals(getTypeUnderInspection()) && mappedPropertyName.equals(propertyName))
            {
               attributes.put(INVERSE_FIELD, reverseProperty.getName());
               break;
            }
         }
      }
   }

   private void getOneToManyBidirectionalProperties(Property property, Map<String, String> attributes)
   {
      String owningProperty = property.getAnnotation(OneToMany.class).mappedBy();
      /*
       * Set the inverse association only when the mappedBy attribute is available. This is as per the JPA
       * specification. We'll ignore the ability of JPA providers like Hibernate to automatically treat a OneToMany
       * field as the inverse side of the association.
       */
      if (!owningProperty.isEmpty())
      {
         attributes.put(INVERSE_FIELD, property.getName());
         for (Property reverseProperty : getProperties(property.getGenericType()).values())
         {
            String reversePropertyName = reverseProperty.getName();
            String reversePropertyType = reverseProperty.getType();
            if (reverseProperty.isAnnotationPresent(ManyToOne.class) && reversePropertyName.equals(owningProperty)
                     && reversePropertyType.equals(getTypeUnderInspection()))
            {
               attributes.put(OWNING_FIELD, reversePropertyName);
               break;
            }
         }
      }
   }

   private void getManyToManyBidirectionalProperties(Property property, Map<String, String> attributes)
   {
      String owningProperty = property.getAnnotation(ManyToMany.class).mappedBy();
      String propertyName = property.getName();
      /*
       * Set the inverse association only when the mappedBy attribute is available. This is as per the JPA
       * specification. We'll ignore the ability of JPA providers like Hibernate to automatically treat a ManyToMany
       * field as the inverse side of the association.
       */
      if (!owningProperty.isEmpty())
      {
         attributes.put(INVERSE_FIELD, propertyName);
         for (Property reverseProperty : getProperties(property.getGenericType()).values())
         {
            String reversePropertyName = reverseProperty.getName();
            String reversePropertyGenericType = reverseProperty.getGenericType();
            if (reverseProperty.isAnnotationPresent(ManyToMany.class) && reversePropertyName.equals(owningProperty)
                     && reversePropertyGenericType.equals(getTypeUnderInspection()))
            {
               attributes.put(OWNING_FIELD, reversePropertyName);
               break;
            }
         }
      }
      else
      {
         for (Property reverseProperty : getProperties(property.getGenericType()).values())
         {
            String reversePropertyName = reverseProperty.getName();
            String reversePropertyGenericType = reverseProperty.getGenericType();
            if (reverseProperty.isAnnotationPresent(ManyToMany.class)
                     && !reverseProperty.getAnnotation(ManyToMany.class).mappedBy().isEmpty())
            {
               owningProperty = reverseProperty.getAnnotation(ManyToMany.class).mappedBy();
               if (propertyName.equals(owningProperty)
                        && reversePropertyGenericType.equals(getTypeUnderInspection()))
               {
                  attributes.put(OWNING_FIELD, propertyName);
                  attributes.put(INVERSE_FIELD, reversePropertyName);
                  break;
               }
            }
         }
      }
   }

}
