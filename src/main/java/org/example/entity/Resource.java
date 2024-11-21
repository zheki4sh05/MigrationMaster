package org.example.entity;

import lombok.*;

@Data
@Builder
public class Resource {
    private String file;
    private Long checksum;

}
