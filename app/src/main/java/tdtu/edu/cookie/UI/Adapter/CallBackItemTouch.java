package tdtu.edu.cookie.UI.Adapter;

import androidx.recyclerview.widget.RecyclerView;

public interface CallBackItemTouch {
    void itemTouchOnMove(int oldPosition, int newPosition);
    void onSwiped(RecyclerView.ViewHolder viewHolder, int position);
}
