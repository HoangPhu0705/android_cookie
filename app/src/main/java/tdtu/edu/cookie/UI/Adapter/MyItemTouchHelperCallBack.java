package tdtu.edu.cookie.UI.Adapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class MyItemTouchHelperCallBack extends ItemTouchHelper.Callback {
    CallBackItemTouch callBackItemTouch;

    public MyItemTouchHelperCallBack(CallBackItemTouch callBackItemTouch) {
        this.callBackItemTouch = callBackItemTouch;
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
        //this method is used to specify the directions of movement
        final int dragFlag = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlag = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlag, swipeFlag);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        // this method is called when the item is moved
//        callBackItemTouch.itemTouchOnMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // this method is called when the item is swiped
        callBackItemTouch.onSwiped(viewHolder, viewHolder.getAdapterPosition());
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        } else {
            final View backgroundView = ((AdapterWords.ViewHolder) viewHolder).viewBackground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, backgroundView, dX, dY, actionState, isCurrentlyActive);
        }
        // this method is called when the item is being moved or swiped
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        if (actionState != ItemTouchHelper.ACTION_STATE_DRAG) {
            final View foregroundView = ((AdapterWords.ViewHolder) viewHolder).viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
        //when we swipe the item, the background view is drawn first and then the foreground view is drawn
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        super.clearView(recyclerView, viewHolder);
        final View foregroundView = ((AdapterWords.ViewHolder) viewHolder).viewForeground;

        //this will set color of tem when we drag and leave any position of the recycler view
        foregroundView.setBackgroundColor(ContextCompat.getColor(((AdapterWords.ViewHolder) viewHolder).viewForeground.getContext(), android.R.color.white));
        getDefaultUIUtil().clearView(foregroundView);
        // this will clear the view when we swipe or move the item
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (viewHolder != null) {
            final View foregroundView = ((AdapterWords.ViewHolder) viewHolder).viewForeground;
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
