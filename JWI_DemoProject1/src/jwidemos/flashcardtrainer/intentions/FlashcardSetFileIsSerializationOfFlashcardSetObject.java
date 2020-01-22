package jwidemos.flashcardtrainer.intentions;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import jwidemos.flashcardtrainer.FlashcardSet;

/*

intention FlashcardSetFileIsSerializationOfFlashcardSetObject {
    description {{
        Flashcard set files on disk are simply serializations of the FlashcardSet class.
        To create a FlashcardSet object based on a .fcs file on disk, use code such as
        the following: 
        
        <pre>
		ObjectInputStream in = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(sourceFile)));
		FlashcardSet fcs = (FlashcardSet) in.readObject();
		in.close();
		</pre>
    }}
}

*/


public class FlashcardSetFileIsSerializationOfFlashcardSetObject {
    // Dummy class for now
}
