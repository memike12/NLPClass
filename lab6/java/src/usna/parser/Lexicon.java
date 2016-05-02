package usna.parser;

import java.util.List;
import java.util.Set;

import usna.ling.Tree;
import usna.util.Counter;
import usna.util.CounterMap;


/**
 * Simple default implementation of a lexicon, which scores word,
 * tag pairs with a smoothed estimate of P(tag|word)/P(tag).
 */
public class Lexicon {
  CounterMap<String,String> wordToTagCounters = new CounterMap<String, String>();
  double totalTokens = 0.0;
  double totalWordTypes = 0.0;
  Counter<String> tagCounter = new Counter<String>();
  Counter<String> wordCounter = new Counter<String>();
  Counter<String> typeTagCounter = new Counter<String>();

  /**
   * Builds a lexicon of Part of Speech tags to words from the tags in 
   * the given list of training trees. 
   */
  public Lexicon(List<Tree<String>> trainTrees) {
    for (Tree<String> trainTree : trainTrees) {
      List<String> words = trainTree.getYield();
      List<String> tags = trainTree.getPreTerminalYield();
      for (int position = 0; position < words.size(); position++) {
        String word = words.get(position);
        String tag = tags.get(position);
        tallyTagging(word, tag);
      }
    }
  }
  
  /**
   * Get all possible part of speech tags.
   * @return All part of speech tags in the lexicon.
   */
  public Set<String> getAllTags() {
    return tagCounter.keySet();
  }

  /**
   * True if the given word is in the lexicon somewhere.
   * @return True if the given word is in the lexicon somewhere. False otherwise.
   */
  public boolean isKnown(String word) {
    return wordCounter.keySet().contains(word);
  }

  /**
   * Returns the rule probability of the rule: TAG -> word
   * @param tag  A part of speech tag.
   * @param word A word.
   * @return The probability of the tag->word rule: P(word | tag)
   */
  public double getRuleProbability(String tag, String word) {
    double p_tag = tagCounter.getCount(tag) / totalTokens;
    double c_word = wordCounter.getCount(word);
    double c_tag_and_word = wordToTagCounters.getCount(word, tag);
    if (c_word < 10) { // rare or unknown
      c_word += 1.0;
      c_tag_and_word += typeTagCounter.getCount(tag) / totalWordTypes;
    }
    double p_word = (1.0 + c_word) / (totalTokens + totalWordTypes);
    double p_tag_given_word = c_tag_and_word / c_word;
    return p_tag_given_word / p_tag * p_word;
  }

  private void tallyTagging(String word, String tag) {
    if (! isKnown(word)) {
      totalWordTypes += 1.0;
      typeTagCounter.incrementCount(tag, 1.0);
    }
    totalTokens += 1.0;
    tagCounter.incrementCount(tag, 1.0);
    wordCounter.incrementCount(word, 1.0);
    wordToTagCounters.incrementCount(word, tag, 1.0);
  }
}
