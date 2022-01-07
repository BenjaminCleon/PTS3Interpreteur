package AlgoPars.Metier;

import java.util.Scanner;
import iut.algo.Console;

import AlgoPars.Metier.Interpreteur;
import AlgoPars.Metier.Donnee      ;

/**
 * Classe permettant de réaliser les entrées et les sorties en pseudo-code
 */
public class EntreeSortie
{
	/**
	 * Permet de lire une donnée
	 * @param data
	 */
	public static String lire(String ligne, Interpreteur interpreteur)
	{
		String saisie;
		String var   ;
		Donnee tmp   ;

		interpreteur.actualiser();

		if ( ligne.contains("(") )ligne = ligne.substring(ligne.indexOf("(")+1, ligne.lastIndexOf(")")).replaceAll(" ", "");

		saisie = "";
		try
		{
			if ( ligne.contains(",") )var = ligne.substring(0, ligne.indexOf(",") );
			else                      var = ligne.substring(0);

			tmp = interpreteur.getDonnee(var);
			//Scanner sc = new Scanner(System.in);
			saisie = Console.lireString();//sc.next(); 
			Util.setValeurBySwitch(tmp, saisie, -1);
			//sc.close();
			if ( ligne.contains(",") )return saisie + " " + EntreeSortie.lire(ligne.substring(ligne.indexOf(",")+1), interpreteur);
		}catch(Exception e)
		{
			System.out.println("Erreur 101");
			e.printStackTrace();
		}

		return saisie;
	}

	/**
	 * Permet de faire l'équivalent d'un écrire
	 * @param ligne
	 *   ligne a interprété
	 */
	public static String ecrire(String ligne, Interpreteur interpret)
	{
		ligne = ligne.substring(ligne.indexOf("(")+1, ligne.lastIndexOf(")"));
		String[] tab = ligne.split(",");
		String res = "";

		for ( String s : tab )
			res += Util.expression(s);
		
		return res;
	}

	/**
	 * Retourne la chaine contenu entre les symboles de concaténation
	 */
	public static String concat(String ligne, Interpreteur interpret)
	{
		String sTmp;
		Donnee dTmp;
		ligne = ligne.replaceAll( "^©? *\"|\" *$", "");

		if ( ligne.contains("©") )
		{
			sTmp = ligne.substring(ligne.lastIndexOf("©")+1).replaceAll( "^©? *\"|\" *$", "");
			dTmp = interpret.getDonnee(sTmp.replaceAll(" |\t", ""));
			return EntreeSortie.concat(ligne.substring(0, ligne.lastIndexOf("©")), interpret) + ((dTmp!=null)?dTmp.getValeur():sTmp);
		}

		dTmp = interpret.getDonnee(ligne.replaceAll(" |\t", ""));
		if (dTmp == null ) return ligne;
		else               return dTmp.getValeur() + "";
	}
}
