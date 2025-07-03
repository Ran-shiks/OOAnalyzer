# Progetto: Parser JavaCC per metriche orientate agli oggetti

## Obiettivo del progetto

Sviluppare da zero (oppure utilizzando una grammatica disponibile online) un parser statico per analizzare programmi OO scritti in un solo linguaggio (Java o Python) utilizzando **JavaCC**.

Il parser deve analizzare file di codice sorgente e costruire la struttura sintattica interna (ad esempio un albero AST) per estrarre informazioni utili al calcolo di metriche di programmi orientati agli oggetti.

L’obiettivo finale è calcolare e restituire automaticamente un insieme di metriche OO descrittive del progetto analizzato (ad esempio complessità delle classi, accoppiamenti, profondità di ereditarietà, coesione, ecc.).

In questo modo il progetto combina concetti di compilatori e di analisi statica del codice.

---

## Specifiche tecniche

* **Linguaggio e strumenti**: implementazione in Java, utilizzando *JavaCC* (Java Compiler Compiler).
* **Grammatica**: lo studente definirà o utilizzerà una grammatica JavaCC per il linguaggio scelto.
* **Parser e struttura**: si svilupperà un parser LL(1) con JavaCC. Il file di input (sorgente Java o Python) verrà parsato per costruire internamente la rappresentazione sintattica del programma (solitamente un AST tramite *JJTree*). Il parser dovrà gestire correttamente la struttura OO: definizioni di classi, metodi, attributi, ereditarietà, chiamate di metodi, riferimenti ad attributi, ecc.
* **Formato di input**: il programma accetterà uno o più file di codice sorgente (con estensione `.java` o `.py`). I file saranno processati singolarmente dal parser generato con JavaCC.
* **Output**: il risultato del tool sarà un report testuale (in file) che elenchi, per ogni classe individuata nel sorgente, i valori delle metriche richieste. Ad esempio si potrà produrre un’uscita tabellare o CSV con righe del tipo:

  ```
  Classe, WMC, DIT, NOC, CBO, advCBO, RFC, LCOM
  ```

  È essenziale documentare chiaramente il formato di output e includere esempi. Il report dovrà essere leggibile e, se possibile, ordinato per rilevanza o nome di classe.

> L’idea è anche quella di confrontare le metriche rilevate su programmi disponibili o implementati ad-hoc con programmi equivalenti generati automaticamente (ad esempio tramite ChatGPT o strumenti simili), a partire dalla stessa traccia di programma.
> L’obiettivo è confrontare la qualità del software OO scritto manualmente rispetto a quello generato automaticamente dall’intelligenza artificiale generativa.

Ovviamente il parser per la valutazione della qualità del software deve essere lo stesso, indipendentemente da come è stato ottenuto il codice.

---

## Metriche orientate agli oggetti da implementare

Si richiede di calcolare almeno **sei metriche OO ben note**, preferibilmente la suite di *Chidamber e Kemerer*:

* **WMC (Weighted Methods per Class)**
  Misura la complessità di una classe sommando le complessità dei suoi metodi. Tipicamente ogni metodo ha complessità pari alla sua complessità ciclomatica; in alternativa, si può definire WMC come il **numero di metodi dichiarati in una classe**. Un valore alto di WMC indica una classe complessa e potenzialmente difficoltosa da mantenere.

* **DIT (Depth of Inheritance Tree)**
  Indica la profondità di una classe nella gerarchia di ereditarietà. È definito come il **numero massimo di nodi (livelli di classe padre) tra la classe e la radice** (ad esempio `java.lang.Object` in Java). Maggiore è il DIT, più complessa può essere la previsione del comportamento della classe e maggiore il potenziale riuso, ma anche il rischio di complessità.

* **NOC (Number Of Children)**
  Conta il **numero di sottoclassi dirette di una classe**. Un valore elevato di NOC può indicare un alto riuso della classe base, ma anche suggerire una gerarchia complessa da testare.

* **CBO (Coupling Between Object classes)**
  Misura il coupling, ovvero il **numero di occorrenze di collaborazioni o dipendenze fra una classe e le altre classi del sistema**. Un alto valore di CBO riduce la modularità e la riusabilità, aumentando la fragilità del software.

