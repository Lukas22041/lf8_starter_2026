# LF8-Starter: Projektverwaltung

Dieses Gerüst zeigt Hello durch alle Schichten und den Aufruf eines Employee-Service. Die Projektverwaltung mit ihren eigenen Fachregeln entwickelt ihr selbst.

> **Projektauftrag:** Startet mit den [Arbeitsaufträgen](aufgabe/04-arbeitsauftraege.md). Dort
> sind auch [Ausgangssituation](aufgabe/01-ausgangssituation.md),
> [Anforderungsdefinition](aufgabe/02-anforderungsdefinition.md) und
> [Musterstory](aufgabe/03-musterstory.md) verlinkt. Diese README ist die technische Anleitung
> für den Starter.

## 1. Voraussetzungen

- JDK 26 und IntelliJ IDEA mit Maven-Unterstützung und HTTP Client
- Docker mit Docker Compose (Docker muss für die Tests laufen)

Alle Befehle in dieser Anleitung führt ihr im Verzeichnis dieses Starters aus.

## 2. Vorbereitung zu Hause

Die Container-Images sind groß. Ladet sie schon zu Hause herunter:

```bash
docker compose pull
```

## 3. Starten

1. Startet die lokalen Dienste mit `docker compose up -d`. Prüft mit `docker compose ps`, ob der Employee-Service läuft; wartet auf die Startmeldung in `docker compose logs -f employee` (mit Strg+C beendet ihr nur die Log-Anzeige).
2. Startet `Lf8StarterApplication` in IntelliJ mit JDK 26 (grüner Pfeil neben der `main`-Methode). Die App läuft auf Port 8080.
3. Führt `GetToken.http` im IntelliJ HTTP Client aus. Die Datei speichert das Zugriffstoken automatisch als `{{token}}`.
4. Führt die Beispiele aus `SampleRequests.http` aus: Hello anlegen, danach bei Bedarf die erhaltene ID für DELETE einsetzen; Begrüßung und direkte Employee-Aufrufe testen.

Zum Stoppen der Container: `docker compose down`. Die App stoppt ihr separat in IntelliJ oder im Terminal.

## 4. Swagger

Öffnet <http://localhost:8080/swagger>; die Adresse leitet auf die Swagger-UI unter `/swagger-ui/index.html` weiter. Klickt auf **Authorize** und fügt das `access_token` aus der Antwort von `GetToken.http` ein, **ohne** das Wort „Bearer“. Die API-Beschreibung gibt es auch unter <http://localhost:8080/v3/api-docs>.

## 5. Dienste und Ports

| Dienst | Host-Port | Zweck |
| --- | --- | --- |
| LF8-App | 8080 | REST und Swagger |
| projekt-db | 5433 | Projekt-PostgreSQL; Webshop kann parallel auf 5432 laufen |
| auth | 9001 | Stellt JWTs und öffentliche Schlüssel bereit |
| employee | 8089 | Mitarbeiter und Qualifikationen |
| employee-db | keiner | Datenbank nur im Compose-Netz |

## 6. Datenbank in IntelliJ ansehen

1. Öffnet das Fenster **Database** und fügt eine neue Datenquelle **PostgreSQL** hinzu.
2. Tragt als URL `jdbc:postgresql://localhost:5433/lf8_starter` ein.
3. Benutzer: `lf8_starter`, Passwort: `geheim`. Testet die Verbindung; die Container müssen dafür laufen.

Die Werte stehen auch in `compose.yml` und `src/main/resources/application.properties`.

## 7. Aufbau des Codes

- `hello/`: Entity, Repository, Service, DTOs, Mapper und Controller – eine vollständige kleine REST-Kette. Die Suche läuft über `GET /hello?message=…`; `GET /hello/{id}` ruft einen Eintrag anhand der ID ab.
- `employee/`: `EmployeeClient.findById(long)` ruft den fremden Dienst auf und reicht das JWT der eingehenden Anfrage weiter. Ergänzt weitere Methoden nach diesem Muster: URL aufrufen, Token mitsenden und eine fehlende Antwort gezielt behandeln.
- `common/`: Fehlerantworten für Validierung, unbekannte IDs und nicht erreichbare Dienste.
- `security/`: JWT-Schutz; `/welcome` und Swagger/OpenAPI sind ohne Token erreichbar.
- `config/`: RestClient und OpenAPI-Konfiguration.

`HelloService.greet` zeigt, wo Prüfungen gegen den Employee-Service stehen: `GET /hello/greeting/{employeeId}` führt von einer fremden Antwort zur eigenen Antwort: Mitarbeiter gefunden → Begrüßung; unbekannte ID → 404; Dienst nicht erreichbar → 503.

Wie Token, Anmeldedienst, euer Service und Employee-Service zusammenspielen, zeigt die interaktive Grafik [`docs/oauth-ablauf.html`](docs/oauth-ablauf.html). Öffnet sie lokal im Browser und wählt oben „Unser Projekt“.

## 8. Tests

In IntelliJ: Rechtsklick auf den Ordner `src/test/java` → *Run All Tests*. Einen einzelnen Test
startet ihr mit dem grünen Pfeil neben der Testmethode oder der Testklasse.

Oder auf der Konsole, ohne installiertes Maven (der Maven-Wrapper liegt im Projekt):

```bash
./mvnw verify        # Linux, macOS
mvnw.cmd verify      # Windows
```

Docker muss laufen, **`docker compose up` ist für Tests nicht nötig**: Testcontainers startet eine eigene PostgreSQL mit `@ServiceConnection`. Die Hello-Tests erben `@MockitoBean EmployeeClient` aus `AbstractIntegrationTest` und nutzen `jwt()` für authentifizierte Anfragen statt `@WithMockUser`. `EmployeeClientTest` simuliert den fremden HTTP-Dienst ohne Compose.

## 9. Fehlerhilfe

| Symptom | Ursache | Lösung |
| --- | --- | --- |
| „Port 8080 was already in use“ beim App-Start | Ein anderes Programm belegt den App-Port 8080 | Programm beenden oder `server.port` in `application.properties` ändern und die App-URLs in den HTTP-Dateien anpassen. |
| „port is already allocated“ beim Compose-Start | Docker-Host-Port 8089, 9000 oder 5433 ist belegt | Freien Host-Port in `compose.yml` wählen; zugehörige URLs in `application.properties` und den HTTP-Dateien anpassen. Bei 9000 auch den Hinweis in `OpenApiConfig` ändern. |
| 401 bei geschützten Endpunkten | Token fehlt oder ist abgelaufen | `GetToken.http` erneut ausführen, dann den Request wiederholen. |
| 503 bei der Begrüßung | Employee-Service ist nicht erreichbar, startet noch oder lehnt das Token ab | `docker compose ps` und `docker compose logs employee` prüfen; bei abgelaufenem Token `GetToken.http` erneut ausführen. |
| 500 mit Spalten-/Constraint-Fehler nach Änderung einer Entity | `ddl-auto=update` lässt alte Datenbankspalten stehen | `docker compose down -v` löscht beide Datenbanken; danach `docker compose up -d`. |
| Beispieldaten zurücksetzen | Alte Daten liegen in den Volumes | `docker compose down -v` löscht **beide** Datenbanken; anschließend `docker compose up -d`. |
