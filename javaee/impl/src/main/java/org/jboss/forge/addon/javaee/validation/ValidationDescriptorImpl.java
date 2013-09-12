/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.javaee.validation;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.javaee.descriptor.ValidationDescriptor;
import org.jboss.shrinkwrap.descriptor.spi.node.Node;
import org.jboss.shrinkwrap.descriptor.spi.node.NodeDescriptorImplBase;

/**
 * @author Kevin Pollet
 */
public class ValidationDescriptorImpl extends NodeDescriptorImplBase implements ValidationDescriptor
{
   private final Node model;

   public ValidationDescriptorImpl(String descriptorName)
   {
      this(descriptorName, new Node("validation-config")
               .attribute("xmlns", "http://jboss.org/xml/ns/javax/validation/configuration")
               .attribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"));
   }

   public ValidationDescriptorImpl(String descriptorName, Node model)
   {
      super(descriptorName);
      this.model = model;
   }

   @Override
   public ValidationDescriptor setDefaultProvider(String defaultProvider)
   {
      model.getOrCreate("default-provider").text(defaultProvider);
      return this;
   }

   @Override
   public ValidationDescriptor setMessageInterpolator(String messageInterpolator)
   {
      model.getOrCreate("message-interpolator").text(messageInterpolator);
      return this;
   }

   @Override
   public ValidationDescriptor setTraversableResolver(String traversableResolver)
   {
      model.getOrCreate("traversable-resolver").text(traversableResolver);
      return this;
   }

   @Override
   public ValidationDescriptor setConstraintValidatorFactory(String constraintValidatorFactory)
   {
      model.getOrCreate("constraint-validator-factory").text(constraintValidatorFactory);
      return this;
   }

   @Override
   public ValidationDescriptor setConstraintMapping(String constraintMapping)
   {
      model.createChild("constraint-mapping").text(constraintMapping);
      return this;
   }

   @Override
   public String getDefaultProvider()
   {
      final Node defaultProvider = model.getSingle("default-provider");
      return defaultProvider == null ? null : defaultProvider.getText();
   }

   @Override
   public String getMessageInterpolator()
   {
      final Node messageInterpolator = model.getSingle("message-interpolator");
      return messageInterpolator == null ? null : messageInterpolator.getText();
   }

   @Override
   public String getTraversableResolver()
   {
      final Node traversableResolver = model.getSingle("traversable-resolver");
      return traversableResolver == null ? null : traversableResolver.getText();
   }

   @Override
   public String getConstraintValidatorFactory()
   {
      final Node constraintValidatorFactory = model.getSingle("constraint-validator-factory");
      return constraintValidatorFactory == null ? null : constraintValidatorFactory.getText();
   }

   @Override
   public List<String> getConstraintMappings()
   {
      List<String> mappings = new ArrayList<String>();
      for (Node oneNode : model.get("constraint-mapping"))
      {
         mappings.add(oneNode.getText());
      }
      return mappings;
   }

   @Override
   public Node getRootNode()
   {
      return model;
   }
}
