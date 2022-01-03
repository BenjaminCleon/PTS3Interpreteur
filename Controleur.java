package AlgoPars;

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

		this.ihm.afficher(0);
	}

	public String getFichier(int n)
	{
		return this.metier.getFichier(n);
	}

	public String getDonnee()
	{
		return this.metier.getDonnee();
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