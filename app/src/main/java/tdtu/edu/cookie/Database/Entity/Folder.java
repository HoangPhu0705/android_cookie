package tdtu.edu.cookie.Database.Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "folders"
        )
public class Folder {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;
    public String user_id;

    public Folder(int id, String title, String user_id) {
        this.id = id;
        this.title = title;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setId(int id) {
        this.id = id;
    }
public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
