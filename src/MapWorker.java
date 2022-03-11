import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;
import java.util.concurrent.Callable;

public class MapWorker implements Callable<MapTaskResult> {

    private final String fileName;
    private final Long dimension;
    private final Long offset;

    public MapWorker(String fileName, Long dimension, Long offset) {
        this.fileName = fileName;
        this.dimension = dimension;
        this.offset = offset;
    }

    @Override
    public MapTaskResult call() throws Exception {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(this.fileName, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        char character = 0;
        int c = 0;
        StringBuilder text = new StringBuilder();
        boolean bec = false;
        String delimiters
                = ";|:|/|˜|\\.|,|>|<|‘|!|@|#|\\*|\\|$|%|ˆ|&|-|`|_|\\+|=|”| |\\?|[\\(||\\)]|[\\[||\\]]|[\\{||\\}]|'|\n|\t|\r|\\\\";
        if (this.offset != 0L) {
            try {
                file.seek(this.offset - 1); // pentru a verifica caracterul de dinainte
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                character = (char) file.read(); // citesc caracterul de dinainte de offset
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (delimiters.indexOf(character) == -1) { // daca apartine unui cuvant, atunci retinem ca trebuie sa ignoram
                // restul cuvantului fiind deja luat in considerare in fragmentul anterior
                bec = true;
            }
        }
        for (Long i = 0L; i < this.dimension; i++) {
            try {
                text.append((char) file.read()); // citim cat ne permite dimensiunea
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bec) { // trebuie sa sterg restul de cuvant nefolosit de acest thread
            while (text.length() > 0) {
                if (delimiters.indexOf(text.charAt(0)) == -1) { // daca primul caracter nu este un delimitator
                    text.deleteCharAt(0);
                } else {
                    break;
                }
            }
        }
        if (text.length() != 0) {
            if (delimiters.indexOf(text.charAt(text.length() - 1)) == -1) { // daca ultimul caracter din sirul retinut nu
                // este delimitator, atunci ne-am oprit in mijlocul unui cuvant
                while (true) { // citim restul de cuvant pana la delimitator
                    try {
                        c = file.read();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (c == -1) { // daca ajung la finalul fisierului
                        break;
                    }
                    if (delimiters.indexOf((char) c) == -1) { // daca nu este delimitator, adaug
                        text.append((char) c);
                    } else { // daca ajung la delimitator, ma opresc
                        break;
                    }
                }
            }
        }
        String[] words = text.toString().split(delimiters);
        HashMap<Integer, Integer> map = new HashMap<>();
        int var;
        for (String word : words) {
            if (map.containsKey(word.length())) {  // daca lungimea cuvantului este deja cheie
                var = map.get(word.length());
                var++;
                map.put(word.length(), var); // adun 1 la numarul de aparitii ale lungimii respective
            } else {
                if (word.length() != 0) { // daca lungimea e noua si nu e vida, o adaug ca cheie
                    map.put(word.length(), 1);
                }
            }
        }
        List<String> wordsForComp = Arrays.asList(words);
        wordsForComp.sort(new LengthComparator()); // sortez cuvintele descrescator dupa lungime
        List<String> maxWords = new ArrayList<>();
        int maxLen = wordsForComp.get(0).length(); // primul cuvant este cel mai lung
        for (String word : wordsForComp) {
            if (word.length() != maxLen) {
                // fiind in ordine descrescatoare, daca gasesc o lungime diferita inseamna ca result sunt mai mici
                // decat maxLen
                break;
            } else {
                maxWords.add(word); // adaug toate cuvintele ce au lungimea egala cu maxLen
            }
        }
        MapTaskResult result = new MapTaskResult(map, maxWords, this.fileName);
        return result;
    }
}