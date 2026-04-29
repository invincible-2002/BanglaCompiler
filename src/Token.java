public class Token {
    public enum Type {
        IDENTIFIER, NUMBER, ASSIGN, PLUS, MULTIPLY, EOF
    }

    public final Type type;
    public final String value;

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return "Token(" + type + ", " + value + ")";
    }
}