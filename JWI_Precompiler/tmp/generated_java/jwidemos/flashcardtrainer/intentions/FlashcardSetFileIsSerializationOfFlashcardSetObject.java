package jwidemos.flashcardtrainer.intentions;


/* intention FlashcardSetFileIsSerializationOfFlashcardSetObject {
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
} */
