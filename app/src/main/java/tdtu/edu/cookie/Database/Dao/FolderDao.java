package tdtu.edu.cookie.Database.Dao;

import androidx.room.Dao;
import androidx.room.Insert;

import tdtu.edu.cookie.Database.Entity.Folder;

@Dao
public interface FolderDao {
    @Insert
    void insert(Folder folder);

}
