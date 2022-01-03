import Metier.Interpreteur;
import Vue.CUI;

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
	public Controleur()
	{

	}

	/**
	 * 
	 * @param args
	 *     arguments passé au lancement du programme
	 */
	public static void main(String[] args)
	{
		new Controleur();
	}
}