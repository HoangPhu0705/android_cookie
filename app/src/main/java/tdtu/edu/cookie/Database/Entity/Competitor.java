package tdtu.edu.cookie.Database.Entity;

import com.google.firebase.Timestamp;

public class Competitor {
    public String Name;
    public String email;
    public double score;
    public int time;
    public Timestamp timestamp;
    public int quiz;
    public String id;

    public Competitor(String id,String name, String email, double score, int time, Timestamp timestamp,int quiz) {
        this.id =id;
        Name = name;
        this.email = email;
        this.score = score;
        this.time = time;
        this.timestamp = timestamp;
        this.quiz=quiz;
    }
}
