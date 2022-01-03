package AlgoPars;

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

	/**
	 * Constructeur de la classe Controleur
	 */
	public Controleur(String nomFic)
	{
		this.ihm    = new CUI         (this);
		this.metier = new Interpreteur(this, nomFic);

		this.lectureUtilisateur();
	}

	public String getFichier(int n)
	{
		return this.metier.getFichier(n);
	}

	public String getDonnee()
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
			Scanner input = new Scanner(System.in);
			int numLigne = 0;
			while(numLigne<this.metier.getSizeContenu())
			{
				this.ihm.afficher(numLigne);
				line = input.nextLine();
				switch(line.toUpperCase())
				{
					case ""  -> {numLigne++;}
					case "B" -> {numLigne--;}
					default ->
					{
						switch((line.charAt(0) + "").toUpperCase())
						{
							case "L" -> {numLigne = Integer.parseInt(line.substring(1))-1;}
							case "I" -> {/*S'arreter dans une itération */}
						}
					}
				}
				this.metier.interpreter(numLigne);
			}
			input.close();
		} catch (Exception e) {System.out.println("Erreur 002 : Deplacement ligne par ligne"); e.printStackTrace();}
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