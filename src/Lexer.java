public class Lexer {
    private final String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public Token nextToken() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }

        if (pos >= input.length()) {
            return new Token(Token.Type.EOF, "");
        }

        char current = input.charAt(pos);

        if (isBanglaDigit(current)) {
            StringBuilder sb = new StringBuilder();
            while (pos < input.length() && isBanglaDigit(input.charAt(pos))) {
                sb.append(input.charAt(pos++));
            }
            return new Token(Token.Type.NUMBER, sb.toString());
        }

        if (isBanglaLetter(current)) {
            StringBuilder sb = new StringBuilder();
            while (pos < input.length() &&
                  (isBanglaLetter(input.charAt(pos)) || isBanglaDigit(input.charAt(pos)))) {
                sb.append(input.charAt(pos++));
            }
            return new Token(Token.Type.IDENTIFIER, sb.toString());
        }

        pos++;

        return switch (current) {
            case '=' -> new Token(Token.Type.ASSIGN, "=");
            case '+' -> new Token(Token.Type.PLUS, "+");
            case '*' -> new Token(Token.Type.MULTIPLY, "*");
            default -> throw new RuntimeException("Unknown character: " + current);
        };
    }

    private boolean isBanglaLetter(char c) {
        return c >= '\u0980' && c <= '\u09FF';
    }

    private boolean isBanglaDigit(char c) {
        return c >= '\u09E6' && c <= '\u09EF';
    }
}