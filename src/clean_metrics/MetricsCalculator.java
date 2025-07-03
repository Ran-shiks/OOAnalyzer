package clean_metrics;

import java.util.*;

public class MetricsCalculator {

    private final Map<String, ClassMetrics> classMetricsMap;

    public MetricsCalculator(Map<String, ClassMetrics> classMetricsMap) {
        this.classMetricsMap = classMetricsMap;
    }

    public void computeMetrics() {
        for (ClassMetrics cm : classMetricsMap.values()) {
            String className = cm.getClassName();

            int wmc = computeWMC(cm);
            int dit = computeDIT(cm);
            int noc = cm.getChildren().size();
            int cbo = computeCBO(cm);
            int rfc = computeRFC(cm);
            int lcom = computeLCOM(cm);
            int cboAdvanced = ComputeAdvancedCBO(cm);

            System.out.printf(
                    "Class: %s%n WMC: %d%n DIT: %d%n NOC: %d%n CBO: %d%n AdvCBO: %d%n RFC: %d%n LCOM: %d%n%n",
                    className, wmc, dit, noc, cbo, cboAdvanced, rfc, lcom
            );
        }
    }

    // --------------
    // METRICHE
    // --------------

    // WMC: Weighted Methods per Class
    // Qui semplificato come numero di metodi (peso unitario)
    private int computeWMC(ClassMetrics cm) {
        return cm.getMethods().size();
    }

    // DIT: Depth of Inheritance Tree
    private int computeDIT(ClassMetrics cm) {
        int depth = 0;
        String currentParent = cm.getParentClass();
        while (currentParent != null) {
            depth++;
            ClassMetrics parentMetrics = classMetricsMap.get(currentParent);
            if (parentMetrics != null) {
                currentParent = parentMetrics.getParentClass();
            } else {
                break;
            }
        }
        return depth;
    }

    // CBO: Coupling Between Object classes
    private int computeCBO(ClassMetrics cm) {
        return cm.getCoupledClasses().size();
    }

    // RFC: Response For a Class
    private int computeRFC(ClassMetrics cm) {
        Set<String> responseSet = new HashSet<>();
        responseSet.addAll(cm.getMethods());
        responseSet.addAll(cm.getInvokedMethods());
        return responseSet.size();
    }

    // LCOM: Lack of Cohesion of Methods
    private int computeLCOM(ClassMetrics cm) {
        int lcom = 0;
        List<Set<String>> accessedFieldsPerMethod = new ArrayList<>();

        for (String method : cm.getMethods()) {
            Set<String> accessedFields = cm.getMethodToAccessedFields().getOrDefault(method, Collections.emptySet());
            accessedFieldsPerMethod.add(accessedFields);
        }

        int n = accessedFieldsPerMethod.size();
        int np = 0; // number of method pairs with shared fields
        int nq = 0; // number of method pairs with no shared fields

        for (int i = 0; i < n; i++) {
            for (int j = i+1; j < n; j++) {
                Set<String> intersection = new HashSet<>(accessedFieldsPerMethod.get(i));
                intersection.retainAll(accessedFieldsPerMethod.get(j));
                if (intersection.isEmpty()) {
                    nq++;
                } else {
                    np++;
                }
            }
        }

        lcom = nq - np;
        if (lcom < 0) lcom = 0;

        return lcom;
    }

    /**
     * Calcola il valore della metrica CBO avanzata.
     * Formula:
     *   AdvCBO = |couplingOut| + |couplingIn| - |couplingInOut|
     * dove
     *   couplingOut = classi usate da questa classe
     *   couplingIn = classi che usano questa classe
     *   couplingInOut = intersezione tra couplingOut e couplingIn
     */
    public int ComputeAdvancedCBO(ClassMetrics cmo) {
        // Passaggio 0: riprendo couplingOut classi usate da questa classe
        Set<String> couplingOut = cmo.getCoupledClasses();
        // Passaggio 1: costruisco mappa da classe -> set di classi che la usano (couplingIn)
        Set<String> couplingIn = new HashSet<>();

        for (ClassMetrics cm : classMetricsMap.values()) {
            if (cm.getCoupledClasses().contains(cmo.getClassName())) {
                couplingIn.add(cm.getClassName());
            }
        }
        Set<String> couplingInOut = new HashSet<>();

        // Passaggio 2: calcola intersezione couplingInOut per ogni classe
        for (String cls : couplingOut) {
            if (couplingIn.contains(cls)) {
                couplingInOut.add(cls);
            }
        }
        return couplingOut.size() + couplingIn.size() - couplingInOut.size();
    }

}

