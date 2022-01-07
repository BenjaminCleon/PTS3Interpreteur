 package AlgoPars.Metier;

import java.util.ArrayList;
import java.util.List;

/**
 * Permet pour une donnee écrite en pseudo-code
 * d'être stockée ici avec son type ( en pseudo-code )
 * et sa valeur.
 */
public class Donnee
{
	private String  nom        ; // nom de la donnée
	private String  type       ; // type de la donnée
	private Object  valeur     ; // valeur de la donnée ou E représente sont type en objet
	private boolean estConstant; // la donnée est-elle constante
   
	/**
	 * Constructeur de la classe Donnee
	 * permet de créer une donnée avec son nom, type, valeur et savoir si elle est constante
	 *
	 * @param nom
	 *     nom de la donnée
	 * @param type
	 *     type de la donnée
	 * @param valeur
	 *     valeur de la donnée
	 * @param estConstant
	 *     constante ou variable
	 */
	public Donnee(String nom, String type, Object valeur, boolean estConstant, int taille)
	{
		if ( valeur instanceof List )for (int i=0;i<taille;i++)((List)valeur).add(null);
		
		this.nom         = nom        ;
		this.type        = type       ;
		this.valeur      = valeur     ;
		this.estConstant = estConstant;

		
	}
	public Donnee(String nom, String type, Object valeur, boolean estConstant)
	{
		this(nom, type, valeur, estConstant, 0);
	}

	/**
	 * @return le nom de la donnée
	 */
	public String getNom   () { return this.nom   ; }

	/**
	 * @return le type de donnée
	 */
	public String getType  () { return this.type  ; }

	/**
	 * @return la valeur de la donnée
	 */
	public Object getValeur(Integer... args)
	{
		if ( this.valeur instanceof List )return this.getValeurTableau(args);

		return this.valeur;
	}

	/**
	 * Si la donnée est un tableau alors cela retourne la valeur situé à cette donnée
	 * @param args
	 * @return
	 */
	public Object getValeurTableau(Integer... args)
	{
		if ( ((List)this.valeur).get(0) instanceof List )
		{
			if ( ((List)((List)this.valeur).get(0)) instanceof List )
				return ((List)((List)((List)this.valeur).get(args[0])).get(args[1])).get(args[2]);
			else
				return ((List)((List)this.valeur).get(args[0])).get(args[1]);
		}
		else
			return ((List)this.valeur).get(args[0]);
	}

	/**
	 * Permet la création de tableau jusqu'à trois dimensions
	 * @param dimen
	 *     nombre de dimensions du tableau
	 * @param args
	 *     taille de chaque dimension
	 */
	public void creerDimension(int dimen, Integer... args)
	{
		List<Object> l = ((List)this.valeur);

		for ( int i=0;i<args[0];i++)
		{
			if ( dimen >= 2 )
			{
				l.add(new ArrayList<Object>());

				for ( int j=0;j<args[1];j++)
				{
					if ( dimen == 3 )
					{
						((List<Object>)l.get(i)).add(new ArrayList<Object>());

						for (int k=0;k<args[2];k++)
						{
							List<Object> tmp = ((List<Object>)l.get(i));
							((List<Object>)tmp.get(j)).add(null);
						}
					}
					else
						((List<Object>)l.get(i)).add(null);
				}
			}
			else
				l.add(null);
		}
	}

	/**
	 * @return true si la donnee est constante
	 */
	public boolean estConstant() { return this.estConstant; }

	/**
	 * change la valeur si possible
	 * @param valeur
	 * @return true si la valeur a été changé
	 */
	public boolean setValeur(Object valeur, Integer... args)
	{
		if ( this.estConstant )return false;
		if(!(this.valeur instanceof List))
		{
			this.valeur = valeur;
		}
		else
		{
			if ( ((List)this.valeur).get(0) instanceof List )
			{
				if ( ((List)((List)this.valeur).get(0)) instanceof List )
					((List)((List)((List)this.valeur).get(args[0])).get(args[1])).set(args[2], valeur);
				else
					((List)((List)this.valeur).get(args[0])).set(args[1], valeur);
			}
			else
				((List)this.valeur).set(args[0], valeur);
		}
		return true;
	}
}

