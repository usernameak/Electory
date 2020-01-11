package electory.scripting;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;

public class ScriptTokenizer {
	private Reader rd;
	private int lastChar;
	private boolean unwind = false;

	public ScriptTokenizer(Reader rd) {
		this.rd = rd;
	}

	private int read() throws IOException {
		if (unwind) {
			unwind = false;
			return lastChar;
		}
		int c = rd.read();
		this.lastChar = c;
		return c;
	}

	private void unwind() {
		if (unwind)
			throw new IllegalStateException();
		unwind = true;
	}

	private Character[] specialTokens = new Character[] { '{', '}', '[', ']', '(', ')', '+', '-', '*', '/', '=', '|', '&', '^',
			'~', '!', '?', ':', ';' };

	public ScriptToken nextToken() {
		try {
			while (true) {
				int c = read();
				if (c == -1) {
					return new EOFToken();
				} else if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_') {
					String id = String.valueOf((char) c);
					c = read();
					while ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= '0' && c <= '9')) {
						id += (char) c;
						c = read();
					}
					unwind();
					return new IdentifierToken(id);
				} else if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
					continue;
				} else if (c == '"') {
					String s = "";
					c = read();
					while (c != '"') {
						if (c == -1) {
							throw new ParseError("unexpected EOF");
						} else if (c == '\\') {
							c = read();
							if (c == -1) {
								throw new ParseError("unexpected EOF");
							} else if (c == 'n') {
								s += '\n';
							} else if (c == 't') {
								s += '\t';
							} else if (c == '\\') {
								s += '\\';
							} else if (c == '"') {
								s += '"';
							} else if (c == 'r') {
								s += '\r';
							} else {
								throw new ParseError("unknown escape \\" + (char) c);
								// TODO: more escapes
							}
						} else {
							s += (char) c;
						}
						c = read();
					}
					return new StringToken(s);
				} else if (c >= '0' && c <= '9') {
					String numStr = String.valueOf((char) c);
					while (c >= '0' && c <= '9') {
						numStr += (char) c;
						c = read();
					}
					unwind();
					return new IntegerToken(Integer.valueOf(numStr));
				} else if (Arrays.asList(specialTokens).contains(Character.valueOf((char)c))) {
					return new SpecialToken((char) c);
				} else {
					throw new ParseError("unexpected '" + (char) c + "'");
				}
			}
		} catch (IOException e) {
			throw new ParseError(e);
		}
	}
}
