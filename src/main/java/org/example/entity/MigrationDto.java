package org.example.entity;

import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
public class MigrationDto {
    private List<Migration> migrationList;
}
