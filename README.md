# MenetPro - Vonat menetrend kereső

Android beadandó projekt.

## Cél

A MenetPro egy egyszerű, vonat menetrend kereső alkalmazás, ahol a felhasználó
megadhatja, hogy **honnan** szeretne indulni, **hová** szeretne utazni,
mikor indulna, illetve legfeljebb hány átszállást fogad el. A találatok
jelenleg demonstrációs célból **véletlenszerűen generált útvonalak**, de
a funkcionalitás megfelel egy valódi menetrend kereső logikájának.

Az alkalmazás **Pixel 6** készülékre (1080×2400 felbontás) lett elsődlegesen
optimalizálva, egy portré orientációra.

Készítették: **Rózsavári Zsolt (D3TOTJ)** és **Sallai Norbert (CJDYAF)**.

## Fő funkciók

### Keresés (Search)

- Honnan / Hová mezők (EditText)
- Dátum és idő választása (DatePickerDialog + TimePickerDialog)
- Max. átszállások beállítása (SeekBar)
- „Kedvencekhez adás” CheckBox:
  - ha be van pipálva, a keresés mentésre kerül a helyi adatbázisba
  - a mentett keresés a Kedvencek menüpontban jelenik meg
- „Keresés” gomb:
  - megnyitja a találati listát (ResultsFragment)
  - a találatok a megadott paraméterek alapján **random generált** útvonalak

### Találati lista (Results)

- RecyclerView-ben jelennek meg az útvonalak
- Minden elem tartalmaz:
  - indulási és érkezési állomást
  - indulási és érkezési időt
  - menetidőt
  - átszállások számát
  - szolgáltató nevét
  - illusztrációs képet (Glide-dal betöltve)
- Szűrés:
  - felső szövegmezőben (EditText) rész-szöveg alapú keresés
- Rendezés:
  - Spinner segítségével:
    - indulási idő szerint
    - érkezési idő szerint
    - átszállások száma szerint
- Listaelemekre kattintva részletes nézet (RouteDetailFragment) nyílik meg,
  **ha az adott útvonal az adatbázisban is szerepel** (pl. Kedvencekből érkezve).

### Kedvencek (Favorites)

- RecyclerView listázza az adatbázisban szereplő `favorite = 1` útvonalakat
  és mentett kereséseket.
- Listaelemekre kattintva:
  - **RouteDetailFragment** nyílik meg az adott útvonal részleteivel.
- Új kedvenc felvétele:
  - jobb alsó plusz gombbal (FloatingActionButton) megnyíló dialogból
  - itt kézzel is megadható Honnan / Hová / időpont / menetidő / átszállások /
    szolgáltató / kép URL.

### Részletes nézet (Route detail)

- Kép (Glide-dal betöltve)
- Teljes útvonal leírás (állomások, idők, menetidő, átszállások, szolgáltató)
- Funkciók:
  - **Kedvencekhez adás / eltávolítás** (favorite flag Room adatbázisban)
  - **Szerkesztés**: dialogban módosíthatók az adatok, Room `update`
  - **Törlés**: útvonal törlése az adatbázisból, Room `delete`
  - **Újra keresés ezzel az útvonallal**:
    - visszavisz a ResultsFragment-re,
    - új, véletlenszerű találatlistát generál ugyanarra a
      Honnan / Hová / max. átszállás kombinációra.

### Névjegy (About)

- Egyszerű statikus képernyő a projekt leírásával és a készítők nevével.

## Technológiai követelmények

- **Android Studio**, **Android SDK**
- **Java** (forráskód)
- **1 Activity** + több Fragment:
  - `MainActivity`
  - `SearchFragment`
  - `ResultsFragment`
  - `FavoritesFragment`
  - `RouteDetailFragment`
  - `AboutFragment`
- **ConstraintLayout** alapú felületek
- **RecyclerView** listák
- **Navigation Drawer** (MainActivity + NavigationView)
- **Room** adatbázis:
  - `Route` entitás
  - `RouteDao`
  - `AppDatabase`
- **Glide** nagyfelbontású képek betöltésére

A projekt git repository-ba tehető, ahol mindkét csapattag
önálló commitokkal tud hozzájárulni a beadandóhoz.
