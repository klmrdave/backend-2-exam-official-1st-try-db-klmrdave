package hu.nive.ujratervezes.albums;

public class AlbumManager {

    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;

    public AlbumManager(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }
}
