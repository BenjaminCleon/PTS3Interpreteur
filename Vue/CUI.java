package AlgoPars.Vue;

import AlgoPars.Controleur;

/**
 * Classe principale liée à la vue du modèle MVC
 * @author LHEAD
 */
public class CUI
{
	private Controleur controleur; // controleur associé

	/**
	 * Constructeur principale
	 */
	public CUI(Controleur controleur)
	{
		this.controleur = controleur;
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
		data    = this.controleur.getDonnee ( );

		tabFichier = fichier.split("\n");
		tabData    = fichier.split("\n");

		res = "\n";
		res += "¨¨¨¨¨¨¨¨¨¨" + String.format("%-74s", "") + "¨¨¨¨¨¨¨¨¨¨¨\n";
		res += "|  CODE  |" + String.format("%-74s", "") + "| DONNEES |\n";
		for ( int i=0;i<84;i++)res+="¨";
		res += " ";
		for ( int i=0;i<10;i++)res+="¨";

		res += "\n";

		for (int i=0;i<40;i++)
			res += "|" + tabFichier[i] + "|" + tabData[i] + "|\n";

		for ( int i=0;i<95;i++)res+="¨";
		
		res += "\n\n¨¨¨¨¨¨¨¨¨¨¨\n" + "| CONSOLE |\n";
		for ( int i=0;i<95;i++)res+="¨";
		
		// ajouter la trace d'éxécution

		for ( int i=0;i<95;i++)res+="¨";

		System.out.println(res);
	}
}
