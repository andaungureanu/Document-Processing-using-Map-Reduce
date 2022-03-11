import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ReduceWorker implements Callable<ReduceTaskResult> {
    private final String fileName;
    private final List<Map<Integer, Integer>> mapList;
    private final List<List<String>> maxWords;

    public ReduceWorker(String fileName, List<Map<Integer, Integer>> mapList, List<List<String>> maxWords) {
        this.fileName = fileName;
        this.mapList = mapList;
        this.maxWords = maxWords;
    }

    public static int fib(int n) {
        if (n <= 1) {
            return n;
        }
        return fib(n - 1) + fib(n - 2);
    }

    @Override
    public ReduceTaskResult call() throws Exception {
        int wordCount = 0;
        int sum = 0;
        int maxLen = 0;
        int noOfWords = 0;
        for (Map<Integer, Integer> map : this.mapList) {
            for (Integer key : map.keySet()) {
                sum += fib(key + 1) * map.get(key);
                wordCount += map.get(key);
                // numarul total de cuvinte = suma valorilor dictionarului
                if (key > maxLen) {
                    maxLen = key;
                }
            }
        }
        double rank = (double)sum/wordCount;
        for (List<String> list : this.maxWords) {
            for (String word : list) {
                if (word.length() == maxLen) {
                    noOfWords++;
                }
            }
        }
        ReduceTaskResult result = new ReduceTaskResult(this.fileName.substring(12), rank, maxLen, noOfWords);
        return result;
    }
}
