package tdtu.edu.cookie.UI.Adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TopicTouchHelperCallBack extends ItemTouchHelper.Callback {
    CallBackTopicTouch callBackTopicTouch;

    public TopicTouchHelperCallBack(CallBackTopicTouch callBackTopicTouch) {
        this.callBackTopicTouch = callBackTopicTouch;
    }
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        final int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlag, swipeFlag);    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        callBackTopicTouch.topicTouchOnMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        callBackTopicTouch.onSwipedTopic(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else {
            final View backgroundView = ((AdapterTopics.ViewHolder) viewHolder).viewBackgroundTopic;
            getDefaultUIUtil().onDrawOver(c, recyclerView, backgroundView, dX, dY, actionState, isCurrentlyActive);
        }
        // this method is called when the item is being moved or swiped
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
            final View foregroundView = ((AdapterTopics.ViewHolder) viewHolder).viewForegroundTopic;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
        //when we swipe the item, the background view is drawn first and then the foreground view is drawn
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
        final View foregroundView = ((AdapterTopics.ViewHolder) viewHolder).viewForegroundTopic;

        //this will set color of tem when we drag and leave any position of the recycler view
        foregroundView.setBackgroundColor(ContextCompat.getColor(((AdapterTopics.ViewHolder) viewHolder).viewForegroundTopic.getContext(), android.R.color.white));
        getDefaultUIUtil().clearView(foregroundView);
        // this will clear the view when we swipe or move the item
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder != null) {
            final View foregroundView = ((AdapterTopics.ViewHolder) viewHolder).viewForegroundTopic;
            if ((actionState == ItemTouchHelper.ACTION_STATE_DRAG)) {
                foregroundView.setBackgroundColor(Color.LTGRAY);
                //this will set color of tem when we drag and leave any position of the recycler view
            }
            getDefaultUIUtil().onSelected(foregroundView);
        }

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
}
