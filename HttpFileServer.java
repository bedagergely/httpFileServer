import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpFileServer {
 private int port;
 private int backLog;

 private boolean isRunning;

 public HttpFileServer(int port, int backlog) {
     this.port = port;
     this.backLog = backlog;
     this.isRunning = false;
 }

 public HttpFileServer(int port) {
     this(port, 1);
 }

 public  HttpFileServer() {
     this(8080);
 }

 public void start()  {
    this.isRunning = true;
     try (ServerSocket serverSocket = new ServerSocket(this.port, this.backLog)){
         while (this.isRunning) {
             Socket socket = serverSocket.accept();
             handleNewConnection(socket);
         }
     } catch (IOException e) {
         throw new RuntimeException(e);
     } finally {
         this.isRunning = false;
     }
 }

 private void handleNewConnection(Socket socket) {
     try {
         new Thread(() -> {
             try {
                 InputStream inputStream = socket.getInputStream();
                 OutputStream outputStream = socket.getOutputStream();

                 byte[] bytes = loadMessage(inputStream);

                 HttpRequest httpRequest = new HttpRequest();
                 HashMap<String, String> parsedRequest = httpRequest.parseRequest(bytes);
                 String body = handleGetRequest(parsedRequest.get("GET"));

                 HttpResponse httpResponse = new HttpResponse();
                 httpResponse.setBody(body);

                 respond(outputStream, httpResponse);

                 socket.close();
             } catch (IOException ioException) {
                 throw new RuntimeException(ioException);
             }
         }).start();
     } catch (Exception e) {
         throw new RuntimeException(e);
     }
 }

 private byte[] loadMessage(InputStream inputStream) throws IOException {
     return inputStream.readNBytes(inputStream.available());
 }

 private void respond(OutputStream outputStream, HttpResponse response) throws IOException {
     outputStream.write(response.getBytes());
     outputStream.flush();
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

}
