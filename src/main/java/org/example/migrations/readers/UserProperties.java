package org.example.migrations.readers;

import lombok.*;
import lombok.extern.slf4j.*;
import org.example.exceptions.*;

@Data
@Builder
public class UserProperties {
    private String url;
    private String username;
    private String password;
    private String driverName;
    private Boolean rollbackAll=true;
    private Integer retryTime=3000;
    private Integer rateLimiter=0;

    public static boolean checkProperties(UserProperties properties) throws ApplicationPropertiesException {
            if(properties.getUrl() ==null || properties.driverName==null || properties.username==null || properties.password==null){
                return false;
            }else {
                return true;
            }
    }

}
