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


## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.
