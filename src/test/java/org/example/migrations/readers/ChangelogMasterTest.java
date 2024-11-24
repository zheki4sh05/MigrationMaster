package org.example.migrations.readers;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ChangelogMasterTest {

    @Test
    public void test_is_correct_true(){
        assertTrue(ChangelogMaster.isCorrect("V1__test.sql"));
    }

    @Test
    public void test_is_correct_false(){
        assertFalse(ChangelogMaster.isCorrect("V__test.sql"));
        assertFalse(ChangelogMaster.isCorrect("1_test.sql"));
        assertFalse(ChangelogMaster.isCorrect("M1_test.sql"));
    }

    @Test
    public void test_number_correct(){
        assertEquals(ChangelogMaster.getNumber("V1__test.sql"), 1);
    }

    @Test
    public void test_number_not_correct(){
        assertNotEquals(ChangelogMaster.getNumber("V1__test.sql"), 2);
    }


}