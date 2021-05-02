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

        get("api/appointments", (req, res) -> {
            Appointment appointment1 = new Appointment("123", "Gray", "2015", "Subaru", "Forester", "111");
            Appointment appointment2 = new Appointment("234", "Black", "1975", "VW", "Bus", "222");

            Appointment[] appointments = {appointment1, appointment2};
            AppointmentsResponse appointmentsResponse = new AppointmentsResponse(appointments);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(appointmentsResponse, AppointmentsResponse.class);
        });

        get("api/vehicletypes", (req, res) -> {
            VehicleType vehicleType1 = new VehicleType("Subaru", "Forester","2015", "111");
            VehicleType vehicleType2 = new VehicleType ( "VW", "Bus", "1975", "222");

            VehicleType[] vehicleTypes = {vehicleType1, vehicleType2};
            VehicleTypeResponse vehicleTypeResponse = new VehicleTypeResponse(vehicleTypes);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(vehicleTypeResponse, VehicleTypeResponse.class);
        });

        get("api/appointmenttasks", (req, res) -> {
            String appointmentId = req.queryParams("appointmentId");

            TestTask test1 = new TestTask("123", "Brake Test", "321", "Brake Replacement");
            TestTask test2 = new TestTask("234", "Alternator Test", "432", "Alternator Replacement");
            TestTask[] tests = {test1, test2};

            ReplacementTask replacement1 = new ReplacementTask("345", "Filter Replacement", "543", "Filter", 10.00);
            ReplacementTask replacement2 = new ReplacementTask("456", "Oil Change", "654", "Oil", 15.00);
            ReplacementTask[] replacements = {replacement1, replacement2};

            AppointmentTasksResponse appTasksRes = new AppointmentTasksResponse(tests, replacements);


            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(appTasksRes, AppointmentTasksResponse.class);
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

class TestTask {
    public String taskId;
    public String taskName;
    public String testFailureTaskId;
    public String testFailureTaskName;

    public TestTask(String taskId, String taskName, String testFailureTaskId, String testFailureTaskName) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.testFailureTaskId = testFailureTaskId;
        this.testFailureTaskName = testFailureTaskName;
    }
}

class ReplacementTask {
    public String taskId;
    public String taskName;
    public String partId;
    public String partName;
    public double costOfPart;

    public ReplacementTask(String taskId, String taskName, String partId, String partName, double costOfPart) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.partId = partId;
        this.partName = partName;
        this.costOfPart = costOfPart;
    }
}

class AppointmentTasksResponse{
    public TestTask[] tests;
    public ReplacementTask[] partReplacements;

    public AppointmentTasksResponse(TestTask[] tests, ReplacementTask[] partReplacements) {
        this.tests = tests;
        this.partReplacements = partReplacements;
    }
}


class VehicleType {
    public String make;
    public String model;
    public String year;
    public String vehicleId;

    public VehicleType(String make, String model, String year, String vehicleId) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.vehicleId = vehicleId;
    }
}

class VehicleTypeResponse {
    public VehicleType[] vehicleTypes;

    public VehicleTypeResponse(VehicleType[] vehicleTypes) {
        this.vehicleTypes = vehicleTypes;
    }
}

class Appointment {
    public String appointmentId;
    public String color;
    public String year;
    public String make;
    public String model;
    public String vehicleId;

    public Appointment(String appointmentId, String color, String year, String make, String model, String vehicleId) {
        this.appointmentId = appointmentId;
        this.color = color;
        this.year = year;
        this.make = make;
        this.model = model;
        this.vehicleId = vehicleId;
    }
}

class AppointmentsResponse {
    public Appointment[] appointments;

    public AppointmentsResponse(Appointment[] appointments) {
        this.appointments = appointments;
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