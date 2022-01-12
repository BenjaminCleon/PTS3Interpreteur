package AlgoPars.Metier;

import AlgoPars.Controleur;
import AlgoPars.Metier.Donnee;
import AlgoPars.Metier.EntreeSortie;
import AlgoPars.Metier.GestionDonnee;

import java.io.FileInputStream;

import java.util.Stack;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Scanner;

import javax.lang.model.util.ElementScanner14;

import iut.algo.Console;
import iut.algo.CouleurConsole;

/**
 * Classe principale liée au modèle de l'architecture MVC
 * 
 * @author LHEAD
 */
public class Interpreteur
{
	private Controleur        controleur; // controleur associé

	private List<Donnee>       lstDonnee; // liste des données
	private List<String>      lstContenu; // contenu du fichier
	private List<Integer>          lstBk; // Liste des points d'arret
	private List<String> traceDexecution; // trace d'éxécution du code
	private List<Integer>      traceLire; // retient les numéros de ligne où il y a eu un lire

	private String            nomFichier; // nom du fichier à lire
	private int              numeroLigne; // Numéro de la ligne en cours
	private int                 lignePrc; // Numéro de la ligne précedente
	private int           numDebutBoucle; // Numéro du début d'une boucle
	private int             cptIteration; // Nombre d'itération d'une boucle

	private boolean            lectureVariable; // permet de connaitre si nous sommes dans la déclaration des variables
	private boolean           lectureConstante; // permet de connaitre si nous sommes dans la déclaration des constantes
	private boolean                         bw; // permet de connaitre si nous reculons dans le programme
	private boolean                  estALaFin; // permet de connaitre si nous sommes à la fin d'une boucle
	private boolean              estDansBoucle; // permet de connaitre si nous sommes dans une boucle
	private boolean    structureConditionnelle; // permet de connaitre si nous sommes à l'intérieur d'une structure conditionnelle
	private boolean structureConditionnelleAlt; // permet de connaitre si nous sommes à l'intérieur d'une structure conditionnelle alternative
	private boolean                  condition; // permet de connaitre le résultat du test pour entrer dans la structure conditionnelle
	private boolean         estDansCommentaire; // permet de savoir si nous sommes dans des commentaires

	// booléens qui permettent de savoir si il y a des commentaires
	private boolean enComm;
	private boolean commOk;

	private Stack<Boolean>    lstStructureConditionnelle; // Liste des booléens pour la structure conditionnelle
	private Stack<Boolean> lstStructureConditionnelleAlt; // Pour la structure conditionnelle alternative
	private Stack<Boolean>                  lstCondition; // Liste des conditions valides ou non
	private Stack<Integer>           lstLigneDebutBoucle; // Numéro de la ligne ou il y a un début de boucle

	private GestionDonnee gestionDonnee; // permet de gérer les données souhaitant être traiter

	/**
	 * Constructeur principale
	 * Il initialise toutes les variables
	 * @param controleur
	 * 		Controleur de l'application
	 * @param nomFichier
	 * 		Nom du fichier .algo
	 */
	public Interpreteur(Controleur controleur, String nomFichier)
	{
		this.controleur = controleur;
		this.nomFichier = nomFichier;

		this.numeroLigne    = 0;
		this.lignePrc       = 0;
		this.cptIteration   = 0;
		this.numDebutBoucle = 0;

		this.lstContenu      = new ArrayList<String> ();
		this.lstDonnee       = new ArrayList<Donnee> ();
		this.lstBk           = new ArrayList<Integer>();
		this.traceDexecution = new ArrayList<String> ();
		this.traceLire       = new ArrayList<Integer>();

		this.lectureVariable            = false;
		this.lectureConstante           = false;
		this.bw                         = false;
		this.structureConditionnelle    = false;
		this.structureConditionnelleAlt = false;
		this.estDansBoucle              = false;
		this.estALaFin                  = false;
		this.enComm                     = false;
		this.estDansCommentaire         = false;
		this.commOk                     =  true;
		this.condition                  =  true;

		this.lstStructureConditionnelle    = new Stack<Boolean>();
		this.lstStructureConditionnelleAlt = new Stack<Boolean>();
		this.lstCondition                  = new Stack<Boolean>();

		this.lstLigneDebutBoucle = new Stack<Integer>();

		this.lectureFichier();
		this.gestionDonnee = new GestionDonnee(nomFichier, this);
	}

