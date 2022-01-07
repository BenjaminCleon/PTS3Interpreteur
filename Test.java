import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import org.fusesource.jansi.AnsiConsole;
import java.util.ArrayList;
import java.util.List;

class Test{
	public static void main(String[] args)
	{
		String var = "Bonjour /* je n'apparait pas */ fin de /**/ test";	
		System.out.println(var);
	}
}