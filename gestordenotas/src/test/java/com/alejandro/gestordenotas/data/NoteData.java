package com.alejandro.gestordenotas.data;

import java.util.Arrays;
import java.util.List;

import com.alejandro.gestordenotas.entities.Note;

// The class that contains the data to be mocked in the service and controller methods
public class NoteData {

    public static final List<Long> idsValid = Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);

    public static Note createNote001() {
        return new Note(idsValid.get(0), "This is the note No. 1");
    }

    public static Note createNote002() {
        return new Note(idsValid.get(1), "This is the note No. 2");
    }

    public static Note createNote003() {
        return new Note(idsValid.get(2), "This is the note No. 3");
    }

    public static Note createNote004() {
        return new Note(idsValid.get(3), "This is the note No. 4");
    }

    public static Note createNote005() {
        return new Note(idsValid.get(4), "This is the note No. 5");
    }

    public static Note createNote006() {
        return new Note(idsValid.get(5), "This is the note No. 6");
    }

    public static Note createNote007() {
        return new Note(idsValid.get(6), "This is the note No. 7");
    }

    public static Note createNote008() {
        return new Note(idsValid.get(7), "This is the note No. 8");
    }

    public static Note createNote009() {
        return new Note(idsValid.get(8), "This is the note No. 9");
    }

    public static Note createNote010() {
        return new Note(idsValid.get(9), "This is the note No. 10");
    }

    public static List<Note> createNotes001() {
        return Arrays.asList(createNote001());
    }

    public static List<Note> createNotes002() {
        return Arrays.asList(createNote002(), createNote003(), createNote004());
    }

    public static List<Note> createNotes003() {
        return Arrays.asList(createNote005(), createNote006());
    }

    public static List<Note> createNotes004() {
        return Arrays.asList(createNote007(), createNote008());
    }

    public static List<Note> createNotes005() {
        return Arrays.asList(createNote009(), createNote010());
    }
    
}
