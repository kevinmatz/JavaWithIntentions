package jwidemos.flashcardtrainer;

import java.util.List;


/* intention QuizStateIntention {
    description {{
        Class QuizState maintains the state of the current quiz, i.e., the current
        session in which all of the flashcards in a flashcard set will be presented
        once. This class is responsible for keeping track of the current flashcard,
        the user's score, and the application's mode (whether a game is in progress
        or is stopped).
    }}

/*    
    requirementsreference[] satisficesRequirements = {
        EachFlashcardPresentedOncePerQuizSession,
        KeepScore
    };
    
    intentionreference playsRoleInPattern = FlashcardTrainerMVCPatternInstance;
*/

} */


public class QuizState /* implementsintention FlashcardTrainerMVCPatternInstance, QuizStateIntention */ {

	private ApplicationMode applicationMode;
	
	private FlashcardSet cardSetInUse;
	private int currentCardIndex;
    private int scoreSoFar;
    
    public QuizState() {
    	applicationMode = ApplicationMode.NO_FLASHCARD_SET_LOADED;
    }

    public ApplicationMode getApplicationMode() {
		return applicationMode;
	}

	public void setApplicationMode(ApplicationMode applicationMode) {
		this.applicationMode = applicationMode;
	}

	public FlashcardSet getCardSetInUse() {
		return cardSetInUse;
	}

	public void setCardSetInUse(FlashcardSet cardSetInUse) {
		this.cardSetInUse = cardSetInUse;
	}

	public int getCurrentCardIndex() {
		return currentCardIndex;
	}
	
	public void setCurrentCardIndex(int currentCardIndex) {
		this.currentCardIndex = currentCardIndex;
	}
	
	public int getScoreSoFar() {
		return scoreSoFar;
	}
	
	public void setScoreSoFar(int scoreSoFar) {
		this.scoreSoFar = scoreSoFar;
	}
	
	public void incrementScore() {
		scoreSoFar++;
	}
	
	/**
	 * Resets the QuizState object for re-use with a new flashcard set.
	 * 
	 * @param flashcardSet
	 */
	public void resetForNewCardSet(FlashcardSet flashcardSet) /* implementsrequirement ShuffleCards */ {
        setCardSetInUse(flashcardSet);
        flashcardSet.shuffle();
        setCurrentCardIndex(0);
        setScoreSoFar(0);
        setApplicationMode(ApplicationMode.QUIZ_IN_PROGRESS);
	}

	/**
	 * Returns the Flashcard object at the specified index in the list.
	 * 
	 * @param index
	 * @return
	 */
	public Flashcard getFlashcardAtIndex(int index) {
        if (cardSetInUse == null) {
        	return null;
        }
        
        List<Flashcard> list = cardSetInUse.getFlashcardList();
        if (list == null) {
        	return null;
        }
        
        try {
            return list.get(index);
        } catch (IndexOutOfBoundsException e) {
        	return null;
        }
	}

	/**
	 * Returns the current Flashcard object.
	 * 
	 * @return
	 */
	public Flashcard getCurrentFlashcard() {
		return getFlashcardAtIndex(currentCardIndex);
	}

}
