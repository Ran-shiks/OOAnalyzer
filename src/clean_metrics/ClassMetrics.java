package clean_metrics;

import java.util.*;

public class ClassMetrics {

    private final String className;
    private String parentClass;

    private final Set<String> fields = new HashSet<>();
    private final Set<String> methods = new HashSet<>();
    private final Map<String, Set<String>> methodToAccessedFields = new HashMap<>();

    private final Set<String> coupledClasses = new HashSet<>();
    private final Set<String> invokedMethods = new HashSet<>();
    private final List<String> children = new ArrayList<>();

    public ClassMetrics(String className) {
        this.className = className;
    }

    // ----------------------------
    // GETTER & SETTER
    // ----------------------------
    public String getClassName() {
        return className;
    }

    public String getParentClass() {
        return parentClass;
    }

    public void setParentClass(String parentClass) {
        this.parentClass = parentClass;
    }

    public Set<String> getFields() {
        return fields;
    }

    public Set<String> getMethods() {
        return methods;
    }

    public Map<String, Set<String>> getMethodToAccessedFields() {
        return methodToAccessedFields;
    }

    public Set<String> getCoupledClasses() {
        return coupledClasses;
    }

    public Set<String> getInvokedMethods() {
        return invokedMethods;
    }

    public List<String> getChildren() {
        return children;
    }

    // ----------------------------
    // SUPPORT METODI AGGIUNTIVI
    // ----------------------------
    public void addChild(String child) {
        children.add(child);
    }

    public void addField(String field) {
        fields.add(field);
    }

    public void addMethod(String method) {
        methods.add(method);
    }

    public void addCoupledClass(String coupled) {
        coupledClasses.add(coupled);
    }

    public void addInvokedMethod(String invoked) {
        invokedMethods.add(invoked);
    }

    public void addMethodAccessedField(String method, String field) {
        methodToAccessedFields
                .computeIfAbsent(method, k -> new HashSet<>())
                .add(field);
    }


}
