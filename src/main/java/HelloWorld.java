import static spark.Spark.*;
import com.google.gson.*;

public class HelloWorld {
    public static void main(String[] args) {
        Gson gson = new Gson();
        get("/hello", (req, res) -> gson.toJson("abecd"));
    }
}