package tdtu.edu.cookie.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Repository.Repository;

public class TopicVM extends AndroidViewModel {
    private Repository repository;
    private LiveData<List<Topic>> topics;
    public  TopicVM(@NonNull Application application){
        super(application);
        repository = new Repository(application);
//        topics = repository.getAllTopic();
    }
    public List<Topic> getAllTopic(){
        return repository.getAllTopic();
    }
    public void insert(Topic t){
        repository.InsertTopic(t);
    }
}
