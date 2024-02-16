package tdtu.edu.cookie.Repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import tdtu.edu.cookie.Database.Dao.FolderDao;
import tdtu.edu.cookie.Database.Dao.TopicDao;
import tdtu.edu.cookie.Database.Dao.UserDao;
import tdtu.edu.cookie.Database.Dao.WordsDao;
import tdtu.edu.cookie.Database.Entity.Folder;
import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.Database.MainDatabase.CookieDatabase;

import java.util.List;


public class Repository {
    private FolderDao folderDao;
    private TopicDao topicDao;
    private WordsDao wordsDao;
    private UserDao userDao;

    public Repository(Application application) {
        CookieDatabase db = CookieDatabase.getDatabase(application);
        folderDao = db.folderDao();
        topicDao = db.topicDao();
        wordsDao = db.wordsDao();
        userDao = db.UserDao();

    }

    public void InsertFolder(Folder f) {
        folderDao.insert(f);
    }

    public void InsertTopic(Topic t) {
        topicDao.insert(t);
    }

    public void InsertWord(Words w) {
        wordsDao.insert(w);
    }

    public void UpdateWord(Words w) {
        wordsDao.update(w);
    }

    public void DeleteWord(Words w) {
        wordsDao.delete(w);
    }

    public List<Topic> getAllTopic() {
        return topicDao.getAllTopic();
    }

    public List<Words> getAllWords() {
        return wordsDao.GetAllWords();
    }
    public List<Words> getAllWordsByTopic(int topic_id) {
        return wordsDao.GetALlWordsByTopic(topic_id);
    }


}
