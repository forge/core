package org.jboss.forge.spec.javaee.ejb.api;

public enum TransactionAttributeType
{

   mandatory("javax.ejb.TransactionAttributeType.MANDATORY"),
   required("javax.ejb.TransactionAttributeType.REQUIRED"),
   requiresNew("javax.ejb.TransactionAttributeType.REQUIRES_NEW"),
   supoorts("javax.ejb.TransactionAttributeType.SUPPORTS"),
   notSupported("javax.ejb.TransactionAttributeType.NOT_SUPPORTED"),
   never("javax.ejb.TransactionAttributeType.NEVER");

   private String annotation;

   private TransactionAttributeType(String annotation)
   {
      this.annotation = annotation;
   }

   public String getAnnotation()
   {
      return annotation;
   }
}
