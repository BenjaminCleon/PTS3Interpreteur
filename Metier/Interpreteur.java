package AlgoPars.Metier;

import AlgoPars.Controleur          ;
import AlgoPars.Metier.Donnee       ;
import AlgoPars.Metier.EntreeSortie ;
import AlgoPars.Metier.GestionDonnee;
//import AlgoPars.Metier.Tableau      ;

import java.io.FileInputStream;

import java.util.List         ;
import java.util.Collections  ;
import java.util.ArrayList    ;
import java.util.Scanner      ;

import iut.algo.Console;
import iut.algo.CouleurConsole;




/**
 * Classe principale liée au modèle de l'architecture MVC
 * @author LHEAD
 */
public class Interpreteur
{
	private Controleur  controleur; // controleur associé

	private List<Donnee>  lstDonnee ; // liste des données
	//private List<Tableau> lstTableau; // liste des tableaux
	private List<String>  lstContenu; // contenu du fichier
	private List<Integer> lstBk;
	private List<String>  traceDexecution; // trace d'éxécution du code
	private List<Integer> traceLire      ; // retient les numéros de ligne où il y a eu un lire

	private String nomFichier; // nom du fichier à lire
	private int    numeroLigne; // Numéro de la ligne en cours
	private int    move;
	private int    lignePrc;
	private int    cptVal;

	private boolean lectureVariable ; // permet de connaitre si nous sommes dans la déclaration des variables
	private boolean lectureConstante; // permet de connaitre si nous sommes dans la déclaration des constantes

	private GestionDonnee gestionDonnee; // permet de gérer les données souhaitant être traiter

	/**
	 * Constructeur principale
	 */
	public Interpreteur(Controleur controleur, String nomFichier)
	{
		this.controleur  = controleur;
		this.nomFichier  = nomFichier;
		this.numeroLigne = 0;
		this.move        = 0;
		this.lignePrc    = 0;
		this.cptVal      = 0;

		this.lstContenu      = new ArrayList<String> ();
		this.lstDonnee       = new ArrayList<Donnee> ();
		this.lstBk           = new ArrayList<Integer>();
		this.traceDexecution = new ArrayList<String> ();
		this.traceLire       = new ArrayList<Integer>();

		this.lectureVariable  = false;
		this.lectureConstante = false;

		this.lectureFichier();
		this.gestionDonnee = new GestionDonnee(nomFichier, this);
	}

	/**
	 * Définit le numéro de la ligne en cours
	 */
	public void setNumeroLigne(int numeroLigne){this.numeroLigne = numeroLigne;}


	/**
	 * Interprete la ligne n en partant du principe que ce qui est au-dessus est interpréter
	 * @param n
	 */
	public void interpreter(int n)
	{
		String ligneAInterpreter;
		int indexComment;

		if ( n < this.lstContenu.size() && n >= 0 )
		{
			ligneAInterpreter = this.lstContenu.get(n);
			indexComment = ligneAInterpreter.indexOf("//");

			if ( indexComment != -1 )ligneAInterpreter = ligneAInterpreter.substring(0, indexComment);

			if ( ligneAInterpreter.equals("DEBUT") )this.lectureConstante = this.lectureVariable = false;
			
			if ( ligneAInterpreter.equals("variable:" ) )
			{
				this.lectureConstante = false;
				this.lectureVariable  = true ;
			}
			if ( ligneAInterpreter.equals("constante:") )this.lectureConstante = true;

			if ( (this.lectureVariable || this.lectureConstante) && 
				!(ligneAInterpreter.equals("constante:") || ligneAInterpreter.equals("variable:")) )
			{
				// Alan c'est ton moment
				this.creerDonnee(ligneAInterpreter);
				
			}
			else
			{
				if ( ligneAInterpreter.contains("ecrire") )this.traceDexecution.add(EntreeSortie.ecrire(ligneAInterpreter, this));
				if ( ligneAInterpreter.contains("<--"   ) )this.affecter(ligneAInterpreter);
				if ( ligneAInterpreter.contains("lire"  ) ){ this.traceDexecution.add(EntreeSortie.lire(ligneAInterpreter, this));this.traceLire.add(this.traceLire.size()+1); }
			}
		}

		this.lignePrc = n;
	}

	public void reset()
	{
		this.lstDonnee       = new ArrayList<Donnee> ();
		this.traceDexecution = new ArrayList<String> ();
	}

	public void goTo(int n)
	{
		if ( n < 0 || n >= this.lstContenu.size() )return;
		
		int courant = 1;
		if( this.lignePrc > n )//Si on recule
			this.reset();
		else
			courant = this.lignePrc;
		
		while(courant < n)
			this.interpreter(courant++);
	}

