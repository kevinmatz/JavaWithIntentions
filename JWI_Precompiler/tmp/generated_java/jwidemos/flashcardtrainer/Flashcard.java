package jwidemos.flashcardtrainer;

import java.io.Serializable;


/* intention FlashcardIntention {
    description {{
        The Flashcard class represents a flashcard, with a cue and a list of acceptable answers.
    }}
} */


public class Flashcard implements Serializable /* implementsintention ThisIsAFakeIntentionName */ {

	private String promptText;
	private String[] acceptableAnswers;
	
	public Flashcard(String promptText, String singleAnswer) {
		this.promptText = promptText;
		acceptableAnswers = new String[1];
		acceptableAnswers[0] = singleAnswer;
	}

	public Flashcard(String promptText, String[] acceptableAnswers) {
		this.promptText = promptText;
		this.acceptableAnswers = acceptableAnswers;
	}
	
	public String getPromptText() {
		return promptText;
	}
	
	public void setPromptText(String promptText) {
		this.promptText = promptText;
	}
	
	public String[] getAcceptableAnswers() {
		return acceptableAnswers;
	}
	
	public void setAcceptableAnswers(String[] acceptableAnswers) {
		this.acceptableAnswers = acceptableAnswers;
	}

	// TODO: Move logic to check answer against acceptable answers here
	
}
