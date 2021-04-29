import static spark.Spark.*;
import com.google.gson.*;

public class HelloWorld {
    public static void main(String[] args) {
        Gson gson = new Gson();

        // get report
        get("/report", (req, res) -> {
           ReportRequest repreq = gson.fromJson(req.body(), ReportRequest.class);



           ReportResponse repres = new ReportResponse("123", "bmw","x5" ,"2020" , 10, 10.0);


            return gson.toJson(repres, ReportResponse.class);
        });
    }
}

class ReportRequest {
    public String startDate;
    public String endDate;
}

class ReportResponse {
    public String vehicleID;
    public String make;
    public String model;
    public String year;
    public int totalSold;
    public double profit;

    public ReportResponse (
             String VehicleID,
             String Make,
             String Model,
             String Year,
             int TotalSold,
             double Profit
    ){
        vehicleID = VehicleID;
        make = Make;
        model = Model;
        year = Year;
        totalSold = TotalSold;
        profit = Profit;
    }
}

//  [{:vehicle-id ""
//          :make ""
//          :model ""
//          :year ""
//          :total-sold 100
//          :profit 100.00}]