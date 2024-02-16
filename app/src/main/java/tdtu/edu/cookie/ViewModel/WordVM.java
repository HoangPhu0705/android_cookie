package tdtu.edu.cookie.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.Repository.Repository;

public class WordVM extends AndroidViewModel {
    private Repository repository;
    public List<Words> words;

    public WordVM(@NonNull Application application) {
        super(application);
        repository = new Repository(application);
//        words = repository.getAllWords();
    }

    public List<Words> getAllWords() {
        return repository.getAllWords();
    }
    public List<Words> getAllWordsByTopic(int topic_id) {
        return repository.getAllWordsByTopic(topic_id);
    }

    public void insert(Words w) {
        repository.InsertWord(w);
    }

    public void update(Words w) {
        repository.UpdateWord(w);
    }

    public void delete(Words w) {
        repository.DeleteWord(w);
    }
}
