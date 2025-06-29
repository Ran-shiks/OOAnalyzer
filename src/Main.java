

import metrics.ClassMetrics;
import metrics.MetricVisitor;
import parser.*;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("Java Parser Version 1.1:  Reading from file ");
        Node root = null;
        JavaParser OOparser = new JavaParser("/Users/izzofrancesco/Desktop/JetbrainsProjects/OOAnalyzer/input/esempioComplesso2.java");
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
    }
}

