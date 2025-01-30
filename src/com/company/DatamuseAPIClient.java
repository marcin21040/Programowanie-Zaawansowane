package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

public class DatamuseAPIClient {
    private static final String API_URL = "https://api.datamuse.com/words?sp=";

    public static String getRandomWord() throws Exception {
        String queryPattern = "?????";
        URL url = new URL(API_URL + queryPattern + "&max=100");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        String response = content.toString();
        List<String> words = new ArrayList<>();

        if (response.startsWith("[") && response.length() > 2) {
            String[] wordEntries = response.split("\\},\\{");
            for (String wordEntry : wordEntries) {
                String word = wordEntry.split(":")[1].split("\"")[1];
                words.add(word.toUpperCase());
            }

            if (!words.isEmpty()) {
                Random random = new Random();
                return words.get(random.nextInt(words.size()));
            }
        }

        throw new Exception("Brak wyników z API");
    }

    public static boolean isValidWord(String word) throws Exception {
        URL url = new URL(API_URL + word.toLowerCase() + "&max=1");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        connection.disconnect();

        String response = content.toString();
        System.out.println("Response from API for word '" + word + "': " + response);

        if (response.equals("[]")) {
            return false;
        }

        JSONArray jsonArray = new JSONArray(response);
        if (jsonArray.length() > 0) {
            JSONObject wordData = jsonArray.getJSONObject(0);
            String foundWord = wordData.getString("word");
            int score = wordData.getInt("score");

            return foundWord.equalsIgnoreCase(word) && score > 1000;
        }

        return false;
    }
}
