package by.giava.ejb;

import javax.ejb.Stateless;
import javax.inject.Inject;
import by.giava.ejb.Flower2;
import java.io.Serializable;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
public @TransactionAttribute(TransactionAttributeType.REQUIRED)
class Flower2 extends Serializable implements Serializable
{

   @Inject
   private Flower2 flower;
}