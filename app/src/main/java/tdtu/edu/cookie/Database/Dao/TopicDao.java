package tdtu.edu.cookie.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import tdtu.edu.cookie.Database.Entity.Topic;

@Dao
public interface TopicDao {
    @Insert
    void insert(Topic topic);
    @Query("Select * from topic")
    List<Topic> getAllTopic();

}
