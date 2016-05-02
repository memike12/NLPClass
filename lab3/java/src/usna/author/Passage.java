package usna.author;


/**
 * @author Nate Chambers
 */
public class Passage {
  public static enum AUTHOR { AUSTEN, CONRAD, DICKENS, ELIOT, HARDY, HAWTHORNE, JAMES, SHAKESPEARE, SHAW, TWAIN };

  // The author of this passage.
  private AUTHOR author = null;
  // The entire text of this passage, may include newlines.
  private String text = null;
	
  public Passage(AUTHOR author, String text) {
    this.author = author;
    this.text = text;
  }

  /**
   * Accessor functions.
   */
  public String getText() { return text; }
  public AUTHOR getAuthor() { return author; }
    
  public void setAuthor(AUTHOR author) { this.author = author; }
    
  public String toString() {
    return author.toString() + "\t" + text;
  }
}
