package tdtu.edu.cookie.UI.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import tdtu.edu.cookie.Database.Entity.TestModel;
import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.R;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {

    private List<Words> listWords;

    private Context context;

    private List<Boolean> frontCardStates;


    public CardStackAdapter( Context context, List<Words> listWords) {
        this.context = context;
        this.listWords = listWords;

    }

    @NonNull
    @Override
    public CardStackAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.card_item, parent, false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardStackAdapter.ViewHolder holder, int position) {
        holder.setData(listWords.get(position));


    }

    @Override
    public int getItemCount() {
        return listWords.size();
    }

    public int updateData(int position) {
        // Modify the item at the specified position in the listWords
        // For example, you can update the item's properties or replace it with a new object
        // listWords.set(position, updatedItem);

        // Notify the adapter that the item at the specified position has changed
        notifyItemChanged(position);

        // Return the previous position
        return position - 1;
    }


    //ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView english;
        TextView definition;


        TextView wordForm;


        public ViewHolder(@NonNull View itemView){
            super(itemView);

            english = itemView.findViewById(R.id.englishWord);
            definition = itemView.findViewById(R.id.txt_definition);
            wordForm = itemView.findViewById(R.id.txt_wordform);
        }
        void setData(Words word){
            english.setText(word.getEnglish_word());
            definition.setText(word.getVietnamese_word());
            wordForm.setText(word.getWord_form());
        }








    }






}

