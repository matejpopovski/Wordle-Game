import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends JFrame {
    
    private JPanel boardPanel;
    private List<String> words;

    public Main() {
        // Set up the main window
        setTitle("Wordle Game");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Load the words from the file
        loadWords();

        // Set up the game board
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(6, 5, 5, 5)); // 6 rows for guesses, 5 columns for letters
        add(boardPanel);

        // Add text fields for the Wordle grid
        for (int i = 0; i < 6 * 5; i++) {
            JTextField textField = new JTextField();
            textField.setEditable(false);
            textField.setHorizontalAlignment(JTextField.CENTER);
            boardPanel.add(textField);
        }

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
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
