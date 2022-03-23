# Albums
Mindig a legfrissebb zenéket szeretném hallgatni, ezért szükségem van egy programra ami megmondja melyik évben adták ki a legújabb albumukat a kedvenc énekeseim. Lehet hogy lemaradtam valamiről?

# Adatbázis

Az adatbázis két táblából áll amelynek nevei `albums` és `singers`. 

A `albums` tábla következő oszlopokal rendelkezik:

- album_name VARCHAR(255)
- year INT
- singer_id INT

Például:

| album_name        | release_year | singer_id  |
|:------------------|:-------------|:-----------|
| Pilgrim           | 1998         | 3          |
| Believe           | 1998         | 1          | 
| Heart of Stone    | 1989         | 1          | 
| Love hurts        | 1991         | 1          |
| Moonflower        | 1977         | 2          |
| Shaman            | 2002         | 2          | 

A `singers` tábla következő oszlopokal rendelkezik:

- singer_id SERIAL
- singer_name VARCHAR(255)

Például:

| singer_id | singer_name     | 
|:----------|:----------------|
| 1         |  Cher           |  
| 2         |  Santana        |  
| 3         |  Eric Clapton   |
| 4         |  Bryan Adams    |


# Java alkalmazás

Az `AlbumManager` osztály konstruktora a következő paraméterekkel rendelkezik: 
- `String dbUrl` az url amin az adatbázis elérhető.
- `String dbUser` felhasználónév amivel csatlakozhatunk az adatbázishoz.
- `String dbPassword`  A `dbUser`-hez tartozó jelszó.

Készítsd el a `AlbumManager` osztály `getSingersWithTheirLatestAlbum` metódusát! Abban az esetben ha az adatbázis üres a metódus térjen vissza egy üres `String` listával. 
Egyéb esetben a a metódus térjen vissza egy `String` listában az énekesek nevét és a legutóbbi albumuk megjelenésének évét összefűző szöveggel, a következő formátumban `"Cher - 1998"`. Az eredmény legyen rendezve az album kiadási éve szerinti csökkenő sorrendben.
Minden énekes csak egyszer szerepelhet a listában.
A megoldáshoz használj `PreparedStatement`-et!

# Test-ek
```java
class AlbumManagerTest {
    private static final List<String> EXPECTED_SINGERS = List.of("Santana - 2002", "Cher - 1998" ,"Eric Clapton - 1998","Bryan Adams - null");

    private static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    private AlbumManager albumManager;

    @BeforeEach
    void init() throws SQLException {
        albumManager = new AlbumManager(DB_URL, DB_USER, DB_PASSWORD);
        createTable();
    }

    @AfterEach
    void destruct() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String dropTableAlbums = "DROP TABLE IF EXISTS albums";
            Statement statementAlbums = connection.createStatement();
            statementAlbums.execute(dropTableAlbums);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String dropTableSinger = "DROP TABLE IF EXISTS singers";
            Statement statementSinger = connection.createStatement();
            statementSinger.execute(dropTableSinger);
        }
    }


    private void createTable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String createTableAlbums = "CREATE TABLE IF NOT EXISTS albums (" +
                    "album_name VARCHAR(255), " +
                    "release_year INT, " +
                    "singer_id INT"+
                    ");";
            Statement statementAlbums = connection.createStatement();
            statementAlbums.execute(createTableAlbums);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String createTableSinger = "CREATE TABLE IF NOT EXISTS singers (" +
                    "singer_id SERIAL, " +
                    "singer_name VARCHAR(255)"+
                    ");";
            Statement statementSinger = connection.createStatement();
            statementSinger.execute(createTableSinger);
        }
    }

    @Test
    void test_getSingersWithExistingAlbums_anyOrder() throws SQLException {
        insertMultipleSingersAndAlbums();
        List<String> actualSingers = albumManager.getSingersWithTheirLatestAlbum();

        for (String singer : actualSingers) {
            assertTrue(EXPECTED_SINGERS.contains(singer));
        }
    }


    @Test
    void test_getUniqueSingers_alphabeticOrder() throws SQLException {
        insertMultipleSingersAndAlbums();
        List<String> actualSingers = albumManager.getSingersWithTheirLatestAlbum();
        assertEquals(EXPECTED_SINGERS, actualSingers);
    }

    @Test
    void test_getSingersWithAlbums_checkAllSingersIncluded_anyOrder() throws SQLException {
        insertMultipleSingersAndAlbums();
        List<String> actualSingers = albumManager.getSingersWithTheirLatestAlbum();
        assertEquals(EXPECTED_SINGERS.size(), actualSingers.size());
        for (String Singer : EXPECTED_SINGERS) {
            assertTrue(actualSingers.contains(Singer));
        }
    }

    @Test
    void test_getUniqueSingers_emptyDatabase() {
        assertEquals(List.of(), albumManager.getSingersWithTheirLatestAlbum());
    }

    private void insertMultipleSingersAndAlbums() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertAlbums = "INSERT INTO albums (album_name, release_year, singer_id) VALUES " +
                    "('Pilgrim', 1998, 3), "+
                    "('Believe', 1998, 1), " +
                    "('Heart of Stone', 1989, 1), " +
                    "('Love hurts', 1991, 1), " +
                    "('Moonflower', 1977, 2), " +
                    "('Shaman', 2002, 2);";
            Statement statement = connection.createStatement();
            statement.execute(insertAlbums);
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String insertSingers = "INSERT INTO singers (singer_id, singer_name) VALUES " +
                    "(1,'Cher'), " +
                    "(2,'Santana'), " +
                    "(3,'Eric Clapton'), " +
                    "(4,'Bryan Adams');";
            Statement statement = connection.createStatement();
            statement.execute(insertSingers);
        }
    }
}
```

## Pontozás

Egy feladatra 0 pontot ér, ha:

- nem fordul le
- lefordul, de egy teszteset sem fut le sikeresen.
- ha a forráskód olvashatatlan, nem felel meg a konvencióknak, nem követi a clean code alapelveket.

0 pont adandó továbbá, ha:

- kielégíti a teszteseteket, de a szöveges követelményeknek nem felel meg

Pontokat a további működési funkciók megfelelősségének arányában kell adni a vizsgafeladatra:

- 5 pont: az adott projekt lefordul, néhány teszteset sikeresen lefut, és ezek funkcionálisan is helyesek. Azonban több
  teszteset nem fut le, és a kód is olvashatatlan.
- 10 pont: a projekt lefordul, a tesztesetek legtöbbje lefut, ezek funkcionálisan is helyesek, és a clean code elvek
  nagyrészt betartásra kerültek.
- 20 pont: ha a projekt lefordul, a tesztesetek lefutnak, funkcionálisan helyesek, és csak apróbb funkcionális vagy
  clean code hibák szerepelnek a megoldásban.

Gyakorlati pontozás a project feladatokhoz:

- Alap pontszám egy feladatra(max 20): lefutó egység tesztek száma / összes egység tesztek száma * 20, feltéve, hogy a
  megoldás a szövegben megfogalmazott feladatot valósítja meg
- Clean kód, programozási elvek, bevett gyakorlat, kód formázás megsértéséért - pontlevonás jár. Szintén pontlevonás
  jár, ha valaki a feladatot nem a leghatékonyabb módszerrel oldja meg - amennyiben ez értelmezhető.