	/**
	 * Retourne la trace d'execution actuelle
	 * @return
	 */
	public List<String> getTraceDexecution()
	{
		return this.traceDexecution;
	}

	/**
	 * Retourne la trace des numéros de ligne lu
	 * @return
	 */
	public List<Integer> getTraceLire()
	{
		return this.traceLire;
	}

	/**
	 * Permet de lire le fichier et de récupérer son contenu
	 */
	public void lectureFichier()
	{
		String line = "";
		try
		{
			Scanner sc = new Scanner (new FileInputStream(nomFichier + ".algo"), "UTF8");
			while(sc.hasNextLine())
			{
				line = sc.nextLine();
				this.lstContenu.add(line.replaceAll("\t", "    "));
			}
			sc.close();
		}catch (Exception e) {System.out.println("Erreur 001 : Lecture du fichier .algo"); e.printStackTrace();}
	}

	/**
	 * Renvoie la donnée avec le nom en paramètre
	 * @param nom de la donnée
	 * @return
	 *    la donnée associée au nom
	 */
	public Donnee getDonnee(String nom)
	{
		for ( Donnee data : this.lstDonnee )
			if ( data.getNom().equals(nom) )return data;
		
		return null;
	}

	/**
	 * retourne le nombre de données
	 * @return
	 *    nombre de données
	 */
	public int getNbDonnee(){ return this.lstDonnee.size(); }
	
	/**
	 * Retourne le contenu avec + ou - 40 lignes
	 * @param n
	 *     la ligne ou se fait l'interprétation
	 * @return
	 *    contenu du fichier sous forme de chaine
	 */
	public String getFichier(int n)
	{
		String res ;
		String tmp ;
		int    size;
		
		int i;
		int l;
		res = "";
		size = this.lstContenu.size();
		this.setNumeroLigne(n);

		if( size <= 40)
		{
			for(l=0; l<size; l++)
				res += this.getLigne(l);	
			
			for(int a = l; a<40; a++)
				res += String.format("%-80s", " ") + "\n";
		}
		else
		{
			int move = 0;
			if( this.numeroLigne < 20)
			{
				//Affichage des 40 premieres lignes
				for(i = 0+move; i<40+move; i++)
					res += this.getLigne(i);
			}
			else
			{
				move = this.numeroLigne-20;

				if((size-move)>40)
					for(int j = move; j<size; j++)
						res += this.getLigne(j);
				else
					for (int j = size-40; j < size; j++)
						res += this.getLigne(j);
			}		
		}
		return res;
	}
	/** Change le format de la ligne en paramètre
	 * 
	 * @param val
	 * 	la ligne à lire
	 * @return
	 * 	la ligne à lire avec le bon format
	 */
	public String getLigne(int val)
	{
		String sRet = "";
		String sVal = "";
		if (this.numeroLigne == val)
			sRet = CouleurConsole.JAUNE.getFond() + "";
		if(this.lstBk.contains(val))
		{
			sVal = CouleurConsole.ROUGE.getFont() + "" + String.format("%2d ", val) + CouleurConsole.NOIR.getFont();
		}
		else
		{
			sVal = CouleurConsole.NOIR.getFont() + String.format("%2d ", val) + "";
		}
		sRet+= sVal + String.format("%-80s", this.lstContenu.get(val))+ CouleurConsole.BLANC.getFond() + "\n";
		return sRet;
	}

	public void goNextBk(int courant)
	{
		if( this.lstBk.isEmpty())return;
		for (int i = 0; i < this.lstBk.size(); i++)
		{
			if(this.lstBk.get(i) >= courant)
			{
				this.goTo(this.lstBk.get(i));
				return;
			}
		}
	}

	/** Ajoute un point d'arret
	 * @param ligne
	 * 	Ligne du point d'arret
	 * @return 
	 * 	Retourne true si la ligne est ajoutée
	 */
	public boolean addBk(int ligne)
	{
		
		if(ligne > this.getSizeContenu() || ligne< 0 || this.lstBk.contains(ligne)) return false;
		this.lstBk.add(ligne);
		Collections.sort(this.lstBk);
		return true;
	}

	/** Enleve un point d'arret
	 * @param ligne
	 * 	Ligne du point d'arret
	 * @return
	 * 	Retourne true si la ligne est enlevée
	 */
	public boolean rmBk(int ligne)
	{
		if(!this.lstBk.contains(ligne)) return false;
		for(int i=0; i<this.lstBk.size(); i++)
			if(this.lstBk.get(i) == ligne)
				this.lstBk.remove(i);
		return true;
	}

