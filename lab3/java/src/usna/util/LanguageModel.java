package usna.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Language models assign probabilities to sentences and generate sentences.
 *
 * @author Dan Klein
 */
public abstract class LanguageModel {

  protected static final String START = "<S>";
  protected static final String STOP  = "</S>";
  protected static final String UNK   = "<UNK>";

  /**
   * Constructs a language model from a collection of sentences.
   */
  public abstract void train(Collection<List<String>> trainingSentences);

  /**
   * Returns the probability, according to the model, of the word specified
   * by the argument sentence and index. Index ranges from 0 to sentence.size(),
   * inclusive. If index==sentence.size(), return P(STOP | context).
   */
  public abstract double getWordProbability(List<String> sentence, int index);

  /**
   * Returns the set of tokens the model makes predictions for. This
   * includes STOP (and maybe UNK), but not START (because we do not need to
   * compute P(START | context)).
   */
  public abstract Collection<String> getVocabulary();

  /**
   * Returns a random sentence sampled according to the model.
   */
  public abstract List<String> generateSentence();

  //sentence length
  public abstract double getLengthProbability(String size);
  
  //word length
  public abstract double getWordSizeProbability(String size);
  //-----------------------------------------------------------------------

  /**
   * Returns the probability, according to the model, of the specified
   * sentence.  This is the product of the probabilities of each word in
   * the sentence (including a final stop token).
   */
  public double getSentenceProbability(List<String> sentence) {
    double probability =1.0;
    for (int i=0; i<=sentence.size(); i++) {
    	
    //vinilla
      //probability += Math.log(getWordProbability(sentence, i));
      
      
      //with wordLength
    	probability += Math.log(getWordProbability(sentence, i));
    }
    //sentence  length model
     //return (probability + Math.log(getLengthProbability(Integer.toString(sentence.size()))));
    
    //word Length
    //return (probability + Math.log(getLengthProbability(Integer.toString(sentence.size()))));
    return probability;
  }

  /**
   * Given a list of words, sums over the probabilities of every token that
   * could follow. If the model implements a valid probability 
   * distribution, this should always sum to 1.
   */
  public double checkProbability(List<String> context) {
    double modelsum = 0.0;
    for (String token : getVocabulary()) {
      context.add(token);
      int lastIndex = context.size() - 1;
      modelsum += getWordProbability(context, lastIndex);
      context.remove(lastIndex);
    }
    return modelsum;
  }

}
