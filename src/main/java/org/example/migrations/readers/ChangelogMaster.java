package org.example.migrations.readers;

import java.util.regex.*;

import static org.example.settings.BaseSettings.nameVersionPatter;

public class ChangelogMaster {

    public static Boolean isCorrect(String fileName){
        Pattern pattern = Pattern.compile(nameVersionPatter);
        Matcher matcher =pattern.matcher(fileName);
        return matcher.find();
    }

    public static Integer getNumber(String fileName){
        Pattern pattern = Pattern.compile(nameVersionPatter);
        Matcher matcher =pattern.matcher(fileName);
        if (matcher.find()) { return Integer.parseInt(matcher.group(1)); } return 0;
    }



}
