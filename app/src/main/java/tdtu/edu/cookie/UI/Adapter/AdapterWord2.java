package tdtu.edu.cookie.UI.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tdtu.edu.cookie.Database.Entity.Words;
import tdtu.edu.cookie.Database.Entity.wordsQuizz;
import tdtu.edu.cookie.R;

public class AdapterWord2 extends  RecyclerView.Adapter<AdapterWord2.ViewHolder>{
    Context context;
    ArrayList<wordsQuizz> words;
    Vibrator vibrator;

    public AdapterWord2(Context context,ArrayList<wordsQuizz> words){
        this.words=words;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vocabularies, parent, false);
        return new AdapterWord2.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int positionWord = position;

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        holder.textViewWord.setText(words.get(position).getEnglish_word());
        holder.textViewWordForm.setText(words.get(position).getWord_form());
        holder.textViewSpelling.setText(words.get(position).getSpelling());
        holder.textViewMeaning.setText(words.get(position).getVietnamese_word());
        holder.textViewDate.setText(words.get(position).getDate());
        holder.heartButton.setVisibility(View.GONE);
        holder.topicButton.setVisibility(View.GONE);
        holder.imageViewSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrator.vibrate(5);

                playAudio(words.get(position).getAudio());
            }
        });
    }

    private void playAudio(String url) {

        //  Play audio from url but not use intent
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(context, "There is no sound of this word", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewWord;
        TextView textViewWordForm;
        TextView textViewSpelling;
        TextView textViewMeaning;
        TextView textViewDate;
        RelativeLayout viewBackground;
        LinearLayout viewForeground;
        CardView cardViewVocabulary;
        ImageView topicButton;
        com.varunest.sparkbutton.SparkButton heartButton;
        ImageView imageViewSpeaker;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewWord = itemView.findViewById(R.id.textViewWord);
            textViewWordForm = itemView.findViewById(R.id.textViewWordForm);
            textViewSpelling = itemView.findViewById(R.id.textViewSpelling);
            textViewMeaning = itemView.findViewById(R.id.textViewMeaning);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            viewBackground = itemView.findViewById(R.id.viewBackground);
            viewForeground = itemView.findViewById(R.id.viewForeground);
            heartButton = itemView.findViewById(R.id.heartButton);
            cardViewVocabulary = itemView.findViewById(R.id.cardViewVocabulary);
            imageViewSpeaker = itemView.findViewById(R.id.imageViewSpeaker);
            topicButton = itemView.findViewById(R.id.topicButton);

        }
    }
}
