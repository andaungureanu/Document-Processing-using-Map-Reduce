import java.util.HashMap;
import java.util.List;

public class MapTaskResult {
    public HashMap<Integer, Integer> map;
    public List<String> maxWords;
    public String fileName;

    public MapTaskResult(HashMap<Integer, Integer> map, List<String> maxWords, String fileName) {
        this.map = map;
        this.maxWords = maxWords;
        this.fileName = fileName;
    }
}
