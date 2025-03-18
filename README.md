# TimeTrack - Activity Time Tracker
TimeTrack è un’applicazione web per la gestione e il monitoraggio del tempo dedicato alle varie attività durante la giornata.
Gli utenti possono creare, organizzare e tracciare le proprie attività con sessioni temporali e visualizzare alcune statistiche sul tempo dedicato a ciascuna attività.

![record](/docs/record.gif)

## Funzionalità principali

### Autenticazione e gestione utenti
- Registrazione e login con Spring Security
- Accesso limitato alle proprie attività e sessioni

### Gestione attività
- Creazione, modifica ed eliminazione di attività
- Classificazione delle attività in categorie personalizzate
- Definizione di un tempo obiettivo (GOAL) o tempo massimo (MAX_TIME)

### Monitoraggio del tempo
- Avvio e interruzione delle sessioni di un’attività
- Salvataggio automatico della durata delle sessioni

### Statistiche avanzate
- Tempo medio per sessione (ultima settimana, ultimo mese)
- Tempo medio giornaliero (ultima settimana, ultimo mese)
- Tempo totale dedicato (ultima settimana, ultimo mese)

## Tecnologie utilizzate

### Backend

- Spring Boot - Framework principale per il backend
- Spring Security - Gestione autenticazione e autorizzazione
- Spring Data JPA - Interazione con il database
- PostgreSQL - Database relazionale

### Frontend

- Thymeleaf - Template engine per la generazione dinamica delle pagine
- Bootstrap - Per lo stile e il layout responsive

## Come avviare il progetto
1. Clonare il repository
```
git clone git@gitlab.com:chri.fer.emi/timetrack.git
cd timetrack
```
2. Creare un database PostgreSQL
3. Creare un file .env nella root del progetto con le seguenti variabili d'ambiente
```
DB_URL=jdbc:postgresql://localhost:5432/<database-name>
DB_USER=<username>
DB_PASS=<password>
```
4. Avviare l'applicazione
```
./mvnw spring-boot:run
```
5. Accedere all'applicazione all'indirizzo http://localhost:8080

## Scelte implementative principali

### Sicurezza con Spring Security
- Gli utenti possono visualizzare e modificare solo le proprie attività e sessioni
- I metodi del service sono protetti con @PreAuthorize

### Uso di Duration per il tempo
- Le durate sono gestite con java.time.Duration per maggiore correttezza e facilità di manipolazione
- Un formatter personalizzato viene usato per renderizzare il tempo nel formato hh:mm:ss
- Due converter personalizzati per la conversione da Duration a String e viceversa

### Statistiche calcolate dinamicamente
- Le statistiche non sono salvate nel database, ma vengono calcolate on-demand

## Architettura MVC
L'applicazione segue il pattern Model-View-Controller (MVC) per mantenere una
chiara separazione delle responsabilità, migliorare la manutenibilità e
facilitare la scalabilità.

### Model
Il Model rappresenta i dati e la logica di business dell'applicazione.

Le entità principali sono mappate su tabelle del database utilizzando JPA/Hibernate.\
Il repository fornisce metodi per interagire con il database, recuperare e salvare i dati.\
La logica di business, come il calcolo delle statistiche, è implementata nei services.

### View
Il livello View è responsabile della presentazione dei dati all'utente.

L'app utilizza Thymeleaf per generare pagine HTML dinamiche.\
I dati vengono passati dai controller alle viste tramite ModelAttributes.\
Formattatori personalizzati sono utilizzati per visualizzare i dati in modo leggibile (es. conversione di Duration in formato leggibile).

### Controller
Il livello Controller gestisce le richieste HTTP e funge da intermediario tra il Model e la View.

ActivityController gestisce la creazione, modifica e cancellazione delle attività.\
ActivitySessionController gestisce l'avvio e l'interruzione delle sessioni di attività.\
CategoryController gestisce la creazione, e modifica delle categorie.\
StatsController recupera e passa le statistiche alla vista.

L'uso di Spring Security assicura che gli utenti possano accedere solo alle proprie attività.
