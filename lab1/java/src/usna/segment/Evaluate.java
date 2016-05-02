package usna.segment;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;


/**
 * Complete the functions below. Output the average WER on one line.
 *
 * java Evaluate <guesses-path> <golds-path>
 *   - This outputs the average WER.
 * 
 */
public class Evaluate {
  public Evaluate() {
  }

  /**
   * YOU COMPLETE THIS FUNCTION
   * Computes the average WER score given a file of guesses and a file of 
   * gold correct answers.
   * HINT: the second avgWER() function is already completed! You just
   *       need to create two List objects with the guesses and golds.
   * @return The average WER between guesses and golds.
   */

private double avgWER(String guessPath, String goldPath) {
    // Read the files in to ArrayList objects and call avgWER with the two Lists.
	  List<String> myGuess = new ArrayList<String>();
	  List<String> gold = new ArrayList<String>();
	  try{
			BufferedReader myGuessesFile = new BufferedReader(new FileReader(new File(guessPath)));
			BufferedReader goldFile = new BufferedReader(new FileReader(new File(goldPath)));
			String line;
			while((line = myGuessesFile.readLine()) != null)  {
				//System.out.println(line);
				myGuess.add(line);
				gold.add(goldFile.readLine());
			}
			myGuessesFile.close();
			goldFile.close();
			
		} catch( Exception ex ) { ex.printStackTrace(); }
    return avgWER(myGuess, gold);
  }

  /**
   * Computes the average WER score given a list of guesses and gold
   * correct answers.
   * @return The average WER between guesses and golds.
   */
  private double avgWER(List<String> guesses, List<String> golds) {
    double avg = 0.0;
    int i = 0;

    for( String guess : guesses )
      avg += WER(guess, golds.get(i++));

    return avg / guesses.size();
  }

  /**
   * YOU COMPLETE THIS FUNCTION: compute WER between two strings
   * HINT: this is a VERY short function. Read the lab's Part 2
   *       instructions for how to do this.
   * @return The WER between two strings.
   */
  private double WER(String guess, String gold) {
    // Removing leading/trailing whitespace, just in case any exists.
    guess = guess.trim();
    gold = gold.trim();
    //System.out.println("mine: " + guess + "    and gold: " + gold);
    // Only a little bit of code needed! Levenshtein to the rescue?
    Levenshtein myScore = new Levenshtein();

    return myScore.score(guess, gold)/guess.length();
  }


  /**
   * DO NOT MAKE ANY CHANGES TO THIS
   */
  public static void main(String[] args) {
    if (args.length == 2) {
      Evaluate eval = new Evaluate();
      double avg = eval.avgWER(args[0], args[1]);
      System.out.println(avg);
      
      if( avg > 1.0 )  System.out.println("That's an abnormally high error.");
      if( avg > 0.2 )  System.out.println("Your average error is a bit high. Are you computing the WER correctly?");
      if( avg < 0.02 ) System.out.println("Wow that's a fantastic low error. Are you sure you aren't cheating?");

    } else {
      System.err.println("java usna.segment.Evaluate <guesses> <golds>");
    }
  }
}
