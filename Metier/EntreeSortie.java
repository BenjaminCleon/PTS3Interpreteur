package AlgoPars.Metier;

import java.util.Scanner;

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
		Donnee tmp   ;

		ligne = ligne.substring(ligne.indexOf("(")+1, ligne.indexOf(")")).replaceAll(" ", "");
		tmp   = interpreteur.getDonnee(ligne);

		try
		{
			Scanner sc = new Scanner(System.in);
			System.out.print("saisie: ");
			saisie = sc.nextLine();
			System.out.println(saisie);

			Util.setValeurBySwitch(tmp, saisie);
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
