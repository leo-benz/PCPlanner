# Users
- Anzahl an Usern
- Umgebung aktuell Windows
- Umgebung (Windows, Mac, Linux, iOS, Android)

## Martina
- Datenbank lokal auf Windows Rechner
## Jocco

# Journeys
## Daten einpflegen
Jedes Jahr aktuelle Listen:
Liste als Papier/Gescanntes PDF
Golden, Diamanten, Eiserne Hochzeiten
Liste mit allen die in diesem Jahr 80 Jahre alt Werden
Liste mit allen > 85 Jahren Geburtstagen

Bisher manuell abgeglichen mit alten daten und 80 und hochzeiten händisch eingegeben

## Rückmeldung kein Anschreiben gewünscht
Eintragen
Über Jahre hinweg beibehalten
Abgleich über Name, Vorname, Addresse ändert sich

Aktuell Martina trägt ein oder Jocco leitet an Martina weiter

## Daten löschen/aktualisieren
Wenn verstorben info von Pfarramt Sekretariat direkt an Martina, dann Daten löschen
Umzüge bekommen wir unter dem Jahr nicht mit, nur im nächsten Jahr

## Termine Planen
1. Liste mit allen Jubilaren
   1. Alle Hochzeiten
   2. Alle 80 Geburtstage
   3. Alle >=85 Geburtstage
2. Jubilare während Sommerferien identifizieren
   1. Nachholtermin vor Kirche Datum festlegen
   2. Deadline Termin für Rückmeldung festlegen
3. Ständchen Sonntage Festlegen
   1. Zweiter Sonntag im Monat verboten
   2. Ansonsten immer jeden zweiten Sonntag
   3. Manuelle Anpassungen basierend auf Auftritten
4. Match von Jubilaren auf nächstmöglichen Termin
5. Balacing von "überbuchten" Terminen (wenn einer viele Anschreiben hat und der nächste nur wenige dann die letzen aufs nächste Schieben und 3 Wochen in Kauf nehmen )
6. Jahresliste Speichern

## Briefe drucken
1. Zeitraum auswählen
2. Alle Anschreiben für Ständchen in diesem Zeitraum generieren
3. Alle Anschreiben in ein Word Dokument
4. Word Dokument Drucken

## Daten ansehen
Suche nach Name

1. Gesamttabelle aller Jubilare (auch mit denen die im aktuellen Jahr kein Ständchen bekommen)
2. Gesamttabelle fürs aktuelle Jahr (inkl. Ständchen Daten)
3. Stänchen Sonntage anzeigen / aktualisieren
4. Gefiltert auf ausgewählten Zeitraum
5. Liste mit Ständchen auf Altersheime (Use-Case frühzeitig in Altersheimen Bescheid geben wenn Ständchen

## Rückmeldungen sammeln
Export Jahreslist nach Excel (Ausdruck der neben Telefon liegt um Rückmeldung eintragen zu können)
Rückmeldung: Nicht mehr, Ja/Nein (aktuell analog in der Probe)
Verbesserung Version 2: Rückmeldung in Datenbank direkt eintragen (von mehreren Usern dezentral)

## Migration der Daten
- Opt-Out in neue Datenbank übertragen (Excel import)

## Improvements (V2)
- Tracking ob abgelehnt oder keine Rückmeldung und nach 3 Jahren automatisches Opt-Out


# Anforderungen
## Funktional
Datenstruktur
- Name
- Vorname
- Geschlecht
- Geburstag
- Adresse
- Opt-Out
- Bemerkung
- Hochzeitsmarker (Eisern, Golden, Diamanten, Gnaden)

Ad-Hoc Umbuchungen manuell angepasst

Word Dokument als Vorlage
- Anschrift (Herr/Frau/Ehepaar)
- Begrüßung (Herr/Frau/Ehepaar)
- Erstell Datum (Monat Jahr)
- Ständchen Datum
- Rückmelde Datum (Dienstag vor Ständchen)

Eine Vorlage/Formulierung für "normale" Ständchen und eines für Nachhol Termin nach den Ferien

## Nicht-Funktional
- Hash funktion um daten zu aktualisieren ohne Namen und Addressen zu übertragen
(Speichern Hashes von Name, Vorname, Geburtsdatum) + Ständle Datum + Opt-Out + Bemerkung
- Anleitung
- Text template zentral anpassbar über Backend (Version 2)
- Send Termine zu Konzertmeister (Version 2)

