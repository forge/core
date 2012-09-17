package by.giava.ejb;

import javax.ejb.Stateless;
import java.io.Serializable;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

@Stateless
public @TransactionAttribute(TransactionAttributeType.REQUIRED)
class Flower3 implements Serializable
{
}