* **Advanced CBO (Advanced Coupling Between Object classes)**
  Misura il coupling, ovvero il **numero di occorrenze di collaborazioni o dipendenze fra una classe e le altre classi del sistema**. Un alto valore di CBO riduce la modularità e la riusabilità, aumentando la fragilità del software. Misurata secondo la relazione AdvCBO = |couplingOut| + |couplingIn| - |couplingInOut|.


* **RFC (Response For a Class)**
  Definisce la dimensione del metodo di risposta di una classe: è **il numero di metodi che possono essere invocati in risposta a un messaggio inviato a un oggetto della classe**. Un valore alto di RFC significa che la classe interagisce con molte altre funzionalità, aumentando la complessità di test e debugging.

* **LCOM (Lack of Cohesion in Methods)**
  Misura la **coesione tra i metodi di una classe rispetto agli attributi utilizzati**. Un valore alto indica bassa coesione (metodi che operano su dati disgiunti), segnalando possibili difetti di progettazione. Al contrario, un valore basso indica alta coesione e buona qualità del design.

* **(Opzionale) Numero di Attributi per Classe**
  Oltre alle metriche CK, si può considerare anche il **numero di attributi dichiarati in una classe**. Una classe con molti attributi può risultare complessa o poco coesa, fornendo un ulteriore indicatore di qualità.

## Scelte preliminari

Tra le scelte preliminari da fare c'è la necessità di scegliere il linguaggio target.
il linguaggio target scelto è Java questo perché:

- La grammatica è più facilmente disponibile e gestibile con JavaCC.
- Java è strettamente OO, quindi più adatto al calcolo delle metriche CK.
- Esistono grammatiche già pronte per JavaCC o JJTree.

### Struttura del progetto

```text
OOAnalyzer/
├── src/
│   ├── parser/                # File .jj e .jjt
│   ├── metrics/               # Calcolo delle metriche
│   ├── clean_metrics/         # Calcolo delle metriche
│   ├── report/                # Generazione dei report
│   └── Main.java              # Entry point
├── input/                     # File Java da analizzare
├── output/                    # File CSV o TXT generati
└── README.md
```

## Parser con JavaCC e JJTree

Il progetto è iniziato da un file .jj trovato sul github di javacc.

https://github.com/javacc/javacc/blob/master/examples/JavaGrammars/1.5/Java1.5.jj

Il file contiene la grammatica javacc per riconoscere la sintassi java JDK 1.5. Il parser strutturato su questa grammatica gestisce generici, nuovi cicli for, importazioni statiche e tipi di annotazione.

## Costruzione dell’AST

Per costruire un Abstract Syntaxt Tree in maniera diretta possiamo utilizzare JJTree.

```
options {
  STATIC = false;
  MULTI = true;
  VISITOR = true;
  JJTREE = true;
  NODE_PREFIX = "AST";
}
```

Partendo dal file scaricato .jj è stato creato il file .jjt, un file JavaCC con marcatori extra per JJTree.
Ogni produzione può generare un nodo del tree se segnato da annotazioni particolati nella forma:

Convertire un file .jj in .jjt vuol dire aggiungere le istruzioni di JJTree al tuo file JavaCC esistente, in modo che possa generare l’AST automatico.
I passi che si sono stati eseguiti sono i seguenti:

- Si rinomina il file da .jj a .jjt
- Si aggiunge l’intestazione option JJTREE = true;
- Si trasforma ogni produzione in una produzione JJTree

Ovvero per ogni regola di produzione di interesse si aggiunge un l'annotazione di questo tipo:
```
# NomeDelNodo
```
Una volta definito questo file lo si può compilare con jjtree e questo produrrà un file .jj standard, con codice JavaCC già modificato da JJTree per costruire il tree.

```
jjtree parser.jjt
```
Una volta ottenuto il nuovo file .jj lo si può compilare così:
```
javacc parser.jj
```
Con questa istruzione viene generato il parser MiniJavaParser.java, e tutte le classi dei nodi AST (ASTClassDeclaration.java, ASTFieldDeclaration.java, ecc.).
Usiamo il parser per parsare codice e ottenere un albero AST.

Si può navigare l’albero, stamparlo con dump(), o analizzarlo con il pattern Visitor, cosa che si farà per calcolare le metriche di interesse.

