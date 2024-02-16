package tdtu.edu.cookie.Database.Entity;

import android.graphics.Bitmap;

public class Question {
    String questionContext;
    String oA;
    String oB;
    String oC;
    String oD;

    public String getoD() {
        return oD;
    }

    public void setoD(String oD) {
        this.oD = oD;
    }

    String ans;
    public Bitmap image;

    public Question(String questionContext, String oA, String oB, String oC, String oD, String ans) {
        this.questionContext = questionContext;
        this.oA = oA;
        this.oB = oB;
        this.oC = oC;
        this.ans = ans;
        this.oD = oD;
    }

    public Question(String questionContext, String oA, String ans, Bitmap image) {
        this.questionContext = questionContext;
        this.oA = oA;
        this.ans = ans;
        this.image = image;
    }

    public String getQuestionContext() {
        return questionContext;
    }

    public void setQuestionContext(String questionContext) {
        this.questionContext = questionContext;
    }

    public String getoA() {
        return oA;
    }

    public void setoA(String oA) {
        this.oA = oA;
    }

    public String getoB() {
        return oB;
    }

    public void setoB(String oB) {
        this.oB = oB;
    }

    public String getoC() {
        return oC;
    }

    public void setoC(String oC) {
        this.oC = oC;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }
}
