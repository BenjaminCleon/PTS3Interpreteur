 package AlgoPars.Metier;

import java.util.List;

/**
 * Permet pour une donnee écrite en pseudo-code
 * d'être stockée ici avec son type ( en pseudo-code )
 * et sa valeur.
 */
public class Donnee<E>
{
	private String  nom        ; // nom de la donnée
	private String  type       ; // type de la donnée
	private E       valeur     ; // valeur de la donnée ou E représente sont type en objet
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
	public Donnee(String nom, String type, E valeur, boolean estConstant, int taille)
	{
		if ( valeur instanceof List )for (int i=0;i<taille;i++)((List)valeur).add(null);
		
		this.nom         = nom        ;
		this.type        = type       ;
		this.valeur      = valeur     ;
		this.estConstant = estConstant;

		
	}
	public Donnee(String nom, String type, E valeur, boolean estConstant)
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
	public E      getValeur() { return this.valeur; }

	/**
	 * @return true si la donnee est constante
	 */
	public boolean estConstant() { return this.estConstant; }

	/**
	 * change la valeur si possible
	 * @param valeur
	 * @return true si la valeur a été changé
	 */
	public boolean setValeur(E valeur, int ind)
	{
		if ( this.estConstant )return false;
		if(ind == -1)
		{
			this.valeur = valeur;
		}
		else
		{
			((List)this.valeur).set(ind, valeur);
		}
		return true;
	}
}

