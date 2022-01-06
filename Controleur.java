package AlgoPars;

import iut.algo.Console;

import java.util.List;
import java.util.Scanner;

import AlgoPars.Metier.Interpreteur;
import AlgoPars.Vue.CUI;

/**
 * Classe controleur de l'interpréteur
 * C'est à partir de cette classe que l'on peut lancer l'interpréteur
 * @author LHEAD
 */
public class Controleur
{
	private CUI          ihm   ; // Partie visuelle
	private Interpreteur metier; // Partie métier

	private int numLigne       ; // numéro de la ligne actuelle

	/**
	 * Constructeur de la classe Controleur
	 */
	public Controleur(String nomFic)
	{
		this.ihm    = new CUI         (this);
		this.metier = new Interpreteur(this, nomFic);

		this.numLigne = 0;

		this.lectureUtilisateur();
	}

	public String getFichier(int n)
	{
		return this.metier.getFichier(n);
	}

	public String getDonnees()
	{
		return this.metier.getDonnees();
	}

	/*
	 * Lecture de l'utilisateur pour se deplacer dans le code
	*/ 
	public void lectureUtilisateur()
	{
		String line;

		try
		{
			//Scanner input = new Scanner(System.in);
			while(this.numLigne<this.metier.getSizeContenu())
			{
				this.ihm.afficher(this.numLigne);
				
				line = Console.lireString();//input.nextLine();
				switch(line.toUpperCase())
				{
					case ""  -> {this.numLigne++;}
					case "B" -> {this.numLigne--;}
					default ->
					{
						switch((line.charAt(0) + "").toUpperCase())
						{
							case "L" -> {this.numLigne = Integer.parseInt(line.substring(1));}
							case "I" -> {/*S'arreter dans une itération */}
						}
					}
				}
				this.metier.interpreter(this.numLigne);
			}
			//input.close();
		} catch (Exception e) {System.out.println("Erreur 002 : Deplacement ligne par ligne"); e.printStackTrace();}
	}

	/**
	 * 
	 * @param args
	 */
	public void actualiser()
	{
		this.ihm.afficher(this.numLigne);
	}

	/**
	 * 
	 * @return la trace d'éxécuton
	 */
	public List<String> getTraceDexecution()
	{
		return this.metier.getTraceDexecution();
	}

	/**
	 * 
	 * @return la trace des numéros ou il y a eu un lire
	 */
	public List<Integer> getTraceLire()
	{
		return this.metier.getTraceLire();
	}

	/**
	 * @param args
	 *     arguments passé au lancement du programme
	 */
	public static void main(String[] args)
	{
		try
		{
			new Controleur(args[0]);
		}catch(Exception e)
		{
			System.out.println("Passer le nom du fichier en paramètre sans extension\n java AlgoPars.Controleur Monfichier");
		}
	}
}
