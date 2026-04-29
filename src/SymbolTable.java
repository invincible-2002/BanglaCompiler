import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private final Map<String, String> table = new HashMap<>();

    public void set(String name, String type) {
        table.put(name, type);
    }

    public boolean exists(String name) {
        return table.containsKey(name);
    }

    public String get(String name) {
        if (!table.containsKey(name)) {
            throw new RuntimeException("Variable not declared: " + name);
        }
        return table.get(name);
    }

    public void display() {
        System.out.println("\n--- Symbol Table ---");
        if (table.isEmpty()) {
            System.out.println("Empty");
        } else {
            table.forEach((k, v) ->
                    System.out.println("Variable: " + k + " | Type: " + v));
        }
        System.out.println("--------------------");
    }
}