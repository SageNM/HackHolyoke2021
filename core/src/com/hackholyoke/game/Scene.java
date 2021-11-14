package com.hackholyoke.game;

import java.util.ArrayList;

public class Scene {
    private int id;
    private String speaker;
    private ArrayList<String[]> dialogue = new ArrayList<String[]>();
    private int status = 0;
    private int textStatus = 0;

    public Scene(int id, String speaker, String dialogue) {
        // locations, squids, and dialogue should all be of the same length
        this.id = id;
        this.speaker = speaker;
        // Breaking down dialogue by line
        this.dialogue.add(dialogue.split("\n"));
    }

    public boolean advance() {
        // If moved past dialogue, note so we can move to next scene
        if (status >= dialogue.size() || ((textStatus + 1) >= dialogue.get(status).length && status == dialogue.size() - 1)) {
            return true;
        }
        String[] currentDialogue = dialogue.get(status);
        // if at last dialogue of section, advance section
        if (textStatus + 1 >= currentDialogue.length) {
            status++;
            textStatus = 0;
            currentDialogue = dialogue.get(status);
        } else {
            textStatus++;
        }
        return false;
    }

    public String[] getCurrentScene() {
        String[] sceneData = new String[2];

        if (status >= dialogue.size()) {
            return null;
        }

        String[] currentDialogue = dialogue.get(status);
        sceneData[0] = speaker;
        sceneData[1] = currentDialogue[textStatus];

        return sceneData;
    }

    public int getId() {
        return this.id;
    }
}
