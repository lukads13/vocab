# Backlog

Ideen, die besprochen, aber noch nicht umgesetzt sind.

## Geteilte Links direkt in der App öffnen (Android App Links)
Heute führt ein geteilter Link erst auf eine kleine Webseite (`/vocab/i/`), deren Button die App öffnet.
Mit verifizierten App Links würde Android den https-Link direkt in der App öffnen – ohne Zwischenseite.

- Voraussetzung: Datei `/.well-known/assetlinks.json` ganz oben auf der Domain, also ein zusätzliches Repo
  `lukads13.github.io` (GitHub-Pages-Benutzerseite) mit dieser Datei.
- Inhalt: Paketname `at.lukads13.vocab` und SHA-256-Fingerabdruck des Signierschlüssels.
- In der App: zusätzlicher Intent-Filter mit `android:autoVerify="true"` für `https://lukads13.github.io/vocab/i/`.
- Die Webseite bleibt als Rückfall für Geräte ohne App bestehen.

## Lerngruppe im WLAN
2–3 Personen im selben WLAN, ohne Cloud: gemeinsame Wortlisten, Serien und Testergebnisse sehen (Vorbild: HabitShare).

## Kindle-Import
Vokabelheft (`vocab.db`) oder Markierungs-Export aus der Kindle-App importieren – offen, welcher Weg und welches Kindle-Modell.
