import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        currentToken = lexer.nextToken(); 
    }

    private void consume(Token.Type expectedType) { 
        if (currentToken.type == expectedType) {
            currentToken = lexer.nextToken();
        } else {
            throw new RuntimeException(
                "Syntax Error: Expected " + expectedType + " but found " + currentToken.type
            );
        }
    }

    public ASTNode parse() {
        List<ASTNode> stmtList = new ArrayList<>(); 

        while (currentToken.type != Token.Type.EOF) {
            stmtList.add(statement());
        }

        return new BlockNode(stmtList);
    }

    private ASTNode statement() {
        String varName = currentToken.value; 
        consume(Token.Type.IDENTIFIER);
        consume(Token.Type.ASSIGN);

        ASTNode expr = expression(); 
        return new AssignNode(varName, expr);
    }

    private ASTNode expression() {
        ASTNode result = term(); 

        while (currentToken.type == Token.Type.PLUS) {
            consume(Token.Type.PLUS);
            result = new BinaryOpNode(result, '+', term());
        }

        return result;
    }

    private ASTNode term() {
        ASTNode result = factor(); 

        while (currentToken.type == Token.Type.MULTIPLY) {
            consume(Token.Type.MULTIPLY);
            result = new BinaryOpNode(result, '*', factor());
        }

        return result;
    }

    private ASTNode factor() {
        if (currentToken.type == Token.Type.NUMBER) {
            String numberValue = currentToken.value; 
            consume(Token.Type.NUMBER);
            return new NumberNode(numberValue);
        } 
        else if (currentToken.type == Token.Type.IDENTIFIER) {
            String varName = currentToken.value; 
            consume(Token.Type.IDENTIFIER);
            return new VariableNode(varName);
        }

        throw new RuntimeException("Unexpected token: " + currentToken.type);
    }
}
