package AlgoPars.Metier;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import iut.algo.Console;

import AlgoPars.Metier.Interpreteur;
import AlgoPars.Metier.Donnee      ;

/**
 * Classe permettant de réaliser les entrées et les sorties en pseudo-code
 * @author LHEAD
 */
public class EntreeSortie
{
	private static ArrayList<Integer> alNum = new ArrayList<Integer>();
	private static Map <Integer, ArrayList<Object>> hashMap = new HashMap<Integer, ArrayList<Object>>();
	private static int cpt;
	
	/**
	 * Permet de lire une donnée
	 * @param data
	 */
	public static String lire(String ligne, int numero, Interpreteur interpreteur)
	{
		String saisie = "";
		String var   ;
		String nomVar;
		Donnee tmp   ;
		
		int ind = -1;
		
		interpreteur.actualiser();

		if ( ligne.contains("(") )ligne = ligne.substring(ligne.indexOf("(")+1, ligne.lastIndexOf(")")).replaceAll(" ", "");

		if(!interpreteur.getBw())
		{
			saisie = "";
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
				Console.print("Ecrivez une valeur pour " + nomVar + " : ");
				saisie = Console.lireString();//sc.next(); 
				Util.setValeurBySwitch(tmp, saisie);

				if(EntreeSortie.hashMap.containsKey(numero))
					hashMap.get(numero).add(saisie);
				else
				{
					ArrayList<Object> alObj = new ArrayList<Object>(); 
					alObj.add(saisie);
					hashMap.put(numero, alObj);
				}

				if ( ligne.contains(",") )return saisie + " " + EntreeSortie.lire(ligne.substring(ligne.indexOf(",")+1), numero, interpreteur);
			}catch(Exception e)
			{
				System.out.println("Erreur 101");
				e.printStackTrace();
			}
		}
		else
		{
			String[] vars = ligne.split(",");// tableau des variables
			String tmpSaisie = "";
			if( hashMap.get(numero) != null)
			{
				if( hashMap.get(numero).size()>1 )
				{
					for(int i = 0; i<hashMap.get(numero).size(); i++)
					{
						tmp = interpreteur.getDonnee(vars[i]);
						Object o = EntreeSortie.hashMap.get(numero).get(i);
						tmpSaisie = (String)o;
						Util.setValeurBySwitch(tmp, tmpSaisie);
						saisie += tmpSaisie + " ";
					}
				}
				else//Si il n'y a qu'une variable à lire
				{
					tmp = interpreteur.getDonnee(vars[0]);
					saisie = (String)EntreeSortie.hashMap.get(numero).get(0);
					Util.setValeurBySwitch(tmp, saisie);
				}
			}
		}
		return saisie;
	}

	/**
	 * Remet à 0 la liste des variables sauvegardées dans la HashMap
	 * 
	 * @param n
	 * 		Ligne jusqu'a laquelle nous souhaitons aller
	 */
	public static void resetHashMap(int n)
	{
		for(Integer i : EntreeSortie.hashMap.keySet())
			if(i>n)EntreeSortie.hashMap.get(i).clear();
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
			res += Util.expression(s, interpret);
		
		return res;
	}
}
