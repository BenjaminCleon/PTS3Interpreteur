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
	public static void lire(String ligne, Interpreteur interpreteur)
	{
		String saisie;
		String var   ;
		String nomVar;
		Donnee tmp   ;
		
		int ind = -1;

		interpreteur.actualiser();

		if ( ligne.contains("(") )ligne = ligne.substring(ligne.indexOf("(")+1, ligne.indexOf(")")).replaceAll(" ", "");

		try
		{
			if ( ligne.contains(",") )var = ligne.substring(0, ligne.indexOf(",") );
			else                      var = ligne.substring(0);

			if ( var.contains("[") )
			{
				nomVar = var.substring(0, var.indexOf("["));
				ind    = Integer.parseInt(var.substring(var.indexOf("[")+1, var.indexOf("]")));
			}
			else
			{
				nomVar = var;
			}
			
			tmp = interpreteur.getDonnee(nomVar);
			//Scanner sc = new Scanner(System.in);
			saisie = Console.lireString();//sc.next(); 
			Util.setValeurBySwitch(tmp, saisie, ind);
			//sc.close();
			if ( ligne.contains(",") )EntreeSortie.lire(ligne.substring(ligne.indexOf(",")+1), interpreteur);
		}catch(Exception e)
		{
			System.out.println("Erreur 101");
			e.printStackTrace();
		}
	}

	/**
	 * Permet de faire l'équivalent d'un écrire
	 * @param ligne
	 *   ligne a interprété
	 */
	public static String ecrire(String ligne, Interpreteur interpret)
	{
		ligne = ligne.substring(ligne.indexOf("(")+1, ligne.indexOf(")"));
		return String.format("| %-123s |", EntreeSortie.ecrireRec(ligne.replaceAll(" ", ""), interpret));
	}

	private static String ecrireRec(String ligne, Interpreteur interpret)
	{
		Donnee data = interpret.getDonnee(ligne);
		return data.getValeur() + "";
	}
}
