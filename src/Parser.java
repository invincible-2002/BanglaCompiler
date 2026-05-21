import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;
    private Token currentToken;
    private final List<String> errors = new ArrayList<>();

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        currentToken = lexer.nextToken();
    }

    public List<String> getErrors() { return errors; }

    // ── Token helpers ────────────────────────────────────────────────────────

    private void advance() {
        currentToken = lexer.nextToken();
    }

    private void consume(Token.Type expectedType) {
        if (currentToken.type == expectedType) {
            advance();
        } else {
            String msg = "Syntax Error [line " + lexer.getLine() + "]: Expected "
                         + expectedType + " but found " + currentToken.type
                         + " (\"" + currentToken.value + "\")";
            errors.add(msg);
            System.err.println(msg);
            // Error recovery: skip to next semicolon or end of block
            recover();
        }
    }

    /** Basic error recovery — skips tokens until ; } or EOF */
    private void recover() {
        while (currentToken.type != Token.Type.SEMICOLON
               && currentToken.type != Token.Type.RBRACE
               && currentToken.type != Token.Type.EOF) {
            advance();
        }
        if (currentToken.type == Token.Type.SEMICOLON) {
            advance(); // consume the semicolon
        }
    }

    // ── Top-level parse ─

    public ASTNode parse() {
        List<ASTNode> stmts = new ArrayList<>();
        while (currentToken.type != Token.Type.EOF) {
            ASTNode s = statement();
            if (s != null) stmts.add(s);
        }
        return new BlockNode(stmts);
    }

    // ── Statement dispatcher ─────────────────────────────────────────────────

    private ASTNode statement() {
        try {
            return switch (currentToken.type) {
                case IF         -> parseIf();
                case WHILE      -> parseWhile();
                case PRINT      -> parsePrint();
                case IDENTIFIER -> parseAssign();
                default -> {
                    String msg = "Syntax Error [line " + lexer.getLine()
                                 + "]: Unexpected token " + currentToken.type
                                 + " (\"" + currentToken.value + "\")";
                    errors.add(msg);
                    System.err.println(msg);
                    recover();
                    yield null;
                }
            };
        } catch (RuntimeException e) {
            errors.add(e.getMessage());
            System.err.println(e.getMessage());
            recover();
            return null;
        }
    }

    // ── Assignment: identifier = expr ; ─────────────────────────────────────

    private ASTNode parseAssign() {
        String varName = currentToken.value;
        consume(Token.Type.IDENTIFIER);
        consume(Token.Type.ASSIGN);
        ASTNode expr = expression();
        consumeSemicolon();
        return new AssignNode(varName, expr);
    }

    // ── Print: দেখাও ( expr ) ; ──────────────────────────────────────────────

    private ASTNode parsePrint() {
        consume(Token.Type.PRINT);
        consume(Token.Type.LPAREN);
        ASTNode expr = expression();
        consume(Token.Type.RPAREN);
        consumeSemicolon();
        return new PrintNode(expr);
    }

    // ── If: যদি ( cond ) { block } [ নাহলে { block } ] ──────────────────────

    private ASTNode parseIf() {
        consume(Token.Type.IF);
        consume(Token.Type.LPAREN);
        ASTNode cond = comparison();
        consume(Token.Type.RPAREN);
        consume(Token.Type.LBRACE);
        ASTNode thenBlock = parseBlock();
        consume(Token.Type.RBRACE);

        ASTNode elseBlock = null;
        if (currentToken.type == Token.Type.ELSE) {
            consume(Token.Type.ELSE);
            consume(Token.Type.LBRACE);
            elseBlock = parseBlock();
            consume(Token.Type.RBRACE);
        }

        return new IfNode(cond, thenBlock, elseBlock);
    }

    // ── While: যতক্ষণ ( cond ) { block } ────────────────────────────────────

    private ASTNode parseWhile() {
        consume(Token.Type.WHILE);
        consume(Token.Type.LPAREN);
        ASTNode cond = comparison();
        consume(Token.Type.RPAREN);
        consume(Token.Type.LBRACE);
        ASTNode body = parseBlock();
        consume(Token.Type.RBRACE);
        return new WhileNode(cond, body);
    }

    // ── Block: sequence of statements until } ────────────────────────────────

    private ASTNode parseBlock() {
        List<ASTNode> stmts = new ArrayList<>();
        while (currentToken.type != Token.Type.RBRACE
               && currentToken.type != Token.Type.EOF) {
            ASTNode s = statement();
            if (s != null) stmts.add(s);
        }
        return new BlockNode(stmts);
    }

    // ── Comparison: expr ( == | != | < | > | <= | >= ) expr ─────────────────

    private ASTNode comparison() {
        ASTNode left = expression();
        String op = switch (currentToken.type) {
            case EQ  -> "==";
            case NEQ -> "!=";
            case LT  -> "<";
            case GT  -> ">";
            case LTE -> "<=";
            case GTE -> ">=";
            default  -> null;
        };
        if (op != null) {
            advance();
            ASTNode right = expression();
            return new BinaryOpNode(left, op, right);
        }
        return left; // bare expression (e.g. boolean var)
    }

    // ── Expression: term ( + | - term )* ────────────────────────────────────

    private ASTNode expression() {
        ASTNode result = term();
        while (currentToken.type == Token.Type.PLUS
               || currentToken.type == Token.Type.MINUS) {
            String op = currentToken.type == Token.Type.PLUS ? "+" : "-";
            advance();
            result = new BinaryOpNode(result, op, term());
        }
        return result;
    }

    // ── Term: factor ( * | / factor )* ──────────────────────────────────────

    private ASTNode term() {
        ASTNode result = factor();
        while (currentToken.type == Token.Type.MULTIPLY
               || currentToken.type == Token.Type.DIVIDE) {
            String op = currentToken.type == Token.Type.MULTIPLY ? "*" : "/";
            advance();
            result = new BinaryOpNode(result, op, factor());
        }
        return result;
    }

    // ── Factor: number | string | bool | identifier | ( expr ) ──────────────

    private ASTNode factor() {
        return switch (currentToken.type) {
            case NUMBER -> {
                String val = currentToken.value;
                advance();
                yield new NumberNode(val);
            }
            case STRING -> {
                String val = currentToken.value;
                advance();
                yield new StringLitNode(val);
            }
            case TRUE -> {
                advance();
                yield new BoolNode(true);
            }
            case FALSE -> {
                advance();
                yield new BoolNode(false);
            }
            case IDENTIFIER -> {
                String name = currentToken.value;
                advance();
                yield new VariableNode(name);
            }
            case LPAREN -> {
                advance(); // consume (
                ASTNode inner = expression();
                consume(Token.Type.RPAREN);
                yield inner;
            }
            default -> throw new RuntimeException(
                "Syntax Error [line " + lexer.getLine() + "]: Unexpected token "
                + currentToken.type + " (\"" + currentToken.value + "\")"
            );
        };
    }

    // ── Semicolon consumer (with soft error recovery) ────────────────────────

    private void consumeSemicolon() {
        if (currentToken.type == Token.Type.SEMICOLON) {
            advance();
        } else {
            String msg = "Syntax Error [line " + lexer.getLine()
                         + "]: Missing ';' after statement";
            errors.add(msg);
            System.err.println(msg);
            // Don't call recover() — just warn and continue
        }
    }
}
