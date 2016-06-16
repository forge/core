/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.ejb;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;

import javax.ejb.MessageDriven;

import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class EJBOperationsTest
{

   EJBOperations ejbOp = new EJBOperations();

   /**
    * Test method for
    * {@link org.jboss.forge.addon.javaee.ejb.EJBOperations#addActivationConfig(org.jboss.forge.roaster.model.source.JavaClassSource, java.lang.String, java.lang.String)}
    * .
    */
   @Test
   public void testSetupMessageDrivenBean()
   {
      JavaClassSource mdb = Roaster.create(JavaClassSource.class).setName("FooMDB");
      ejbOp.setupMessageDrivenBean(mdb, JMSDestinationType.QUEUE, "queue/foo");
      Assert.assertThat(mdb.hasAnnotation(MessageDriven.class), is(true));
      Assert.assertThat(mdb.getInterfaces(), hasItem("javax.jms.MessageListener"));
      AnnotationSource<JavaClassSource> annotation = mdb.getAnnotation(MessageDriven.class);
      AnnotationSource<JavaClassSource> configs[] = annotation.getAnnotationArrayValue("activationConfig");

      Assert.assertThat(configs[0].getStringValue("propertyName"), equalTo("destinationType"));
      Assert.assertThat(configs[0].getStringValue("propertyValue"), equalTo("javax.jms.Queue"));

      Assert.assertThat(configs[1].getStringValue("propertyName"), equalTo("destination"));
      Assert.assertThat(configs[1].getStringValue("propertyValue"), equalTo("queue/foo"));
   }

}
