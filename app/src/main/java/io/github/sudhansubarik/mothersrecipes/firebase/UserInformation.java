package io.github.sudhansubarik.mothersrecipes.firebase;

/**
 * Created by sudhansu on 18-Mar-18.
 */

public class UserInformation {

    public String name, email, dob, mobile;

    UserInformation() {
    }

    public UserInformation(String name, String email, String dob, String mobile) {
        this.name = name;
        this.email = email;
        this.dob = dob;
        this.mobile = mobile;
    }
}
