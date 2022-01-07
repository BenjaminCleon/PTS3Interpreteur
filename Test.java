import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import org.fusesource.jansi.AnsiConsole;
import java.util.ArrayList;
import java.util.List;

class Test{
	public static void main(String[] args) {
		List<Object> l = new ArrayList<Object>();
		System.out.println(l);
		Test.creerDimension(3, l, 4, 5, 2);
		System.out.println(l);
	}

	public static void creerDimension(int dimen, List<Object> l, Integer... args)
	{
		for ( int i=0;i<args[0];i++)
		{
			if ( dimen >= 2 )
			{
				l.add(new ArrayList<Object>());

				for ( int j=0;j<args[1];j++)
				{
					if ( dimen == 3 )
					{
						((List<Object>)l.get(i)).add(new ArrayList<Object>());

						for (int k=0;k<args[2];k++)
						{
							List<Object> tmp = ((List<Object>)l.get(i));
							((List<Object>)tmp.get(j)).add(null);
						}
					}
					else
						((List<Object>)l.get(i)).add(null);
				}
			}
			else
				l.add(null);
		}
	}
}