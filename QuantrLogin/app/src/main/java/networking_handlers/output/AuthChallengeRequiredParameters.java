package networking_handlers.output;

import androidx.annotation.Nullable;

public class AuthChallengeRequiredParameters {
    public String email, sessionID, challengeName, newPassword;

    public AuthChallengeRequiredParameters(String email, String sessionID, String challengeName, @Nullable String newPassword){
        this.email = email;
        this.sessionID = sessionID;
        this.challengeName = challengeName;
        this.newPassword = newPassword;
    }
}
