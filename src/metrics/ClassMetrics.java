package metrics;

import java.util.*;

public class ClassMetrics {

    private static final Set<String> STANDARD_CLASSES = Set.of(
            "String", "System", "List", "Map", "Set", "ArrayList", "HashMap", "HashSet"
            // aggiungi altri tipi standard
    );



    private final String className;
    private String parentClass;
    private int attributeCount = 0;
    private int methodCount = 0;
    private int cbo = 0;
    private int lcom = 0;

    Set<String> coupledClasses = new HashSet<>();
    private Set<String> invokedMethods = new HashSet<>();
    private final List<String> children = new ArrayList<>();


    public final Set<String> fields = new HashSet<>();
    public final Map<String, Set<String>> methodToAccessedFields = new HashMap<>();


    public ClassMetrics(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void addChild(String childClassName) {
        children.add(childClassName);
    }

    public int getDIT(Map<String, ClassMetrics> all) {
        int depth = 0;
        String current = parentClass;
        while (current != null && all.containsKey(current)) {
            depth++;
            current = all.get(current).getParentClass();
        }
        return depth;
    }

    public int getNOC() {
        return children.size();
    }

    public int getLcom() {
        return lcom;
    }



    public void addCoupledClass(String CoupledClass) {
        if (CoupledClass == null || className == null) return;

        String simpleName = CoupledClass.contains(".")
                ? CoupledClass.substring(CoupledClass.lastIndexOf('.') + 1)
                : CoupledClass;

        // Scarta riferimenti alla classe stessa e a librerie standard
        if (!simpleName.equals(className) && !STANDARD_CLASSES.contains(simpleName)) {
            coupledClasses.add(simpleName);
        }
    }
    public void calculateLCOM() {
        int methodPairs = 0;
        int noSharedAttributes = 0;

        List<String> methods = new ArrayList<>(methodToAccessedFields.keySet());

        for (int i = 0; i < methods.size(); i++) {
            for (int j = i + 1; j < methods.size(); j++) {
                methodPairs++;
                Set<String> fields1 = methodToAccessedFields.getOrDefault(methods.get(i), Collections.emptySet());
                Set<String> fields2 = methodToAccessedFields.getOrDefault(methods.get(j), Collections.emptySet());

                Set<String> intersection = new HashSet<>(fields1);
                intersection.retainAll(fields2);

                if (intersection.isEmpty()) {
                    noSharedAttributes++;
                }
            }
        }

        if (methodPairs > 0) {
            lcom = noSharedAttributes;
        } else {
            lcom = 0;
        }

        System.out.println("LCOM debug for class: " + className);
        for (String method : methods) {
            System.out.println("  Method: " + method + " -> fields: " + methodToAccessedFields.get(method));
        }
        System.out.println("  Total method pairs: " + methodPairs + ", no shared fields: " + noSharedAttributes + ", LCOM: " + lcom);
    }



    public void finalizeMetrics() {
        this.cbo = coupledClasses.size();
        calculateLCOM();
    }

    public void incrementAttributeCount() {
        attributeCount++;
    }

    public void incrementMethodCount() {
        methodCount++;
    }

    public void addInvokedMethods(Set<String> methods) {
        invokedMethods.addAll(methods);
    }

    public int getRFC() {
        return methodCount + invokedMethods.size();
    }

    public int getCBO() {
        return cbo;
    }

    public int getAttributeCount() {
        return attributeCount;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    public String getParentClass() {
        return parentClass;
    }

}

