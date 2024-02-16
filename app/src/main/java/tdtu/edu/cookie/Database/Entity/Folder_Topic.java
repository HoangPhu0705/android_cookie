package tdtu.edu.cookie.Database.Entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        primaryKeys = {"folder_id", "topic_id"},
        foreignKeys = {
                @ForeignKey(entity = Topic.class,
                        parentColumns = "id",
                        childColumns = "topic_id"),
                @ForeignKey(entity = Folder.class,
                        parentColumns = "id",
                        childColumns = "folder_id")
        })
public class Folder_Topic {
    public int topic_id;
    public int folder_id;
}
