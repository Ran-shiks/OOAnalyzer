

import clean_metrics.MetricsCalculator;
import clean_metrics.ClassMetrics;
import clean_metrics.MetricVisitor;
import parser.*;

import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Java Parser Version 1.1:  Reading from file ");
        Node root = null;
        JavaParser OOparser = new JavaParser("/Users/izzofrancesco/Desktop/JetbrainsProjects/OOAnalyzer/input/Esempio Java Project/Originale/Main.java");
        try {
            root = OOparser.CompilationUnit();
            System.out.println("Java Parser Version 1.1:  Java program parsed successfully.");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.out.println("Java Parser Version 1.1:  Encountered errors during parse.");
        }


        /*
        if(root != null) {
            ((SimpleNode) root).dump("");
        } else {
            System.out.println("NOP");
        }
        */

        MetricVisitor visitor = new MetricVisitor();
        root.jjtAccept(visitor, null);
        Map<String, ClassMetrics> metricsMap = visitor.getMetricsMap();

        MetricsCalculator calc = new MetricsCalculator(metricsMap);
        calc.computeMetrics();
        calc.exportMetricsToCSV("/Users/izzofrancesco/Desktop/JetbrainsProjects/OOAnalyzer/src/report/originale.csv");




        Node root2 = null;
        JavaParser OOparser2 = new JavaParser("/Users/izzofrancesco/Desktop/JetbrainsProjects/OOAnalyzer/input/Esempio Java Project/chatGPT/main.java");
        try {
            root2 = OOparser2.CompilationUnit();
            System.out.println("Java Parser Version 1.1:  Java program parsed successfully.");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.out.println("Java Parser Version 1.1:  Encountered errors during parse.");
        }

        MetricVisitor visitor2 = new MetricVisitor();
        root2.jjtAccept(visitor2, null);
        Map<String, ClassMetrics> metricsMap2 = visitor.getMetricsMap();

        MetricsCalculator calc2 = new MetricsCalculator(metricsMap2);
        calc2.computeMetrics();
        calc2.exportMetricsToCSV("/Users/izzofrancesco/Desktop/JetbrainsProjects/OOAnalyzer/src/report/chatGPT.csv");
        /*
        MetricVisitor visitor = new MetricVisitor();
        assert root != null;
        root.jjtAccept(visitor, null);

        for (var entry : visitor.getMetricsMap().entrySet()) {
            ClassMetrics m = entry.getValue();
            System.out.println("Classe: " + entry.getKey());
            System.out.println("  DIT: " + m.getDIT(visitor.getMetricsMap()));
            System.out.println("  NOC: " + m.getNOC());
            System.out.println("  RFC: " + m.getRFC());
            System.out.println("  Attributi: " + m.getAttributeCount());
            System.out.println("CBO className raw: " + entry.getKey());
            System.out.println("  CBO: " + m.getCBO());
            System.out.println("  LCOM: " + m.getLcom());

        }
        //CKMetricsVisitor visitor = new CKMetricsVisitor();
        //root.jjtAccept(visitor, null);

         */
    }
}

