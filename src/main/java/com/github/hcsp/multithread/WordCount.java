package com.github.hcsp.multithread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class WordCount {
    private final ExecutorService threadPool;

    public WordCount(int threadNum) {
        threadPool = Executors.newFixedThreadPool(threadNum);

    }

    // 统计文件中各单词的数量
    public Map<String, Integer> count(List<File> files) throws IOException, ExecutionException, InterruptedException {
        List<Future<Map<String, Integer>>> futures = new ArrayList<>();

        for (File file : files) {
            futures.add(threadPool.submit((Callable<Map<String, Integer>>) () -> countFile(file)));
        }

        Map<String, Integer> finalResult = new HashMap<>();

        for (Future<Map<String, Integer>> future : futures) {

            mergeFutureToFinalResult(finalResult, future.get());
        }

        return finalResult;
    }

    private void mergeFutureToFinalResult(Map<String, Integer> finalResult, Map<String, Integer> future) {
        for (Map.Entry<String, Integer> entry : future.entrySet()) {
            int tempResult = finalResult.getOrDefault(entry.getKey(), 0) + entry.getValue();
            finalResult.put(entry.getKey(), tempResult);
        }
    }

    public Map<String, Integer> countFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        Map<String, Integer> result = new HashMap<>();
        while ((line = reader.readLine()) != null) {
            String[] words = line.split(" ");
            for (String word : words) {
                result.put(word, result.getOrDefault(word, 0) + 1);
            }
        }
        return result;
    }
}