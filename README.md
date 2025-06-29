
Struttura del progetto:

OOAnalyzer/
├── src/
│   ├── parser/                # File .jj e .jjt
│   ├── ast/                   # Strutture dati per l'AST
│   ├── metrics/               # Calcolo delle metriche
│   ├── report/                # Generazione dei report
│   └── Main.java              # Entry point
├── input/                     # File Java da analizzare
├── output/                    # File CSV o TXT generati
└── README.md

Nella directory src/parser ho creato un file in cui ho messo la grammatica java 1.5:
https://github.com/javacc/javacc/blob/master/examples/JavaGrammars/1.5/Java1.5.jj

Ho trasformato il file da un .jj a un .jjt e ho taggato le produzioni per la quali si devono creare dei nodi nell'albero utilialle metriche.
Le produzioni scelte sono:

| Produzione                                    | Motivo dell'etichettatura                                  |
| --------------------------------------------- | ---------------------------------------------------------- |
| `CompilationUnit`                             | Nodo radice dell'AST. Necessario.                          |
| `ClassOrInterfaceDeclaration`                 | Per identificare classi e interfacce (WMC, DIT, NOC, etc.) |
| `MethodDeclaration`                           | Per contare metodi (WMC), vedere chiamate (RFC), etc.      |
| `ConstructorDeclaration`                      | Anche i costruttori vanno contati in WMC.                  |
| `FieldDeclaration`                            | Per trovare gli attributi usati in LCOM e RFC.             |
| `EnumDeclaration`                             | Se vuoi supportare enum per completezza.                   |
| `ImplementsList` e `ExtendsList`              | Per DIT, NOC e CBO.                                        |
| `ClassOrInterfaceType`                        | Per capire i riferimenti ad altre classi (CBO).            |
| `MethodDeclarator`                            | Utile se vuoi separare la firma del metodo.                |
| `FormalParameter`                             | Per analisi dei parametri nei metodi.                      |
| `Block`                                       | Per identificare corpo dei metodi (opzionale).             |
| `Name`                                        | Spesso rappresenta riferimenti a classi/metodi/variabili.  |
| `PrimaryExpression`                           | Contiene chiamate a metodi, importante per RFC e CBO.      |
| `Arguments`                                   | Per vedere quante chiamate a metodi (RFC).                 |
| `Type`                                        | Usato per determinare il tipo dei campi/metodi (CBO).      |
| `VariableDeclarator` / `VariableDeclaratorId` | Per estrarre nomi di attributi/variabili locali.           |

Ho successivamente eseguito i comandi per la compilazione:

jjtree parser.jjt

javacc parser.jj

**Strategia generale**

Si costruisce un visitor (MetricVisitor) che visiti i nodi rilevanti (classi, metodi, chiamate, campi, ecc.).

Si crea una struttura dati per memorizzare informazioni su ogni classe (nome, superclassi, metodi, attributi, dipendenze).

Visita tutto l’AST e popola questa struttura.

Calcola le metriche a partire dalla struttura creata.