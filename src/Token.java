public class Token {
    public enum Type {
        // Literals
        IDENTIFIER, NUMBER, STRING,

        // Operators
        ASSIGN,       // =
        PLUS,         // +
        MINUS,        // -
        MULTIPLY,     // *
        DIVIDE,       // /

        // Comparison
        EQ,           // ==
        NEQ,          // !=
        LT,           // <
        GT,           // >
        LTE,          // <=
        GTE,          // >=

        // Delimiters
        SEMICOLON,    // ;
        LPAREN,       // (
        RPAREN,       // )
        LBRACE,       // {
        RBRACE,       // }

        // Keywords (Bangla)
        IF,           // যদি
        ELSE,         // নাহলে
        WHILE,        // যতক্ষণ
        PRINT,        // দেখাও
        TRUE,         // সত্য
        FALSE,        // মিথ্যা

        // Special
        EOF,
        UNKNOWN
    }

    public final Type type;
    public final String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return "Token(" + type + ", \"" + value + "\")";
    }
}
