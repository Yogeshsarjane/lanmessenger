package com.lanmessenger.service;

import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    public enum Visibility {
        PUBLIC,
        PRIVATE
    }

    // Default visibility is PUBLIC
    private Visibility userListVisibility = Visibility.PUBLIC;

    public Visibility getUserListVisibility() {
        return userListVisibility;
    }

    public void setUserListVisibility(Visibility visibility) {
        this.userListVisibility = visibility;
        System.out.println("User list visibility set to: " + visibility);
    }
}
