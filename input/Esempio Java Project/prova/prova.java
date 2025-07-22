


import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import it.unisannio.pascarella.exceptions.ElementoNonTrovatoException;

public class Archivio {

    // Costruttori

    /**
     * Costruttore con tutti i campi specificati. Se non è specificato un file di storico scontrini
     * verrà in automatico usato il file "storico.dati"
     * @param films elenco di film dell'archivio
     * @param articoli elenco degli articoli dell'archivio
     * @param prenotazioni elenco base di prenotazioni dell'archivio
     * @param fileStoricoScontriniStream PrintStream del file di storico scontrini
     */
    public Archivio(List<Film> films, List<Articolo> articoli, Map<String, Prenotazione> prenotazioni, PrintStream fileStoricoScontriniStream) {
        this.films = films;
        this.articoli = articoli;
        this.prenotazioni = prenotazioni;

        if(fileStoricoScontriniStream == null) {
            try {
                this.fileStoricoScontriniStream = new PrintStream(new File("storico.dati"));
            } catch (FileNotFoundException e) {
                System.out.println("Impossibile trovare il file base storico.dati per lo storico scontrini. Gli scontrini non verranno salvati");
                this.fileStoricoScontriniStream = null;
            }
        }
    }

    /**
     * Costruttore base vuoto. Tutti gli elenchi sono vuoti e
     * nessun file di storico scontrini è specificato.
     * In questo caso generico verranno usati ArrayList per film e articoli
     * dato che ci sono molti accessi sequenziali e poche aggiunte dopo la creazione iniziale.
     * Si usa una HashMap per collegare l'id alle prenotazioni senza interessarsene dell'ordine di
     * aggiunta o di qualsiasi altro ordine
     */
    public Archivio() {
        this(new ArrayList<Film>(), new ArrayList<Articolo>(), new HashMap<String, Prenotazione>(), null);
    }

    // Metodi

    //I-O
    /**
     * Lettura in input di un archivio dato uno scanner per l'elenco iniziale dei film
     * e uno scanner per l'elenco iniziale di articoli. Nota che le prenotazioni verranno fatte in seguito
     * alla creazione dell'archivio (si devono conoscere gli elenchi di base di film e articoli per verificare la correttezza delle nuove prenotazioni)
     * @param scanFilms scanner per elenco film
     * @param scanArticoli scanner per elenco articoli
     * @return l'Articolo letto
     */
    public static Archivio read(Scanner scanFilms, Scanner scanArticoli) {
        Archivio archivio = new Archivio();
        archivio.leggiFilms(scanFilms);
        archivio.leggiArticoli(scanArticoli);

        return archivio;
    }

    /**
     * Lettura di nuovi film da uno scanner di input
     * @param scan scanner di input
     */
    public  void leggiFilms(Scanner scan) {
        Film film = Film.read(scan);
        while(film != null) {
            aggiungiFilm(film);
            film = Film.read(scan);
        }
    }

    /**
     * Lettura di nuovi articoli da uno scanner di input
     * @param scan scanner di input
     */
    public  void leggiArticoli(Scanner scan) {
        Articolo articolo = Articolo.read(scan);
        while(articolo != null) {
            aggiungiArticolo(articolo);
            articolo = Articolo.read(scan);
        }
    }

    /**
     * Stampa dell'archivio completo: film, articoli e prenotazioni
     * @param target PrintStream di output
     */
    public void print(PrintStream target) {
        target.println("FILMS");
        stampaFilms(target);
        target.println("ARTICOLI");
        stampaArticoli(target);
        target.println("PRENOTAZIONI");
        stampaPrenotazioni(target);
    }

    /**
     * Stampa solo dell'elenco di film
     * @param target PrintStream di output
     */
    public void stampaFilms(PrintStream target) {
        films.stream().forEach( f.print(target));
    }

    /**
     * Stampa solo dell'elenco di articoli
     * @param target PrintStream di output
     */
    public void stampaArticoli(PrintStream target) {
        articoli.stream().forEach( a.print(target));
    }