#### Esempio Produzione

```
void ClassDeclaration() #ClassDeclaration : {}
{
  "class" Identifier() "{" ( MethodDeclaration() )* "}"
}
```

JJTree crea una classe ASTClassDeclaration (figlia di SimpleNode).
Quando il parser riconosce una classe nel codice, crea un nodo ASTClassDeclaration nell’AST e lo inserisce nel tree.
I figli (es. metodi) sono aggiunti come figli del nodo classe.

Ogni nodo generato da JJTree estende SimpleNode.
```
public class ASTClassDeclaration extends SimpleNode {
    public ASTClassDeclaration(int id) {
        super(id);
    }

    public ASTClassDeclaration(MiniJavaParser p, int id) {
        super(p, id);
    }

    // Puoi aggiungere metodi personalizzati se vuoi!
}
```
Ogni nodo:

- Può avere figli (jjtGetChild(i))
- Può essere visitato (visitor pattern)
- Può stampare se stesso (dump())

Le opzioni che abbiamo inserito nel file .jjt servono a dare delle direttive importanti durante la compilazione del file da parte di jjtre.

```
options {
  JJTree=true;         // abilita JJTree
  VISITOR=true;        // genera interfaccia Visitor
  NODE_PREFIX="AST";   // prefisso delle classi nodo
  MULTI=true;          // abilita più figli (non monadi)
}
```

## Estrazione delle metriche

Dato che il progetto riguarda analizzare codice Java (OO) per calcolare metriche di qualità come WMC, DIT, NOC, CBO, RFC, LCOM, la priorità è identificare correttamente classi, metodi, attributi, interfacce, chiamate di metodo, e relazioni di ereditarietà o implementazione.

Quindi l'obiettivo è etichettare le produzioni chiave in modo che generino nodi nell'albero di sintassi astratto (AST), utili per estrarre le metriche CK.

Le seguenti sono le regole di produzione scelte da etichettare:

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

## Scrittura del Visitor

Una volta usato JJTree con VISITOR=true, allora si può implementare il design pattern Visitor per camminare l'albero sintattico e calcolare le metriche.

L'obiettivo del visitor è quello di visitare l'albero e analizzarlo in modo da calcolare delle metriche che poi si possono usare per calcolare le seguenti metriche.

- WMC
- DIT
- NOC
- RFC
- LCOM
- CBO

Per calcolare le metrciche si è diviso il codice in tre classi principali:

### MetricsVisitor

La classe `MetricVisitor` estende `JavaParserDefaultVisitor` ed è progettata per visitare un albero sintattico astratto (AST) di codice Java, con l’obiettivo di raccogliere metriche strutturali sulle classi analizzate. Tali metriche sono memorizzate all’interno di una mappa (`metricsMap`) che associa ogni nome di classe a un oggetto `ClassMetrics`.

#### Principali caratteristiche

* **Gestione delle classi**

    * Visita i nodi `ASTClassOrInterfaceDeclaration` per registrare ogni classe o interfaccia nel file sorgente.
    * Analizza la relazione di ereditarietà (gestione di `extends`) e memorizza la classe padre per ciascuna classe.
    * Al termine della visita dell’unità di compilazione (`ASTCompilationUnit`), costruisce la mappa dei figli (per la metrica NOC - Number Of Children).

* **Gestione dei metodi**

    * Visita i nodi `ASTMethodDeclaration` per registrare i metodi definiti in ciascuna classe.
    * Colleziona le invocazioni di metodi interni ed esterni all’interno del metodo visitato tramite la classe interna `MethodCallCollector`.

* **Gestione dei campi**

    * Visita i nodi `ASTFieldDeclaration` per individuare e memorizzare i campi dichiarati nella classe.
    * Registra anche, nei metodi, l’accesso a campi di istanza (field access) sfruttando l'analisi dei nodi `ASTPrimaryExpression`.
    * Stampa in console i campi individuati e i riferimenti a essi (ai fini di debugging o tracciamento).

* **Gestione del coupling**

    * Registra i tipi dei parametri dei metodi (`ASTFormalParameter`) come possibili classi accoppiate (CBO - Coupling Between Object classes).
    * Rileva istanziazioni di oggetti tramite `ASTAllocationExpression` per aggiornare ulteriormente il coupling.

