import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpRequest {

    public HashMap<String, String> parseRequest(byte[] bytes) {
        String request = new String(bytes, StandardCharsets.UTF_8);
        String[] parts = request.split("\n");

        HashMap<String, String> requestHeader = new HashMap<>();

        for(int i = 0; i < parts.length; i++) {
            String[] keyValuePair = parts[i].split(" ", 2);
            if(keyValuePair.length < 2) continue;
            requestHeader.put(keyValuePair[0], keyValuePair[1]);
        }
        return requestHeader;
    }


}