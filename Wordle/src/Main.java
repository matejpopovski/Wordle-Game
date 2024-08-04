import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private JPanel boardPanel;
    private List<JTextField> textFields;
    private List<String> words;
    private String correctWord;
    private int currentRow;
    private boolean rowFilled;

    public Main() {
        setTitle("Wordle Game");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        loadWords();  // Load words from file
        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid words loaded. Check words.txt file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Exit if no words are loaded
        }

        correctWord = getRandomWord();  // Set a random correct word for the game
        currentRow = 0;  // Initialize row counter
        rowFilled = false;  // Track if the row is filled

        setUpUI();  // Set up the user interface
        setVisible(true);  // Show the window
    }

    private void loadWords() {
        words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 5) {
                    words.add(line.toLowerCase());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading words: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private String getRandomWord() {
        int index = (int) (Math.random() * words.size());
        return words.get(index);
    }

    private void setUpUI() {
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(6, 5, 5, 5));  // Create a 6x5 grid for guesses
        add(boardPanel, "Center");

        textFields = new ArrayList<>();  // Initialize the list of text fields

        // Add text fields for the Wordle grid
        for (int i = 0; i < 6 * 5; i++) {
            JTextField textField = new JTextField();
            textField.setHorizontalAlignment(JTextField.CENTER);
            textField.setFont(new Font("Arial", Font.BOLD, 20));
            textField.setEditable(true);
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    JTextField source = (JTextField) e.getSource();

                    if (rowFilled && (textFields.indexOf(source) / 5 == currentRow)) {
                        e.consume();  // Ignore input if the row is already filled
                        return;
                    }

                    if (source.getText().length() >= 1) {
                        e.consume();  // Consume the event if more than one character is typed
                        return;
                    }

                    // Move focus to the next text field
                    int index = textFields.indexOf(source);
                    if (index < 4 + (currentRow * 5)) {
                        JTextField nextField = textFields.get(index + 1);
                        nextField.requestFocus();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {
                    JTextField source = (JTextField) e.getSource();
                    int index = textFields.indexOf(source);

                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (rowFilled) {
                            handleGuess();  // Submit the guess if Enter is pressed
                        } else {
                            rowFilled = true;  // Mark the row as filled
                            // Disable editing for the current row
                            for (int i = currentRow * 5; i < (currentRow + 1) * 5; i++) {
                                textFields.get(i).setEditable(false);
                            }
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (source.getText().length() > 0) {
                            // Clear the current letter
                            source.setText("");
                        } else if (index > currentRow * 5) {
                            // Move focus to the previous text field if current field is empty
                            JTextField prevField = textFields.get(index - 1);
                            prevField.setText(""); // Clear the previous letter
                            prevField.requestFocus();
                        }
                        e.consume();  // Consume the event to prevent the default backspace behavior
                    }
                }
            });
            boardPanel.add(textField);
            textFields.add(textField);
        }
    }

    private void handleGuess() {
        if (currentRow >= 6) return;  // Do nothing if the game is over

        StringBuilder guess = new StringBuilder();
        for (int i = currentRow * 5; i < (currentRow + 1) * 5; i++) {
            JTextField textField = textFields.get(i);
            String text = textField.getText().trim().toLowerCase();
            if (text.length() != 1) {
                JOptionPane.showMessageDialog(this, "Each cell must contain exactly one letter.", "Error", JOptionPane.ERROR_MESSAGE);
                rowFilled = false;  // Reset rowFilled to allow further input
                return;
            }
            guess.append(text);
        }

        String guessStr = guess.toString();

        // Validate the guess
        if (guessStr.length() != 5) {
            JOptionPane.showMessageDialog(this, "Guess must be 5 letters long.", "Error", JOptionPane.ERROR_MESSAGE);
            rowFilled = false;  // Reset rowFilled to allow further input
            return;
        }

        if (!words.contains(guessStr)) {
            JOptionPane.showMessageDialog(this, "Invalid word.", "Error", JOptionPane.ERROR_MESSAGE);
            rowFilled = false;  // Reset rowFilled to allow further input
            return;
        }

        if (guessStr.equals(correctWord)) {
            JOptionPane.showMessageDialog(this, "Congratulations! You've guessed the word!", "Win", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
            return;
        }

        updateBoard(guessStr);
        currentRow++;  // Move to the next row

        if (currentRow >= 6) {
            JOptionPane.showMessageDialog(this, "Game over! The correct word was: " + correctWord, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        }

        // Reset the rowFilled flag for the new row
        rowFilled = false;
    }

    private void updateBoard(String guess) {
        for (int i = 0; i < 5; i++) {
            JTextField textField = textFields.get(currentRow * 5 + i);
            char ch = guess.charAt(i);
            textField.setText(String.valueOf(ch));

            if (ch == correctWord.charAt(i)) {
                textField.setBackground(Color.GREEN);  // Correct letter in correct position
            } else if (correctWord.indexOf(ch) != -1) {
                textField.setBackground(Color.YELLOW);  // Correct letter in wrong position
            } else {
                textField.setBackground(Color.GRAY);  // Incorrect letter
            }
        }
    }

    private void resetGame() {
        currentRow = 0;
        rowFilled = false;
        for (int i = 0; i < 6 * 5; i++) {
            JTextField textField = textFields.get(i);
            textField.setText("");
            textField.setBackground(Color.WHITE);  // Reset background color
            textField.setEditable(true);  // Reset editability
        }
        correctWord = getRandomWord();  // Select a new word for the next game
    }

    public static void main(String[] args) {
        new Main();
    }
}
