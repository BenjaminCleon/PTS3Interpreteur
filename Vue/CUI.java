package AlgoPars.Vue;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.HashMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.FileOutputStream;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import AlgoPars.Controleur;

import iut.algo.Console;
import iut.algo.CouleurConsole;

/**
 * Classe principale liée à la vue du modèle MVC
 * @author LHEAD
 */
public class CUI
{
	private Controleur controleur; // controleur associé
	private static ArrayList<String> lstMotsColoration;
	private HashMap<String, ArrayList<String>> hashMap;

	/**
	 * Constructeur principal
	 */
	public CUI(Controleur controleur)
	{
		this.controleur = controleur;
		this.lstMotsColoration = new ArrayList<String>();
		this.hashMap = new HashMap<String, ArrayList<String>>();
		Console.normal();

		this.lectureXML();
	}
	/**
	 * Permet la lecture du fichier coloration.xml
	 * 
	 * Le fichier coloration.xml modifie les couleurs des mots des instructions dans le code du sous-programme
	 */
	public void lectureXML()
	{
		Element  racine;
		Document document;

		
		SAXBuilder sxb = new SAXBuilder();

		/*----------------------------*/
		/*        Instructions         */
		/*----------------------------*/

		document = null;
		try
		{
			document = sxb.build(new File("./coloration.xml"));	
		}catch(Exception e){e.printStackTrace();}

		//Balise france
		racine = document.getRootElement();

		List<Element> lst1 = racine.getChildren("categorie");//4 listes

		for (Element e: lst1)
		{
			List<Element> lstMots = e.getChildren("mots");
			ArrayList<String> alMots = new ArrayList<String>();
			for(Element mot : lstMots)
			{
				alMots.add(mot.getText());
			}
			
				if (!this.hashMap.containsKey(e.getAttributeValue("couleur")))
					this.hashMap.put(e.getAttributeValue("couleur"), alMots);
				else
					this.hashMap.get(e.getAttributeValue("couleur"));
		}
		ArrayList<String> alReset = new ArrayList<String>();
		alReset.add(lst1.get(lst1.size()-1).getAttributeValue("nom"));
		this.hashMap.put(lst1.get(lst1.size()-1).getAttributeValue("couleur"),alReset);
		
		this.ajouterVariableHm();

	}
	
	/**
	 * Ajoute dans une {@code HashMap} des mots à colorier, la liste des variables
	 * située dans le fichier .var 
	 */
	public void ajouterVariableHm()
	{
		this.hashMap.put("MAUVE", this.controleur.getListeVariable());
		System.out.println(this.hashMap);

	}

	/**
	 * Remet la console en mode normal
	 * @return
	 * 		Retourne un caractère vide
	 */
	public String consoleReset()
	{
		Console.normal();
		return "";
	}

	/**
	 * Affiche l'aide dans la console
	 * 
	 * L'aide est composée de la liste des instructions possible dans la console
	 */
	public void afficherAide()
	{
		String sRet = "";
		Console.effacerEcran();
		for ( int i=0;i<40;i++)sRet+="¨";
		sRet += "\n\tListes des fonctionnalités \n\n";
		sRet += String.format("%-38s", "Touche \"entrée\"") + "\t" + String.format("%-15s", "Avance ligne par ligne dans la console.\n");
		sRet += String.format("%-38s", "Touche \"b\"") + "\t" + String.format("%-15s", "Recule ligne par ligne dans la console.\n");
		sRet += String.format("%-38s", "Touche \"L\" + numero") + "\t" + String.format("%-30s", "Se rend à une ligne précise dans la console. (Exemple L41 -> Se déplace à la ligne 41)\n");
		sRet += String.format("%-38s", "Touche \"+\" \"bk\" ligne") + "\t" + String.format("%-15s", "Ajoute un point d'arret à la ligne voulue. (Exemple + bk 10 -> Ajoute un point à la ligne 10)\n");
		sRet += String.format("%-38s", "Touche \"-\" \"bk\" ligne") + "\t" + String.format("%-15s", "Retire un point d'arret à la ligne voulue. (Exemple - bk 10 -> Retire un point à la ligne 10)\n");
		sRet += String.format("%-38s", "Touche \"GO BK\"") + "\t" + String.format("%-15s", "Avance au prochain point d'arret dans la console.\n");
		sRet += String.format("%-38s", "Touche \"DET var\" +  nom variable") + "\t" + String.format("%-15s", "Affiche le détail d'une variable voulue. (Exemple DET var x -> Affiche le détail de la variable x)\n");
		sRet += String.format("%-38s", "Touche \"PP\"") + "\t" + String.format("%-15s", "Ajoute le détail de la variable dans le presse papier.\n");
		sRet += String.format("%-38s", "Touche \"TRACE\"") + "\t" + String.format("%-15s", "Génére la trace des variables sous forme tabulaire dans le presse-papier.\n");

		sRet += String.format("%-38s", "\nTouche Q pour quitter l'aide.");
		Console.println(sRet);
		
		char input = Console.lireChar();
		while((input != 'q') && (input != 'Q'))
			input = Console.lireChar();
	}