* **Collettore interno di chiamate a metodo**

    * La classe annidata `MethodCallCollector` esplora in profondità un metodo e raccoglie le chiamate a metodi utilizzando un ulteriore visitor dedicato.

* **Accesso ai risultati**

    * Fornisce un metodo `getMetricsMap()` per ottenere la mappa completa delle metriche calcolate su ciascuna classe.

### ClassMetrics

La classe `ClassMetrics` è un contenitore di dati (data holder) progettato per memorizzare e organizzare un insieme di metriche relative a una singola classe Java, estratte tramite l'analisi dell’AST.

#### Principali caratteristiche

* **Identificativo della classe**

    * `className`: nome univoco della classe monitorata.
    * `parentClass`: eventuale super-classe (ereditarietà diretta).

* **Campi e metodi**

    * `fields`: insieme dei campi (attributi) dichiarati nella classe.
    * `methods`: insieme dei metodi dichiarati nella classe.
    * `methodToAccessedFields`: mappa che collega ogni metodo ai campi (fields) che esso utilizza (utile per la metrica LCOM - Lack of Cohesion in Methods).

* **Relazioni di coupling e invocazioni**

    * `coupledClasses`: insieme delle altre classi con cui questa classe è accoppiata, ad esempio tramite parametri di metodo o istanziazioni di oggetti (CBO - Coupling Between Object classes).
    * `invokedMethods`: insieme dei nomi di metodi invocati, sia interni che esterni (RFC - Response For a Class).

* **Gerarchia ereditaria**

    * `children`: lista dei nomi delle sottoclassi dirette (per la metrica NOC - Number of Children).

#### Metodi di supporto

* **Accessors (getters/setters)**

    * Forniscono accesso completo a tutte le metriche raccolte (campi, metodi, classi accoppiate, figli, ecc.).

* **Metodi di aggiornamento**

    * `addField(String field)`: aggiunge un campo alla classe.
    * `addMethod(String method)`: registra un nuovo metodo.
    * `addCoupledClass(String coupled)`: aggiunge una classe accoppiata.
    * `addInvokedMethod(String invoked)`: registra un metodo invocato.
    * `addChild(String child)`: aggiunge un figlio alla lista di sottoclassi.
    * `addMethodAccessedField(String method, String field)`: registra il fatto che un certo metodo accede a un campo.

#### Utilizzo tipico

Questa classe viene popolata dal `MetricVisitor` durante la visita dell’AST, e costituisce la struttura principale per conservare metriche utili per calcolare successivamente le metriche di qualità del codice orientato agli oggetti.

### MetricsCalculator

La classe `MetricsCalculator` si occupa del calcolo effettivo delle metriche di qualità orientate agli oggetti, a partire dai dati raccolti all’interno delle strutture `ClassMetrics`.

#### Principali caratteristiche

* **Input**

    * Riceve in ingresso una mappa `Map<String, ClassMetrics>` che associa il nome di ciascuna classe all’oggetto `ClassMetrics` corrispondente, già popolato dal visitor di analisi (ad es. `MetricVisitor`).

* **Funzione principale**

    * Il metodo `computeMetrics()` itera su tutte le classi presenti nella mappa e calcola le seguenti metriche per ciascuna:

        * **WMC (Weighted Methods per Class)**
        * **DIT (Depth of Inheritance Tree)**
        * **NOC (Number of Children)**
        * **CBO (Coupling Between Object classes)**
        * **AdvCBO (Advanced Coupling Between Object classes)**
        * **RFC (Response For a Class)**
        * **LCOM (Lack of Cohesion of Methods)**
    * Stampa i valori calcolati in output formattato.

#### Metriche calcolate

**WMC (Weighted Methods per Class)**

* Calcolato come il numero di metodi dichiarati nella classe (peso unitario per ciascun metodo).

**DIT (Depth of Inheritance Tree)**

* Calcolato risalendo la gerarchia di ereditarietà attraverso `parentClass`, contando quanti livelli ci separano dalla radice (profondità).

**NOC (Number Of Children)**

* Determinato contando il numero di sottoclassi dirette (children) registrate nella `ClassMetrics`.

**CBO (Coupling Between Object classes)**

