package naverAPI.demo.domain;

import java.time.LocalDateTime;

public class User {
    private String username;
    private String nickname;
    private String email;
    private LocalDateTime birth;
    private String code;
    private String state;

    public User(String code, String state) {
        this.code = code;
        this.state = state;
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", birth=" + birth +
                ", code='" + code + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