	/**
	 * Définit le numéro de la ligne en cours
	 * @param numeroLigne
	 * 		Numéro de la ligne 
	 */
	public void setNumeroLigne(int numeroLigne)
	{
		this.numeroLigne = numeroLigne;
	}

	/**
	 * retourne la liste des variables
	 * @return
	 *   liste des variables
	 */
	public ArrayList<String> getListeVariable()
	{
		return this.gestionDonnee.getListeVariable();
	}

	/**
	 * Interprete la ligne n.
	 * Elle interprète la ligne en partant du principe que ce qui est au-dessus est interprété
	 * 
	 * @param n
	 * 		Ligne à interpreter
	 */
	public void interpreter(int n)
	{
		String ligneAInterpreter;

		int indexSimpleCom, indexDbGrosCom;
		int indexComment;
		boolean condition, structureConditionnelle, structureConditionnelleAlt;

		if (n < this.lstContenu.size() && n >= 0) // si nous sommes bien dans un intervalle valide
		{
			ligneAInterpreter = this.lstContenu.get(n);

			this.commOk = true;
			ligneAInterpreter = this.commenter(this.lstContenu.get(n)); // gestion des commentaires pour la ligne

			// mise à jour pour les structures conditionnelles
			condition                  =  true;
			structureConditionnelle    = false;
			structureConditionnelleAlt = false;

			if (!this.lstCondition.isEmpty())
				condition = this.lstCondition.peek();

			if (!this.lstStructureConditionnelle.isEmpty())
				structureConditionnelle = this.lstStructureConditionnelle.peek();

			if (!this.lstStructureConditionnelleAlt.isEmpty())
				structureConditionnelleAlt = this.lstStructureConditionnelleAlt.peek();

			// si la ligne peut-être interprété normalement
			if (condition && !structureConditionnelle && !structureConditionnelleAlt
					|| structureConditionnelle && condition || structureConditionnelleAlt && !condition)
			{
				indexComment = ligneAInterpreter.indexOf("//");

				// commentaire sur une ligne
				if (indexComment != -1)
					ligneAInterpreter = ligneAInterpreter.substring(0, indexComment);

				if (ligneAInterpreter.equals("DEBUT")) // mot clé DEBUT
					this.lectureConstante = this.lectureVariable = false;

				// prend connaissance que l'on commence la déclaration de variable
				if (ligneAInterpreter.equals("variable:"))
				{
					this.lectureConstante = false;
					this.lectureVariable = true;
				}

				// prend connaisssance que l'on commence la déclaration de constante
				if (ligneAInterpreter.equals("constante:"))
					this.lectureConstante = true;

				// dans le cas ou nous devons créer une donnée
				if ((this.lectureVariable || this.lectureConstante) &&
						!(ligneAInterpreter.equals("constante:") || ligneAInterpreter.equals("variable:")))
				{
					this.creerDonnee( ligneAInterpreter );
				}

				if (ligneAInterpreter.contains("ecrire")) // ecrire à l'ecran
					this.traceDexecution.add(EntreeSortie.ecrire(ligneAInterpreter, this));

				if (ligneAInterpreter.contains("<--")) // affectation
					this.affecter(ligneAInterpreter);

				if (ligneAInterpreter.contains("lire")) // lire une valeur pour lui affecter
				{
					this.traceDexecution.add(EntreeSortie.lire(ligneAInterpreter, n, this));
					this.traceLire.add(n);
				}
			}

			// getsion du si alors
			if (ligneAInterpreter.matches("\\s*si .* alors"))
			{
				ligneAInterpreter = ligneAInterpreter.replace("si", "");
				ligneAInterpreter = ligneAInterpreter.replace("alors", "");
				this.lstStructureConditionnelle.add(true);
				this.lstStructureConditionnelleAlt.add(false);

				if (Util.expression(ligneAInterpreter, this).matches("true"))
					this.lstCondition.add(true);
				else
					this.lstCondition.add(false);
			}

			// gestion du sinon
			if (ligneAInterpreter.matches("\\s*sinon$"))
			{
				this.lstStructureConditionnelleAlt.pop();
				this.lstStructureConditionnelleAlt.add(true);
				this.lstStructureConditionnelle.pop();
				this.lstStructureConditionnelle.add(false);
			}

			if (ligneAInterpreter.contains("fsi"))
			{
				this.lstStructureConditionnelle.pop();
				this.lstStructureConditionnelleAlt.pop();
				this.lstCondition.pop();
			}

			// cas d'une boucle de type tq faire
			if (ligneAInterpreter.matches("\\s*tq .* faire"))
			{
				this.estDansBoucle = true;
				ligneAInterpreter  = ligneAInterpreter.replace(   "tq", "");
				ligneAInterpreter  = ligneAInterpreter.replace("faire", "");

				if (Util.expression(ligneAInterpreter, this).matches("true"))
				{
					boolean present = false;
					for (Integer i : this.lstLigneDebutBoucle)
						if (i == n)
							present = true;

					if (!present)
					{
						this.numDebutBoucle = n;
						this.lstLigneDebutBoucle.add(n);
						this.lstCondition.add(true);
					}
				}
				else
				{
					this.lstCondition.add(false);
					this.estDansBoucle = false;
				}
			}

			// fin d'une boucle
			if (ligneAInterpreter.contains("ftq"))
			{
				this.cptIteration++;
				this.estALaFin = true;
				if (this.lstCondition.peek())
				{
					interpreter(this.lstLigneDebutBoucle.pop());
					if (this.lstCondition.pop())
					{
						this.controleur.setNumLigne(this.lstLigneDebutBoucle.peek());
					}
				}
				else
				{
					this.estDansBoucle = false;
					this.controleur.setNumLigne(n + 1);
					this.lstCondition.pop();
				}
			}
		}
		this.lignePrc = n;
	}

