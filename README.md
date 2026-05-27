# 💣 Bomberman Arcade Remake

Rozbudowana, zręcznościowa gra akcji 2D będąca nowoczesną interpretacją klasycznego Bombermana. Projekt pozwala na lokalną rozgrywkę wieloosobową na jednym ekranie oraz zabawę jednoosobową z zaawansowanymi botami sterowanymi przez sztuczną inteligencję.

## 🕹️ Tryby Rozgrywki

Gra oferuje cztery unikalne tryby zabawy, całkowicie zmieniające dynamikę rundy:

* **Klasyczny:** Tradycyjna walka o przetrwanie, w której celem jest eliminacja przeciwników za pomocą podstawowych bomb.
* **Ulepszenia:** Tryb wzbogacony o system przedmiotów modyfikujących statystyki postaci, stakowanie pancerzy ochronnych oraz niebezpieczne klątwy (np. odwrócenie sterowania, bieg bez zatrzymania).
* **Battle Royale:** Intensywne starcie ze stopniowo zacieśniającą się, fioletową strefą burzy, która systematycznie zmniejsza pole gry i eliminuje uwięzionych w niej graczy.
* **Paint Mode:** Innowacyjny tryb czasowy inspirowany grami typu *Splatoon*. Gracze są odporni na wybuchy bomb, a ich celem jest zamalowanie terytorium swoim kolorem – wygrywa ten, kto zdobędzie najwięcej kafelków przed upływem minuty.

## 🛠️ Kreator i Edytor Map

Aplikacja posiada wbudowany edytor poziomów pozwalający na tworzenie autorskich aren bezpośrednio z poziomu gry.
* **Bezpieczeństwo:** Baza domyślnych map jest zablokowana przed modyfikacją lub usunięciem.
* **Zarządzanie:** Edytor umożliwia nadanie mapie własnej nazwy, zapis do zewnętrznego folderu `custom_maps`, ponowną edycję istniejących projektów oraz ich usuwanie.
* **Zabezpieczenia:** Inteligentny pędzel automatycznie uniemożliwia postawienie ściany w punktach startowych (spawnach) graczy i botów, co zapobiega zablokowaniu postaci.
* **Dedykowany Interfejs:** Edytor posiada dolny kokpit z podglądem współrzędnych kursora oraz kompletną legendą skrótów klawiszowych.

## 🤖 Zaawansowana Sztuczna Inteligencja

Komputerowi przeciwnicy posiadają dwa poziomy trudności:
* **Łatwy:** Boty poruszają się w losowych kierunkach i okazjonalnie zostawiają bomby.
* **Trudny:** Boty aktywnie skanują mapę, unikają stref rażenia bomb oraz ognia, niszczą skrzynki blokujące drogę, a po oczyszczeniu terenu aktywnie ścigają gracza.

## 📺 Cechy Wizualne i Interfejs

* **Cyberpunkowe Menu Główne:** Zastosowano ciemną paletę barw z neonowymi podświetleniami, cieniami pod napisami oraz efektownymi kursorami wyboru.
* **Skalowanie Arcade:** Plansza zachowuje idealny kształt kwadratu niezależnie od wymiarów okna. Pusta przestrzeń po bokach (*letterboxing*) została zapełniona retro siatką.
* **Interaktywne Boki (Sidebars):** Podczas gry na pełnym ekranie na marginesach wyświetla się dynamiczna legenda przedmiotów oraz informacje o wybranym trybie gry.
* **Uporządkowany HUD:** Dolny panel statystyk został podzielony na równe, czytelne warstwy, dzięki czemu statusy klątw, punktów i pancerza nigdy nie zasłaniają danych innych graczy.

## ⌨️ Domyślne Sterowanie

* **Gracz 1:** Ruch: `W`, `A`, `S`, `D` | Bomba: `SPACJA`
* **Gracz 2:** Ruch: `Strzałki` | Bomba: `ENTER`
* **Edytor:** Ruch kursora: `WSAD` lub `Strzałki` | [1] Mur, [2] Skrzynia, [3] Gumka
* **Zarządzanie:** [ESC] Pauza / Powrót | [E] Edycja mapy w menu | [BACKSPACE] Usunięcie mapy
