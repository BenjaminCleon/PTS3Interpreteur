package AlgoPars.Vue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.HashMap;
import java.io.File;

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
	 * Constructeur principale
	 */
	public CUI(Controleur controleur)
	{
		this.controleur = controleur;
		this.lstMotsColoration = new ArrayList<String>();
		this.hashMap = new HashMap<String, ArrayList<String>>();
		Console.normal();
		lectureXML();
	}

	public void lectureXML()
	{
		//CUI.lst = new ArrayList<String>();

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
	}
	/**
	 * Méthode ayant pour objectif l'affichage complet
	 * @param n
	 */
	public void afficher(int n)
	{
		String res;

		String fichier, data;
		String[] tabFichier, tabData;

		fichier = this.controleur.getFichier(n);
		data    = this.controleur.getDonnees();

		tabFichier = fichier.split("\n");//le divise par ligne
		tabData    = data.split("\n");
		Console.normal();
		res = putColor("defaut") + CouleurConsole.NOIR.getFond() + "\n";
		res += "¨¨¨¨¨¨¨¨¨¨" + String.format("%-74s", "") + "¨¨¨¨¨¨¨¨¨¨¨\n";
		res += "|  CODE  |" + String.format("%-74s", "") + "| DONNEES |\n";
		for ( int i=0;i<84;i++)res+="¨";
		res += " ";
		for ( int i=0;i<42;i++)res+="¨";

		res += "\n";

		res += "|" + String.format("%-83s", tabFichier[0]) + "|     NOM         |          VALEUR       |\n";
		
		for (int i=1;i<40;i++)
		{
		
			int caractere = 0;
						
			String numLig = tabFichier[i].substring(0,2);
			String ligne = tabFichier[i].substring(2);

			Boolean bOk = false;
			for(char c : ligne.toCharArray())
				if (c != ' ') bOk = true;
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
						nouvelleLigne += putColor(tabSplit[cpt]) + tabSplit[cpt] + putColor("defaut") + " ";
					else
						nouvelleLigne += tabSplit[cpt] + " ";

				}
				res += "|" + String.format("%-83s", nouvelleLigne) + "|" + tabData[i-1] + "|\n";
			}
			else // Si c'est que des espaces
			{
				res += "|" + String.format("%-83s", tabFichier[i]) + "|" + tabData[i-1] + "|\n";
			}
		}
		for ( int i=0;i<127;i++)res+="¨";	

		res += "\n\n¨¨¨¨¨¨¨¨¨¨¨\n" + "| CONSOLE |\n";
		for ( int i=0;i<127;i++)res+="¨";
		
		res += "\n" + this.controleur.getTraceDexecution() + "\n";

		for ( int i=0;i<127;i++)res+="¨";

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
		{
			if(this.hashMap.get(couleur).contains(mot))
				sCoul = couleur;
		}
		switch(sCoul)
		{
			case "CYAN" -> sCoul = CouleurConsole.CYAN .getFont();
			case "ROUGE"-> sCoul = CouleurConsole.ROUGE.getFont();
			case "BLEU" -> sCoul = CouleurConsole.BLEU .getFont();
			default     -> sCoul = CouleurConsole.BLANC.getFont();
		}
		return sCoul;
	}
}