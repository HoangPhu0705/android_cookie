package tdtu.edu.cookie.Database.Entity;

public class TestModel {
    private String english, definition;

    public TestModel(String english, String definition) {
        this.english = english;
        this.definition = definition;
    }

    public TestModel() {
    }

    public String getEnglish() {
        return english;
    }

    public void setEnglish(String english) {
        this.english = english;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
