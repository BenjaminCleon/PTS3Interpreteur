import java.util.Scanner;
import java.util.File;

public class LectureDonnee
{
	private ArrayList<Donnee> lstDonnee;
	public LectureDonnee(String fichier)
	{
		this.lstDonnee() = new ArrayList<Donnee>();
		sc = new Scanner(new File(fichier));
		String ligne = sc.nextLine();
		while(sc.hasNext() || ! ligne.equals("DEBUT"))
		{
			ligne = sc.nextLine()
			if(ligne.equals("constante:"))
			{
				while(! ligne.equals("DEBUT") && ! ligne.equals("variable:"))
				{
					ligne = sc.nextLine();
					this.creerConstante(ligne);
				}
				
				
			}
			if(ligne.equals("variable:")
			{
				String l;
				String lSplit;
				while(! ligne.equals("DEBUT"))
				{
					ligne=sc.nextLine();
					l = ligne.split(":");
					lSplit = l[0].split(",");
					for(int i=0; 0<lSplit.length; i++)
						this.creerVariable(ligne, lSplit[i]);
				}
			}
		}
	}
	
	
	public void creerVariable(String ligne, String nom)
	{
		Donnee tmp;
		
		switch(this.getType(ligne))
		{
			case "entier"   : tmp = new Donnee<Integer>   (nom, "entier"              , null, false); break;
			case "réel"     : tmp = new Donnee<Double>    (nom, "réel "               , null, false); break;
			case "caractère": tmp = new Donnee<Character> (nom, "caractère"           , null, false); break;
			case "booléen"  : tmp = new Donnee<Boolean>   (nom, "booléen"             , null, false); break;
			default         : tmp = new Donnee<String>    (nom, "chaine de caractères", null, false); break;
			
		}
		this.lstDonnee.add(tmp);
	}
	
	public void creerConstante(String ligne)
	{
		Donnee tmp;
		
		switch(this.getTypeC(ligne))
		{
			case "entier" : 
			{
				Integer val = Integer.parseInt(this.getValeur(ligne));
				tmp = new Donnee<Integer>   (nom, "entier"             , val, true);
				break;
			}
			case "réel" :
			{
				Double val = Double.ParseDouble(this.getValeur(ligne));
				tmp = new Donnee<Double>    (nom, "réel "              , val, true);
				break;
			}
			case "caractère" :
			{
				Character val = this.getValeur(ligne);
				tmp = new Donnee<Character>(nom, "caractère"          , val, true);
				break;
			}
			case "booléen" :
			{
				Boolean val = this.getValeur(ligne).equals("true");
				tmp = new Donnee<Boolean>   (nom, "booléen"            , val, true);
				break;
			}
			default :
			{
				String valeur[] = ligne.split("=");
				val = valeur[1];
				tmp = new Donnee<String>   (nom, "chaine de caractère", val, true); 
				break;
			}
		}
		this.lstDonnee.add(tmp);
	}
	
	public String getType(String ligne)
	{
		String[] decomp = ligne.split(":");
		String type = decomp[1].replaceAll(" ", "");
		return type;
	}
	
	public String getTypeC(String ligne)
	{
		val = this.getValeur(ligne);
		String type = "entier";
		if(val.matches("\"(.*)\"")) type = "chaine de caractères";
		if(val.matches("\'.\'")   ) type = "caractère";
		if(val.equals("true") || val.equals("false")) type = "booléen";
		if(val.matches("(\d*),(\d*)") type = "réel";
		return type;
	}
	
	public String getValeur(String ligne)
	{
		String valeur[] = ligne.split("=");
		val = valeur[1].replaceAll(" ", "");
		return val;
	}
}
