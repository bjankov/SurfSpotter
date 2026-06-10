# SurfSpot

JavaFX aplikacija za upravljanje surf spotovima. Omogućuje pregled, dodavanje, uređivanje i brisanje surf spotova, instruktora, škola surfanja, obala i država. Podržava dva tipa korisnika — administrator i obični korisnik — te izvoz itinerara u XML format.

---

## Preduvjeti

- Java 21 ili novija
- Maven
- Docker

---

## Instalacija Dockera

### Windows

1. Preuzmite Docker Desktop s [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)
2. Pokrenite instalacijski program i slijedite upute
3. Nakon instalacije pokrenite Docker Desktop
4. Provjerite instalaciju u terminalu:
   ```
   docker --version
   ```

### macOS

1. Preuzmite Docker Desktop s [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)
2. Otvorite `.dmg` datoteku i povucite Docker u Applications
3. Pokrenite Docker iz Applications foldera
4. Provjerite instalaciju u terminalu:
   ```
   docker --version
   ```

### Linux (Ubuntu/Debian)

Pokrenite sljedeće naredbe u terminalu:

```bash
sudo apt update
sudo apt install docker.io docker-compose -y
sudo systemctl start docker
sudo systemctl enable docker
```

Provjerite instalaciju:
```bash
docker --version
```

---

## Postavljanje baze podataka

U korijenu projekta nalazi se `docker-compose.yml` koji pokreće PostgreSQL bazu podataka. Prilikom prvog pokretanja kontejner automatski izvršava dvije skripte redom:

1. `01_ddl.sql` — kreira sve potrebne tablice
2. `02_seed.sql` — popunjava tablice početnim podacima

Pokrenite bazu podataka sljedećom naredbom iz korijena projekta:

```bash
docker-compose up -d
```

Baza će biti dostupna na `localhost:5432` i spremna za korištenje bez ikakvih dodatnih koraka.

> **Napomena:** Inicijalne skripte se pokreću samo prilikom prvog pokretanja, dok baza još ne postoji. Ako želite resetirati bazu na početno stanje, pokrenite `docker-compose down -v` kako biste obrisali sve podatke, a zatim ponovo `docker-compose up -d`.

---

## Konfiguracija aplikacije

U `src/main/resources` nalazi se datoteka `db.properties.example`. Napravite kopiju te datoteke i uklonite `.example` s kraja naziva:

```bash
# Windows
copy src\main\resources\db.properties.example src\main\resources\db.properties

# macOS / Linux
copy src\main\resources\db.properties.example src\main\resources\db.properties
```

Otvorite `db.properties` i popunite podatke:

```properties
db.url=jdbc:postgresql://localhost:5432/surf_spot_db
db.username=surfspot
db.password=surfspot
```

Ako koristite Docker Compose iz ovog projekta, gore navedeni podaci su ispravni i nije potrebno ništa mijenjati.

---

## Pokretanje aplikacije

```bash
mvn clean javafx:run
```

---

## Zaustavljanje baze podataka

Kada završite s radom, možete zaustaviti Docker kontejner:

```bash
docker-compose down
```

Ako želite i obrisati sve podatke iz baze:

```bash
docker-compose down -v
```