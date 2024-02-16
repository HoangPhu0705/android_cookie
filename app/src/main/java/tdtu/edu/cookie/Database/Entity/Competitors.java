package tdtu.edu.cookie.Database.Entity;

import com.google.firebase.Timestamp;

public class Competitors {
    String name;
    String email;
    String score;
    int quizz;
    Timestamp timestamp;
    int time;
    public String stt;
    public String id;
    public Competitors(String id,String stt,String name, String email, String score, int quizz, Timestamp timestamp, int time) {
        this.id=id;
        this.stt=stt;
        this.name = name;
        this.email = email;
        this.score = score;
        this.quizz = quizz;
        this.timestamp = timestamp;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public int getQuizz() {
        return quizz;
    }

    public void setQuizz(int quizz) {
        this.quizz = quizz;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
