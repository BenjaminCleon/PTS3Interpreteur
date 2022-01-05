import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.SynthScrollPaneUI;

public class Test
{
    public static void main(String[] args) {
        System.out.println(Test.expression("2×3+4/(5+6)") );

    }
       
    public static double expression(String ligne)
	{
		int taille   ;
		int dernierOp;

		String tmp     ;
		String ligneTmp  = "";
		String operateur = "";

		Stack<String> pile = new Stack     <String>();
		Queue<String> file = new LinkedList<String>();

        Queue<String> fileRet = new LinkedList<String>();

		dernierOp = 0;
		taille = ligne.length();
		
		while ( (operateur = Test.nextOperateur(ligne)) != null || ligneTmp.equals(operateur) )
		{
			ligneTmp = ligne.substring(0, ligne.indexOf(operateur));

			file.add(ligneTmp);
			
			switch(operateur)
			{
				case "(" -> pile.add(operateur);
				case ")" ->
                {
                    while (  !pile.isEmpty() && !(pile.peek()).equals("(") )file.add(pile.pop());
                    pile.pop();
                }
                default  ->
                {
                    while ( !pile.isEmpty() && Test.prioSupEgal(pile.peek(), operateur) )
                        file.add(pile.pop());


                    pile.add(operateur);
                }
			}

			ligne = ligne.substring(ligne.indexOf(operateur)+operateur.length());
		}
		file.add(ligne);

        while ( !pile.isEmpty() )file.add(pile.pop());

        for ( String val: file)if ( !val.equals(""))fileRet.add(val);

		return Test.evaluerEPO(fileRet);
	}

    private static double evaluerEPO(Queue<String> file)
    {
        Stack<Double> pile = new Stack<Double>();
        double val1, val2;

        for ( String val : file )
        {
            if ( val.matches("(\\(|\\)|<=|>=|!=|<|>|=|xou|ou|et|non|\\+|-|×|\\/){1}") )
            {
                val1 = pile.pop();
                val2 = pile.pop();

                switch(val)
                {
                    case "+" -> pile.add(val2 + val1);
                    case "-" -> pile.add(val2 - val1);
                    case "×" -> pile.add(val2 * val1);
                    case "/" -> pile.add(val2 / val1);
                }
            }
            else
                pile.add(Double.parseDouble(val));
        }

        return pile.pop();
    }

	private static boolean prioSupEgal(String st1, String st2)
	{
		int prioSt1 = 0, prioSt2 = 0;

		final String[][] prio =
		                      {  { "("                             },
		                         { "<", ">", "<=", ">=", "!=", "=" },
		                         { "ou", "+", "-"                  },
		                         { "et", "×", "/"                  },
		                         { "non"                           }
						      };

		for (int numPrio=0;numPrio<prio.length;numPrio++)
			for (int i=0;i<prio[numPrio].length;i++)
			{
				if ( st1.equals(prio[numPrio][i]) )prioSt1 = numPrio;
				if ( st2.equals(prio[numPrio][i]) )prioSt2 = numPrio;
			}

		return prioSt1>=prioSt2;
	}

	public static String nextOperateur(String ligne)
	{
		String nextOperateur = null;

		CharSequence entreeLigne = ligne;
		String patternStr = "(\\(|\\)|<=|>=|!=|<|>|=|xou|ou|et|non|\\+|-|×|\\/){1}";
		Pattern pattern   = Pattern.compile(patternStr);
		Matcher matcher   = pattern.matcher(entreeLigne);

		if ( matcher.find() )
            return ligne.substring(matcher.start(), matcher.end());

		return nextOperateur;
	}
}
