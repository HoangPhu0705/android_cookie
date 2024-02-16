package tdtu.edu.cookie.UI.Adapter;

import android.graphics.Canvas;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import tdtu.edu.cookie.R;

public class TopicInFolderTouchHelper extends ItemTouchHelper.SimpleCallback{

    AdapterTopics adapterTopics;

    public TopicInFolderTouchHelper(AdapterTopics adapterTopics) {
        super(0, ItemTouchHelper.LEFT);
        this.adapterTopics = adapterTopics;
    }
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        final int position  = viewHolder.getAdapterPosition();
        if (direction == ItemTouchHelper.LEFT){
            adapterTopics.deleteTopicInFolder(position);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(Color.parseColor("#AA5656"))
                .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                .addSwipeLeftLabel("Delete")
                .create()
                .decorate();
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
