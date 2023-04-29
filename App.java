public class App 
{
    public static void main( String[] args )
    {
        new HttpFileServer(8080, 1).start();
    }
}
