package jwidemos.flashcardtrainer;

import java.io.File;

import javax.swing.DefaultListModel;


/* intention QuizControllerIntention {
    description {{
        Controller of the MVC pattern.  Mediates between the View and Model so that neither is
        directly dependent on the other.  The user's actions in the View are transmitted to
        this class so that the Model can be updated.  
    }}
} */


public class QuizController /* implementsintention FlashcardTrainerMVCPatternInstance, QuizControllerIntention */ {

    private QuizFrame frame;
    private QuizState state;
    
    public QuizController(QuizFrame frame, QuizState state) {
    	this.frame = frame;
    	this.state = state;
    }
 
    public QuizState getState() {
    	return state;
    }
    
    /**
     * The QuizFrame can use this method to notify the controller that the user has selected
     * a file to open. The controller will open the flashcard set file and start a new quiz
     * session using that flashcard set.
     * 
     * @param selectedFile
     */
    public void startNewQuizSessionForFlashcardSetFile(File selectedFile) {
        
    	state.setApplicationMode(ApplicationMode.QUIZ_IN_PROGRESS);
        
    	try {    		
    	    FlashcardSet fcs = FlashcardSet.readFromFile(selectedFile);
    	    state.resetForNewCardSet(fcs);
    	    frame.presentCue(state.getCurrentFlashcard());
    	}
        catch (Exception e) {
        	// TODO Matz
        }
    }
    
    /**
     * Checks the correctness of the user's submitted answer, updates the user's score
     * appropriately, and updates the UI to present the correct/incorrect status and show
     * the acceptable answers.
     * 
     * @param usersAnswer
     * @param acceptableAnswersListModel
     */
    public void handleSubmissionOfAnswer(String usersAnswer, DefaultListModel acceptableAnswersListModel)
            /* implementsrequirement PresentCueAndAcceptAndCheckAnswer, KeepScore, FeedbackOnAcceptableAnswers */ {
		if (isUsersAnswerForCurrentFlashcardCorrect(usersAnswer)) {
			state.incrementScore();
			frame.handleCorrectAnswer(usersAnswer, acceptableAnswersListModel);
		} else {
			frame.handleIncorrectAnswer(usersAnswer, acceptableAnswersListModel);
		}
    }
    
    /**
     * Returns true if the user's answer matches one of the acceptable answers for
     * the current flashcard.
     * 
     * @param usersAnswer
     * @return true if the answer is deemed correct; false otherwise
     */
    public boolean isUsersAnswerForCurrentFlashcardCorrect(String usersAnswer)
            /* implementsrequirement MultipleAcceptableAnswersPerFlashcard, CheckCorrectnessOfAnswer */ {
        
    	Flashcard flashcard = state.getCurrentFlashcard();
        
    	if ((flashcard == null) || (usersAnswer == null)) {
    		return false;
    	}
    	
    	for (String s : flashcard.getAcceptableAnswers()) {
    		if (usersAnswer.equals(s)) {
    			return true;
    		}
    	}
    	
    	return false;
    }

    /**
     * This method should be called when the user clicks the "Next" button after viewing
     * the results of the checking of the previous answer. This method will cause the QuizState
     * to advance to the next card in the flashcard set, and will cause the UI to present that
     * flashcard, unless the end of the flashcard set has been reached, in which case the
     * end-of-quiz handling will be initiated (the score will be presented).
     */
    public void advanceToNextCard() {
    	
    	frame.clearResultPanel();
    	
    	int currentIndex = state.getCurrentCardIndex();
    
    	if (currentIndex + 1 < state.getCardSetInUse().getSize()) {
            state.setCurrentCardIndex(currentIndex + 1);
        	frame.presentCue(state.getCurrentFlashcard());
    	} else {
    		handleEndOfQuiz();
    	}
    }

    /**
     * Method to be called after the user has answered the last question in
     * the quiz session. Displays the user's score and disables fields on the screen.
     */
    public void handleEndOfQuiz() /* implementsintention DisplayScopeInPopUpDialog */ {
        frame.displayScorePopup(state.getScoreSoFar(), state.getCardSetInUse().getSize());
        frame.clearAndDisableAllFields();
    }

}
