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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {

    private JPanel boardPanel;
    private JTextField guessInput;
    private JButton submitButton;
    private List<String> words;
    private String correctWord;
    private int currentRow;

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

        // Add text fields for the Wordle grid
        for (int i = 0; i < 6 * 5; i++) {
            JTextField textField = new JTextField();
            textField.setEditable(false);
            textField.setHorizontalAlignment(JTextField.CENTER);
            textField.setFont(new Font("Arial", Font.BOLD, 20));
            boardPanel.add(textField);
        }

        JPanel inputPanel = new JPanel(new FlowLayout());
        guessInput = new JTextField(5);
        submitButton = new JButton("Submit");

        inputPanel.add(new JLabel("Enter guess:"));
        inputPanel.add(guessInput);
        inputPanel.add(submitButton);

        add(inputPanel, "South");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleGuess();
            }
        });
    }

    private void handleGuess() {
        String guess = guessInput.getText().toLowerCase();

        if (guess.length() != 5) {
            JOptionPane.showMessageDialog(this, "Guess must be 5 letters long.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!words.contains(guess)) {
            JOptionPane.showMessageDialog(this, "Invalid word.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (guess.equals(correctWord)) {
            JOptionPane.showMessageDialog(this, "Congratulations! You've guessed the word!", "Win", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
            return;
        }

        updateBoard(guess);
        guessInput.setText("");  // Clear the input field

        if (currentRow >= 6) {
            JOptionPane.showMessageDialog(this, "Game over! The correct word was: " + correctWord, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        }
    }

    private void updateBoard(String guess) {
        for (int i = 0; i < 5; i++) {
            JTextField textField = (JTextField) boardPanel.getComponent(currentRow * 5 + i);
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

        currentRow++;
    }

    private void resetGame() {
        currentRow = 0;
        guessInput.setText("");
        for (int i = 0; i < 6 * 5; i++) {
            JTextField textField = (JTextField) boardPanel.getComponent(i);
            textField.setText("");
            textField.setBackground(Color.WHITE);  // Reset background color
        }
        correctWord = getRandomWord();  // Select a new word for the next game
    }

    public static void main(String[] args) {
        new Main();
    }
}
