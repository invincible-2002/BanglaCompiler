import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.setOut(new java.io.PrintStream(System.out, true, StandardCharsets.UTF_8));

            String code = Files.readString(Path.of("input.bn"), StandardCharsets.UTF_8);

            System.out.println("Compiling input.bn...");
            System.out.println("Source:\n" + code);

            Lexer lexer = new Lexer(code);
            Parser parser = new Parser(lexer);
            ASTNode root = parser.parse();

            SymbolTable st = new SymbolTable();
            root.checkType(st);

            System.out.println("\n✅ Review-1 Passed!");
            st.display();

        } catch (Exception e) {
            System.err.println("\n❌ Error: " + e.getMessage());
        }
    }
}