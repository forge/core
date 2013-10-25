package org.jboss.forge.addon.javaee.validation.ui;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.javaee.ui.AbstractJavaEECommand;
import org.jboss.forge.addon.parser.java.beans.Property;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.ui.InputComponentFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.ManyValued;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.InputComponents;
import org.jboss.forge.addon.ui.wizard.UIWizardStep;
import org.jboss.forge.parser.java.AnnotationTarget;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.util.Assert;
import org.jboss.forge.parser.java.util.Strings;

@SuppressWarnings("unchecked")
public class GenerateConstraintWizardStep extends AbstractJavaEECommand implements UIWizardStep
{
   @Inject
   private InputComponentFactory factory;

   @Inject
   private ConverterFactory converterFactory;

   private Map<String, InputComponent<?, ?>> inputs = new HashMap<String, InputComponent<?, ?>>();

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return null;
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      UIContext context = builder.getUIContext();
      ConstraintType constraintType = (ConstraintType) context.getAttribute(ConstraintType.class);
      generateConstraintInputs(builder, constraintType.getConstraint());
   }

   @SuppressWarnings("rawtypes")
   private void generateConstraintInputs(UIBuilder builder, Class<? extends Annotation> constraint)
   {
      for (Method m : constraint.getDeclaredMethods())
      {
         String name = m.getName();
         Class<?> valueType = m.getReturnType();
         final InputComponent<?, Object> inputComponent;
         boolean many = valueType.isArray();
         if (many)
         {
            valueType = valueType.getComponentType();
         }
         if (valueType.isEnum())
         {
            final SelectComponent select;
            if (many)
            {
               select = factory.createSelectMany(name, valueType);
            }
            else
            {
               select = factory.createSelectOne(name, valueType);
            }
            Class<? extends Enum> enumClass = valueType.asSubclass(Enum.class);
            select.setValueChoices(EnumSet.allOf(enumClass));
            inputComponent = select;
         }
         else
         {
            final InputComponent input;
            if (many)
            {
               input = factory.createInputMany(name, String.class);
            }
            else
            {
               input = factory.createInput(name, calculateType(valueType));
            }
            if (valueType == Class.class)
            {
               HintsFacet facet = (HintsFacet) input.getFacet(HintsFacet.class);
               facet.setInputType(InputType.JAVA_CLASS_PICKER);
            }
            inputComponent = input;
         }
         try
         {
            Object defaultValue = m.getDefaultValue();
            if (defaultValue != null)
            {
               if (many)
               {
                  List<Object> defaultValues = toList(valueType, defaultValue);
                  defaultValue = defaultValues;
               }
               InputComponents
                        .setDefaultValueFor(converterFactory, (InputComponent<?, Object>) inputComponent,
                                 defaultValue);
            }
            else
            {
               // No default value found, it is required
               inputComponent.setRequired(true);
            }
         }
         catch (TypeNotPresentException tnpe)
         {
            // No default value found, it is required
            inputComponent.setRequired(true);
         }
         builder.add(inputComponent);
         inputs.put(name, inputComponent);
      }
   }

   /**
    * Converts an array to a {@link List}
    * 
    * @param valueType
    * @param arrayObject
    * @return
    */
   private List<Object> toList(Class<?> valueType, Object arrayObject)
   {
      List<Object> defaultValues = new ArrayList<Object>();
      int length = Array.getLength(arrayObject);
      boolean isClassType = (valueType == Class.class);
      for (int i = 0; i < length; i++)
      {
         Object arrayValue = Array.get(arrayObject, i);
         if (isClassType)
         {
            arrayValue = ((Class<?>) arrayValue).getName();
         }
         defaultValues.add(arrayValue);
      }
      return defaultValues;
   }

   private Class<?> calculateType(Class<?> valueType)
   {
      if (valueType == Integer.TYPE)
      {
         return Integer.class;
      }
      else if (valueType == Long.TYPE)
      {
         return Long.class;
      }
      return valueType;
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      Property property = (Property) context.getAttribute(Property.class);
      ConstraintType constraintType = (ConstraintType) context.getAttribute(ConstraintType.class);
      Boolean onAccessor = (Boolean) context.getAttribute("onAccessor");
      final AnnotationTarget<JavaClass, ?> annotationTarget;
      if (onAccessor)
      {
         annotationTarget = property.getAccessor();
      }
      else
      {
         annotationTarget = property.getActualField();
      }
      Class<? extends Annotation> constraintAnnotation = constraintType.getConstraint();
      org.jboss.forge.parser.java.Annotation<JavaClass> annotation = annotationTarget
               .addAnnotation(constraintAnnotation);
      populateAnnotation(constraintAnnotation, annotation);
      getSelectedProject(context).getFacet(JavaSourceFacet.class).saveJavaSource(annotation.getOrigin());
      return Results.success("Constraint " + constraintAnnotation.getSimpleName() + " successfully configured");
   }

   /**
    * @param annotation
    */
   private void populateAnnotation(Class<? extends Annotation> constraint,
            org.jboss.forge.parser.java.Annotation<JavaClass> annotation)
   {
      for (Method m : constraint.getDeclaredMethods())
      {
         String name = m.getName();
         Class<?> returnType = m.getReturnType();
         InputComponent<?, ?> inputComponent = inputs.get(name);
         Object componentValue = InputComponents.getValueFor(inputComponent);
         if (componentValue == null)
         {
            continue;
         }
         try
         {
            Object defaultValue = m.getDefaultValue();
            if (defaultValue != null)
            {
               if (returnType.isArray())
               {
                  Class<?> componentType = returnType.getComponentType();
                  List<Object> defaultValues = toList(componentType, defaultValue);
                  Collection<Object> values = (Collection<Object>) componentValue;
                  if (values.containsAll(defaultValues))
                  {
                     continue;
                  }
               }
               else
               {
                  if (returnType == Class.class && ((Class<?>) defaultValue).getName().equals(componentValue))
                  {
                     continue;
                  }
                  else if (componentValue.toString().equals(defaultValue.toString()))
                  {
                     continue;
                  }
               }
            }
         }
         catch (TypeNotPresentException tnpe)
         {
            // No default value is present
         }
         if (inputComponent instanceof ManyValued)
         {
            returnType = returnType.getComponentType();
            Collection<Object> values = (Collection<Object>) componentValue;
            setArrayValue(annotation, name, returnType, values, returnType == Class.class);
         }
         else
         {
            if (returnType == Class.class)
            {
               annotation.setLiteralValue(name, componentValue + ".class");
            }
            else if (returnType == String.class)
            {
               annotation.setStringValue(name, componentValue.toString());
            }
            else
            {
               annotation.setLiteralValue(name, componentValue.toString());
            }
         }
      }

   }

   private void setArrayValue(org.jboss.forge.parser.java.Annotation<JavaClass> annotation, String name,
            Class<?> type,
            Iterable<Object> values, boolean isClass)
   {
      Assert.notNull(values, "null array not accepted");

      final List<String> literals = new ArrayList<String>();

      for (Object value : values)
      {
         Assert.notNull(value, "null value not accepted");

         if (!type.isPrimitive())
         {
            annotation.getOrigin().addImport(type);
         }
         literals.add(value + ((isClass) ? ".class" : ""));
      }
      if (!literals.isEmpty())
      {
         annotation.setLiteralValue(name,
                  literals.size() == 1 ? literals.get(0) : String.format("{%s}", Strings.join(literals, ",")));
      }
   }

   @Override
   protected boolean isProjectRequired()
   {
      return false;
   }

}
