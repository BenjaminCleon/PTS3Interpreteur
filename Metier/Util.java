package AlgoPars.Metier;

/**
 * Classe Util avec des méthodes utiles dans diverses fichier
 */
public class Util
{
	/**
	 * 
	 * @param ligne
	 * @param estConstant
	 * @return
	 */
	public static String getType(String ligne, boolean estConstant)
	{
		String type;

		if (!estConstant)
		{
			String[] decomp = ligne.split(":");
			type = decomp[1].replaceAll(" ", "");
		}
		else
		{
			String val  = Util.getValeur(ligne);
			type = "entier";
			if(val.matches("\"(.*)\"")) type = "chaine de caractères";
			if(val.matches("\'.\'")   ) type = "caractère";
			if(val.equals("true") || val.equals("false")) type = "booléen";
			if(val.matches("(\\d*),(\\d*)")) type = "réel";
		}

		return type;
	}

	/**
	 * retourne la valeur contenu dans la ligne
	 * @param ligne
	 * @return la valeur
	 */
	public static String getValeur(String ligne)
	{
		String valeur[] = ligne.split("<--");
		String val = valeur[1].replaceAll(" |\t", "");
		return val;
	}

	public static void setValeurBySwitch(Donnee data, String value)
	{
		switch(data.getType())
		{
			case "entier"    -> data.setValeur(Integer.parseInt    (value));
			case "caractère" -> data.setValeur(value.charAt(0))            ;
			case "réel"      -> data.setValeur(Double.parseDouble  (value));
			case "booléen"   -> data.setValeur(Boolean.parseBoolean(value));
			default          -> data.setValeur(                     value) ;
		}
	}
}