	/**
	 * Permet de s'arreter dans la boucle.
	 * La fonctionne s'arrete dans la boucle à l'iteration nombreDIteration - iteration
	 * @param iteration
	 *                  itération où l'on souhaite aller en partant de la fin
	 */
	public void arreterBoucle(int iteration)
	{

		while (this.estDansBoucle) 
			this.controleur.standardAction();

		int stop = this.cptIteration - iteration; // Nombre d'iteration à faire
		this.goTo( this.numDebutBoucle ); // On va au début de la boucle

		int cpt = 0;
		while (cpt < stop)
		{
			this.estALaFin = false;
			this.controleur.standardAction();
			if (this.estALaFin)
				cpt++;
		}

	}
	/**
	 * Retourne vrai si nous reculons dans le programme
	 * @return
	 * 		Retourne vrai si nous reculons dans le programme
	 */
	public boolean getBw()
	{
		return this.bw;
	}

	/**
	 * Remet à 0 toutes les variables
	 */
	public void reset()
	{
		this.bw = true;

		this.lectureVariable = false;
		this.lectureConstante = false;
		this.structureConditionnelle = false;
		this.structureConditionnelleAlt = false;
		this.condition = true;

		this.enComm = false;
		this.commOk = true;

		this.estDansCommentaire = false;
		this.lstDonnee = new ArrayList<Donnee>();
		this.traceDexecution = new ArrayList<String>();
		this.lstLigneDebutBoucle = new Stack<Integer>();
	}

