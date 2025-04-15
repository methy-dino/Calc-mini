package com.github.basics;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
public class VarStorage{
    final private HashMap<String, String> vars;
    //vars.put("a", "12");
    final private FileHandle file;
    final private Json json;
    final private com.badlogic.gdx.Preferences prefs;
    final private String prefKey;
    public VarStorage(String filePath, Json writer) {
        json = writer;
        prefs = null;
        prefKey = null;
        file = Gdx.files.local(filePath);
        System.out.print("Importing variables...  ");
        vars = json.fromJson(HashMap.class, file);
        System.out.println("Importing sucessful!");
    }
    public VarStorage(String prefName, String key, Json writer){
        json = writer;
        prefs = Gdx.app.getPreferences(prefName);
        prefKey = key;
        file = null;
        System.out.print("Importing variables...  ");
        vars = json.fromJson(HashMap.class, String.class, prefs.getString(prefKey, "{}"));
        System.out.println("Importing sucessful!");
    }
    public Set<String> Keys(){
        return vars.keySet();
    }
    public boolean has(String target){
        return vars.containsKey(target);
    }
    public String getVar(String target) {
        return vars.get(target);
    }
    public void SaveVars(){
        System.out.print("saving vars...");
        if (prefs != null){
            prefs.putString(prefKey, json.prettyPrint(vars));
            prefs.flush();
        } else {
            file.writeString(json.prettyPrint(vars), false);
        }
        System.out.println(" Save was sucessful!");
    }
    public void setVar(String target, String value){
            vars.put(target, value);
    }

    public void deleteVar(String toDelete) {
            vars.remove(toDelete);
    }
}
