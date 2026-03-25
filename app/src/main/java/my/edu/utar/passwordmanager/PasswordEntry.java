package my.edu.utar.passwordmanager;

public class PasswordEntry {
    private long id;
    private String site;
    private String username;
    private String password;
    private String extraPin;
    private String challengeQuestion;
    private String challengeAnswer;

    public PasswordEntry(long id, String site, String username, String password,
                         String extraPin, String challengeQuestion, String challengeAnswer){
        this.id = id;
        this.site = site;
        this.username = username;
        this.password = password;
        this.extraPin = extraPin;
        this.challengeQuestion = challengeQuestion;
        this.challengeAnswer = challengeAnswer;
    }

    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password){
        this.password = password;
    }

    public String getExtraPin() { return extraPin; }
    public void setExtraPin(String extraPin) { this.extraPin = extraPin; }

    public String getChallengeQuestion() { return challengeQuestion; }
    public void setChallengeQuestion(String challengeQuestion) { this.challengeQuestion = challengeQuestion; }

    public String getChallengeAnswer() { return challengeAnswer; }
    public void setChallengeAnswer(String challengeAnswer) { this.challengeAnswer = challengeAnswer; }

}
