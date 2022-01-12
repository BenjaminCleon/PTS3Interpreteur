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
	public void setNumLigne(int n)
	{
		if ( this.numLigne >= 0)this.numLigne = n;
	}
	/*
	 * Lecture de l'utilisateur pour se deplacer dans le code
	*/ 
	public void lectureUtilisateur()
	{
		String line;

		try
		{
			while(this.numLigne<this.metier.getSizeContenu())
			{
				this.ihm.afficher(this.numLigne);
				
				line = Console.lireString();//input.nextLine();
				if(line.matches("DET var \\w*"))
				{
					String var=line.substring(8,(line.length()));
					System.out.println (this.metier.getTraceVariable(var));
					
					line = Console.lireString();//input.nextLine();
					while(! line.isEmpty() ) 
					{
						if(line.equals("PP")) this.metier.traceVariableCopie(var);
						line = Console.lireString();//input.nextLine();
					}
				}
				else
				{
					switch(line.toUpperCase())
					{
						case ""  -> {this.metier.interpreter(++this.numLigne);}
						case "B" -> {this.setNumLigne(--this.numLigne);this.metier.goTo(this.numLigne);}
						case "TRACE" -> {this.metier.trace();}
						case "GO BK" -> {this.metier.goNextBk(this.numLigne);}
						default ->
							{
								switch((line.charAt(0) + "").toUpperCase())
								{
									case "L" -> {
													this.metier.goTo(Integer.parseInt(line.substring(1)));
												}
									case "I" -> {/*S'arreter dans une itération */}
								}
							}
					}
				}
				if( line.toLowerCase().contains("+ bk"))this.metier.addBk(Integer.parseInt(line.substring(5)));
				if( line.toLowerCase().contains("- bk"))this.metier.rmBk(Integer.parseInt(line.substring(5)));
				
				
			}
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
	
	public void changerLigne (int n)
	{
		this.numLigne = n;
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
