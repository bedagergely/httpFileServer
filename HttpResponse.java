import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public class HttpResponse {
    private final String statusCode;
    private final LocalDateTime date;
    private final String server;
    private final LocalDateTime lastModified;
    private int contentLength;
    private final String contentType;
    public final String connection;
    private String body;

    public HttpResponse() {
        statusCode = "200 OK";
        date = LocalDateTime.now();
        server = "JavaNoDependencyServer/1.0.0 (JVM)";
        lastModified = LocalDateTime.now();
        contentLength = 0;
        contentType = "text/html";
        connection = "Closed";
        body = null;
    }

    public void setBody(String body) {
        this.body = body;
        this.contentLength = body.length();
    }

    public String getBody() {
        return this.body;
    }

    public String toString() {
        return "HTTP/1.1 " + this.statusCode + "\n" +
               "Date:" + this.date.toString() + "\n" +
               "Server: " + this.server + "\n" +
               "Last-Modified: " + this.lastModified + "\n" +
               "Content-Length: " + this.contentLength + "\n" +
               "Content-Type: " + this.contentType + "\n" +
               "Connection: " + this.connection + "\n" +
               "\n" +
               this.body;
    }

    public byte[] getBytes() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
}
