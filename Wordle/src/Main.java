import java.io.BufferedReader; // is used to read the text from an input stream (like a file)
import java.io.FileReader; // is a class used to read character files
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

	public static void main(String[] args) {
		
		List<String> words = new ArrayList<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader("words.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.length() == 5) { // Ensure it's a 5-letter word
                    words.add(line.toLowerCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Example: print out the first 10 words
        for (int i = 0; i < 10 && i < words.size(); i++) {
            System.out.println(words.get(i));
        }
		
		
		
	}
	
}
