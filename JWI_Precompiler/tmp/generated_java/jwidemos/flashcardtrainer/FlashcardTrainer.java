package jwidemos.flashcardtrainer;


/* intention FlashcardTrainerApplicationIntention {
    description {{
        The Flashcard Trainer program will be constructed as a simple Java Swing application.
    }}
} */

/* intention MainMethodIntention {
    description {{
        The main method sets up the components of the Model-View-Controller pattern instance
        that form the user interface and logic of the application, and starts the application.
    }}
} */


public class FlashcardTrainer /* implementsintention FlashcardTrainerApplicationIntention */ {

	public static void main(String[] args) /* implementsintention MainMethodIntention */ {
		
		/*
        [[ _1 | Instantiate and interlink the components of the main model-view-controller
               pattern for the application (@see FlashcardTrainerMVCPatternInstance) ]]
		*/
		
		QuizFrame frame = new QuizFrame();
		QuizState state = new QuizState();
		QuizController controller = new QuizController(frame, state);
		
		frame.setController(controller);
		
		/*
		[[ /_1 ]]
		*/

	}

}
