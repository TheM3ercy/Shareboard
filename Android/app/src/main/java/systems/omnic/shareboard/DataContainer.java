package systems.omnic.shareboard;

public class DataContainer {

    private static DataContainer instance = null;

    private String userString = null;

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
}
