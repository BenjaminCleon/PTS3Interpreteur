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

	public void afficher(int n)
	{
		String res = "";
		res += "¨¨¨¨¨¨¨¨¨¨" + String.format("%-74s", "") + "¨¨¨¨¨¨¨¨¨¨¨\n";
		res += "|  CODE  |" + String.format("%-74s", "") + "| DONNEES |\n";
		for ( int i=0;i<84;i++)res+="¨";
		res += " ";
		for ( int i=0;i<10;i++)res+="¨";

		
		System.out.println(res);
	}
}
