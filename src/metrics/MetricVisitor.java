package metrics;

import parser.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MetricVisitor extends JavaParserDefaultVisitor {
    private String currentMethodName;
    private final Map<String, ClassMetrics> metricsMap = new HashMap<>();
    private String currentClass;


    // ASTCompilationUnit
    @Override
    public Object visit(ASTCompilationUnit node, Object data) throws Exception {
        node.childrenAccept(this, data);

        // Costruisci la mappa dei figli (NOC)
        for (ClassMetrics potentialChild : metricsMap.values()) {
            String parent = potentialChild.getParentClass();
            if (parent != null && metricsMap.containsKey(parent)) {
                metricsMap.get(parent).addChild(potentialChild.getClassName());
            }
        }
        // Finalizza metrica CBO e LCOM
        for (ClassMetrics cm : metricsMap.values()) {
            cm.finalizeMetrics();
        }

        return data;
    }

    // ASTClassOrInterfaceDeclaration
    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) throws Exception {
        String className = node.getImage();
        currentClass = className;
        metricsMap.putIfAbsent(className, new ClassMetrics(className));

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTExtendsList) {
                for (int j = 0; j < child.jjtGetNumChildren(); j++) {
                    Node ext = child.jjtGetChild(j);
                    if (ext instanceof ASTClassOrInterfaceType) {
                        String parentClass = ((ASTClassOrInterfaceType) ext).getImage();
                        metricsMap.get(className).setParentClass(parentClass);
                    }
                }
            }

        }

        // Continua visita dei figli
        node.childrenAccept(this, data);
        currentClass = null;

        return data;
    }


    // ASTMethodDeclaration
    @Override
    public Object visit(ASTMethodDeclaration node, Object data) throws Exception {
        if (currentClass != null && metricsMap.containsKey(currentClass)) {

            String methodName = null;
            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                Node child = node.jjtGetChild(i);
                if (child instanceof ASTMethodDeclarator) {
                    ASTMethodDeclarator declarator = (ASTMethodDeclarator) child;
                    methodName = declarator.getImage();
                    break;
                }
            }

            if (methodName == null) {
                methodName = node.toString();
            }

            currentMethodName = methodName;

            metricsMap.get(currentClass).methodToAccessedFields.putIfAbsent(currentMethodName, new HashSet<>());

            Object result = super.visit(node, data);

            metricsMap.get(currentClass).incrementMethodCount();

            Set<String> invoked = MethodCallCollector.collect(node);
            metricsMap.get(currentClass).addInvokedMethods(invoked);

            return result;
        }
        return super.visit(node, data);
    }

    //ASTFormalParameter
    @Override
    public Object visit(ASTFormalParameter node, Object data) throws Exception {
        if (currentClass != null) {
            String paramType = node.toString();
            metricsMap.get(currentClass).addCoupledClass(paramType);
        }
        return super.visit(node, data);
    }

    // ASTAllocationExpression
    @Override
    public Object visit(ASTAllocationExpression node, Object data) throws Exception {
        if (currentClass != null) {
            String instantiatedType = node.toString();
            metricsMap.get(currentClass).addCoupledClass(instantiatedType);
        }
        return super.visit(node, data);
    }



    // ASTFieldDeclaration
    @Override
    public Object visit(ASTFieldDeclaration node, Object data) throws Exception {
        if (currentClass != null && metricsMap.containsKey(currentClass)) {
            ClassMetrics classMetrics = metricsMap.get(currentClass);

            for (int i = 0; i < node.jjtGetNumChildren(); i++) {
                Node child = node.jjtGetChild(i);
                if (child instanceof ASTVariableDeclarator) {
                    for (int j = 0; j < child.jjtGetNumChildren(); j++) {
                        Node grandChild = child.jjtGetChild(j);
                        if (grandChild instanceof ASTVariableDeclaratorId) {
                            String fieldName = ((ASTVariableDeclaratorId) grandChild).getImage();
                            classMetrics.fields.add(fieldName);
                            classMetrics.incrementAttributeCount();
                            System.out.println("Campo trovato nella classe " + currentClass + ": " + fieldName);
                        }
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    // ASTPrimaryExpression
    @Override
    public Object visit(ASTPrimaryExpression node, Object data) throws Exception {
        if (currentClass != null && currentMethodName != null && metricsMap.containsKey(currentClass)) {
            ClassMetrics classMetrics = metricsMap.get(currentClass);

            if (node.jjtGetNumChildren() > 0 && node.jjtGetChild(0) instanceof ASTPrimaryPrefix) {
                ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) node.jjtGetChild(0);
                if (prefix.jjtGetNumChildren() > 0 && prefix.jjtGetChild(0) instanceof ASTName) {
                    ASTName nameNode = (ASTName) prefix.jjtGetChild(0);
                    String name = nameNode.getImage(); // può essere "field", "this.field", "object.method", ecc.

                    // Estrai solo il nome del campo se è this.x o x
                    String fieldName = name.contains(".") ? name.substring(name.lastIndexOf('.') + 1) : name;

                    if (classMetrics.fields.contains(fieldName)) {
                        classMetrics.methodToAccessedFields.putIfAbsent(currentMethodName, new HashSet<>());  // <--- qui
                        classMetrics.methodToAccessedFields.get(currentMethodName).add(fieldName);
                        System.out.println("Metodo " + currentMethodName + " usa campo: " + fieldName);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    public Map<String, ClassMetrics> getMetricsMap() {
        return metricsMap;
    }

    public class MethodCallCollector {
        public static Set<String> collect(Node methodNode) throws Exception {
            Set<String> methodCalls = new HashSet<>();
            methodNode.jjtAccept(new MetricVisitor() {
                @Override
                public Object visit(ASTPrimaryExpression node, Object data) throws Exception {
                    if (node.jjtGetNumChildren() > 0) {
                        Node first = node.jjtGetChild(0);
                        if (first instanceof ASTPrimaryPrefix) {
                            ASTPrimaryPrefix prefix = (ASTPrimaryPrefix) first;
                            if (prefix.jjtGetNumChildren() > 0 &&
                                    prefix.jjtGetChild(0) instanceof ASTName) {
                                ASTName name = (ASTName) prefix.jjtGetChild(0);
                                methodCalls.add(name.toString());
                            }
                        }
                    }
                    return super.visit(node, data);
                }
            }, null);
            return methodCalls;
        }
    }

}

