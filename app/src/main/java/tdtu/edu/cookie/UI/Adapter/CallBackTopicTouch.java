package tdtu.edu.cookie.UI.Adapter;

import androidx.recyclerview.widget.RecyclerView;

public interface CallBackTopicTouch {
    void topicTouchOnMove(int oldPosition, int newPosition);
    void onSwipedTopic(RecyclerView.ViewHolder viewHolder, int position);
}