* Conta quante classi differenti la classe corrente utilizza o istanzia (coupling).

**AdvCBO (Advanced Coupling Between Object classes)**

* Calcola AdvCBO = |couplingOut| + |couplingIn| - |couplingInOut|.

**RFC (Response For a Class)**

* Conta la dimensione dell’unione tra i metodi dichiarati nella classe e tutti i metodi invocati (interna o esterna).

**LCOM (Lack of Cohesion of Methods)**

* Basato su un confronto a coppie di metodi, osservando i campi condivisi.

    * `np`: numero di coppie di metodi che condividono almeno un campo
    * `nq`: numero di coppie di metodi che non condividono alcun campo
    * Formula: `LCOM = nq - np` (con limite minimo 0)

#### Note di implementazione

* La classe è **stateless** rispetto al calcolo: tutte le informazioni provengono dal `ClassMetrics` già costruito.
* Implementa un approccio **bottom-up** per il DIT, risalendo lungo la catena di ereditarietà.
* I risultati sono attualmente stampati a console, ma la logica è separata e facilmente estendibile per altre forme di output (file, DB, ecc.).


## Calcolo delle Metriche


### WMC (Weighted Methods per Class)

#### Teoria

**Definizione**: misura la complessità complessiva della classe, sommando i pesi dei suoi metodi.
In letteratura, spesso si assegna un “peso” a ciascun metodo (ad esempio la sua complessità ciclomatica).
In mancanza di pesi personalizzati, si assume peso unitario per ogni metodo.
**Valore alto**: indica una classe con troppe responsabilità.

#### In pratica

**MetricCalculator:**

```java
private int computeWMC(ClassMetrics cm) {
    return cm.getMethods().size();
}
```

Qui il peso di ciascun metodo è **unitario**.
Il visitor (`MetricVisitor`) raccoglie i nomi dei metodi tramite:

```java
  metricsMap.get(currentClass).addMethod(methodName);
 ```
Poi `getMethods().size()` conteggia quanti metodi distinti la classe dichiara. Niente misure di complessità interne: solo conteggio.

**In pratica: WMC = numero di metodi dichiarati nella classe**.

### DIT (Depth of Inheritance Tree)

#### Teoria

**Definizione**: misura la profondità di una classe nel suo albero di ereditarietà, contando i livelli fino alla radice.
Più è alto, più la classe è “profonda” e quindi potenzialmente più difficile da comprendere e modificare.

#### In pratica

**MetricCalculator:**

```java
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
```

La proprietà `parentClass` è registrata in `ClassMetrics` dal `MetricVisitor`, quando visita l’`ASTExtendsList`.
Poi, partendo dalla classe corrente, risale la gerarchia con un ciclo:

* Se esiste un `parentClass`, incremento `depth`
* Cerco a sua volta il padre del padre
* Fino a quando non trovo la radice (`parentClass == null`)
    * Quindi calcola la distanza **dal nodo corrente fino al capostipite**.

**In pratica: DIT = numero di passaggi per risalire alla radice della gerarchia**.

### NOC (Number Of Children)

#### Teoria

**Definizione**: indica quante sottoclassi dirette ha una classe.
Serve per misurare il potenziale riuso (più figli → classe potenzialmente più generale e riusabile)
Può anche indicare quanto la classe è soggetta a modifiche di propagazione.

#### In pratica

**MetricCalculator:**

```java
int noc = cm.getChildren().size();
```

Nel `MetricVisitor`, alla fine del parsing (`visit(ASTCompilationUnit)`), viene costruita la mappa dei figli:

  ```java
  metricsMap.get(parent).addChild(potentialChild.getClassName());
  ```
Questo significa che ogni `ClassMetrics` sa **quali classi la estendono**.
Il `getChildren().size()` conta semplicemente il numero di sottoclassi registrate.

**In pratica: NOC = numero di sottoclassi dirette**.

### CBO (Coupling Between Object classes)

#### Teoria

**Definizione**: conta quante altre classi vengono usate (accoppiate) dalla classe corrente, tramite parametri, campi o istanziazioni.

Valore alto = classe molto dipendente da altre classi, più difficile da riusare/testare

#### In pratica

**MetricCalculator:**

```java
private int computeCBO(ClassMetrics cm) {
    return cm.getCoupledClasses().size();
}
```

