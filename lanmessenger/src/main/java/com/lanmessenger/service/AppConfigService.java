package com.lanmessenger.service;

import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    public enum Visibility {
        PUBLIC, PRIVATE
    }

    private Visibility userListVisibility = Visibility.PUBLIC; // Default to public

    public Visibility getUserListVisibility() {
        return userListVisibility;
    }

    public void setUserListVisibility(Visibility visibility) {
        this.userListVisibility = visibility;
    }
}