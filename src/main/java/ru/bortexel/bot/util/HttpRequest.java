package ru.bortexel.bot.util;

import ru.bortexel.bot.BortexelBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpRequest {
    private URL url;

    public HttpRequest(String url) {
        try {
            this.url = new URL(url.replace(" ", "%20"));
        } catch (MalformedURLException e) {
            BortexelBot.handleException(e);
        }
    }

    public String getResponse() {
        try {
            HttpURLConnection connection = (HttpURLConnection) this.url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            return response.toString();
        } catch (IOException e) {
            BortexelBot.handleException(e);
        }

        return null;
    }
}
