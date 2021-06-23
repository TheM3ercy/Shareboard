package systems.omnic.shareboard;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Note implements Serializable {

    private String content;
    private LocalDateTime deleteDateTime;
    private boolean isSynced;
    private int id;

    public Note(String content, boolean isSynced, int id) {
        this.content = content;
        this.isSynced = isSynced;
        this.id = id;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    public LocalDateTime getDateTime() {
        return deleteDateTime;
    }

    public void setDateTime(LocalDateTime deleteDateTime) {
        this.deleteDateTime = deleteDateTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
