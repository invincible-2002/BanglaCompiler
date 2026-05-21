import java.util.List;

// ─── Base interface ───────────────────────────────────────────────────────────
interface ASTNode {
    void checkType(SymbolTable symbolTable);
    String generatePython(int indent);
}

// ─── Helper ───────────────────────────────────────────────────────────────────
class ASTHelper {
    static String spaces(int indent) {
        return "    ".repeat(indent);
    }
}

// ─── Block (list of statements) ───────────────────────────────────────────────
class BlockNode implements ASTNode {
    List<ASTNode> statements;

    BlockNode(List<ASTNode> statements) {
        this.statements = statements;
    }

    public void checkType(SymbolTable symbolTable) {
        for (ASTNode stmt : statements) {
            stmt.checkType(symbolTable);
        }
    }

    public String generatePython(int indent) {
        if (statements.isEmpty()) {
            return ASTHelper.spaces(indent) + "pass\n";
        }
        StringBuilder sb = new StringBuilder();
        for (ASTNode stmt : statements) {
            sb.append(stmt.generatePython(indent));
        }
        return sb.toString();
    }
}

// ─── Assignment  x = expr ─────────────────────────────────────────────────────
class AssignNode implements ASTNode {
    String name;
    ASTNode expression;

    AssignNode(String name, ASTNode expression) {
        this.name = name;
        this.expression = expression;
    }

    public void checkType(SymbolTable symbolTable) {
        expression.checkType(symbolTable);
        // Infer type from expression
        String type = inferType(expression, symbolTable);
        symbolTable.set(name, type);
    }

    private String inferType(ASTNode node, SymbolTable st) {
        if (node instanceof NumberNode)   return "NUMBER";
        if (node instanceof StringLitNode) return "STRING";
        if (node instanceof BoolNode)     return "BOOLEAN";
        if (node instanceof VariableNode) return st.getType(((VariableNode) node).name);
        if (node instanceof BinaryOpNode) {
            BinaryOpNode b = (BinaryOpNode) node;
            if (b.op.equals("+") || b.op.equals("-") ||
                b.op.equals("*") || b.op.equals("/")) return "NUMBER";
            return "BOOLEAN"; // comparison ops
        }
        return "NUMBER";
    }

    public String generatePython(int indent) {
        return ASTHelper.spaces(indent) + name + " = " + expression.generatePython(0) + "\n";
    }
}

class NumberNode implements ASTNode {
    String value; // stored in Bangla digits or ASCII

    NumberNode(String value) {
        this.value = value;
    }

    public void checkType(SymbolTable symbolTable) { /* always valid */ }

    public String generatePython(int indent) {
        // Convert Bangla digits to ASCII
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            if (c >= '\u09E6' && c <= '\u09EF') {
                sb.append((char) ('0' + (c - '\u09E6')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}

class StringLitNode implements ASTNode {
    String value;

    StringLitNode(String value) {
        this.value = value;
    }

    public void checkType(SymbolTable symbolTable) { /* always valid */ }

    public String generatePython(int indent) {
        return "\"" + value + "\"";
    }
}

class BoolNode implements ASTNode {
    boolean value;

    BoolNode(boolean value) {
        this.value = value;
    }

    public void checkType(SymbolTable symbolTable) { /* always valid */ }

    public String generatePython(int indent) {
        return value ? "True" : "False";
    }
}

class VariableNode implements ASTNode {
    String name;

    VariableNode(String name) {
        this.name = name;
    }

    public void checkType(SymbolTable symbolTable) {
        if (!symbolTable.exists(name)) {
            throw new RuntimeException("Undefined variable: " + name);
        }
    }

    public String generatePython(int indent) {
        return name;
    }
}

class BinaryOpNode implements ASTNode {
    ASTNode left, right;
    String op;

    BinaryOpNode(ASTNode left, String op, ASTNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public void checkType(SymbolTable symbolTable) {
        left.checkType(symbolTable);
        right.checkType(symbolTable);
    }

    public String generatePython(int indent) {
        return "(" + left.generatePython(0) + " " + op + " " + right.generatePython(0) + ")";
    }
}

class IfNode implements ASTNode {
    ASTNode condition;
    ASTNode thenBranch;
    ASTNode elseBranch; // may be null

    IfNode(ASTNode condition, ASTNode thenBranch, ASTNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public void checkType(SymbolTable symbolTable) {
        condition.checkType(symbolTable);
        thenBranch.checkType(symbolTable);
        if (elseBranch != null) elseBranch.checkType(symbolTable);
    }

    public String generatePython(int indent) {
        String pad = ASTHelper.spaces(indent);
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append("if ").append(condition.generatePython(0)).append(":\n");
        sb.append(thenBranch.generatePython(indent + 1));
        if (elseBranch != null) {
            sb.append(pad).append("else:\n");
            sb.append(elseBranch.generatePython(indent + 1));
        }
        return sb.toString();
    }
}

class WhileNode implements ASTNode {
    ASTNode condition;
    ASTNode body;

    WhileNode(ASTNode condition, ASTNode body) {
        this.condition = condition;
        this.body = body;
    }

    public void checkType(SymbolTable symbolTable) {
        condition.checkType(symbolTable);
        body.checkType(symbolTable);
    }

    public String generatePython(int indent) {
        String pad = ASTHelper.spaces(indent);
        StringBuilder sb = new StringBuilder();
        sb.append(pad).append("while ").append(condition.generatePython(0)).append(":\n");
        sb.append(body.generatePython(indent + 1));
        return sb.toString();
    }
}

class PrintNode implements ASTNode {
    ASTNode expression;

    PrintNode(ASTNode expression) {
        this.expression = expression;
    }

    public void checkType(SymbolTable symbolTable) {
        expression.checkType(symbolTable);
    }

    public String generatePython(int indent) {
        return ASTHelper.spaces(indent) + "print(" + expression.generatePython(0) + ")\n";
    }
}