	/**
	 * Méthode ayant pour objectif l'affichage complet
	 * @param n
	 */
	public void afficher(int n)
	{
		String delta;
		String res;

		String fichier, data;
		String[] tabFichier, tabData;

		List<String>  execution;
		List<Integer> traceLire;

		fichier   = this.controleur.getFichier(n);
		data      = this.controleur.getDonnees() ;
		execution = this.controleur.getTraceDexecution();

		tabFichier = fichier.split("\n");//le divise par ligne
		tabData    = data.split("\n");
		Console.normal();
		res = "\n";

		Console.effacerEcran();
		res += CouleurConsole.BLANC.getFond() + "" + CouleurConsole.NOIR.getFont() +"¨¨¨¨¨¨¨¨¨¨" + String.format("%-74s", "") + "¨¨¨¨¨¨¨¨¨¨¨" + String.format( "%-31s", "") + "¨¨¨¨¨¨¨¨¨¨¨" + String.format( "%-36s", "") + this.consoleReset() + "\n";
		res += CouleurConsole.BLANC.getFond() + "" +"|  CODE  |" + String.format("%-74s", "") + "| DONNEES |" + String.format( "%-31s", "") + "|EXECUTION|" + String.format( "%-36s", "") + this.consoleReset() + "\n";
		for ( int i=0;i<84;i++)res+="¨";
		res += " ";
		for ( int i=0;i<41;i++)res+="¨";
		res += " ";
		for ( int i=0;i<46;i++)res+="¨";
		res +=  this.consoleReset() + "\n";

		res += "| " + String.format("%-83s", tabFichier[0]) + "|     NOM         |          VALEUR       |";
		this.afficher(res, -1);
		this.afficher(String.format(" %-42s |",this.getValueInExec(execution, 0)), 0);
		Console.println("");
		
		for (int i=1;i<40;i++)
		{
			int caractere = 0;
						
			String numLig = tabFichier[i].substring(0,2);
			String ligne = tabFichier[i].substring(2);

			Boolean bOk = false;
			for(char c : ligne.toCharArray())if (c != ' ') bOk = true;

			if(bOk)//Si c'est pas que des espaces
			{		
				while(ligne.charAt(caractere) == ' ' && caractere < ligne.length())
					caractere++;
			
				String[] tabSplit = ligne.substring(caractere).split(" ");
							
				String nouvelleLigne = numLig;
				//Replacement des espaces
				for(int str=0; str<caractere; str++)
					nouvelleLigne += " ";

				//Replacement des mots
				for(int cpt=0; cpt<tabSplit.length; cpt++)
				{
					if(this.estDansListe(tabSplit[cpt]))
						nouvelleLigne += putColor(tabSplit[cpt]) + tabSplit[cpt] + CouleurConsole.NOIR.getFont()  + " ";
					else
						nouvelleLigne += tabSplit[cpt] + " ";

				}
				String tmp = this.getValueInExec(execution, i);

				Console.print("|" + String.format("%-83s", nouvelleLigne) + "|" + tabData[i-1] + "|");
				delta = String.valueOf(42-tmp.length());
				this.afficher(" ", -1);
				this.afficher(tmp, i);
				this.afficher(String.format(" %-" + delta + "s", "") + "|\n", -1);
			}
			else // Si c'est que des espaces
			{
				String tmp = this.getValueInExec(execution, i);

				Console.print("|" + String.format("%-83s", tabFichier[i]) + "|" + tabData[i-1] + "|");
				delta = String.valueOf(42-tmp.length());
				this.afficher(" ", -1);
				this.afficher(tmp, i);
				this.afficher(String.format(" %-" + delta + "s", "") + "|\n", -1);
			}
		}

		res = CouleurConsole.BLANC.getFond() +"";
		for ( int i=0;i<173;i++)res+= CouleurConsole.NOIR.getFont() + "¨";
		res += CouleurConsole.NOIR.getFond();
		res += this.consoleReset();
		res += CouleurConsole.BLANC.getFont();
		Console.println(res);
	}

	public ArrayList<String> getListeMot()
	{
		ArrayList<String> alRetour = new ArrayList<String>();
		for(String key : this.hashMap.keySet())
		{
			ArrayList<String> alTemp = this.hashMap.get(key);
			for(String s : alTemp)
				alRetour.add(s);
		}

		return alRetour;
	}

	public boolean estDansListe(String mot)
	{
		for(String s : this.getListeMot())
			if(s.equals(mot)) return true;
		return false;
	}

	public String putColor(String mot)
	{
		String sCoul = "";
		for(String couleur : this.hashMap.keySet())
			if(this.hashMap.get(couleur).contains(mot))
				sCoul = couleur;
		
		switch(sCoul)
		{
			case "CYAN"  -> sCoul = CouleurConsole.CYAN .getFont();
			case "MAUVE" -> sCoul = CouleurConsole.MAUVE.getFont();
			case "ROUGE" -> sCoul = CouleurConsole.ROUGE.getFont();
			case "BLEU"  -> sCoul = CouleurConsole.BLEU .getFont();
			default      -> sCoul = CouleurConsole.BLANC.getFont();
		}
		return sCoul;
	}

	public void afficher(String ligne, int n)
	{
		if ( this.controleur.getTraceLire().contains(n) )
		{
			Console.souligner();
			Console.print(ligne);
			Console.normal();
			Console.print( CouleurConsole.BLANC.getFond() );
		}
		else
		{
			Console.print(ligne);
		}
	}

	public String getValueInExec(List<String> execution, int i)
	{
		if ( execution != null )return (execution.size()>i)?execution.get(i):"";
		return "";
	}
}
