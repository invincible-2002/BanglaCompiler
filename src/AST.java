import java.util.List;

interface ASTNode {
    void checkType(SymbolTable symbolTable);
}

// For multiple statements
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
}

class AssignNode implements ASTNode {
    String name;
    ASTNode expression;

    AssignNode(String name, ASTNode expression) {
        this.name = name;
        this.expression = expression;
    }

    public void checkType(SymbolTable symbolTable) {
        expression.checkType(symbolTable);
        symbolTable.set(name, "NUMBER");
    }
}

class NumberNode implements ASTNode {
    String value;

    NumberNode(String value) {
        this.value = value;
    }

    public void checkType(SymbolTable symbolTable) {
        // Always valid
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
}

class BinaryOpNode implements ASTNode {
    ASTNode left, right;
    char op;

    BinaryOpNode(ASTNode left, char op, ASTNode right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public void checkType(SymbolTable symbolTable) {
        left.checkType(symbolTable);
        right.checkType(symbolTable);
    }
}