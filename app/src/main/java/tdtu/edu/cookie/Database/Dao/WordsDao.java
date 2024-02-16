package tdtu.edu.cookie.Database.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

import tdtu.edu.cookie.Database.Entity.Words;

@Dao
public interface WordsDao {
    @Insert
    void insert(Words words);
    @Query(
            "SELECT * FROM Words WHERE topic_id = :topic_id"
    )
    List<Words>  GetALlWordsByTopic(int topic_id);
    @Query(
            "SELECT * FROM Words"
    )
    List<Words>  GetAllWords();

    @Update
    void update(Words words);

    @Delete
    void delete(Words words);


}
