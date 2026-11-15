# vocab – Vokabeltrainer EN–DE

Eine Android-App zum Lernen englischer Vokabeln. Über den Tag verteilt fragt sie dich mit kurzen Spontan-Tests ab.

- **Wort hinzufügen:** Englisches Wort eingeben. Die App holt die deutsche Übersetzung (MyMemory) und den Wörterbuch-Eintrag (dictionaryapi.dev) mit Aussprache, Bedeutung, Beispielsatz samt Übersetzung und Synonymen. Vor dem Speichern lässt sich alles anpassen.
- **Spontan-Tests:** Die App würfelt jeden Tag zufällige Zeitpunkte in deinem Zeitfenster (z. B. 4× zwischen 8 und 21 Uhr) und plant sie 7 Tage im Voraus als Benachrichtigungen ein. Das funktioniert auch, wenn die App geschlossen ist. Tippst du auf die Benachrichtigung, startet der Test: Englisch → Deutsch, Deutsch → Englisch oder Lückentext. Schwache Wörter kommen öfter dran.
- **Offline & privat:** Lernen und Tests gehen ohne Internet, nur das Nachschlagen braucht eine Verbindung. Alle Daten bleiben auf dem Handy, es gibt keinen Account und keine Cloud.

## APK installieren

1. Am Handy unter **Releases** (rechts auf der Repo-Seite) die neueste `Vokabeln-1.0.x.apk` herunterladen.
2. Datei öffnen. Beim ersten Mal fragt Android, ob der Browser bzw. Dateimanager Apps installieren darf: erlauben.
3. Beim ersten Start „Benachrichtigungen erlauben“ bestätigen.

**Updates:** Jeder Push auf `main` (oder `android-app`) baut automatisch eine neue Version. Die neue APK installiert sich einfach über die alte, deine Vokabeln bleiben erhalten.

**Update-Prüfung:** Die App schaut alle paar Stunden (und unter *Einstellungen → App → Nach Updates suchen*) nach, ob es ein neueres Release gibt. Wenn ja, zeigt sie auf der Startseite „Neue Version verfügbar“ mit einem Download-Button. Weil das Repo privat ist, braucht die App dafür einen Lese-Zugang:

1. Auf GitHub unter *Settings → Developer settings → Personal access tokens → Fine-grained tokens* einen Token anlegen: nur Repository `vocab`, Berechtigung *Contents: Read-only*.
2. Im Repo unter *Settings → Secrets and variables → Actions* ein Secret `UPDATE_TOKEN` mit diesem Token anlegen.
3. Einmal neu bauen (Push oder *Actions → Android-APK bauen → Run workflow*).

Der Token steckt dann in der APK und kann nur dieses Repo lesen. Ist das Repo öffentlich, braucht es keinen Token, und der Button lädt die APK direkt herunter.

**Tipp:** Manche Hersteller (Samsung, Xiaomi, Huawei) stoppen Apps im Hintergrund aggressiv. Kommen die Tests nicht, in den Android-Einstellungen bei der App unter *Akku* „Nicht einschränken“ wählen.

## Aufbau

| Pfad | Inhalt |
|---|---|
| `www/` | die App selbst (eine HTML-Datei, läuft auch im Browser) |
| `android/` | Android-Projekt (Capacitor 8) |
| `capacitor.config.json` | App-Name, ID, Benachrichtigungs-Icon |
| `.github/workflows/android.yml` | baut die APK bei jedem Push und hängt sie an ein Release |

Signiert wird mit dem Schlüssel `android/app/vocab.keystore`, der im Repo liegt. Damit sind alle Builds gleich signiert, und Updates lassen sich drüberinstallieren. Für die private Nutzung passt das so. Für eine Veröffentlichung im Play Store müsste der Schlüssel geheim (als GitHub Secret) liegen.

## Lokal entwickeln

```bash
npm install
npx cap sync android     # nach jeder Änderung in www/
npx cap open android     # öffnet Android Studio
```

## Später

Lerngruppe im WLAN (2–3 Personen): gemeinsame Wortlisten, Serien und Testergebnisse sehen – nach dem Vorbild von HabitShare, aber ohne Cloud.
