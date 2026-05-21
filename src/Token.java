public class Token {

    public enum Type {
<<<<<<< HEAD
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
=======
        IDENTIFIER,
        NUMBER,
        ASSIGN,
        PLUS,
        MULTIPLY,
        EOF
>>>>>>> cac187d8c579f2005d9a5234af50696a503a00df
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
<<<<<<< HEAD
        return "Token(" + type + ", \"" + value + "\")";
=======
        StringBuilder result = new StringBuilder();
        result.append("Token(")
              .append(type)
              .append(", ")
              .append(value)
              .append(")");
        return result.toString();
>>>>>>> cac187d8c579f2005d9a5234af50696a503a00df
    }
}
