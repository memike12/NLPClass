package usna.parser;

/**
 * Abstract class for BinaryRule and UnaryRule to inherit.
 * This is mainly to allow you to have a List of Rule objects that mix
 * both BinaryRule and UnaryRule together (helpful for debugging storage)
 * 
 * @author chambers
 */
abstract class Rule {
	String parent;
	double score;

	public String getParent() {
		return parent;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}
	
	public abstract String toString();
}