    /**
     * Stampa solo dell'elenco di prenotazioni
     * @param target PrintStream di output
     */
    public void stampaPrenotazioni(PrintStream target) {
        prenotazioni.values().stream().forEach( p.print(target));
    }

    //Logica prenotazioni
    /**
     * Crea una prenotazione dati i campi e verificando che il film e gli articoli
     * siano presenti nell'elenco. Gli articoli non presenti verranno singolarmente saltati
     * nella gestione dell'eccezione. Se il film non è presente l'intera prenotazione non può essere fatta
     * @param id codice prenotazione
     * @param persone numero persone prenotazione
     * @param titoloFilm titolo del film da prenotare
     * @param nomiArticoli nomi degli eventuali articoli nella prenotazione
     */
    public void creaPrenotazione(String id, int persone, String titoloFilm, List<String> nomiArticoli) {
        try {
            // Trovo il film corrispondente
            Optional<Film> film = films.stream().filter( f.getTitolo().equals(titoloFilm)).findAny();
            if(!film.isPresent()) throw new ElementoNonTrovatoException("Film " + titoloFilm + " non trovato");

            // Trovo gli articoli corrispondenti
            ArrayList<Articolo> articoli = new ArrayList<Articolo>();
            for(String nomeArticolo : nomiArticoli) {
                Optional<Articolo> articolo = this.articoli.stream().filter( a.getNome().equals(nomeArticolo)).findAny();

                if(!articolo.isPresent()) throw new ElementoNonTrovatoException("Articolo " + nomeArticolo + " non trovato");
                articoli.add(articolo.get());
            }

            prenotazioni.put(id, new Prenotazione(id, persone, film.get(), articoli));
        } catch (ElementoNonTrovatoException e) {
            System.out.println("! Errore nel creare la prenotazione. " + e.getMessage() + ". La prenotazione non verrà creata");
        }
    }

    /**
     * Aggiunge uno o più articoli dell'elenco a una prenotazione presente nell'archivio.
     * Se un articolo non è presente nell'archivio verrà saltato nella gestione dell'eccezione.
     * Se la prenotazione non è trovata l'aggiunta non può essere compiuta.
     * @param idPrenotazione id della prenotazione a cui aggiungere gli articoli
     * @param nomiArticoli lista dei nomi degli articoli da aggiungere
     */
    public void aggiungiArticoliAPrenotazione(String idPrenotazione, List<String> nomiArticoli) {
        try {
            if(!prenotazioni.containsKey(idPrenotazione)) throw new ElementoNonTrovatoException("Prenotazione " + idPrenotazione + " non trovata");
            List<Articolo> articoliPrenotazione = prenotazioni.get(idPrenotazione).getArticoli();

            for(String nomeArticolo : nomiArticoli) {
                try {
                    Optional<Articolo> articoloTrovato = articoli.stream().filter( a.getNome().equals(nomeArticolo)).findAny();

                    if(!articoloTrovato.isPresent()) throw new ElementoNonTrovatoException("Articolo " + nomeArticolo + " non trovato");

                    articoliPrenotazione.add(articoloTrovato.get());
                } catch (Exception e) {
                    System.out.println("! Errore nell'agginungere l'articolo " + nomeArticolo + " alla prenotazione. " + e.getMessage() + ". L'articolo non verrà aggiunto");
                }
            }
        } catch (ElementoNonTrovatoException e) {
            System.out.println("! Errore nell'agginungere articoli alla prenotazione. " + e.getMessage() + ". Nessun articolo verrà aggiunto");
        }
    }