	/**
	 * Permet de se déplacer jusqu'a la ligne en paramètre.
	 * Elle réexecute le programme en partant de la première ligne du fichier
	 * @param n
	 * 		Ligne jusqu'a laquelle nous souhaitons aller
	 */
	public void goTo(int n)
	{
		if (n < 0 || n >= this.lstContenu.size()) return;

		int courant = 0;
		if (this.lignePrc > n)// Si on recule
			this.reset();
		else
			courant = this.lignePrc;

		this.controleur.setNumLigne( courant );
		while (this.controleur.getNumLigne() < n)
		{
			this.controleur.standardAction();
		}

		this.controleur.actualiser();
		this.resetHashMap( n );
		this.bw = false;
	}

	/**
	 * Remet à 0 la liste des variables sauvegardées dans la HashMap
	 * 
	 * @param n
	 * 		Ligne jusqu'a laquelle nous souhaitons aller
	 */
	public void resetHashMap(int n)
	{
		EntreeSortie.resetHashMap( n );
	}

	/**
	 * Retourne la trace d'execution actuelle
	 * 
	 * @return
	 */
	public List<String> getTraceDexecution()
	{
		return this.traceDexecution;
	}

	/**
	 * Retourne la trace des numéros de ligne lu
	 * 
	 * @return
	 */
	public List<Integer> getTraceLire()
	{
		return this.traceLire;
	}

	/**
	 * Permet de lire le fichier .algo et de récupérer son contenu
	 */
	public void lectureFichier()
	{
		String line = "";
		try
		{
			Scanner sc = new Scanner(new FileInputStream(nomFichier + ".algo"), "UTF8");
			while (sc.hasNextLine())
			{
				line = sc.nextLine();
				this.lstContenu.add(line.replaceAll("\t", "    "));
			}
			sc.close();
		} catch (Exception e) {	System.out.println("Erreur 001 : Lecture du fichier .algo");e.printStackTrace();}
	}

	/**
	 * Renvoie la donnée avec le nom en paramètre
	 * 
	 * @param nom de la donnée
	 * @return
	 *         la donnée associée au nom
	 */
	public Donnee getDonnee(String nom)
	{
		if (nom.indexOf('[') != -1)
			nom = nom.substring(0, nom.indexOf('['));

		for (Donnee data : this.lstDonnee)
			if (data.getNom().equals(nom))
				return data;

		return null;
	}

	/**
	 * retourne le nombre de données
	 * 
	 * @return
	 *         nombre de données
	 */
	public int getNbDonnee()
	{
		return this.lstDonnee.size();
	}

	/**
	 * Retourne le contenu avec + ou - 40 lignes
	 * 
	 * @param n
	 *          la ligne où se fait l'interprétation
	 * @return
	 *         le contenu du fichier sous forme de chaine de caractères
	 */
	public String getFichier(int n)
	{
		String res;
		String tmp;
		int size;
		int i;
		int l;

		res = "";
		size = this.lstContenu.size();
		this.setNumeroLigne( n );

		if (size <= 40)
		{
			for (l = 0; l < size; l++)
				res += this.getLigne( l );

			for (int a = l; a < 40; a++)
				res += String.format("%-80s", " ") + "\n";
		}
		else // Il y a plus de 40 lignes
		{
			int move = 0;
			if (this.numeroLigne < 20)
			{
				// Affichage des 40 premieres lignes
				for (i = 0 + move; i < 40 + move; i++)
					res += this.getLigne( i );
			}
			else
			{
				move = this.numeroLigne - 20;

				if ((size - move) > 40)
					for (int j = move; j < size; j++)
						res += this.getLigne( j );
				else
					for (int j = size - 40; j < size; j++)
						res += this.getLigne( j );
			}
		}
		return res;
	}