Il `MetricVisitor` raccoglie le classi accoppiate:

* dai tipi dei parametri

  ```java
  metricsMap.get(currentClass).addCoupledClass(paramType);
  ```
* dalle allocazioni di oggetti

  ```java
  metricsMap.get(currentClass).addCoupledClass(instantiatedType);
  ```
Tutto viene salvato in `ClassMetrics.coupledClasses` come `Set<String>` per evitare duplicati.

**In pratica: CBO = numero di classi diverse utilizzate come parametri o istanziate**.

### Advanced CBO

#### Teoria 

Nella definizione originale del 1994 di Chidamber & Kemerer, la metrica CBO (Coupling Between Object Classes) di una classe è semplicemente:

Il numero di classi a cui una classe è accoppiata.

E due classi si dicono accoppiate se una usa metodi o attributi dell’altra, o viceversa.
In pratica basta contare quante altre classi la classe in esame “tocca” (accoppiamento in uscita) più quante classi “la toccano” (accoppiamento in ingresso).

La formula `in + out − inout` viene a volte proposta in contesti più avanzati di analisi delle dipendenze per:

distinguere **afferent coupling** (classi che dipendono dalla classe)

**efferent coupling** (classi da cui dipende la classe in esame)

**common coupling** (accoppiamento condiviso)

ma non è la formula standard del CK suite. 
Il CBO classico di CK è solo quante altre classi sono in relazione con questa.
Quindi proprio per questo si è voluto aggiungere una metrica ulteriore, AdvancedCBO.

Questa metrica è definita proprio come `in + out − inout`

#### In Pratica

```
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
```

### RFC (Response For a Class)

#### Teoria

**Definizione**: misura il numero di “risposte potenziali” di una classe, cioè:

* i suoi metodi pubblici
* più tutti i metodi potenzialmente invocati

Rappresenta quanto una classe è complessa nel rispondere a messaggi esterni.

#### In pratica

**MetricCalculator:**

```java
private int computeRFC(ClassMetrics cm) {
    Set<String> responseSet = new HashSet<>();
    responseSet.addAll(cm.getMethods());
    responseSet.addAll(cm.getInvokedMethods());
    return responseSet.size();
}
```

Dal `MetricVisitor`:

* i metodi dichiarati vengono registrati in `cm.methods`
* i metodi chiamati, raccolti via `MethodCallCollector`, finiscono in `cm.invokedMethods`
* La metrica RFC somma l’unione dei due insiemi.

**In pratica: RFC = numero di metodi propri + metodi invocati**, senza duplicati.

### LCOM (Lack of Cohesion of Methods)

#### Teoria

**Definizione**: misura la coesione dei metodi di una classe, guardando se condividono gli stessi campi.

* Se molti metodi usano campi distinti → bassa coesione → alto LCOM
* Se molti metodi usano gli stessi campi → alta coesione → basso LCOM
* La formula classica è:

  $$
  LCOM = \text{n. coppie senza attributi condivisi} - \text{n. coppie con attributi condivisi}
  $$

  con limite a zero se negativo.

#### In pratica

**MetricCalculator:**

```java
private int computeLCOM(ClassMetrics cm) {
    ...
}
```

Il `MetricVisitor` durante il parsing rileva:

* quali campi vengono usati da ciascun metodo
* memorizzandoli in `methodToAccessedFields`

La funzione `computeLCOM`:

1. costruisce la lista di insiemi di campi usati per ogni metodo
2. confronta tutte le **coppie di metodi**
3. calcola:

    * `np` = coppie che condividono almeno un campo
    * `nq` = coppie che non condividono alcun campo
4. infine applica:

   $$
   LCOM = \max(0, nq - np)
   $$

**In pratica: LCOM confronta tutti i metodi a coppie, verifica se condividono campi e misura la coesione**.


## Test e confronto

- Si prende un progetto Java semplice (magari da GitHub).
- Si genera versioni alternative con ChatGPT.
- Si confrontano le metriche per valutarne la “qualità OO”.

I progetti che sono stati individuati sono:

- https://github.com/RaiyanMahin/Java-Project/tree/main/Java%20Project
- https://github.com/aytekinkaplan/Java-OOP-Projects