	/**
	 * permet de creer une donnee
	 * constante ou variable
	 * @param ligne
	 *   la ligne à traiter
	 */
	public void creerDonnee(String ligne)
	{
		Donnee tmp;
		String nom;
		
		tmp = null;
		if(this.lectureVariable) 
		{

			String[] l = ligne.split(":");
			l[0].replaceAll(" |\t", "");
			String[] lSplit = l[0].split(",");
			
			for(int i=0; i<lSplit.length; i++)
			{
				nom = lSplit[i].replaceAll(" |\t", "");
				if(l[1].matches("(.*)tableau(.*)"))
				{
					String type = "";
					String[] lig = l[1].split("de|d'",2);
					type = lig[1].replaceFirst(" ", "");
					type = type  .replaceFirst("s", "");
					
					
					String indices = ligne;
					String[] t;
					indices = indices.substring(indices.indexOf("[")+1, indices.lastIndexOf("]"));
					indices = indices.replaceAll("\\[|\\]$", "");
					t = indices.split("\\]");
					Integer[] taille = new Integer[t.length];
					for(int cpt=0; cpt<t.length; cpt++)taille[cpt] = Integer.parseInt(t[cpt]);
					switch(this.getType(ligne))
					{
						case Type.ENTIER  -> tmp = new Donnee(nom, type, new ArrayList<Integer>  (), this.lectureConstante, taille);
						case Type.REEL    -> tmp = new Donnee(nom, type, new ArrayList<Double>   (), this.lectureConstante, taille);
						case Type.BOOLEEN -> tmp = new Donnee(nom, type, new ArrayList<Boolean>  (), this.lectureConstante, taille);
						case Type.CHAR    -> tmp = new Donnee(nom, type, new ArrayList<Character>(), this.lectureConstante, taille);
						default           -> tmp = new Donnee(nom, type, new ArrayList<String>   (), this.lectureConstante, taille);
					}
					this.lstDonnee.add(tmp);
					
				}
				else
				{
					switch(this.getType(ligne))
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
		if(this.lectureConstante)
		{
			String[] l = ligne.split("<--");
			nom = l[0].replaceAll(" |\t", "");
			String val = Util.getValeur(ligne, true, null);			
			switch(this.getType(ligne))
			{
				case Type.ENTIER  -> tmp = new Donnee(nom, Type.ENTIER , Integer.parseInt(val)    , true);
				case Type.REEL    -> tmp = new Donnee(nom, Type.REEL   , Double.parseDouble(val)  , true);
				case Type.CHAR    -> tmp = new Donnee(nom, Type.CHAR   , val.charAt(0)            , true);
				case Type.BOOLEEN -> tmp = new Donnee(nom, Type.BOOLEEN, Boolean.parseBoolean(val), true);
				default           -> tmp = new Donnee(nom, Type.CHAINE ,                       val, true); 
			}
			this.lstDonnee.add(tmp);
		}
	}

	/**
	 * Permet d'affecter une valeur à une ligne
	 * @param ligne
	 */
	private void affecter(String ligne)
	{
		String nomVar = ligne.substring(0, ligne.indexOf("<--")).replaceAll(" |\t", "");
		String value  = Util.getValeur(ligne, false, this);
		int ind =-1;
		
		//System.out.println(nomVar + " |" + value + "|");

		Donnee tmp = null;
		
		if(nomVar.matches("(.*)[(.*)](.*)"))
		{
			String[] decomp = nomVar.split("\\[|\\]");
			nomVar = decomp[0];
			ind    = Integer.parseInt(decomp[1]);
		}
		
		for ( Donnee data: this.lstDonnee )
			if ( data.getNom().equals(nomVar) )tmp = data;

		Util.setValeurBySwitch(tmp, value);
	}

	/**
	 * Retourne le nombre de ligne du fichier
	 * @return
	 * 	nombre de ligne
	 */
	public int getSizeContenu(){ return this.lstContenu.size(); }

	/**
	 * Retourne la ligne des données
	 * @param ligne
	 * @return
	 */
	private String getType(String ligne)
	{
		return Util.getType(ligne, this.lectureConstante);
	}

	/**
	 * Retourne l'affichage des données séléctionnés
	 * @return
	 */
	public String getDonnees()
	{
		return this.gestionDonnee.getDonneeString();
	}

	public void actualiser()
	{
		this.controleur.actualiser();
	}

	public void trace()
	{
		this.gestionDonnee.traceCopie();
	}
	
	public String getTraceVariable(String var)
	{
		return this.gestionDonnee.traceVar(var);
	}
	
	public void traceVariableCopie(String var)
	{
		this.gestionDonnee.traceVariableCopie(var);
	}
}