	/**
	 * Change le format de la ligne en paramètre
	 * 
	 * @param val
	 *            la ligne à lire
	 * @return
	 *         la ligne à lire avec le bon format
	 */
	public String getLigne(int val)
	{
		String sRet = "";
		String sVal = "";
		if (this.numeroLigne == val) {
			if (this.lstContenu.get(val).matches("\\s*si .* alors") && !this.lstCondition.isEmpty()
					&& this.lstCondition.peek())
				sRet = CouleurConsole.VERT.getFond() + "";
			else if (this.lstContenu.get(val).matches("\\s*si .* alors") && !this.lstCondition.isEmpty()
					&& !this.lstCondition.peek())
				sRet = CouleurConsole.ROUGE.getFond() + "";
			else
				sRet = CouleurConsole.JAUNE.getFond() + "";
		}

		if (this.lstBk.contains(val))
		{
			sVal = CouleurConsole.ROUGE.getFont() + "" + String.format("%3d ", val) + CouleurConsole.NOIR.getFont();
		}
		else
		{
			sVal = CouleurConsole.NOIR.getFont() + String.format("%3d ", val) + "";
		}
		sRet += sVal + String.format("%-80s", this.lstContenu.get(val)) + CouleurConsole.BLANC.getFond() + "\n";
		return sRet;
	}

	/**
	 * Se déplace à la ligne du prochain point d'arret
	 * @param courant
	 * 		Numéro de la ligne courante
	 */
	public void goNextBk(int courant)
	{
		if (this.lstBk.isEmpty()) return;

		if (this.lstBk.size() == 1)
		{
			this.goTo( this.lstBk.get(0) );
		} else
		{
			for (int i = 0; i < this.lstBk.size(); i++) {
				if (this.lstBk.get(i) > courant)// a modifier
				{
					this.goTo( this.lstBk.get(i) );
					this.controleur.setNumLigne( this.lstBk.get( i ) );
					return;
				}
			}
		}
	}

	/**
	 * Ajoute un point d'arret.
	 * Il réorganise la liste des points d'arret par ordre croissant
	 * @param ligne
	 *		Ligne du point d'arret
	 * @return
	 *		Retourne true si la ligne est ajoutée
	 */
	public boolean addBk(int ligne)
	{
		if (ligne > this.getSizeContenu() || ligne < 0 || this.lstBk.contains(ligne)) return false;
		
		this.lstBk.add( ligne );

		Collections.sort( this.lstBk );
		return true;
	}

	/**
	 * Enleve un point d'arret.
	 * Il réorganise la liste des points d'arret par ordre croissant des lignes
	 * 
	 * @param ligne
	 *              Ligne du point d'arret
	 * @return
	 *         Retourne vrai si la ligne est enlevée
	 */
	public boolean rmBk(int ligne)
	{
		if ( !this.lstBk.contains(ligne) ) return false;

		for (int i = 0; i < this.lstBk.size(); i++)
			if (this.lstBk.get(i) == ligne)
				this.lstBk.remove( i );
		
		Collections.sort( this.lstBk );
		return true;
	}

