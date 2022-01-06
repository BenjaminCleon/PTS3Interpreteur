import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;
import org.fusesource.jansi.AnsiConsole;

class Test{
	public static void main(String[] args) {
		AnsiConsole.systemInstall();
		String tmp = ansi().bg(YELLOW) + "tesé×t";
		System.out.println(tmp + ansi().reset());
		
	}}