    /**
     * Emette lo scontrino di una prenotazione.
     * Stampa a video e nell'eventuale file di storico scontrini
     * @param idPrenotazione codice della prenotazione
     */
    public void emettiScontrino(String idPrenotazione) {
        // Trovo la prenotazione
        Prenotazione prenotazione;
        try {
            if(!prenotazioni.containsKey(idPrenotazione)) throw new ElementoNonTrovatoException("Prenotazione " + idPrenotazione + " non trovata");
            prenotazione = prenotazioni.get(idPrenotazione);
        } catch (ElementoNonTrovatoException e) {
            System.out.println("! Errore nell'emissione dello scontrino. " + e.getMessage() + ". Lo scontrino non verrà emesso");
            return;
        }

        // Se c'è il file di storico scontrini ci stampo sopra lo scontrino
        if(fileStoricoScontriniStream != null) {
            prenotazione.stampaScontrino(fileStoricoScontriniStream);
        }

        // Stampo a video lo scontrino
        prenotazione.stampaScontrino(System.out);
    }

    /**
     * Emette tutti gli scontrini delle prenotazioni dell'archivio
     */
    public void emettiTuttiScontrini() {
        prenotazioni.values().stream().forEach(emettiScontrino(p.getId()));
    }

    /**
     * Filtra le prenotazioni di una certa data
     * @param data data da filtrare
     * @return un nuovo Archivio con le prenotazioni filtrate
     */
    public Archivio filtraPrenotazioniPerData(Date data) {
        HashMap<String, Prenotazione> prenotazioniFiltrate = new HashMap<String, Prenotazione>();

        prenotazioni.values().stream()
                .filter( Prenotazione.formatoData.format(p.getData()).equals(Prenotazione.formatoData.format(data)))
                .forEach( prenotazioniFiltrate.put(p.getId(), p));

        return new Archivio(films, articoli, prenotazioniFiltrate, fileStoricoScontriniStream);
    }

    /**
     * Filtra le prenotazioni di una certa data specificata come stringa.
     * Gestisce l'eccezione nel caso che il parsing da data a stringa non venga eseguito con successo
     * @param data data da filtrare
     * @return un nuovo Archivio con le prenotazioni filtrate
     */
    public Archivio filtraPrenotazioniPerData(String dataStr) {
        try {
            Date data = Prenotazione.formatoData.parse(dataStr);
            return filtraPrenotazioniPerData(data);
        } catch (ParseException e) {
            System.out.println("! Errore nel parsing della data " + dataStr + ". Il filtraggio non può essere compiuto");
            return this;
        }
    }

    //Aggiungere ai listini

    /**
     * Aggiunge un film al listino di film dell'archivio
     * @param film da aggiungere
     */
    public void aggiungiFilm(Film film) {
        films.add(film);
    }

    /**
     * Aggiunge un articolo al listino di articoli dell'archivio
     * @param articolo da aggiungere
     */
    public void aggiungiArticolo(Articolo articolo) {
        articoli.add(articolo);
    }

    // Variabili d'istanza
    private List<Film> films;
    private List<Articolo> articoli;
    private Map<String, Prenotazione> prenotazioni;	// mappa l'id alla prenotazione. La chiave è l'id della prenotazione

    private PrintStream fileStoricoScontriniStream;
}




/**
 * Classe che rappresenta un articolo acquistabile per la visione di un film.
 * E' specializzata in Bevanda e Snack (che fanno da sottoclassi)
 * E' astratta perché non è possibile creare un Articolo generico, ma c'è bisogno
 * di crearne uno specifico che descriva la propria categoria
 */
public abstract class Articolo {

    // Costruttori

    /**
     * Costruttore con i campi nome e prezzo
     * @param nome
     * @param prezzo
     */
    public Articolo(String nome, double prezzo) {
        this.nome = nome;
        this.prezzo = prezzo;
    }

    // Metodi

    /**
     * Metodo da sovrascrivere che descrive la categoria dell'Articolo
     * @return
     */
    public abstract String getCategoria();

