package hu.nive.ujratervezes.albums;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
            String createTableAlbums = "CREATE TABLE IF NOT EXISTS albums (album_name VARCHAR(255), release_year INT, singer_id INT);";
            Statement statementAlbums = connection.createStatement();
            statementAlbums.execute(createTableAlbums);
        }
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String createTableSinger = "CREATE TABLE IF NOT EXISTS singers (singer_id SERIAL, singer_name VARCHAR(255));";
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
    void test_getUniqueSingers_inOrder() throws SQLException {
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
