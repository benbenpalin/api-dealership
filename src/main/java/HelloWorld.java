import static spark.Spark.*;
import com.google.gson.*;

public class HelloWorld {
    public static void main(String[] args) {
        Gson gson = new Gson();

        // get report
        get("api/report", (req, res) -> {
            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            ReportResponse repres = new ReportResponse("123", "bmw","x5" ,"2020" , 10, 12.0);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

           return gson.toJson(repres, ReportResponse.class);
        });

        get("api/packages", (req, res) -> {
            Package noPackage = new Package("No Package", "000");
            Package package1 = new Package("1 year", "123");
            Package package2 = new Package("2 year", "234");

            Package[] packages = {noPackage, package1, package2};
            PackagesResponse packagesResponse = new PackagesResponse(packages);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(packagesResponse, PackagesResponse.class);
        });

        post("api/dropoff", (req, res) -> {
            DropoffRequest dropBody = gson.fromJson(req.body(), DropoffRequest.class);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "";
        });
    }
}

class DropoffRequest {
    String appointmentId;
}

class ReportResponse {
    public String vehicleId;
    public String make;
    public String model;
    public String year;
    public int totalSold;
    public double profit;

    public ReportResponse (
             String VehicleId,
             String Make,
             String Model,
             String Year,
             int TotalSold,
             double Profit
    ){
        vehicleId = VehicleId;
        make = Make;
        model = Model;
        year = Year;
        totalSold = TotalSold;
        profit = Profit;
    }
}

class PackagesResponse {
    public Package[] packages;
    public PackagesResponse(
            Package[] pkgs
    ) {
        packages = pkgs;
    }
}

class Package {
    public String name;
    public String packageId;

    public Package(
            String pName,
            String pId
    ) {
        name = pName;
        packageId = pId;
    }
}