    //I-O
    /**
     * Leggi un articolo da input.
     * Restituisce l'articolo specifico in base alla categoria letta.
     * @param scan scanner da fornire
     * @return articolo specifico
     */
    public static Articolo read(Scanner scan) {
        try {
            if(!scan.hasNextLine()) return null;
            String nome = scan.nextLine();
            if(!scan.hasNextLine()) return null;
            String categoria = scan.nextLine();
            if(!scan.hasNextLine()) return null;
            double prezzo = Double.parseDouble(scan.nextLine().split(" €")[0]);

            if(!categoria.equals("Snack") && !categoria.equals("Bevanda")) throw new CategoriaNonValidaException(categoria + " non è una categoria valida");

            if(categoria.equals("Snack")) {
                return new Snack(nome, prezzo);
            }
            else {
                return new Bevanda(nome, prezzo);
            }
        } catch (CategoriaNonValidaException e) {
            System.out.println("! Errore nel leggere l'articolo. " + e + ". L'articolo verrà ignorato");
            return null;
        }
    }

    /**
     * Stampa gli attributi dell'articolo
     * @param target PrintStream dove stampare
     */
    public void print(PrintStream target) {
        target.println(nome);
        target.println(getCategoria());
        target.println(prezzo + " €");
    }

    //Getters/Setters
    /**
     * Ottiene nome dell'articolo
     * @return nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Ottiene prezzo dell'articolo
     * @return prezzo
     */
    public double getPrezzo() {
        return prezzo;
    }

    // Variabili d'istanza
    protected String nome;
    protected double prezzo;
}




/**
 * Sottoclasse di Articolo che definisce la propria categoria
 */
public class Bevanda extends Articolo {

    /**
     * Costruttore con campi da nome e prezzo
     * @param nome
     * @param prezzo
     */
    public Bevanda(String nome, double prezzo) {
        super(nome, prezzo);
    }

    /**
     * Sovrascrittura della categoria con quella della sottoclasse
     */
    @Override
    public String getCategoria() {
        return "Bevanda";
    }
}





/**
 * Sottoclasse di Articolo che definisce la propria categoria
 */
public class Snack extends Articolo {

    /**
     * Costruttore con campi da nome e prezzo
     * @param nome
     * @param prezzo
     */
    public Snack(String nome, double prezzo) {
        super(nome, prezzo);
    }

    /**
     * Sovrascrittura della categoria con quella della sottoclasse
     */
    @Override
    public String getCategoria() {
        return "Snack";
    }
}




/**
 * Classe che rappresenta un Film.
 * Gestisce il suo I/O e i suoi attributi.
 * Gestisce il formato dell'orario del film.
 */
public class Film {

    // Costruttori

    /**
     * Costruttore con tutti i campi del Film
     * @param titolo titolo del film
     * @param salaProiezione numero della sala dove sarà proiettato il film
     * @param orario orario di proiezione del film
     * @param prezzoBiglietto prezzo del biglietto del film
     */
    public Film(String titolo, int salaProiezione, Date orario, double prezzoBiglietto) {
        this.titolo = titolo;
        this.salaProiezione = salaProiezione;
        this.orario = orario;
        this.prezzoBiglietto = prezzoBiglietto;
    }

    // Metodi

    //I-O
    /**
     * Leggi un Film da input
     * @param scan scanner per la lettura
     * @return il film con gli attributi letti
     */
    public static Film read(Scanner scan) {
        if(!scan.hasNextLine()) return null;
        String titolo = scan.nextLine();
        if(!scan.hasNextLine()) return null;
        int salaProiezione = Integer.parseInt(scan.nextLine().substring(5));
        if(!scan.hasNextLine()) return null;
        String orarioStr = scan.nextLine();
        if(!scan.hasNextLine()) return null;
        double prezzoBiglietto = Double.parseDouble(scan.nextLine().split(" €")[0]);

        Date orario;
        try {
            orario = formatoOrario.parse(orarioStr);
        } catch (ParseException e) {
            System.out.println("! L'orario del film " + titolo + " non è nel formato corretto. Il film verrà saltato");
            return null;
        }

        return new Film(titolo, salaProiezione, orario, prezzoBiglietto);
    }

    /**
     * Stampa un Film in output
     * @param target PrintStream dove stampare gli attributi del Film
     */
    public void print(PrintStream target) {
        target.println(titolo);
        target.println("Sala " + salaProiezione);
        target.println(formatoOrario.format(orario));
        target.println(prezzoBiglietto + " €");
    }

