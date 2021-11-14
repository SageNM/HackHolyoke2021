package com.hackholyoke.game;

import java.util.ArrayList;

public class Scene {
    private int id;
    private String speaker;
    private String[] dialogue;
    private int status = 0;

    public Scene(int id, String speaker, String dialogue) {
        // locations, squids, and dialogue should all be of the same length
        this.id = id;
        this.speaker = speaker;
        // Breaking down dialogue by line
        this.dialogue = dialogue.split("\n");
    }

    public boolean advance() {
        // If moved past dialogue, note so we can move to next scene
        if (status >= dialogue.length) {
            return true;
        } else {
            status++;
            return false;
        }
    }

    public String[] getCurrentScene() {
        String[] sceneData = new String[2];
        if (status >= dialogue.length) {
            return null;
        }
        sceneData[0] = speaker;
        sceneData[1] = dialogue[status];
        return sceneData;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getDialogue() {
        if (status >= dialogue.length) {
            return "";
        }
        return dialogue[status];
    }

    public int getId() {
        return this.id;
    }
}
