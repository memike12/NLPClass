package usna.author;

import java.util.List;

/**
 * YOU PROBABLY DON'T NEED TO CHANGE OR ADD CODE TO THIS CLASS.
 *
 * Code to compare predicted labels to gold labeled passages. Computes Accuracy
 * 
 * @author Nate Chambers, US Naval Academy
 */
public class Evaluator {

  /**
   * A simple method that just compares the authors given in the first list to the authors
   * given in the passages of the second list. Both lists should be the same length, and the
   * ith index in each should correspond to the same passage.
   * An accuracy score is computed ( # correct / # passages ) and returned.
   * 
   * @param guesses The list of predicted authors.
   * @param labeledData The list of labeled passages that you predicted.
   * @return A single accuracy score.
   */
  public static double evaluate(List<Passage.AUTHOR> guesses, List<Passage> labeledData) {
    // Number of guesses must be aligned with the actual data.
    if( guesses.size() != labeledData.size() ) {
      System.out.println("ERROR IN EVALUATE(): you gave me " + guesses.size() + " guessed labels, but " + labeledData.size() + " passages.");
      return 0.0;
    }
    
    // Compare the guesses with the gold labels.
    int numRight = 0, numWrong = 0, xx = 0;
    for( Passage.AUTHOR guess : guesses ) {
      Passage passage = labeledData.get(xx);
      if( passage.getAuthor() == guess )
        numRight++;
      else
        numWrong++;
      xx++;
    }

    // Compute accuracy.
    System.out.println("Correct: " + numRight);
    System.out.println("Incorrect: " + numWrong);
    double accuracy = (double)numRight / ((double)numRight+(double)numWrong);
    return accuracy * 100.0;
  }
}