    //Getters/Setters
    /**
     * Ottieni titolo
     * @return titolo
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * Ottieni sala proiezione
     * @return il numero della sala di proiezione
     */
    public int getSalaProiezione() {
        return salaProiezione;
    }

    /**
     * Ottieni l'orario di proiezione
     * @return la data che rappresenta l'orario del film
     */
    public Date getOrario() {
        return orario;
    }

    /**
     * Ottieni il prezzo del biglietto
     * @return prezzo del biglietto
     */
    public double getPrezzoBiglietto() {
        return prezzoBiglietto;
    }

    // Variabili d'istanza
    private String titolo;
    private int salaProiezione;
    private Date orario;
    private double prezzoBiglietto;

    public static final SimpleDateFormat formatoOrario = new SimpleDateFormat("HH:mm");
}




/**
 * Classe che rappresenta una prenotazione di un film con dati articoli.
 * E' rappresentata univocamente dal proprio id.
 * Ha la responsabilità di gestire i propri attributi e di fornirli,
 * di gestire il proprio I/O, di gestire il proprio formato della data
 * e di gestire la stampa dello scontrino corrispondente alla prenotazione
 */
public class Prenotazione {

    // Costruttori

    /**
     * Costruttore base della prenotazione
     * @param id codice prenotazione
     * @param persone numero persone prenotate
     * @param film riferimento a film prenotato
     * @param articoli riferimento a lista di articoli richiesti nella prenotazione
     */
    public Prenotazione(String id, int persone, Film film, List<Articolo> articoli) {
        this.id = id;
        this.persone = persone;
        this.data = new Date();	// La data corrente
        this.film = film;
        this.articoli = articoli;
    }

    // Metodi

    //I-O (non c'è il read perché le prenotazioni vengono create attraverso l'Archivio perché c'è bisongo di verificare che il film e gli articoli siano presenti nell'archivio)
    /**
     * Stampa le informazioni della prenotazione in output
     * @param target il PrintStream dove stampare le informazioni
     */
    public void print(PrintStream target) {
        target.println("id: " + id);
        target.println("persone: " + persone);
        target.println("data: " + formatoData.format(data));
        target.println("film: " + film.getTitolo());
        target.println("articoli: " + articoli.stream().map( a.getNome()).reduce(a + ", " + b).get());
    }

    /**
     * Stampa le informazioni dello scontrino associato a questa prenotazione
     * @param target il PrintStream dove stampare le informazioni
     */
    public void stampaScontrino(PrintStream target) {
        target.println("|Scontrino " + id);
        target.println("Data: " + formatoData.format(data));
        target.println("Film: ");
        film.print(target);
        target.println("Articoli acquistati e loro prezzo: ");
        articoli.stream().forEach( a.print(target));
        target.println("Quantità di articoli acquistati: " + articoli.size());
        double prezzoTotale =
                film.getPrezzoBiglietto() +
                        articoli.stream().collect(Collectors.summingDouble(a.getPrezzo()));
        target.println("Prezzo totale film e articoli: " + prezzoTotale + " €");
    }

    //Getters/Setters
    /**
     * Ottieni id unico della prenotazione
     * @return id prenotazione
     */
    public String getId() {
        return id;
    }

    /**
     * Ottieni il numero di persone della prenotazione
     * @return il numero di persone
     */
    public int getPersone() {
        return persone;
    }

    /**
     * Ottieni la data in cui è stata effettuata la prenotazione
     * @return la data in cui la prenotazione è stata creata
     */
    public Date getData() {
        return data;
    }

    /**
     * Ottieni il film a cui la prenotazione si riferisce
     * @return il film prenotato
     */
    public Film getFilm() {
        return film;
    }

    /**
     * Ottieni gli articoli richiesti nella prenotazione
     * @return la lista di articoli richiesti nella prenotazione
     */
    public List<Articolo> getArticoli() {
        return articoli;
    }

    // Variabili d'istanza
    private String id;
    private int persone;
    private Date data;
    private Film film;
    private List<Articolo> articoli;

    public static SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
}