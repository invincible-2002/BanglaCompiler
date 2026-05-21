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
        IDENTIFIER,
        NUMBER,
        ASSIGN,
        PLUS,
        MULTIPLY,
        EOF
    }

    public final Type type;
    public final String value;

    // Constructor
    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    // Convert token to string representation
    @Override
    public String toString() {
        return "Token(" + type + ", \"" + value + "\")";
        StringBuilder result = new StringBuilder();
        result.append("Token(")
              .append(type)
              .append(", ")
              .append(value)
              .append(")");
        return result.toString();
    }
}
