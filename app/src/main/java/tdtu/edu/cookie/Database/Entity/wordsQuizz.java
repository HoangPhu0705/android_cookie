package tdtu.edu.cookie.Database.Entity;

import android.graphics.Bitmap;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
@Entity
public class wordsQuizz implements Serializable {
    private int id;
    public String english_word;
    public String vietnamese_word;
    public String spelling;
    public String date;
    public String word_form;
    public String example;
    public String audio;
    public boolean is_fav;
    public int topic_id;

    public wordsQuizz(int id, String english_word, String vietnamese_word) {
        this.id = id;
        this.english_word = english_word;
        this.vietnamese_word = vietnamese_word;
    }

    public wordsQuizz(String english_word, String vietnamese_word, String spelling, String date, String word_form, String example, String audio, boolean is_fav, int topic_id) {
        this.english_word = english_word;
        this.vietnamese_word = vietnamese_word;
        this.spelling = spelling;
        this.date = date;
        this.word_form = word_form;
        this.example = example;
        this.audio = audio;
        this.is_fav = is_fav;
        this.topic_id = topic_id;
    }



    public wordsQuizz() {

    }

    public wordsQuizz(int id, String englishWord, String vietnameseWord, String spelling, String date, String wordForm, String example, String audio, boolean isFav, int topicId) {
        this.id = id;
        this.english_word = englishWord;
        this.vietnamese_word = vietnameseWord;
        this.spelling = spelling;
        this.date = date;
        this.word_form = wordForm;
        this.example = example;
        this.audio = audio;
        this.is_fav = isFav;
        this.topic_id = topicId;
    }

    public wordsQuizz(int id, String englishWord, String vietnameseWord, String spelling, String date, String wordForm, String example, String audio, boolean isFav) {
        this.id = id;
        this.english_word = englishWord;
        this.vietnamese_word = vietnameseWord;
        this.spelling = spelling;
        this.date = date;
        this.word_form = wordForm;
        this.example = example;
        this.audio = audio;
        this.is_fav = isFav;
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
}
