package com.github.nameManaging;

import java.io.IOError;
import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

public class ColorStyle {
    static public String defaultColor;
    static public String functionColor;
    static public String bracketColor;
    static public String globalVarColor;
    static public String intervalVarColor;
    static private Preferences pref;
    static private String style;
    public static void loadColorStyle(String styleName) throws IOException{
        pref = Gdx.app.getPreferences("styles");
        style = styleName;
        String[] styleArr = pref.getString(style).split(","); // TODO: define actual def value.
        if (styleArr.length < 5){
            defaultColor = "[#ffffff]";
            functionColor = "[#7f78aa]";
            bracketColor = "[#aaaaaa]";
            globalVarColor = "[#d0a343]";
            intervalVarColor = "[#666666]";
        } else {
            defaultColor = styleArr[0];
            functionColor = styleArr[1];
            bracketColor = styleArr[2];
            globalVarColor = styleArr[3];
            intervalVarColor = styleArr[4];
        }
    }
}
