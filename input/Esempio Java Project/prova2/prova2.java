import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CinemaManager {

    // === CLASSI MODELLO ===
    static class Film {
        String titolo, sala, orario;
        double prezzo;

        public Film(String titolo, String sala, String orario, double prezzo) {
            this.titolo = titolo;
            this.sala = sala;
            this.orario = orario;
            this.prezzo = prezzo;
        }

        public static List<Film> caricaProgrammazione(String path) throws IOException {
            List<Film> lista = new ArrayList();
            for (String riga : Files.readAllLines(Paths.get(path))) {
                String[] parti = riga.split(";");
                lista.add(new Film(parti[0], parti[1], parti[2], Double.parseDouble(parti[3])));
            }
            return lista;
        }

        @Override
        public String toString() {
            return titolo + " (Sala " + sala + ", " + orario + ", €" + prezzo + ")";
        }
    }

    static class Articolo {
        String nome, categoria;
        double prezzo;

        public Articolo(String nome, String categoria, double prezzo) {
            this.nome = nome;
            this.categoria = categoria;
            this.prezzo = prezzo;
        }

        public static List<Articolo> caricaArticoli(String path) throws IOException {
            List<Articolo> lista = new ArrayList();
            for (String riga : Files.readAllLines(Paths.get(path))) {
                String[] parti = riga.split(";");
                lista.add(new Articolo(parti[0], parti[1], Double.parseDouble(parti[2])));
            }
            return lista;
        }

        @Override
        public String toString() {
            return nome + " (" + categoria + ", €" + prezzo + ")";
        }
    }

    static class Prenotazione {
        String id;
        int numeroPersone;
        Film film;
        Map<Articolo, Integer> articoli = new HashMap();
        LocalDate data;

        public Prenotazione(String id, int numeroPersone, Film film) {
            this.id = id;
            this.numeroPersone = numeroPersone;
            this.film = film;
            this.data = LocalDate.now();
        }

        public void aggiungiArticolo(Articolo a, int quantita) {
            articoli.put(a, articoli.getOrDefault(a, 0) + quantita);
        }

        public double calcolaTotale() {
            double totale = numeroPersone * film.prezzo;
            for (Map.Entry<Articolo, Integer> entry : articoli.entrySet()) {
                totale += entry.getKey().prezzo * entry.getValue();
            }
            return totale;
        }

        public String stampaScontrino() {
            StringBuilder sb = new StringBuilder();
            sb.append("Scontrino - Data: ").append(data).append("\n");
            sb.append("Prenotazione ID: ").append(id).append("\n");
            sb.append("Film: ").append(film).append("\n");
            sb.append("Persone: ").append(numeroPersone).append("\n");
            sb.append("Articoli:\n");
            for (Map.Entry<Articolo, Integer> entry : articoli.entrySet()) {
                Articolo a = entry.getKey();
                int qta = entry.getValue();
                sb.append("  - ").append(a.nome).append(" x").append(qta)
                        .append(" (€").append(a.prezzo).append(" cad.)\n");
            }
            sb.append("Totale: €").append(String.format("%.2f", calcolaTotale())).append("\n");
            return sb.toString();
        }

        public String toFileString() {
            return data + ";" + id + ";" + numeroPersone + ";" + film.titolo + ";" + film.sala + ";" + film.orario + ";" + film.prezzo + ";" +
                    articoli.entrySet().stream()
                            .map(e.getKey().nome + ":" + e.getValue())
                            .reduce(  a + "," + b).orElse("") + ";" + calcolaTotale();
        }

        public static Prenotazione fromFileString(String s, List<Film> programmazione, List<Articolo> articoliDisponibili) {
            String[] parts = s.split(";");
            LocalDate data = LocalDate.parse(parts[0]);
            String id = parts[1];
            int persone = Integer.parseInt(parts[2]);
            String titolo = parts[3];
            Film film = programmazione.stream().filter(f.titolo.equals(titolo)).findFirst().orElse(null);
            if (film == null) return null;

            Prenotazione p = new Prenotazione(id, persone, film);
            p.data = data;
            if (!parts[7].isEmpty()) {
                for (String articoloInfo : parts[7].split(",")) {
                    String[] split = articoloInfo.split(":");
                    String nome = split[0];
                    int qta = Integer.parseInt(split[1]);
                    for (Articolo a : articoliDisponibili) {
                        if (a.nome.equals(nome)) {
                            p.aggiungiArticolo(a, qta);
                            break;
                        }
                    }
                }
            }
            return p;
        }
    }

    // === GESTORE PRENOTAZIONI ===
    static Map<String, Prenotazione> prenotazioni = new HashMap();
    static final String STORICO_FILE = "storico.txt";

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        List<Film> programmazione = Film.caricaProgrammazione("programmazione.dati");
        List<Articolo> listino = Articolo.caricaArticoli("snacks.dati");

        while (true) {
            System.out.println("\n--- CINEMA MANAGER ---");
            System.out.println("1. Nuova prenotazione");
            System.out.println("2. Aggiungi snack/bevanda");
            System.out.println("3. Stampa scontrino");
            System.out.println("4. Visualizza storico per data");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");
            int scelta = Integer.parseInt(sc.nextLine());

            switch (scelta) {
                case 1 : {
                    System.out.print("ID prenotazione: ");
                    String id = sc.nextLine();
                    System.out.print("Numero persone: ");
                    int n = Integer.parseInt(sc.nextLine());

                    System.out.println("Film disponibili:");
                    for (int i = 0; i < programmazione.size(); i++) {
                        System.out.println((i + 1) + ". " + programmazione.get(i));
                    }
                    System.out.print("Scegli film (numero): ");
                    int index = Integer.parseInt(sc.nextLine()) - 1;
                    if (index < 0 || index >= programmazione.size()) {
                        System.out.println("Film non valido.");
                        break;
                    }
                    Prenotazione p = new Prenotazione(id, n, programmazione.get(index));
                    prenotazioni.put(id, p);
                    System.out.println("Prenotazione registrata.");
                }

                case 2 : {
                    System.out.print("ID prenotazione: ");
                    String id = sc.nextLine();
                    Prenotazione p = prenotazioni.get(id);
                    if (p == null) {
                        System.out.println("Prenotazione non trovata.");
                        break;
                    }
                    System.out.println("Articoli disponibili:");
                    for (int i = 0; i < listino.size(); i++) {
                        System.out.println((i + 1) + ". " + listino.get(i));
                    }
                    System.out.print("Scegli articolo (numero): ");
                    int index = Integer.parseInt(sc.nextLine()) - 1;
                    if (index < 0 || index >= listino.size()) {
                        System.out.println("Articolo non valido.");
                        break;
                    }
                    System.out.print("Quantità: ");
                    int qta = Integer.parseInt(sc.nextLine());
                    p.aggiungiArticolo(listino.get(index), qta);
                    System.out.println("Articolo aggiunto.");
                }

                case 3 : {
                    System.out.print("ID prenotazione: ");
                    String id = sc.nextLine();
                    Prenotazione p = prenotazioni.get(id);
                    if (p == null) {
                        System.out.println("Prenotazione non trovata.");
                        break;
                    }
                    String scontrino = p.stampaScontrino();
                    System.out.println("\n--- SCONTRINO ---\n" + scontrino);
                    Files.write(Paths.get(STORICO_FILE), (p.toFileString() + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
                    prenotazioni.remove(id);
                }

                case 4 : {
                    System.out.print("Inserisci data (yyyy-MM-dd): ");
                    String data = sc.nextLine();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    List<String> righe = Files.exists(Paths.get(STORICO_FILE))
                            ? Files.readAllLines(Paths.get(STORICO_FILE))
                            : new ArrayList();
                    for (String r : righe) {
                        if (r.startsWith(data)) {
                            Prenotazione p = Prenotazione.fromFileString(r, programmazione, listino);
                            if (p != null) {
                                System.out.println("\n" + p.stampaScontrino());
                            }
                        }
                    }
                }

                case 0 : {
                    System.out.println("Uscita...");
                    return;
                }

                default : { System.out.println("Scelta non valida.");}
            }
        }
    }
}
