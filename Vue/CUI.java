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
		data    = this.controleur.getDonnees();

		tabFichier = fichier.split("\n");
		tabData    = data.split("\n");

		res = "\n";
		res += "¨¨¨¨¨¨¨¨¨¨" + String.format("%-74s", "") + "¨¨¨¨¨¨¨¨¨¨¨\n";
		res += "|  CODE  |" + String.format("%-74s", "") + "| DONNEES |\n";
		for ( int i=0;i<84;i++)res+="¨";
		res += " ";
		for ( int i=0;i<42;i++)res+="¨";

		res += "\n";

		res += "|" + tabFichier[0] + "|     NOM         |          VALEUR       |\n";

		for (int i=1;i<40;i++)
			res += "|" + tabFichier[i] + "|" + tabData[i-1] + "|\n";
		
		for ( int i=0;i<127;i++)res+="¨";	

		res += "\n\n¨¨¨¨¨¨¨¨¨¨¨\n" + "| CONSOLE |\n";
		for ( int i=0;i<127;i++)res+="¨";
		
		res += "\n" + this.controleur.getTraceDexecution() + "\n";

		for ( int i=0;i<127;i++)res+="¨";

		System.out.println(res);
	}
}
