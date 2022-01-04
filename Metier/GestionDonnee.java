package AlgoPars.Metier;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List     ;
import java.util.Locale;
import java.util.Scanner  ;

public class GestionDonnee
{
	private List<String> lstVar      ;
	private Interpreteur interpreteur;
	
	public GestionDonnee (String nomFichier, Interpreteur interpreteur)
	{
		this.lstVar       = new ArrayList<String>();
		this.interpreteur = interpreteur;

		File fichier = new File (nomFichier + ".var" );
		
		if (fichier.exists())
		{
			try
			{
				Scanner sc = new Scanner ( fichier );
				
				while (sc.hasNextLine())
				{
					String ligne = sc.nextLine();
					this.lstVar.add(ligne);
				}
				
			} catch (Exception e) {}
		}
	}
	
	/**
	 * Retourne les données étant à tracer
	 * @return
	 *    les données devant être tracer
	 */
	public String getDonneeString()
	{
		String sRet   = "";
		String valVar;
		
		Donnee tmp;

		for(String var: this.lstVar)
		{
			tmp = this.interpreteur.getDonnee(var);

			if ( tmp == null )valVar = "non declaré";
			else
			{
				if ( tmp.getValeur() == null )valVar = "non initialisé";
				else
				{
					valVar = String.valueOf(tmp.getValeur());
					if(valVar.length() > 10)
					{
						String debut = valVar.substring(0,5);
						String fin   = valVar.substring((valVar.length()-3), valVar.length());
						valVar = debut + fin;
					}
				}
			}
			// à fixer quand c'est un nombre
			sRet += String.format(" %-16s|%22s \n", var, valVar);
		}

		for(int i=this.lstVar.size();i<41;i++)sRet += String.format("%-41s", "") + "\n";
		
		return sRet;
	}
}
