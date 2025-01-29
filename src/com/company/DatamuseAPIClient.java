package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DatamuseAPIClient {
    private static final String API_URL = "https://api.datamuse.com/words?sp=";

    // Pobieramy losowe słowo o długości 5 liter
    public static String getRandomWord() throws Exception {
        String queryPattern = "?????"; // Wzorzec na 5-literowe słowo
        URL url = new URL(API_URL + queryPattern + "&max=100"); // Pobieramy do 100 słów
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

        // Lista do przechowywania słów
        List<String> words = new ArrayList<>();

        // Obsługa odpowiedzi JSON (dodajemy wszystkie słowa do listy)
        if (response.startsWith("[") && response.length() > 2) {
            String[] wordEntries = response.split("\\},\\{"); // Rozdzielamy wyniki na poszczególne słowa
            for (String wordEntry : wordEntries) {
                String word = wordEntry.split(":")[1].split("\"")[1];
                words.add(word.toUpperCase()); // Dodajemy słowo do listy w wielkich literach
            }

            // Wybieramy losowe słowo z listy
            if (!words.isEmpty()) {
                Random random = new Random();
                return words.get(random.nextInt(words.size())); // Zwracamy losowe słowo z listy
            }
        }

        throw new Exception("Brak wyników z API");
    }

    public static boolean isValidWord(String word) throws Exception {
        URL url = new URL(API_URL + word.toLowerCase() + "&max=1"); // Sprawdzamy, czy istnieje
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
        // Jeśli otrzymujemy odpowiedź, to znaczy, że słowo istnieje
        return !response.equals("[]"); // Jeśli odpowiedź nie jest pusta, to słowo istnieje
    }
}
