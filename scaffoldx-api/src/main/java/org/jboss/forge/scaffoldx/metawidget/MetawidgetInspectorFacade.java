/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffoldx.metawidget;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.scaffoldx.ScaffoldProvider;
import org.jboss.forge.scaffoldx.metawidget.inspector.ForgeInspector;
import org.jboss.forge.scaffoldx.metawidget.inspector.propertystyle.ForgePropertyStyle;
import org.jboss.forge.scaffoldx.metawidget.inspector.propertystyle.ForgePropertyStyleConfig;
import org.metawidget.inspector.beanvalidation.BeanValidationInspector;
import org.metawidget.inspector.composite.CompositeInspector;
import org.metawidget.inspector.composite.CompositeInspectorConfig;
import org.metawidget.inspector.impl.BaseObjectInspectorConfig;
import org.metawidget.inspector.jpa.JpaInspector;
import org.metawidget.inspector.jpa.JpaInspectorConfig;
import org.metawidget.inspector.propertytype.PropertyTypeInspector;
import org.metawidget.util.XmlUtils;
import org.w3c.dom.Element;

/**
 * A facade for Metawidget inspectors. This class wraps Metawidget API invocations for {@link ScaffoldProvider}
 * instances to invoke.
 * 
 * @author Vineet Reynolds
 * 
 */
public class MetawidgetInspectorFacade
{

   private Project project;
   private CompositeInspector compositeInspector;

   @Inject
   public MetawidgetInspectorFacade(Project project)
   {
      this.project = project;
   }

   /**
    * Inspects a {@link JavaClass} instance and provides inspection results in return.
    * 
    * @param klass The {@link JavaClass} to inspect.
    * @return A list representing inspection results for the {@link JavaClass}. Each list item corresponds to the
    *         inspection result for every property of the provided {@link JavaClass}.
    */
   public List<Map<String, String>> inspect(JavaClass klass)
   {
      setupCompositeInspector();
      Element inspectionResult = compositeInspector.inspectAsDom(null, klass.getQualifiedName(), (String[]) null);
      Element inspectedEntity = XmlUtils.getFirstChildElement(inspectionResult);

      Element inspectedProperty = XmlUtils.getFirstChildElement(inspectedEntity);
      List<Map<String, String>> viewPropertyAttributes = new ArrayList<Map<String, String>>();
      while (inspectedProperty != null)
      {
         Map<String, String> propertyAttributes = XmlUtils.getAttributesAsMap(inspectedProperty);
         viewPropertyAttributes.add(propertyAttributes);
         inspectedProperty = XmlUtils.getNextSiblingElement(inspectedProperty);
      }
      return viewPropertyAttributes;
   }

   /**
    * Setup the composite inspector. This is not done at construction time, since Metawidget inspectors cache trait
    * lookups, and hence report incorrect values when underlying Java classes change in projects. Therefore, the
    * composite inspector setup and configuration is perform explicitly upon every inspection.
    */
   private void setupCompositeInspector()
   {
      ForgePropertyStyleConfig forgePropertyStyleConfig = new ForgePropertyStyleConfig();
      forgePropertyStyleConfig.setProject(this.project);
      BaseObjectInspectorConfig baseObjectInspectorConfig = new BaseObjectInspectorConfig();
      baseObjectInspectorConfig.setPropertyStyle(new ForgePropertyStyle(forgePropertyStyleConfig));

      PropertyTypeInspector propertyTypeInspector = new PropertyTypeInspector(baseObjectInspectorConfig);

      ForgeInspector forgeInspector = new ForgeInspector(baseObjectInspectorConfig);

      JpaInspectorConfig jpaInspectorConfig = new JpaInspectorConfig();
      jpaInspectorConfig.setHideIds(true);
      jpaInspectorConfig.setHideVersions(true);
      jpaInspectorConfig.setHideTransients(true);
      jpaInspectorConfig.setPropertyStyle(new ForgePropertyStyle(forgePropertyStyleConfig));
      JpaInspector jpaInspector = new JpaInspector(jpaInspectorConfig);

      BeanValidationInspector beanValidationInspector = new BeanValidationInspector(baseObjectInspectorConfig);

      CompositeInspectorConfig compositeInspectorConfig = new CompositeInspectorConfig();
      compositeInspectorConfig.setInspectors(propertyTypeInspector, forgeInspector, jpaInspector,
               beanValidationInspector);
      compositeInspector = new CompositeInspector(compositeInspectorConfig);
   }

}
