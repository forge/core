/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.lang.Override;

@Embeddable
public class EmbeddableProperty implements Serializable
{

   private String attrA;
   private String attrB;
   
   @ManyToOne
   private AssociationInEmbeddable associationInEmbeddable;
   
   public String getAttrA() {
       return attrA;
   }

   public void setAttrA(String attrA) {
       this.attrA = attrA;
   }

   public String getAttrB() {
       return attrB;
   }

   public void setAttrB(String attrB) {
       this.attrB = attrB;
   }
   
   public AssociationInEmbeddable getAssociationInEmbeddable() {
       return associationInEmbeddable;
   }

   public void setAssociationInEmbeddable(AssociationInEmbeddable associationInEmbeddable) {
       this.associationInEmbeddable = associationInEmbeddable;
   }

   @Override
   public boolean equals(Object o) {
       if (this == o)
           return true;
       if (o == null || getClass() != o.getClass())
           return false;

       EmbeddableProperty embeddable = (EmbeddableProperty) o;

       if (attrA != null ? !attrA.equals(embeddable.attrA) : embeddable.attrA != null)
           return false;
       if (attrB != null ? !attrB.equals(embeddable.attrB) : embeddable.attrB != null)
          return false;

       return true;
   }

   @Override
   public int hashCode() {
       int result = attrA != null ? attrA.hashCode() : 0;
       result = 31 * result + (attrB != null ? attrB.hashCode() : 0);
       return result;
   }

   @Override
   public String toString() {
       return attrA + ", " + attrB;
   }
}