package jwidemos.flashcardtrainer;


/* intention ApplicationModeIntention {
	description {{
	    Enumerated type to indicate the general state of the application (e.g.,
	    no flashcard set has yet been loaded, a flashcard set has been loaded
	    and a quiz is in progress, or a quiz has ended).
	}}
} */


public enum ApplicationMode /* implementsintention ApplicationModeIntention */ {

	NO_FLASHCARD_SET_LOADED,
	QUIZ_IN_PROGRESS,
	QUIZ_OVER;

}
