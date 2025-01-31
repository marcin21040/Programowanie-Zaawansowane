# Programowanie-Zaawansowane

# Projekt trzech studentów z imionami na M. :D

## Autorzy
- Marcin Wawszczak
- Mateusz Żero
- Michał Pieciun
  
## Wersja Javy oraz wykorzystanych bibliotek
### openjdk-23.0.1
###json-20210307.jar
###https://repo1.maven.org/maven2/org/json/json/20210307/

## Opis projektu

Projekt składa się z trzech gier w stylu Arcade. Gry zostały wykonane bazując na technologii JSwing w Javie. Umożliwia ona implementację elementów graficznych na interaktywnych oknach. W połączeniu z określeniem odpowiednich zachowań i zależności pomiędzy poszczególnymi elementami gry, możliwe jest stworzenie różnego rodzaju gier. Efektem projektu jest powstanie trzech z nich: Strzelanki kosmicznej, bazującej na klasycznych arcade'owych grach typu Space Invaders, gry logicznej bazującej na grze WORDLE oraz gry typu Tower Defense.

### Gra Michała

Opis gry:

Gra typu "Tower Defense". Polega na stawianiu wież w obrębie planszy w taki sposób, aby przeciwnicy nie mieli możliwości dotarcia do końca ścieżki. Jeżeli pokonani zostaną wszyscy przeciwnicy gra kończy się sukcesem - jeżeli przeciwnicy przejdą całą ścieżkę, gra kończy się porażką. Uniemożliwione jest stawianie wież jeżeli zabraknie na nie wirtualnej waluty gracza lub w obrębie pola ścieżki przeciwników.

Sterowanie:

Lewy oraz prawy przycisk myszy. 

### Gra Marcina

Opis gry:

Gra typu "Logiczna". Gracz musi odgadnąć pięcioliterowe słowo. Ma przy tym pięć prób. Po wstawieniu słowa, na wierszu ekranu gry zaznaczane są litery które występują w słowie lub znajdują się na właściwym miejscu co ułatwia zgadywanie. Jeżeli gracz odgadnie słowo - wygrywa. Jeśli nie, przegrywa i jest informowany o haśle. W grze sterujemy za pomocą graficznego GUI lub klawiatury.

Sterowanie:

Lewy przycisk myszy oraz litery na klawiaturze odpowiadające wstawianym w grze

### Gra Mateusza

Opis gry:

Gra typu "Space Invaders". Gracz porusza się małym statkiem na dole ekranu. Celem gry jest uzyskanie jak największego wyniku. Rekord gry jest zapisywany do pliku i wyświetlany na ekranie. Gracz zdobywa punkty za zbieranie bonusów oraz niszczenie kosmitów. Jednocześnie punkty są naliczane w trakcie trwania gry - im dłużej leci gracz tym większy wynik. Z biegiem czasu statek leci coraz szybciej co zwiększa poziom trudności. Jeżeli statek uderzy w kosmitę lub kometę gra się kończy.

Sterowanie:

Lewa oraz prawa strzałka porusza statkiem, spacja oddaje strzał.
