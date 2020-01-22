package jwidemos.flashcardtrainer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.Border;
import javax.swing.filechooser.FileFilter;


/* intention QuizFrameIntention {
    
    description {{
        A subclass of Swing's JFrame class is used to represent the application's window containing the
        user interface controls.
    }}

/*    
    requirementsreference[] implementsRequirements = {
        UseGUI,
        AppearInOwnWindow,
        UseResizableFloatingLayout    
    }
*/

} */


public class QuizFrame extends JFrame
    /* implementsintention FlashcardTrainerMVCPatternInstance, QuizFrameIntention */ {

	private QuizController controller;
	
	private JMenuBar menuBar = new JMenuBar();

	private JFileChooser fileChooser = new JFileChooser(/* DEFAULT_DIRECTORY */);

	private FileAction openAction = new FileAction("Open...", KeyStroke.getKeyStroke(
			'O', Event.CTRL_MASK));
    private HelpAction aboutAction = new HelpAction("About...");


	/** 
	 * Text to show in the status bar when a quiz is in progress. If no text is desired,
	 * use " " instead of an empty string, as an empty string will cause Swing to collapse
	 * the status bar.
	 */
	private final String NO_PRESENTATION_IN_PROGRESS_LABEL_TEXT = " ";
	
	private JLabel presentationIndicator = new JLabel(NO_PRESENTATION_IN_PROGRESS_LABEL_TEXT);

	private JLabel cuePanelLabel = new JLabel("Cue");
	private JPanel cuePanel = new JPanel();
	private JLabel cuePanelText = new JLabel("");

	private JLabel answerFieldLabel = new JLabel("Answer");
	private JTextField answerField = new JTextField();

	private JLabel resultPanelLabel = new JLabel("Result");
	private JPanel resultPanel = new JPanel();

	private JButton nextButton = new JButton("Next");

	private AnswerFieldListener answerFieldListener = new AnswerFieldListener();
	private NextButtonListener nextButtonListener = new NextButtonListener();


    /**
     * Constructor.
     */
	public QuizFrame() {
		
		setTitle("Vocabulary Trainer");
		
		setJMenuBar(menuBar);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		setupPullDownMenus();
		setupDataEntryPane();
	}

	/**
	 * Setter method for "injecting" an instance of QuizController.
	 * 
	 * @param controller
	 */
	public void setController(QuizController controller) {
		this.controller = controller;
	}
	
	/**
	 * Sets up the application window's pull-down menus and associates the menu entries with actions.
	 */
	private void setupPullDownMenus() /* implementsintention PullDownMenus, OpenFlashcardSetThroughFileMenu */ {

		/*
		[[ _1 | Construct the File menu ]]
		*/
		
		JMenu fileMenu = new JMenu("File");

		fileMenu.setMnemonic('F');

		JMenuItem tempItem = null; // Only used for setting accelerators

		tempItem = fileMenu.add(openAction);
		tempItem.setAccelerator(KeyStroke.getKeyStroke('O', Event.CTRL_MASK));
		
		/*
	    [[ /_1 ]]
		*/

		/*
		[[ _2 | Construct the Help menu ]]
		*/

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        helpMenu.add(aboutAction);

        /*
	    [[ /_2 ]]
		*/
        
		/*
		[[ _3 | Add the File and Help menus to the menu bar ]]
		*/       

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        
		/*
	    [[ /_3 ]]
		*/

	}

	/**
	 * Sets up the screen layout by placing UI components in the application window.
	 */
	private void setupDataEntryPane() /* implementsintention BasicScreenLayout */ {
		
		/*
		[[ _1 | Create three panels (1. the status panel at the top, 2. the input panel at
		       the center, and 3. the control panel at the bottom), and insert them into
		       the window ]]
		*/
		
		JPanel statusPanel = new JPanel();
		JPanel inputPanel = new JPanel();
		JPanel controlPanel = new JPanel();

		Container contentPane = getContentPane();

		contentPane.add(statusPanel, BorderLayout.NORTH);
		contentPane.add(inputPanel, BorderLayout.CENTER);
		contentPane.add(controlPanel, BorderLayout.SOUTH);

		/*
		[[ /_1 ]]
		*/
		

		/*
		[[ _2 | Set up the status panel at the top of the window ]]
        */
	
		GridBagLayout statusPanelGridBag = new GridBagLayout();
		statusPanel.setLayout(statusPanelGridBag);
		
		GridBagConstraints constraints = new GridBagConstraints();

		Border edge2 = BorderFactory.createRaisedBevelBorder();
		statusPanel.setBorder(edge2);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.EAST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 10;
		constraints.weighty = 10;
		statusPanelGridBag.setConstraints(presentationIndicator, constraints);
		statusPanel.add(presentationIndicator);

        /*
        [[ /_2 ]]
        */
  
		/*
		[[ _3 | Add the cue, answer, and result fields and labels to the input panel,
		       which occupies the center of the window ]] 
        */

		GridBagLayout inputPanelGridBag = new GridBagLayout();
		inputPanel.setLayout(inputPanelGridBag);

		final int verticalSeparation = 10;
		Border edge = BorderFactory.createEtchedBorder();

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.NONE;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 10;
		constraints.weighty = 30;
		inputPanelGridBag.setConstraints(cuePanelLabel, constraints);
		inputPanel.add(cuePanelLabel);

		cuePanel.setBorder(edge);
		cuePanel.setBackground(Color.gray);

		cuePanel.setMinimumSize(new Dimension(300, 60));
		cuePanel.setPreferredSize(new Dimension(300, 60));
		
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 80;
		constraints.weighty = 30;
		inputPanelGridBag.setConstraints(cuePanel, constraints);
		inputPanel.add(cuePanel);

		cuePanelText.setText("");
        // cuePanelText.setForeground(cuePanelTextColor);
        // cuePanelText.setFont(cuePanelTextFont);

		/*
        [[ _3_1 | Insert the "cuePanelText" component into the "cuePanel" panel.
		         To get proper vertical centering within the "cuePanel" panel,
	          it appears that we must use another GridBagLayout layout manager. ]]
		*/

		GridBagLayout cuePanelGridBag = new GridBagLayout();
		cuePanel.setLayout(cuePanelGridBag);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 1;
		constraints.weighty = 1;
		inputPanelGridBag.setConstraints(cuePanelText, constraints);
		cuePanel.add(cuePanelText);

		/*
        [[ /_3_1 ]]
        */
		
		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 10;
		constraints.weighty = 10;
		inputPanelGridBag.setConstraints(answerFieldLabel, constraints);
		inputPanel.add(answerFieldLabel);

		answerField.setMinimumSize(new Dimension(300, 25));
		answerField.setPreferredSize(new Dimension(300, 25));

		constraints.gridx = 1;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 80;
		constraints.weighty = 10;
		inputPanelGridBag.setConstraints(answerField, constraints);
		inputPanel.add(answerField);		
		
		answerField.addActionListener(answerFieldListener);

		constraints.gridx = 0;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 10;
		constraints.weighty = 50;
		inputPanelGridBag.setConstraints(resultPanelLabel, constraints);
		inputPanel.add(resultPanelLabel);

		resultPanel.setBorder(edge);
		resultPanel.setBackground(Color.gray);

		resultPanel.setMinimumSize(new Dimension(300, 130));
		resultPanel.setPreferredSize(new Dimension(300, 130));

		constraints.gridx = 1;
		constraints.gridy = 2;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.WEST;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 80;
		constraints.weighty = 50;
		inputPanelGridBag.setConstraints(resultPanel, constraints);
		inputPanel.add(resultPanel);

		/*
		[[ /_3 ]]
		*/
		
		/*
		[[ _4 | Add control buttons to the control panel ]]
		*/

		Box controlPanelBox = Box.createHorizontalBox();

		controlPanelBox.add(nextButton);
		nextButton.setMnemonic('N');

		controlPanel.add(controlPanelBox);

		nextButton.addActionListener(nextButtonListener);

		/*
		[[ /_4 ]]		
		*/

		/*
		[[ _5 | Make the window visible ]]
		*/	

		pack();
		setSize(getPreferredSize());
		show();

		setVisible(true);

		/*
		[[ /_5 ]]		
		*/
	}


	/**
	 * Updates the display after the user has entered a correct answer.
	 * 
	 * @param answerText Answer text entered by the user
	 * @param acceptableAnswers List of acceptable answers for the flashcard
	 */
	public void handleCorrectAnswer(String answerText, DefaultListModel acceptableAnswers) {
		
		/* [[ _1 | Color field backgrounds green, display the text "Correct!", and
		          display the list of acceptable answers ]] */
		
		answerField.setBackground(Color.green);

		resultPanel.removeAll();

		// Use the GridBagLayout layout manager:
		GridBagLayout resultPanelGridBag = new GridBagLayout();
		resultPanel.setLayout(resultPanelGridBag);
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel correctLabelPanel = new JPanel();
		correctLabelPanel.setBackground(Color.black);

		JLabel correctLabel = new JLabel("Correct!");
		correctLabel.setForeground(Color.green);

		correctLabelPanel.add(correctLabel);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 10;
		resultPanelGridBag.setConstraints(correctLabelPanel, constraints);
		resultPanel.add(correctLabelPanel);

		JList acceptableAnswersList = new JList(acceptableAnswers);
		acceptableAnswersList.setVisibleRowCount(4);
		JScrollPane acceptableAnswersListScrollPane = new JScrollPane(
				acceptableAnswersList);
		acceptableAnswersList.setVisibleRowCount(4);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 1;
		constraints.weighty = 70;
		resultPanelGridBag.setConstraints(acceptableAnswersListScrollPane,
				constraints);
		
		resultPanel.add(acceptableAnswersListScrollPane);

		/* [[ /_1 ]] */

		/* [[ _2 | Temporarily disable the "Answer" field ]] */
		disableAnswerField();
		/* [[ /_2 ]] */

		/* [[ _3 | Enable the "Next" button and switch the focus to that button ]] */
		nextButton.setEnabled(true);
		nextButton.requestFocus();
		/* [[ /_3 ]] */

		/* [[ _4 | Refresh the status panel (updates the count of flashcards presented so far) ]] */
		refreshStatusPanel();
		setVisible(true);
		/* [[ /_4 ]] */
	}


	/**
	 * Updates the display after the user has entered an incorrect answer.
	 * 
	 * @param answerText Answer text entered by the user
	 * @param acceptableAnswers List of acceptable answers for the flashcard
	 */
	public void handleIncorrectAnswer(String answerText, DefaultListModel acceptableAnswers) {

		/* [[ _1 | Color field backgrounds red, display the text "Incorrect...", and
                  display the list of acceptable answers ]] */

		answerField.setBackground(Color.red);

		resultPanel.removeAll();

		// Use the GridBagLayout layout manager:
		GridBagLayout resultPanelGridBag = new GridBagLayout();
		resultPanel.setLayout(resultPanelGridBag);
		GridBagConstraints constraints = new GridBagConstraints();

		JPanel correctLabelPanel = new JPanel();
		correctLabelPanel.setBackground(Color.black);

		JLabel correctLabel = new JLabel("Incorrect...");
		correctLabel.setForeground(Color.red);

		correctLabelPanel.add(correctLabel);

		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTH;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1;
		constraints.weighty = 10;
		resultPanelGridBag.setConstraints(correctLabelPanel, constraints);
		resultPanel.add(correctLabelPanel);

		JList acceptableAnswersList = new JList(acceptableAnswers);
		acceptableAnswersList.setVisibleRowCount(4);
		JScrollPane acceptableAnswersListScrollPane = new JScrollPane(
				acceptableAnswersList);
		acceptableAnswersList.setVisibleRowCount(4);

		constraints.gridx = 0;
		constraints.gridy = 1;
		constraints.gridwidth = 1;
		constraints.gridheight = 1;
		constraints.fill = GridBagConstraints.BOTH;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(5, 5, 5, 5);
		constraints.weightx = 1;
		constraints.weighty = 70;
		resultPanelGridBag.setConstraints(acceptableAnswersListScrollPane,
				constraints);

		resultPanel.add(acceptableAnswersListScrollPane);

		/* [[ /_1 ]] */
		
		/* [[ _2 | Detach the "answerFieldListener" from the "answerField" component, so the
		          user cannot press <ENTER> in the field again. (I'd like to simply disable
		          "answerField" with "answerField.setEnabled (false);", and this works, but
		          it changes the foreground color so that the text is hard to read with a
                  red background...) ]] */
		disableAnswerField();

		nextButton.setEnabled(true);
		nextButton.requestFocus();

		setVisible(true);
        /* [[ /_2 ]] */
	}

    
	private File operateFileSelectionDialog(String dialogTitle,
			String approveButtonText, String approveButtonTooltip,
			char approveButtonMnemonic, File initialSelectedFile) /* implementsintention OpenFlashcardSetThroughFileMenu */ {
		
		fileChooser.setDialogTitle(dialogTitle);
		fileChooser.setApproveButtonText(approveButtonText);
		fileChooser.setApproveButtonToolTipText(approveButtonTooltip);
		fileChooser.setFileSelectionMode(fileChooser.FILES_ONLY);
		fileChooser.rescanCurrentDirectory();
		fileChooser.setSelectedFile(initialSelectedFile);

		ExtensionFilter fileFilter = new ExtensionFilter(
				FlashcardSet.FLASHCARD_SET_FILE_EXTENSION, "Flashcard Set files ("
						+ FlashcardSet.FLASHCARD_SET_FILE_EXTENSION + ")");
		fileChooser.addChoosableFileFilter(fileFilter);
		fileChooser.setFileFilter(fileFilter);

		// Open and operate the dialog, and get the result code:
		int result = fileChooser.showDialog(this, null);

		if (result == fileChooser.APPROVE_OPTION) {
			return (fileChooser.getSelectedFile());
		} else {
			return null;
		}
	}

	private void performOpenOperation() /* implementsintention OpenFlashcardSetThroughFileMenu */ {	
		File selectedFile = operateFileSelectionDialog("Open Flashcard Set",
				"Open", "Load the selected flashcard set", 'O', null);

		if (selectedFile != null) {
			controller.startNewQuizSessionForFlashcardSetFile(selectedFile);
		}
	}

	private void disableAnswerField() {
		// Detach the "answerFieldListener" from the "answerField"
		// component, so the user cannot press <ENTER> in the field again.
		// (I'd like to simply disable "answerField" with
		// "answerField.setEnabled (false);", and this works, but it changes
		// the foreground color so that the text is hard to read with a
		// red background...)
		
		answerField.setEnabled(false);
	}

	private void enableAnswerField() {
		answerField.setEnabled(true);
	}

	/**
	 * Updates the UI to present the cue for the given flashcard. The "Answer" field
	 * is set up to accept the user's response.
	 * 
	 * @param flashcard
	 */
	public void presentCue(Flashcard flashcard) /* implementsrequirement PresentCueAndAcceptAndCheckAnswer */ {

		// Show the cue text:
		cuePanelText.setText(flashcard.getPromptText());

		// Clear out the answer field:
		answerField.setText("");
		answerField.setBackground(Color.white);
		enableAnswerField();
		
		// Clear out the Result panel here...
		resultPanel.removeAll();

		// Temporarily disable (gray-out) the "Next" button -- we
		// want the user to enter an answer before he/she can proceed:
		nextButton.setEnabled(false);

		// Refresh the status panel:
		refreshStatusPanel();
	}

	private void refreshStatusPanel() {
		
		/*
		[[ _1 | Display the "Presentation" counter; the presentation number is based on the
		       current card index, but 1 is added so that the first presentation is 1, not 0 ]]
		*/
		
		String tempString = "Presentation " + (controller.getState().getCurrentCardIndex() + 1) + " of " + controller.getState().getCardSetInUse().getSize();
		presentationIndicator.setText(tempString);

		/*
		[[ /_1 ]]
		*/
	}

	private void displayClearedStatusPanel() {
		presentationIndicator.setText(NO_PRESENTATION_IN_PROGRESS_LABEL_TEXT);
	}

	/**
	 * Clears and disables all fields.
	 */
	public void clearAndDisableAllFields() {
		presentationIndicator.setText(NO_PRESENTATION_IN_PROGRESS_LABEL_TEXT);

		cuePanelText.setText("");

		answerField.setText("");
		answerField.setBackground(Color.white);
		disableAnswerField();

		clearResultPanel();

		nextButton.setEnabled(false);
	}

	/**
	 * Clears the results panel.
	 */
	public void clearResultPanel() {
		resultPanel.removeAll();
		resultPanel.validate();
		resultPanel.repaint();
	}
	
	/**
	 * Displays a pop-up indicating the user's score for a quiz session.
	 * 
	 * @param score
	 * @param numberOfCards
	 */
	public void displayScorePopup(int score, int numberOfCards) /* implementsintention DisplayScoreInPopUpDialog */ {
		JOptionPane.showMessageDialog(this,
				"You answered " + score + " out of " + numberOfCards + " correctly.",
				"Quiz completed",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Shows the "About" dialog.
	 */
	public void showAboutDialog()
    {
        String[] messageText = {
            "Vocabulary Trainer 1.0",   // TODO: Version number as constant
            "A simple Java With Intentions demo project",
            " ",
            "Copyright 2010-2011, Kevin Matz, All Rights Reserved.",
            " ",
            "Contact: kevin@kevinmatz.com",
        };

        JOptionPane.showMessageDialog(null, messageText, "About",
            JOptionPane.INFORMATION_MESSAGE);
    }

	public class AnswerFieldListener implements ActionListener /* implementsintention BasicScreenLayout */ /* implementsrequirement PresentCueAndAcceptAndCheckAnswer */ {
		public void actionPerformed(ActionEvent e) {

			if (! ApplicationMode.QUIZ_IN_PROGRESS.equals(controller.getState().getApplicationMode())) {
				return;
			}
		
			DefaultListModel answersListModel = new DefaultListModel();
			for (String s : controller.getState().getCurrentFlashcard().getAcceptableAnswers()) {
				answersListModel.addElement(s);
			}

			String usersAnswer = answerField.getText().trim();
			
			controller.handleSubmissionOfAnswer(usersAnswer, answersListModel);
		}
	}

	public class NextButtonListener implements ActionListener /* implementsintention BasicScreenLayout */ {
		public void actionPerformed(ActionEvent e) {
			
			if (ApplicationMode.QUIZ_IN_PROGRESS.equals(controller.getState().getApplicationMode())) {
				controller.advanceToNextCard();
			}
		}
	}

	public class FileAction extends AbstractAction /* implementsintention PullDownMenus */ {
		public FileAction(String name) {
			super(name);
		}

		public FileAction(String name, KeyStroke keystroke) {
			this(name);
			if (keystroke != null) {
				putValue(ACCELERATOR_KEY, keystroke);
			}

		}

		public void actionPerformed(ActionEvent e) {
			// Get the name of the File menu option selected:
			String name = (String) getValue(NAME);

			if (name.equals(openAction.getValue(NAME))) {
				performOpenOperation();
			}
		}
	}

    public class HelpAction extends AbstractAction /* implementsintention PullDownMenus */ {
        
    	public HelpAction(String name) {
            super(name);
        }

        public HelpAction(String name, KeyStroke keystroke) {
            this(name);
            if (keystroke != null) {
                putValue(ACCELERATOR_KEY, keystroke);
            }
        }

        public void actionPerformed (ActionEvent e) {
            // Get the name of the Help menu option selected:
            String name = (String) getValue(NAME);

            if (name.equals(aboutAction.getValue(NAME))) {
                showAboutDialog();
            }
        }
    }

}



public class ExtensionFilter extends FileFilter /* implementsintention OpenFlashcardSetThroughFileMenu */ /* , FlashcardSetFilesHaveFCSExtension */ {

	private String extension;
    private String desc;
    
    public ExtensionFilter(String ext, String descr) {
        extension = ext.toLowerCase();
        desc = descr;
    }

    public boolean accept(File file) {
        return (file.isDirectory() ||
            file.getName().toLowerCase().endsWith(extension));
    }

    public String getDescription() {
        return desc;
    }

}

