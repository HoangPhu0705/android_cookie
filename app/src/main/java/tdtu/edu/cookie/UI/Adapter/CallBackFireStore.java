package tdtu.edu.cookie.UI.Adapter;

import java.util.List;

import tdtu.edu.cookie.Database.Entity.Topic;
import tdtu.edu.cookie.Database.Entity.Words;

public interface CallBackFireStore {
    void onDataLoaded(List<?> data) ;

    void onDataLoadFailed(String errorMessage);
}
