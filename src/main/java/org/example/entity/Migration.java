package org.example.entity;

import lombok.*;

import java.sql.*;
@Data
@Builder
@ToString
public class Migration {
    private String script;
    private Long checksum;
    private Timestamp executed;
    private String state;
    private Boolean locked;

}
