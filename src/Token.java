public class Token {

    public enum Type {
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
        StringBuilder result = new StringBuilder();
        result.append("Token(")
              .append(type)
              .append(", ")
              .append(value)
              .append(")");
        return result.toString();
    }
}
