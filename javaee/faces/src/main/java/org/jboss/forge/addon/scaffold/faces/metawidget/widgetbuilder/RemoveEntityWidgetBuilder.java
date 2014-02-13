/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.faces.metawidget.widgetbuilder;

import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.INVERSE_FIELD;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_MANY_TO_MANY;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_MANY_TO_ONE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_ONE_TO_MANY;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_ONE_TO_ONE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.JPA_REL_TYPE;
import static org.jboss.forge.addon.scaffold.faces.metawidget.inspector.ForgeInspectionResultConstants.OWNING_FIELD;
import static org.metawidget.inspector.InspectionResultConstants.ENTITY;
import static org.metawidget.inspector.InspectionResultConstants.NAME;
import static org.metawidget.inspector.InspectionResultConstants.PARAMETERIZED_TYPE;
import static org.metawidget.inspector.InspectionResultConstants.TYPE;

import java.awt.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.metawidget.statically.javacode.JavaStatement;
import org.metawidget.statically.javacode.StaticJavaMetawidget;
import org.metawidget.statically.javacode.StaticJavaStub;
import org.metawidget.statically.javacode.StaticJavaWidget;
import org.metawidget.util.ClassUtils;
import org.metawidget.util.simple.StringUtils;
import org.metawidget.widgetbuilder.iface.WidgetBuilder;

/**
 * Builds Java widgets that implement the behavior for updation of bidirectional relationships during deletion of
 * entities.
 * 
 * @author Vineet Reynolds
 */
public class RemoveEntityWidgetBuilder implements WidgetBuilder<StaticJavaWidget, StaticJavaMetawidget>
{

