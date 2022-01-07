package AlgoPars.Metier;

import java.util.List;
import java.util.ArrayList;

public class Tableau<E>
{
	private String  nom;
	private String type;
	private int  taille;
	
	private List<E> valeurs;
	
	public Tableau(String nom, String type, int taille)
	{
		this.nom     =           nom;
		this.type    =          type;
		this.taille  =        taille;
		this.valeurs = new ArrayList<E>();
	}
	
	public boolean ajouterValeur(E val)
	{
		if(this.valeurs.size() >= this.taille) return false;
		this.valeurs.add(val);
		return true;
	}
	
	public E getValeur(int n)
	{
		if(n >= this.valeurs.size())return null;
		return this.valeurs.get(n);
	}
	
	
}
