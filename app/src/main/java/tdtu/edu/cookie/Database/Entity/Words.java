package tdtu.edu.cookie.Database.Entity;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


@Entity(foreignKeys = @ForeignKey(entity = Topic.class, parentColumns = "id", childColumns = "id"))
public class Words implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    public String english_word;
    public String vietnamese_word;
    public String spelling;
    public String date;
    public Bitmap image;
    public String word_form;
    public String example;
    public String audio;
    public boolean is_fav;
    public int topic_id;

    public String status;
    public String user_id;

    public Words(int id, String english_word, String vietnamese_word) {
        this.id = id;
        this.english_word = english_word;
        this.vietnamese_word = vietnamese_word;
    }



    public Words(int id, String english_word, String vietnamese_word, String word_form, String audio) {
        this.id = id;
        this.english_word = english_word;
        this.vietnamese_word = vietnamese_word;
        this.word_form = word_form;
        this.audio = audio;
    }


    public Words(int id, String english_word, String vietnamese_word, String word_form, String audio, String status) {
        this.id = id;
        this.english_word = english_word;
        this.vietnamese_word = vietnamese_word;
        this.word_form = word_form;
        this.audio = audio;
        this.status = status;
    }

    public Words(String english_word, String vietnamese_word, String spelling, String date, Bitmap image, String word_form, String example, String audio, boolean is_fav, int topic_id) {
        this.english_word = english_word;
        this.vietnamese_word = vietnamese_word;
        this.spelling = spelling;
        this.date = date;
        this.image = image;
        this.word_form = word_form;
        this.example = example;
        this.audio = audio;
        this.is_fav = is_fav;
        this.topic_id = topic_id;
    }


    public Words() {

    }

    public Words(int id, String englishWord, String vietnameseWord, String spelling, String date, Bitmap photo, String wordForm, String example, String audio, boolean isFav, int topicId) {
        this.id = id;
        this.english_word = englishWord;
        this.vietnamese_word = vietnameseWord;
        this.spelling = spelling;
        this.date = date;
        this.image = photo;
        this.word_form = wordForm;
        this.example = example;
        this.audio = audio;
        this.is_fav = isFav;
        this.topic_id = topicId;
    }

    public Words(int id, String englishWord, String vietnameseWord, String spelling, String date, Bitmap photo, String wordForm, String example, String audio, boolean isFav, int topicId, String status) {
        this.id = id;
        this.english_word = englishWord;
        this.vietnamese_word = vietnameseWord;
        this.spelling = spelling;
        this.date = date;
        this.image = photo;
        this.word_form = wordForm;
        this.example = example;
        this.audio = audio;
        this.is_fav = isFav;
        this.topic_id = topicId;
        this.status = status;
    }

    public Words(int id, String englishWord, String vietnameseWord, String spelling, String date, Bitmap photo, String wordForm, String example, String audio, boolean isFav, int topicId, String status, String user_id) {
        this.id = id;
        this.english_word = englishWord;
        this.vietnamese_word = vietnameseWord;
        this.spelling = spelling;
        this.date = date;
        this.image = photo;
        this.word_form = wordForm;
        this.example = example;
        this.audio = audio;
        this.is_fav = isFav;
        this.topic_id = topicId;
        this.status = status;
        this.user_id = user_id;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEnglish_word() {
        return english_word;
    }

    public void setEnglish_word(String english_word) {
        this.english_word = english_word;
    }

    public String getVietnamese_word() {
        return vietnamese_word;
    }

    public void setVietnamese_word(String vietnamese_word) {
        this.vietnamese_word = vietnamese_word;
    }

    public String getSpelling() {
        return spelling;
    }

    public void setSpelling(String spelling) {
        this.spelling = spelling;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getWord_form() {
        return word_form;
    }

    public void setWord_form(String word_form) {
        this.word_form = word_form;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }


    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public boolean isIs_fav() {
        return is_fav;
    }

    public void setIs_fav(boolean is_fav) {
        this.is_fav = is_fav;
    }

    public int getTopic_id() {
        return topic_id;
    }

    public void setTopic_id(int topic_id) {
        this.topic_id = topic_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

}
