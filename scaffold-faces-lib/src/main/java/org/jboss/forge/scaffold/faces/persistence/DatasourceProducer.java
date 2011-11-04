package org.jboss.forge.scaffold.faces.persistence;

import java.io.Serializable;

import javax.ejb.Stateless;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.solder.core.ExtensionManaged;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Stateless
public class DatasourceProducer implements Serializable
{
   private static final long serialVersionUID = -5267593171036179836L;

   @Produces
   @ExtensionManaged
   @PersistenceContext
   static EntityManager em;
}
