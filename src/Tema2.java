import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.concurrent.ExecutionException;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.Collections;
import java.util.Map;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.concurrent.Executors;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Tema2 {
    public static void main(final String[] args) throws IOException, ExecutionException, InterruptedException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }
        int noOfThreads = Integer.parseInt(args[0]);
        String inputFile = args[1];
        String outputFile = args[2];
        File input = new File(inputFile);
        Scanner scanner = new Scanner(input);
        Long fragmentSize = scanner.nextLong();
        int noOfFiles = scanner.nextInt();
        scanner.nextLine();
        ArrayList<String> files = new ArrayList<>();
        ArrayList<Long> lengthOfFiles = new ArrayList<>();
        for (int i = 0; i < noOfFiles; ++i) {
            files.add(scanner.nextLine());
            lengthOfFiles.add(Files.size(Paths.get(files.get(i))));
        }
        ArrayList<String> filesMapTask = new ArrayList<>();
        ArrayList<Long> offsetMapTask = new ArrayList<>();
        ArrayList<Long> dimensionMapTask = new ArrayList<>();
        for (int j = 0; j < noOfFiles; j++) { // parcurgem toate fisierele
            Long offset = 0L;
            while (lengthOfFiles.get(j) > 0L) { // pana cand ajungem la finalul fisierului
                if (lengthOfFiles.get(j) >= fragmentSize) { // daca putem lua o portiune egala cu fragmentSize
                    filesMapTask.add(files.get(j));
                    offsetMapTask.add(offset);
                    dimensionMapTask.add(fragmentSize);
                    offset += fragmentSize;
                    lengthOfFiles.set(j, lengthOfFiles.get(j) - fragmentSize);
                }
                else { // daca nu, portiunea va fi mai mica decat result si va contine restul de fisier
                    filesMapTask.add(files.get(j));
                    offsetMapTask.add(offset);
                    dimensionMapTask.add(lengthOfFiles.get(j));
                    lengthOfFiles.set(j, lengthOfFiles.get(j) - fragmentSize);
                }
            }
        }
        ExecutorService executorMap = Executors.newFixedThreadPool(noOfThreads);
        List<Future> mapResults = new ArrayList<>();
        for (int i = 0; i < filesMapTask.size(); i++) {
            Future<MapTaskResult> future = executorMap.submit(new MapWorker(filesMapTask.get(i), dimensionMapTask.get(i), offsetMapTask.get(i)));
            mapResults.add(future);
        }
        executorMap.shutdown();
        if (!executorMap.awaitTermination(800L, TimeUnit.MILLISECONDS)) {
            executorMap.shutdownNow();
        }
        ArrayList<MapTaskResult> results1 = new ArrayList<>();
        for (int i = 0; i < filesMapTask.size(); i++) {
            Future<MapTaskResult> future = mapResults.get(i);
            MapTaskResult result1 = future.get();
            results1.add(result1);
        }
        // fiecare fisier va avea o lista de map-uri provenite din task-urile de map
        ArrayList<List<Map<Integer, Integer>>> mapsReduceTask = new ArrayList<>(noOfFiles);
        for (int i = 0; i < noOfFiles; i++) {
            mapsReduceTask.add(Collections.emptyList());
        }
        // fiecare fisier va avea o lista de liste (cuvintele de lungime maxima) provenite din task-urile de map
        ArrayList<List<List<String>>> maxWordsReduceTask = new ArrayList<>(noOfFiles);
        for (int i = 0; i < noOfFiles; i++) {
            maxWordsReduceTask.add(Collections.emptyList());
        }
        // construiesc parametrii pentru task-urile reduce, fiecare fisier avand o lista de map-uri si o lista de liste
        // de cuvinte de lungime maxima
        for (MapTaskResult mapTaskResult : results1) {
            int index = files.indexOf(mapTaskResult.fileName);
            List<Map<Integer, Integer>> aux1 = new ArrayList<>(mapsReduceTask.get(index));
            aux1.add(mapTaskResult.map);
            mapsReduceTask.set(index, aux1);
            List<List<String>> aux2 = new ArrayList<>(maxWordsReduceTask.get(index));
            aux2.add(mapTaskResult.maxWords);
            maxWordsReduceTask.set(index, aux2);
        }
        ExecutorService executorReduce = Executors.newFixedThreadPool(noOfThreads);
        List<Future> reduceResults = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            Future<ReduceTaskResult> future = executorReduce.submit(new ReduceWorker(files.get(i), mapsReduceTask.get(i), maxWordsReduceTask.get(i)));
            reduceResults.add(future);
        }
        executorReduce.shutdown();
        if (!executorReduce.awaitTermination(800L, TimeUnit.MILLISECONDS)) {
            executorReduce.shutdownNow();
        }
        ArrayList<ReduceTaskResult> results2 = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            Future<ReduceTaskResult> future = reduceResults.get(i);
            ReduceTaskResult result2 = future.get();
            results2.add(result2);
        }
        // sortare in functie de rank
        for (int i = 0; i < files.size() - 1; i++) {
            for (int j = 0; j < files.size() - 1 - i; j++) {
                if (results2.get(j).rank < results2.get(j + 1).rank) {
                    ReduceTaskResult tmp = results2.get(j);
                    results2.set(j, results2.get(j + 1));
                    results2.set(j + 1, tmp);
                }
            }
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        for (ReduceTaskResult result : results2) {
            String rankStr = String.format("%.2f", result.rank);
            writer.write(result.fileName + "," + rankStr + "," + result.maxLen + "," + result.noOfWords + "\n");
        }
        writer.close();
    }
}