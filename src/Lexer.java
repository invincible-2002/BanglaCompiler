public class Lexer {

    private final String source;
    private int current;
    private int lineNumber;

    public Lexer(String source) {
        this.source     = source;
        this.current    = 0;
        this.lineNumber = 1;
    }

    public int getLine() {
        return lineNumber;
    }

    public Token nextToken() {
        skipWhitespaceAndComments();

        if (isAtEnd()) {
            return new Token(Token.Type.EOF, "");
        }

        char ch = peek();

        if (isBanglaDigit(ch))     return readBanglaNumber();
        if (Character.isDigit(ch)) return readAsciiNumber();
        if (isBanglaLetter(ch))    return readWordOrKeyword();
        if (ch == '"')             return readStringLiteral();

        return readOperatorOrDelimiter();
    }

    private void skipWhitespaceAndComments() {
        while (!isAtEnd()) {
            char ch = peek();
            if (ch == '\n') {
                lineNumber++;
                current++;
            } else if (Character.isWhitespace(ch)) {
                current++;
            } else if (ch == '/' && peekNext() == '/') {
                while (!isAtEnd() && peek() != '\n') current++;
            } else {
                break;
            }
        }
    }

    private Token readBanglaNumber() {
        StringBuilder number = new StringBuilder();
        while (!isAtEnd() && (isBanglaDigit(peek()) || peek() == '.')) {
            number.append(advance());
        }
        return new Token(Token.Type.NUMBER, number.toString());
    }

    private Token readAsciiNumber() {
        StringBuilder number = new StringBuilder();
        while (!isAtEnd() && (Character.isDigit(peek()) || peek() == '.')) {
            number.append(advance());
        }
        return new Token(Token.Type.NUMBER, number.toString());
    }

    private Token readWordOrKeyword() {
        StringBuilder word = new StringBuilder();
        while (!isAtEnd() && (isBanglaLetter(peek()) || isBanglaDigit(peek()))) {
            word.append(advance());
        }
        String text = word.toString();
        return new Token(matchKeyword(text), text);
    }

    private Token.Type matchKeyword(String word) {
        return switch (word) {
            case "যদি"     -> Token.Type.IF;
            case "নাহলে"  -> Token.Type.ELSE;
            case "যতক্ষণ" -> Token.Type.WHILE;
            case "দেখাও"  -> Token.Type.PRINT;
            case "সত্য"   -> Token.Type.TRUE;
            case "মিথ্যা" -> Token.Type.FALSE;
            default        -> Token.Type.IDENTIFIER;
        };
    }

    private Token readStringLiteral() {
        advance();
        StringBuilder content = new StringBuilder();
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') lineNumber++;
            content.append(advance());
        }
        if (!isAtEnd()) advance();
        return new Token(Token.Type.STRING, content.toString());
    }

    private Token readOperatorOrDelimiter() {
        char ch = advance();

        if (ch == '=' && peek() == '=') { advance(); return new Token(Token.Type.EQ,  "=="); }
        if (ch == '!' && peek() == '=') { advance(); return new Token(Token.Type.NEQ, "!="); }
        if (ch == '<' && peek() == '=') { advance(); return new Token(Token.Type.LTE, "<="); }
        if (ch == '>' && peek() == '=') { advance(); return new Token(Token.Type.GTE, ">="); }

        return switch (ch) {
            case '=' -> new Token(Token.Type.ASSIGN,    "=");
            case '+' -> new Token(Token.Type.PLUS,      "+");
            case '-' -> new Token(Token.Type.MINUS,     "-");
            case '*' -> new Token(Token.Type.MULTIPLY,  "*");
            case '/' -> new Token(Token.Type.DIVIDE,    "/");
            case '<' -> new Token(Token.Type.LT,        "<");
            case '>' -> new Token(Token.Type.GT,        ">");
            case ';' -> new Token(Token.Type.SEMICOLON, ";");
            case '(' -> new Token(Token.Type.LPAREN,    "(");
            case ')' -> new Token(Token.Type.RPAREN,    ")");
            case '{' -> new Token(Token.Type.LBRACE,    "{");
            case '}' -> new Token(Token.Type.RBRACE,    "}");
            default  -> new Token(Token.Type.UNKNOWN,   String.valueOf(ch));
        };
    }

    private char peek() {
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isBanglaLetter(char c) {
        return c >= '\u0980' && c <= '\u09FF';
    }

    private boolean isBanglaDigit(char c) {
        return c >= '\u09E6' && c <= '\u09EF';
    }
}
