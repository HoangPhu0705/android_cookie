package tdtu.edu.cookie.Database.Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Topic {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String title;
    public boolean is_public;
    public String user_id;
    public String email;

    public Topic() {
    }

    @Ignore
    public Topic(int id, String title, boolean is_public) {
        this.id = id;
        this.title = title;
        this.is_public = is_public;
    }

    @Ignore
    public Topic(int id, String title, boolean is_public, String user_id) {
        this.id = id;
        this.title = title;
        this.is_public = is_public;
        this.user_id = user_id;
    }

    public Topic(int id, String title, boolean is_public, String user_id, String email) {
        this.id = id;
        this.title = title;
        this.is_public = is_public;
        this.user_id = user_id;
        this.email = email;
    }

    @Ignore
    public Topic(String title, boolean is_public){
        this.title = title;
        this.is_public = is_public;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIs_public() {
        return is_public;
    }

    public void setIs_public(boolean is_public) {
        this.is_public = is_public;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUser_id() {
        return user_id;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

}
