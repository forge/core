package by.giava.service;

import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import java.io.Serializable;
import java.util.Comparable;
import javax.inject.Inject;
import by.giava.service.Flower1;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@LocalBean
@Stateless
public @TransactionAttribute(TransactionAttributeType.REQUIRED)
class Sammy1 implements Serializable, Comparable
{

   @Inject
   private Flower1 flower1;
}