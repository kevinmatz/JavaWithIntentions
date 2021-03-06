package jwidemos.flashcardtrainer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/* intention FlashcardSetIntention {

    description {{
        The FlashcardSet class represents a set of flashcards.  The flashcards (Flashcard objects)
        are maintained in a particular order, but the flashcards are generally intended to be
        shuffled or randomized, so the term "set" is still reasonable.
        
        Note: The class implements Serializable so that FlashcardSets can be written to and read from
        files via the serialization mechanism.
    }}

/*    
    intentionreference[] seeAlso = {
        FlashcardSetFileFormat
    }
*/

} */


/**
 * Class to represent a set of flashcards.
 */
public class FlashcardSet implements Serializable /* implementsintention FlashcardSetIntention, FlashcardSetFileFormat */ {

	public static final String FLASHCARD_SET_FILE_EXTENSION = ".fcs";

	private static Random randomGenerator = new Random(); // (seed is based on time)

	private List<Flashcard> flashcardList;


	/**
	 * Constructor, for an empty flashcard set.
	 */
	public FlashcardSet() {
	    flashcardList = new ArrayList<Flashcard>();	
	}
	
	/**
	 * Constructor to create a FlashcardSet object from a List of Flashcard objects.
	 * 
	 * @param flashcardList
	 */
	public FlashcardSet(List<Flashcard> flashcardList) {
		this.flashcardList = flashcardList;
	}
	
	/**
	 * Returns the contents of this flashcard set as a List of Flashcard objects.
	 * 
	 * @return
	 */
	public List<Flashcard> getFlashcardList() {
		return flashcardList;
	}

	/**
	 * Setter to assign the contents of the flashcardList parameter to this FlashcardSet
	 * object (overwriting any existing contents of this FlashcardSet).
	 * 
	 * @param flashcardList
	 */
	public void setFlashcardList(List<Flashcard> flashcardList) {
		this.flashcardList = flashcardList;
	}
	
	/**
	 * Returns the number of flashcards in this flashcard set.
	 * 
	 * @return size of this flashcard set
	 */
	public int getSize() {
		if (this.flashcardList == null) {
			return 0;
		}
		
		return flashcardList.size();
	}
	
	/**
	 * Reads a .fcs flashcard set file from disk and returns a FlashcardSet object with the
	 * contents from the file.
	 * 
	 * @param sourceFile Filename of .fcs file to open
	 * @return FlashcardSet object containing contents of the input file
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static FlashcardSet readFromFile(File sourceFile) /* implementsintention FlashcardSetFileIsSerializatonOfFlashcardSetObject */
	        throws FileNotFoundException, IOException, ClassNotFoundException {
	
		ObjectInputStream in = new ObjectInputStream(
				new BufferedInputStream(new FileInputStream(sourceFile)));
		FlashcardSet fcs = (FlashcardSet) in.readObject();
		in.close();
		
		return fcs;
	}
	
	/**
	 * Writes the contents of this FlashcardSet object to disk using the supplied filename.
	 * If a file with the given filename already exists, it will be overwritten. 
	 * 
	 * @param destinationFilename
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void writeToFile(String destinationFilename) throws IOException, FileNotFoundException
        	/* implementsintention FlashcardSetFIleIsSerializatonOfFlashcardSetObject */ {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(destinationFilename));
		out.writeObject(this);
		out.close();
	}

	/**
	 * Shuffles (randomizes the order of) the flashcards in this flashcard set.
	 */
	public void shuffle() /* implementsrequirement ShuffleFlashcards */ {
        Flashcard tempCard = null;
        int otherIndex;
        
        if ((flashcardList != null) && (flashcardList.size() > 1)) {
            	

        	/* [[ _1 | Iterate through the list of flashcards ]] */
            for (int i = 0; i < flashcardList.size(); i++) {

            	/*
            	[[ _1_1 | Generate a random number, which will serve as the index of the
            	         card to be swapped with the current index ]] 
            	*/
                otherIndex = randomGenerator.nextInt(flashcardList.size());
                /*
                [[ /_1_1 ]] 
                */
                
                /*
                [[ _1_2 | Swap the records at indices i and otherIndex ]]
                */
                tempCard = (Flashcard) flashcardList.get(i);
                flashcardList.set(i, flashcardList.get(otherIndex));
                flashcardList.set(otherIndex, tempCard);
                /*
                [[ /_1_2 ]]
                */
            }
            /*
        	[[ /_1  ]]
        	*/
        }
	}

}
