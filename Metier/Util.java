package AlgoPars.Metier;

import AlgoPars.Metier.Type;

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
			type = getTypeAfterValue(val);
		}

		return type;
	}

	public static String getTypeAfterValue(String val)
	{
		String type = "";

		if(val.equals("true") || val.equals("false")) type = Type.BOOLEEN;
		if(val.matches("^\\d+$")                    ) type = Type.ENTIER ;
		if(val.matches("(\\d*)\\.(\\d*)")           ) type = Type.REEL   ;
		if(val.matches("\'.\'")                     ) type = Type.CHAR   ;
		if(type.equals("")                          ) type = Type.CHAINE ;

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
		if ( valeur[1].indexOf("\"") == -1 )valeur[1] = valeur[1].replaceAll(" ", "");
		valeur[1] = valeur[1].replaceAll(" \"|\t|\"|'|" ,"");

		return valeur[1];
	}

	public static void setValeurBySwitch(Donnee data, String value)
	{
		switch(data.getType())
		{
			case Type.ENTIER  -> data.setValeur(Integer.parseInt    (value));
			case Type.REEL    -> data.setValeur(Double.parseDouble  (value));
			case Type.CHAR    -> data.setValeur(value.charAt(0))            ;
			case Type.BOOLEEN -> data.setValeur(Boolean.parseBoolean(value));
			default           -> data.setValeur(                     value) ;
		}
	}
}
