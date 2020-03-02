package networking_handlers.output;

public class AuthChallengeRequiredParameters {
    public String email, sessionID, challengeName;

    public AuthChallengeRequiredParameters(String email, String sessionID, String challengeName){
        this.email = email;
        this.sessionID = sessionID;
        this.challengeName = challengeName;
    }
}