   @Override
   public StaticJavaWidget buildWidget(String elementName, Map<String, String> attributes,
            StaticJavaMetawidget metawidget)
   {
      if (ENTITY.equals(elementName))
      {
         return null;
      }

      String name = attributes.get(NAME);
      String type = attributes.get(TYPE);
      boolean oneToOne = attributes.get(JPA_REL_TYPE) != null && attributes.get(JPA_REL_TYPE).equals(JPA_ONE_TO_ONE);
      boolean manyToOne = attributes.get(JPA_REL_TYPE) != null && attributes.get(JPA_REL_TYPE).equals(JPA_MANY_TO_ONE);
      boolean oneToMany = attributes.get(JPA_REL_TYPE) != null && attributes.get(JPA_REL_TYPE).equals(JPA_ONE_TO_MANY);
      boolean manyToMany = attributes.get(JPA_REL_TYPE) != null
               && attributes.get(JPA_REL_TYPE).equals(JPA_MANY_TO_MANY);
      String ownerField = attributes.get(OWNING_FIELD);
      String inverseField = attributes.get(INVERSE_FIELD);

      String capitalizedName = StringUtils.capitalize(name);
      if (oneToOne && ownerField != null && inverseField != null)
      {
         StaticJavaStub toReturn = new StaticJavaStub();
         JavaStatement getValue = new JavaStatement(ClassUtils.getSimpleName(type) + " " + name
                  + " = deletableEntity.get"
                  + capitalizedName + "()");
         getValue.putImport(type);
         toReturn.getChildren().add(getValue);
         String fieldToNullify = name.equals(ownerField) ? inverseField : ownerField;
         JavaStatement removeRelationship = new JavaStatement(name + ".set"
                  + StringUtils.capitalize(fieldToNullify)
                  + "(null)");
         toReturn.getChildren().add(removeRelationship);
         JavaStatement mergeModification = new JavaStatement("this.entityManager.merge(" + name + ")");
         toReturn.getChildren().add(mergeModification);
         return toReturn;
      }
      else if (manyToOne && inverseField != null)
      {
         StaticJavaStub toReturn = new StaticJavaStub();
         JavaStatement getValue = new JavaStatement(ClassUtils.getSimpleName(type) + " " + name
                  + " = deletableEntity.get"
                  + capitalizedName + "()");
         getValue.putImport(type);
         toReturn.getChildren().add(getValue);
         JavaStatement removeInverse = new JavaStatement(name + ".get"
                  + StringUtils.capitalize(inverseField)
                  + "().remove(deletableEntity)");
         toReturn.getChildren().add(removeInverse);
         JavaStatement nullOwner = new JavaStatement("deletableEntity.set" + capitalizedName + "(null)");
         toReturn.getChildren().add(nullOwner);
         JavaStatement mergeModification = new JavaStatement("this.entityManager.merge(" + name + ")");
         toReturn.getChildren().add(mergeModification);
         return toReturn;
      }
      else if (oneToMany && ownerField != null)
      {
         if (type.equals(Collection.class.getName()) || type.equals(Set.class.getName())
                  || type.equals(List.class.getName()))
         {
            String parameterizedType = attributes.get(PARAMETERIZED_TYPE);
            String simpleParameterizedType = ClassUtils.getSimpleName(parameterizedType);

            StaticJavaStub toReturn = new StaticJavaStub();
            JavaStatement iterator = new JavaStatement("Iterator<" + simpleParameterizedType + "> iter"
                     + capitalizedName + " = deletableEntity.get"
                     + capitalizedName + "().iterator()");
            iterator.putImport(Iterator.class.getName());
            iterator.putImport(parameterizedType);
            toReturn.getChildren().add(iterator);

            JavaStatement collectionModifiers = new JavaStatement("for (; iter" + capitalizedName + ".hasNext() ;) ");

            String scopedVariable = "nextIn" + capitalizedName;
            JavaStatement iterable = new JavaStatement(simpleParameterizedType + " " + scopedVariable
                     + " = iter" + capitalizedName + ".next()");
            collectionModifiers.getChildren().add(iterable);

            JavaStatement nullOwner = new JavaStatement(scopedVariable + ".set"
                     + StringUtils.capitalize(ownerField)
                     + "(null)");
            collectionModifiers.getChildren().add(nullOwner);

            JavaStatement removeInverse = new JavaStatement("iter" + capitalizedName + ".remove()");
            collectionModifiers.getChildren().add(removeInverse);

            JavaStatement mergeModification = new JavaStatement("this.entityManager.merge(" + scopedVariable + ")");
            collectionModifiers.getChildren().add(mergeModification);

            toReturn.getChildren().add(collectionModifiers);
            return toReturn;
         }
      }
      else if (manyToMany && ownerField != null && inverseField != null)
      {
         if (type.equals(Collection.class.getName()) || type.equals(Set.class.getName())
                  || type.equals(List.class.getName()))
         {
            String parameterizedType = attributes.get(PARAMETERIZED_TYPE);
            String simpleParameterizedType = ClassUtils.getSimpleName(parameterizedType);

            StaticJavaStub toReturn = new StaticJavaStub();
            JavaStatement iterator = new JavaStatement("Iterator<" + simpleParameterizedType + "> iter"
                     + capitalizedName + " = deletableEntity.get"
                     + capitalizedName + "().iterator()");
            iterator.putImport(Iterator.class.getName());
            iterator.putImport(parameterizedType);
            toReturn.getChildren().add(iterator);

            JavaStatement collectionModifiers = new JavaStatement("for (; iter" + capitalizedName + ".hasNext() ;) ");

            String scopedVariable = "nextIn" + capitalizedName;
            JavaStatement iterable = new JavaStatement(simpleParameterizedType + " " + scopedVariable
                     + " = iter" + capitalizedName + ".next()");
            collectionModifiers.getChildren().add(iterable);

            String collectionToModify = name.equals(ownerField) ? inverseField : ownerField;
            JavaStatement removeOtherMember = new JavaStatement(scopedVariable + ".get"
                     + StringUtils.capitalize(collectionToModify)
                     + "().remove(deletableEntity)");
            collectionModifiers.getChildren().add(removeOtherMember);

            JavaStatement removeInverse = new JavaStatement("iter" + capitalizedName + ".remove()");
            collectionModifiers.getChildren().add(removeInverse);

            JavaStatement mergeModification = new JavaStatement("this.entityManager.merge(" + scopedVariable + ")");
            collectionModifiers.getChildren().add(mergeModification);

            toReturn.getChildren().add(collectionModifiers);
            return toReturn;
         }
      }
      return new StaticJavaStub();
   }

}
