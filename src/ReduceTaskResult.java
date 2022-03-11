public class ReduceTaskResult {
    public String fileName;
    public double rank;
    public int maxLen;
    public int noOfWords;

    public ReduceTaskResult(String fileName, double rank, int maxLen, int noOfWords) {
        this.fileName = fileName;
        this.rank = rank;
        this.maxLen = maxLen;
        this.noOfWords = noOfWords;
    }
}
