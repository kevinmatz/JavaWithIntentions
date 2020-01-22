package jwidemos.flashcardtrainer.test;

import java.util.ArrayList;

import jwidemos.flashcardtrainer.Flashcard;
import jwidemos.flashcardtrainer.FlashcardSet;

public class CreateSampleFlashcardSetFile {

	public static void main(String[] args) {
		
		Flashcard fc01 = new Flashcard("cat", "die Katze");
		Flashcard fc02 = new Flashcard("dog", "der Hund");
		Flashcard fc03 = new Flashcard("house", "das Haus");
		Flashcard fc04 = new Flashcard("street", new String[] {"die Stra§e", "die Strasse"});
		
		ArrayList<Flashcard> list = new ArrayList<Flashcard>();
		
		list.add(fc01);
		list.add(fc02);
		list.add(fc03);
		list.add(fc04);
		
		FlashcardSet fcs = new FlashcardSet(list);
		
		try {
			fcs.writeToFile("rsrc/FlashcardSets/German1.fcs");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
