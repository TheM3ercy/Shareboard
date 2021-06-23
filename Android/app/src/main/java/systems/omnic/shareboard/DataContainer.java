package systems.omnic.shareboard;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataContainer {

    private static DataContainer instance = null;
    private final String TAG = DataContainer.class.getSimpleName();

    private String userString = null;
    private boolean stayLoggedIn = false;
    private String username = "";
    private List<Note> recyclingBin = new ArrayList<>();
    private boolean autoSync = false;
    private boolean showNotifications = true;

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

    public List<Note> getRecyclingBin() {
        return recyclingBin;
    }

    public void setRecyclingBin(List<Note> recyclingBin) {
        this.recyclingBin = recyclingBin;
    }

    public void clear(){
        instance = new DataContainer();
    }

    public boolean isAutoSync() {
        return autoSync;
    }

    public void setAutoSync(boolean autoSync) {
        this.autoSync = autoSync;
    }

    public boolean isShowNotifications() {
        return showNotifications;
    }

    public void setShowNotifications(boolean showNotifications) {
        this.showNotifications = showNotifications;
    }

    public void saveConf(Context context) {
        Log.d(TAG, "saveConf: Method entered");

        File directory = new File(context.getFilesDir(), "data");
        if (!directory.exists())
            directory.mkdir();
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(directory, "conf.txt")))){
            bw.write(stayLoggedIn ? DataContainer.getInstance().getUserString() + ";" + stayLoggedIn + ";" + username + ";" + autoSync:"null;" + stayLoggedIn + ";;" + autoSync);
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
