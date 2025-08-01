import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GeminiChatbot {
    private static final String API_KEY = "AIzaSyAGLSA6QttJ5PqXFrucX4ouUoAbwvwahfQ";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Gemini Chatbot (Hi! How can I help you?)");

        while (true) {
            System.out.print("You: ");
            String userInput = scanner.nextLine();
            if (userInput.equalsIgnoreCase("exit")) break;

            try {
                String response = sendPrompt(userInput);
                System.out.println("Gemini: " + response);
            } catch (Exception e) {
                System.out.println("Gemini: Sorry, there was an error. " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static String sendPrompt(String prompt) throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.setDoOutput(true);

        // JSON request body
        String jsonInputString = "{"
                + "\"contents\": [{"
                + "  \"parts\": [{\"text\": \"" + prompt + "\"}]"
                + "}]"
                + "}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        return parseResponse(response.toString());
    }

    private static String parseResponse(String jsonResponse) {
        // Basic extraction of the first text response from Gemini
        try {
            int index = jsonResponse.indexOf("\"text\":");
            if (index != -1) {
                int start = jsonResponse.indexOf("\"", index + 7) + 1;
                int end = jsonResponse.indexOf("\"", start);
                return jsonResponse.substring(start, end);
            }
        } catch (Exception e) {
            return "Error parsing response: " + e.getMessage();
        }

        return "Sorry, I couldn't parse the Gemini response.";
    }
}
