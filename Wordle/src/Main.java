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
    private int currentColumn;

    public Main() {
        setTitle("Wordle Game");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadWords();
        if (words.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No valid words loaded. Check words.txt file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        correctWord = getRandomWord();
        currentRow = 0;
        currentColumn = 0;
        rowFilled = false;

        setUpUI();
        setVisible(true);
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
        boardPanel.setLayout(new GridLayout(6, 5, 5, 5));
        add(boardPanel, "Center");

        textFields = new ArrayList<>();

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
                        e.consume();
                        return;
                    }

                    if (source.getText().length() >= 1) {
                        e.consume();
                        return;
                    }

                    int index = textFields.indexOf(source);
                    if (index < 4 + (currentRow * 5)) {
                        currentColumn++;
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
                            handleGuess();
                        } else {
                            rowFilled = true;
                            for (int i = currentRow * 5; i < (currentRow + 1) * 5; i++) {
                                textFields.get(i).setEditable(false);
                            }
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                        if (source.getText().length() > 0) {
                            source.setText("");
                            currentColumn--;
                        } else if (index > currentRow * 5) {
                            JTextField prevField = textFields.get(index - 1);
                            prevField.setText("");
                            prevField.requestFocus();
                            currentColumn--;
                        }
                        e.consume();
                    }
                }
            });
            boardPanel.add(textField);
            textFields.add(textField);
        }
    }

    private void handleGuess() {
        if (currentRow >= 6) return;

        StringBuilder guess = new StringBuilder();
        for (int i = currentRow * 5; i < (currentRow + 1) * 5; i++) {
            JTextField textField = textFields.get(i);
            String text = textField.getText().trim().toLowerCase();
            if (text.length() != 1) {
                JOptionPane.showMessageDialog(this, "Each cell must contain exactly one letter.", "Error", JOptionPane.ERROR_MESSAGE);
                rowFilled = false;
                return;
            }
            guess.append(text);
        }

        String guessStr = guess.toString();

        if (guessStr.length() != 5 || !words.contains(guessStr)) {
            JOptionPane.showMessageDialog(this, "Invalid word.", "Error", JOptionPane.ERROR_MESSAGE);
            rowFilled = false;
            for (int i = currentRow * 5; i < (currentRow + 1) * 5; i++) {
                textFields.get(i).setEditable(true);
            }
            return;
        }

        if (guessStr.equals(correctWord)) {
            JOptionPane.showMessageDialog(this, "Congratulations! You've guessed the word!", "Win", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
            return;
        }

        updateBoard(guessStr);
        currentRow++;
        currentColumn = 0;

        if (currentRow >= 6) {
            JOptionPane.showMessageDialog(this, "Game over! The correct word was: " + correctWord, "Game Over", JOptionPane.INFORMATION_MESSAGE);
            resetGame();
        }

        rowFilled = false;
    }

    private void updateBoard(String guess) {
        for (int i = 0; i < 5; i++) {
            JTextField textField = textFields.get(currentRow * 5 + i);
            char ch = guess.charAt(i);
            textField.setText(String.valueOf(ch));

            if (ch == correctWord.charAt(i)) {
                textField.setBackground(Color.GREEN);
            } else if (correctWord.indexOf(ch) != -1) {
                textField.setBackground(Color.YELLOW);
            } else {
                textField.setBackground(Color.GRAY);
            }
        }
    }

    private void resetGame() {
        currentRow = 0;
        currentColumn = 0;
        rowFilled = false;
        for (int i = 0; i < 6 * 5; i++) {
            JTextField textField = textFields.get(i);
            textField.setText("");
            textField.setBackground(Color.WHITE);
            textField.setEditable(true);
        }
        correctWord = getRandomWord();
    }

    public static void main(String[] args) {
        new Main();
    }
}