	/**
	 * permet de creer une donnee.
	 * constante ou variable
	 * 
	 * @param ligne
	 *              la ligne à traiter
	 */
	public void creerDonnee(String ligne)
	{
		Donnee tmp;
		String nom;
		tmp = null;
		if (this.lectureVariable)
		{
			String[] l = ligne.split(":");
			l[0].replaceAll(" |\t", "");
			String[] lSplit = l[0].split(",");

			for (int i = 0; i < lSplit.length; i++)
			{
				// de la ligne ou on est
				nom = lSplit[i].replaceAll(" |\t", "");

				if (l[1].matches("(.*)tableau(.*)"))
				{
					String type = "";
					String[] lig = l[1].split("de|d'", 2);
					type = lig[1].replaceFirst(" ", "");
					type = type.replaceFirst("s", "");

					String indices = ligne;
					indices = indices.substring(indices.indexOf("[") + 1, indices.lastIndexOf("]"));
					indices = indices.replaceAll("\\[|\\]$", "");

					String[] taille = indices.split("\\]");
					for (int cpt = 0; cpt < taille.length; cpt++)
						if (this.getDonnee(taille[cpt]) != null)
							taille[cpt] = (this.getDonnee(taille[cpt]).getValeur()) + "";

					Integer[] dims = new Integer[taille.length];
					for (int cpt = 0; cpt < taille.length; cpt++)
						dims[cpt] = Integer.parseInt(taille[cpt]);

					// création de tableau
					switch (this.getType(ligne))
					{
						case Type.ENTIER  -> tmp = new Donnee(nom, type, new ArrayList<Integer>  (), this.lectureConstante,dims);
						case Type.REEL    -> tmp = new Donnee(nom, type, new ArrayList<Double>   (), this.lectureConstante,dims);
						case Type.BOOLEEN -> tmp = new Donnee(nom, type, new ArrayList<Boolean>  (), this.lectureConstante,dims);
						case Type.CHAR    -> tmp = new Donnee(nom, type, new ArrayList<Character>(), this.lectureConstante,dims);
						default           -> tmp = new Donnee(nom, type, new ArrayList<String>   (), this.lectureConstante,dims);
					}
					this.lstDonnee.add(tmp);
				}
				else
				{ // cas classique
					switch (this.getType(ligne))
					{
						case Type.ENTIER  -> tmp = new Donnee(nom, Type.ENTIER , null, false);
						case Type.REEL    -> tmp = new Donnee(nom, Type.REEL   , null, false);
						case Type.CHAR    -> tmp = new Donnee(nom, Type.CHAR   , null, false);
						case Type.BOOLEEN -> tmp = new Donnee(nom, Type.BOOLEEN, null, false);
						default           -> tmp = new Donnee(nom, Type.CHAINE , null, false);
					}
				}
				this.lstDonnee.add(tmp);
			}
		}
		if (this.lectureConstante) // si ajout de constante
		{
			String[] l = ligne.split("<--");
			nom = l[0].replaceAll(" |\t", "");
			String val = Util.getValeur(ligne, true, null);
			switch (this.getType(ligne))
			{
				case Type.ENTIER  -> tmp = new Donnee(nom, Type.ENTIER , Integer.parseInt(val), true);
				case Type.REEL    -> tmp = new Donnee(nom, Type.REEL   , Double.parseDouble(val), true);
				case Type.CHAR    -> tmp = new Donnee(nom, Type.CHAR   , val.charAt(0), true);
				case Type.BOOLEEN -> tmp = new Donnee(nom, Type.BOOLEEN, Boolean.parseBoolean(val), true);
				default           -> tmp = new Donnee(nom, Type.CHAINE , val, true);
			}
			this.lstDonnee.add(tmp);
		}
	}

	/**
	 * Permet d'affecter une valeur à une ligne
	 * 
	 * @param ligne
	 */
	private void affecter(String ligne)
	{
		Integer[] taille;
		String indices = ligne;
		String[] t;

		String nomVar = ligne.substring(0, ligne.indexOf("<--")).replaceAll(" |\t", "");
		String value = Util.getValeur(ligne, false, this);
		int ind = -1;

		Donnee tmp = null;

		taille = null;

		// si dans un tableau
		if (nomVar.contains("["))
		{
			nomVar = nomVar.substring(0, nomVar.indexOf("["));

			indices = indices.substring(indices.indexOf("[") + 1, indices.lastIndexOf("]")).replaceAll("\\[|\\]$", "");
			t = indices.split("\\]");
			taille = new Integer[t.length];
			for (int cpt = 0; cpt < t.length; cpt++)
				taille[cpt] = Integer.parseInt(t[cpt]);
		}

		tmp = this.getDonnee(nomVar);

		if (taille != null)
			Util.setValeurBySwitch(tmp, value, taille);
		else
			Util.setValeurBySwitch(tmp, value);
	}

