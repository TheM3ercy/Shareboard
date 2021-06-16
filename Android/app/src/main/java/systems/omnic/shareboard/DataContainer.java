package systems.omnic.shareboard;

public class DataContainer {

    private static DataContainer instance = null;

    private String userString = null;
    private boolean stayLoggedIn = false;
    private String username = "";

    private DataContainer(){}

    public static DataContainer getInstance(){
        if (instance == null)
            instance = new DataContainer();
        return instance;
    }

    public String getUserString() {
        return userString;
    }

    public void setUserString(String userString) {
        this.userString = userString;
    }

    public boolean isStayLoggedIn() {
        return stayLoggedIn;
    }

    public void setStayLoggedIn(boolean stayLoggedIn) {
        this.stayLoggedIn = stayLoggedIn;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
