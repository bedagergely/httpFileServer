import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Server {

    private boolean kill = false;
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(8080, 1)){
            while (!kill) {
                Socket socket = serverSocket.accept();
                new Thread(() ->{
                    try {
                        handleNewConnection(socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException();
        }
    }

    public void handleNewConnection(Socket socket) throws IOException {
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            byte[] request = readAllBytes(inputStream);

            String path = parseRequest(request).get("GET");
            String responseBody = handleGetRequest(path);

            if(path.contains("stop")) kill = true;

            HTMLResponse htmlResponse = new HTMLResponse();
            htmlResponse.setBody(responseBody);

            outputStream.write(htmlResponse.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            socket.close();
        }
    }

    private String handleGetRequest(String path) {
        String[] purePath = path.split(" ");
        File file = new File(purePath[0].replaceFirst("/", ""));
        String content = null;
        if(!file.exists()) file = new File("index.html");
        try(FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = fileInputStream.readAllBytes();
            content = new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        return content;
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        int maxSize = 1000;
        int nextByte;
        byte[] bytes = new byte[maxSize];
        int index = 0;
        while ((nextByte = inputStream.read() )!= -1) {
            if(index >= maxSize) break;
            bytes[index] = ((byte) nextByte);
            if(index > 3) {
                if(isEndOfHttpRequest(bytes[index],bytes[index-1],bytes[index-2],bytes[index-3])) break;
            }
            index++;
        }
        return bytes;
    }

    private boolean isEndOfHttpRequest(byte b1, byte b2, byte b3, byte b4) {
        if(b1 == (byte) 10 && b2 == (byte) 13 && b3 == (byte) 10 && b4 == (byte) 13) {
            return true;
        }
        return false;
    }

    private HashMap<String, String> parseRequest(byte[] bytes) {
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