	/**
	 * Permet la gestion de commentaire
	 */
	public String commenter(String ligne)
	{
		if (ligne.contains("/*") && ligne.contains("*/")) // si ça contient des commentaires et qu'il y a l'ouverture et la fermeture
		{
			this.commOk = false; // toute la ligne n'est pas a commenter
			int indDebCom, indFinCom;
			String l = "";
			indDebCom = ligne.lastIndexOf("/*");// on récupère le dernier indice de /*
			indFinCom = ligne.indexOf("*/"); // on récupère le premier indice de */
			if (indDebCom < indFinCom)
			{
				l = ligne.substring(0, indDebCom) + " " + ligne.substring(indFinCom + 2);
				this.enComm = false;
			}
			// si la dernière ouverture de commentaire est fermée par la première fermeture,
			// pas besoin d'effectuer d'autre tests, on supprime juste cette partie de la
			// ligne a interpreter
			else
			{
				String finLigne = ligne.substring(ligne.lastIndexOf("*/") + 2);// on prépare la fin de ligne si il y en
																				// a après la dernière fermeture
				l = ligne.substring(indFinCom + 2, indDebCom);
				this.enComm = true;
				if (ligne.lastIndexOf("*/") > indDebCom)
				{
					l = l + " " + finLigne;
					this.enComm = false;
				} // si c'est le cas on l'ajoute après la première partie de la ligne
			}
			return commenter(l);// on utilise cette fonction en récursivité car on peut avoir plusieurs
								// commentaires dans la même ligne
		}
		if (ligne.contains("/*"))
		{
			this.enComm = true;
			this.commOk = false;
			return commenter( ligne.substring(0, ligne.lastIndexOf("/*")) );
		}
		// si la ligne ne contient que une ouverture de commentaire on indique que les
		// prochaines lignes jusqu'a la fermeture, seront commentée puis on appelle la
		// fonction pour finir le traitement
		if (ligne.contains("*/"))
		{
			this.enComm = false;
			this.commOk = false;
			return commenter( ligne.substring(ligne.indexOf("*/") + 2, ligne.length()) );
		}
		// si la ligne contient uniquement une fermeture de commentaire, on indique que
		// la suite sera interprété et on enlève la partie commentée puis on rappelle la
		// fonction
		if (this.enComm && this.commOk)
			return "";

		// si la ligne est désignée comme commentaire (donc on a eu un /* sans */) et
		// qu'il n'y a pas de signe de commentaire dans la ligne originelle, la ligne
		// entière est commentée alors on ne renvois rien
		if (ligne != null && !ligne.equals("") && ligne.charAt(0) == ' ')
			ligne = ligne.substring(1);
		// on enlève le premier caractère si c'est un espace
		return ligne;// on retourne la ligne sans commentaire

	}

	/**
	 * Retourne le nombre de ligne du fichier
	 * 
	 * @return
	 *         nombre de ligne
	 */
	public int getSizeContenu()
	{
		return this.lstContenu.size();
	}

	/**
	 * Retourne la ligne des données
	 * 
	 * @param ligne
	 * @return
	 */
	private String getType(String ligne)
	{
		return Util.getType( ligne, this.lectureConstante );
	}

	/**
	 * Retourne l'affichage des données séléctionnés
	 * 
	 * @return
	 */
	public String getDonnees()
	{
		return this.gestionDonnee.getDonneeString();
	}
	/**
	 * Permet d'afficher le CUI dans la console
	 */
	public void actualiser() 
	{
		this.controleur.actualiser();
	}

	/**
	 * trace des variables dans le presse papier
	 */
	public void trace()
	{
		this.gestionDonnee.traceCopie();
	}

	/**
	 * Retourne le detail d'une variable
	 * @return le detail d'une variables
	 */
	public String getTraceVariable(String var)
	{
		return this.gestionDonnee.traceVar( var );
	}

    /**
	 * met le detail d'une variable dans le presse papier
	 */
	public void traceVariableCopie(String var)
	{
		this.gestionDonnee.traceVariableCopie( var );
	}
}