package org.example.entity;

import lombok.*;

import java.sql.*;
@Data
@Builder
public class Migration {
    private String script;
    private Long checksum;
    private Timestamp executed;
    private String state;
    private Boolean locked;

    @Override
    public String toString() {
        return "Migration{" +
                "script='" + script + '\'' +
                ", checksum=" + checksum +
                ", executed=" + executed +
                ", state='" + state + '\'' +
                ", locked=" + locked +
                '}';
    }
}
