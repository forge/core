package by.giava.ejb;

import javax.ejb.MessageDriven;
import javax.ejb.LocalBean;

@LocalBean
public @MessageDriven(name = "Flower10MDB")
class Flower10MDB
{
}