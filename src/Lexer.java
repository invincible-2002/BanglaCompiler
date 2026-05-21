public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;

    public Lexer(String input) {
        this.input = input;
    }

    public int getLine() { return line; }

    public Token nextToken() {
        // Skip whitespace
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            if (input.charAt(pos) == '\n') line++;
            pos++;
        }

        if (pos >= input.length()) {
            return new Token(Token.Type.EOF, "");
        }

        char current = input.charAt(pos);

        // Skip single-line comments: // ...
        if (current == '/' && pos + 1 < input.length() && input.charAt(pos + 1) == '/') {
            while (pos < input.length() && input.charAt(pos) != '\n') pos++;
            return nextToken();
        }

        // Bangla digits → NUMBER token
        if (isBanglaDigit(current)) {
            StringBuilder sb = new StringBuilder();
            while (pos < input.length() && (isBanglaDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                sb.append(input.charAt(pos++));
            }
            return new Token(Token.Type.NUMBER, sb.toString());
        }

        // ASCII digits → NUMBER token (for convenience)
        if (Character.isDigit(current)) {
            StringBuilder sb = new StringBuilder();
            while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
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
            String word = sb.toString();
            return switch (word) {
                case "যদি"     -> new Token(Token.Type.IF,    word);
                case "নাহলে"  -> new Token(Token.Type.ELSE,  word);
                case "যতক্ষণ" -> new Token(Token.Type.WHILE, word);
                case "দেখাও"  -> new Token(Token.Type.PRINT, word);
                case "সত্য"   -> new Token(Token.Type.TRUE,  word);
                case "মিথ্যা" -> new Token(Token.Type.FALSE, word);
                default        -> new Token(Token.Type.IDENTIFIER, word);
            };
        }

        if (current == '"') {
            pos++; // skip opening quote
            StringBuilder sb = new StringBuilder();
            while (pos < input.length() && input.charAt(pos) != '"') {
                if (input.charAt(pos) == '\n') line++;
                sb.append(input.charAt(pos++));
            }
            if (pos < input.length()) pos++; // skip closing quote
            return new Token(Token.Type.STRING, sb.toString());
        }

        pos++;

        if (current == '=' && pos < input.length() && input.charAt(pos) == '=') {
            pos++; return new Token(Token.Type.EQ,  "==");
        }
        if (current == '!' && pos < input.length() && input.charAt(pos) == '=') {
            pos++; return new Token(Token.Type.NEQ, "!=");
        }
        if (current == '<' && pos < input.length() && input.charAt(pos) == '=') {
            pos++; return new Token(Token.Type.LTE, "<=");
        }
        if (current == '>' && pos < input.length() && input.charAt(pos) == '=') {
            pos++; return new Token(Token.Type.GTE, ">=");
        }

        return switch (current) {
            case '='  -> new Token(Token.Type.ASSIGN,    "=");
            case '+'  -> new Token(Token.Type.PLUS,      "+");
            case '-'  -> new Token(Token.Type.MINUS,     "-");
            case '*'  -> new Token(Token.Type.MULTIPLY,  "*");
            case '/'  -> new Token(Token.Type.DIVIDE,    "/");
            case '<'  -> new Token(Token.Type.LT,        "<");
            case '>'  -> new Token(Token.Type.GT,        ">");
            case ';'  -> new Token(Token.Type.SEMICOLON, ";");
            case '('  -> new Token(Token.Type.LPAREN,    "(");
            case ')'  -> new Token(Token.Type.RPAREN,    ")");
            case '{'  -> new Token(Token.Type.LBRACE,    "{");
            case '}'  -> new Token(Token.Type.RBRACE,    "}");
            default   -> new Token(Token.Type.UNKNOWN,   String.valueOf(current));
        };
    }

    private boolean isBanglaLetter(char c) {
        return c >= '\u0980' && c <= '\u09FF';
    }

    private boolean isBanglaDigit(char c) {
        return c >= '\u09E6' && c <= '\u09EF';
    }
}
