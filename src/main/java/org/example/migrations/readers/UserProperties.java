package org.example.migrations.readers;

import lombok.*;

@Data
@Builder
public class UserProperties {
    private String url;
    private String username;
    private String password;
    private String driverName;

    public void checkProperties(UserProperties properties) {



    }
}
