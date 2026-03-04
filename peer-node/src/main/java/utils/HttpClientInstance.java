package utils;


public class HttpClientInstance {

    private static volatile java.net.http.HttpClient instance;

    public static java.net.http.HttpClient getInstance() {
        if (instance == null) {
            synchronized (HttpClientInstance.class) {
                if (instance == null) {
                    instance = java.net.http.HttpClient.newHttpClient();
                }
            }
        }
        return instance;
    }
}
