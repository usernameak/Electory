package electory.scripting;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ScriptingTest {
	public static void main(String[] args) {
		InputStreamReader isr = new InputStreamReader(ScriptingTest.class.getResourceAsStream("/scripts/test.esl"),
				StandardCharsets.UTF_8);
		ScriptTokenizer tokenizer = new ScriptTokenizer(isr);
		ScriptToken token = null;
		do {
			token = tokenizer.nextToken();
			System.out.println(token);
		} while (!(token instanceof EOFToken));
	}
}
