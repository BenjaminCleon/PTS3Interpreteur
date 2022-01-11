import java.util.LinkedList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class Test{
	public static void main(String[] args)
	{
		String line = "car(ord('A'))";
		line = Test.traiterPrimitive(line);
		System.out.println(line);
	}

	private static String traiterPrimitive(String ligne)
	{
		CharSequence entreeLigne = ligne;
		String patternStr = "car\\(|ord\\(|enChaine\\(|enEntier\\(|enReel\\(|plancher\\(|arrondi\\(";;
		Pattern pattern = Pattern.compile(patternStr) ;
		Matcher matcher = null;

		String primi    = "";
		String ligneRet = "";
		String ligneTmp = "";
		String ligneContenu = "";
		int posLastBracket   = 0;
		int nbBracketWaiting = 1;

		matcher = pattern.matcher(ligne);

		if ( matcher.find() )
		{
			do
			{
				if ( matcher.start() != 0 )ligneRet += ligne.substring(0, matcher.start());

				for (int i=matcher.start();i<ligne.length()&&nbBracketWaiting!=0;i++)
				{
					if ( ligne.charAt(i) == ')' ){ posLastBracket = i; nbBracketWaiting--; }
					if ( ligne.charAt(i) == '(' )nbBracketWaiting++;
				}

				ligneRet = Test.traiterPrimitive(ligne.substring(matcher.end(), posLastBracket));
				matcher = pattern.matcher(ligne);
			}while(matcher.find());
		}

		return ligneRet;
	}

	private static Character car( int  value ){ return (char)value; }
	private static Integer   ord( char value ){ return (int )value; }

	private static String enChaine ( double value ){ return String.valueOf(value); }
	private static String enChaine ( int    value ){ return String.valueOf(value); }

	private static Integer enEntier( String chaine ){ return Integer.parseInt   (chaine); }
	private static Double  enReel  ( String chaine ){ return Double .parseDouble(chaine); }

	private static Integer plancher ( double value ){ return (int)Math.floor(value); }
	private static Integer arrondi  ( double value ){ return (int)Math.round(value); }

	private static String aujourdhui(){ return DateTimeFormatter.ofPattern("dd/MM/yyyy").format(LocalDateTime.now()); }
	private static String jour      (){ return aujourdhui().substring(0, 2); }
	private static String mois      (){ return aujourdhui().substring(3, 5); }
	private static String annee     (){ return aujourdhui().substring(6)   ; }

	private static Boolean estReel  ( String chaine ){ return chaine.matches("^\\d+.\\d+$"); }
	private static Boolean estEntier( String chaine ){ return chaine.matches("^\\d$"      ); }

	private static Integer hasard( int valeur ){ return (int)(Math.random()*valeur); }
}
