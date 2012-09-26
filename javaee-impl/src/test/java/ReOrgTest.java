import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.parser.java.Method;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ReOrgTest extends AbstractShellTest
{

   @Test
   public void simple1() throws Exception
   {
      JavaClass from = JavaParser.parse(JavaClass.class,
               "package it.coopservice.consulenti.model; " +
                        "import javax.persistence.Entity; " +
                        "import javax.persistence.Column; " +
                        "import java.io.Serializable; " +
                        "@Entity public class Responsabile implements Serializable{" +
                        " @Column  private String nome;" +
                        "public String getNome(){ return this.nome;  }" +
                        "public void setNome(final String nome) {this.cognome = cognome;} " +
                        "@Column   private String cognome;" +
                        "public String getCognome() { return this.cognome; }" +
                        "public void setCognome(final String cognome) {this.cognome = cognome; }" +
                        "}");
      JavaClass secondFrom = JavaParser
               .parse(JavaClass.class,
                        ""
                                 +
                                 "package it.coopservice.progetti.model;"
                                 +
                                 " import javax.persistence.Entity;"
                                 +
                                 " import java.io.Serializable;"
                                 +
                                 " import javax.persistence.Id;"
                                 +
                                 " import javax.persistence.GeneratedValue;"
                                 +
                                 " import javax.persistence.GenerationType;"
                                 +
                                 " import javax.persistence.Column;"
                                 +
                                 " import javax.persistence.Version;"
                                 +
                                 " import java.lang.Override;"
                                 +
                                 " import java.util.Set;"
                                 +
                                 " import java.util.HashSet;"
                                 +
                                 " import it.coopservice.progetti.model.Dipendente;"
                                 +
                                 " import javax.persistence.ManyToMany;"
                                 +
                                 " import javax.xml.bind.annotation.XmlRootElement;"
                                 +

                                 " @Entity public class Progetto implements Serializable{"
                                 +

                                 "  @Id   private @GeneratedValue(strategy = GenerationType.AUTO)   @Column(name = \"id\", updatable = false, nullable = false)   Long id = null;"
                                 +
                                 "  @Version   private @Column(name = \"version\")   int version = 0;"
                                 +

                                 " public Long getId()   {      return this.id;   }"
                                 +

                                 " public void setId(final Long id)   {      this.id = id;   }"
                                 +

                                 " public int getVersion()  {  return this.version;   }"
                                 +

                                 " public void setVersion(final int version){this.version = version; }"
                                 +

                                 "  @Override"
                                 +
                                 " public boolean equals(Object that)  {"
                                 +
                                 "  if (this == that)      {         return true;      }"
                                 +
                                 "  if (that == null){return false;}"
                                 +
                                 "  if (getClass() != that.getClass())"
                                 +
                                 "  { return false; }"
                                 +
                                 "  if (id != null) { return id.equals(((Progetto) that).id); }return super.equals(that);}"
                                 +

                                 " @Override public int hashCode(){if (id != null){return id.hashCode(); }return super.hashCode(); }"
                                 +

                                 " @Column private String nome;" +

                                 "    public String getNome(){return this.nome;}" +

                                 " public void setNome(final String nome){this.nome = nome;}" +

                                 " @Column private String url;" +

                                 " @Transient public String getUrl(){return this.url;}" +

                                 "  public void setUrl(final String url){ this.url = url;}" +

                                 " public String toString(){String result = \"\";" +
                                 " if (nome != null && !nome.trim().isEmpty())result += nome;" +
                                 " if (url != null && !url.trim().isEmpty())result += \" \" + url;" +
                                 "  return result;}" +

                                 " private @ManyToMany(mappedBy = \"progetti\") Set<Dipendente> dipendenti = new HashSet<Dipendente>();"
                                 +

                                 "   public Set<Dipendente> getDipendenti(){return this.dipendenti; }" +

                                 "  public void setDipendenti(final Set<Dipendente> dipendenti){this.dipendenti = dipendenti;}"
                                 +
                                 "}");

      reorg(from);
      System.out.println("********************************");
      reorg(secondFrom);
   }

   private void reorg(JavaClass from)
   {
      System.out.println("*********AFTER**********");
      System.out.println("********************************");
      System.out.println(from.toString());

      List<Method<JavaClass>> methods = from.getMethods();
      for (Method<JavaClass> met : from.getMethods())
      {
         from.removeMethod(met);
      }
      System.out.println("**********REMOVE MTHODS***************");
      System.out.println("********************************");
      System.out.println(from.toString());
      System.out.println("*********RE-ADD METHODS*************");
      System.out.println("********************************");
      for (Method<JavaClass> met : methods)
      {
         from.addMethod(met.toString());
      }
      System.out.println(from.toString());
   }
}