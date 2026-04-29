import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.currentToken = lexer.nextToken();
    }

    private void consume(Token.Type type) {
        if (currentToken.type == type) {
            currentToken = lexer.nextToken();
        } else {
            throw new RuntimeException(
                "Syntax Error: Expected " + type + " but found " + currentToken.type
            );
        }
    }

    public ASTNode parse() {
        List<ASTNode> statements = new ArrayList<>();

        while (currentToken.type != Token.Type.EOF) {
            statements.add(statement());
        }

        return new BlockNode(statements);
    }

    private ASTNode statement() {
        String name = currentToken.value;
        consume(Token.Type.IDENTIFIER);
        consume(Token.Type.ASSIGN);
        return new AssignNode(name, expression());
    }

    private ASTNode expression() {
        ASTNode node = term();

        while (currentToken.type == Token.Type.PLUS) {
            consume(Token.Type.PLUS);
            node = new BinaryOpNode(node, '+', term());
        }

        return node;
    }

    private ASTNode term() {
        ASTNode node = factor();

        while (currentToken.type == Token.Type.MULTIPLY) {
            consume(Token.Type.MULTIPLY);
            node = new BinaryOpNode(node, '*', factor());
        }

        return node;
    }

    private ASTNode factor() {
        if (currentToken.type == Token.Type.NUMBER) {
            String val = currentToken.value;
            consume(Token.Type.NUMBER);
            return new NumberNode(val);
        }
        else if (currentToken.type == Token.Type.IDENTIFIER) {
            String name = currentToken.value;
            consume(Token.Type.IDENTIFIER);
            return new VariableNode(name);
        }

        throw new RuntimeException("Unexpected token: " + currentToken.type);
    }
}