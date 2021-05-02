import static spark.Spark.*;
import com.google.gson.*;

public class HelloWorld {
    public static void main(String[] args) {
        Gson gson = new Gson();

        // get report
        get("api/report", (req, res) -> {
            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            ReportResponse repres = new ReportResponse(1, "bmw","x5" ,"2020" , 10, 12.0);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

           return gson.toJson(repres, ReportResponse.class);
        });

        get("api/packages", (req, res) -> {
            Package noPackage = new Package("No Package", 0);
            Package package1 = new Package("1 year", 1);
            Package package2 = new Package("2 year", 2);

            Package[] packages = {noPackage, package1, package2};
            PackagesResponse packagesResponse = new PackagesResponse(packages);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(packagesResponse, PackagesResponse.class);
        });

        get("api/appointments", (req, res) -> {
            Appointment appointment1 = new Appointment(1, "Gray", "2015", "Subaru", "Forester", 1);
            Appointment appointment2 = new Appointment(2, "Black", "1975", "VW", "Bus", 2);

            Appointment[] appointments = {appointment1, appointment2};
            AppointmentsResponse appointmentsResponse = new AppointmentsResponse(appointments);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(appointmentsResponse, AppointmentsResponse.class);
        });

        get("api/vehicletypes", (req, res) -> {
            VehicleType vehicleType1 = new VehicleType("Subaru", "Forester","2015", 1);
            VehicleType vehicleType2 = new VehicleType ( "VW", "Bus", "1975", 2);

            VehicleType[] vehicleTypes = {vehicleType1, vehicleType2};
            VehicleTypeResponse vehicleTypeResponse = new VehicleTypeResponse(vehicleTypes);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(vehicleTypeResponse, VehicleTypeResponse.class);
        });

        get("api/appointmenttasks", (req, res) -> {
            String appointmentId = req.queryParams("appointmentId");

            TestTask test1 = new TestTask(1, "Brake Test", 3, "Brake Replacement");
            TestTask test2 = new TestTask(2, "Alternator Test", 4, "Alternator Replacement");
            TestTask[] tests = {test1, test2};

            ReplacementTask replacement1 = new ReplacementTask(2, "Filter Replacement", 4, "Filter", 10.00);
            ReplacementTask replacement2 = new ReplacementTask(4, "Oil Change", 6, "Oil", 15.00);
            ReplacementTask[] replacements = {replacement1, replacement2};

            AppointmentTasksResponse appTasksRes = new AppointmentTasksResponse(tests, replacements);


            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(appTasksRes, AppointmentTasksResponse.class);
        });

        //TODO make all ids ints

        get("api/tasksinpackage", (req, res) -> {
            String packageId = req.queryParams("packageId");

            PackageTask task1 = new PackageTask(1, "Brake Test", 1);
            PackageTask task2 = new PackageTask(2, "Alternator Test", 2);
            PackageTask[] tasksInPackage = {task1, task2};

            PackageTask task3 = new PackageTask(3, "Filter Replacement", 2);
            PackageTask task4 = new PackageTask(4, "Oil Change", 1);
            PackageTask[] tasksNotInPackage = {task3, task4};


            TasksInPackageResponse response = new TasksInPackageResponse(tasksInPackage, tasksNotInPackage);


            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, TasksInPackageResponse.class);
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

class PackageTask{
    public int taskId;
    public String taskName;
    public int estdTime;

    public PackageTask(int taskId, String taskName, int estdTime) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.estdTime = estdTime;
    }
}

class TasksInPackageResponse {
    public PackageTask[] inPackage;
    public PackageTask[] notInPackage;

    public TasksInPackageResponse(PackageTask[] inPackage, PackageTask[] notInPackage) {
        this.inPackage = inPackage;
        this.notInPackage = notInPackage;
    }
}

class TestTask {
    public int taskId;
    public String taskName;
    public int testFailureTaskId;
    public String testFailureTaskName;

    public TestTask(int taskId, String taskName, int testFailureTaskId, String testFailureTaskName) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.testFailureTaskId = testFailureTaskId;
        this.testFailureTaskName = testFailureTaskName;
    }
}

class ReplacementTask {
    public int taskId;
    public String taskName;
    public int partId;
    public String partName;
    public double costOfPart;

    public ReplacementTask(int taskId, String taskName, int partId, String partName, double costOfPart) {
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
    public int vehicleId;

    public VehicleType(String make, String model, String year, int vehicleId) {
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
    public int appointmentId;
    public String color;
    public String year;
    public String make;
    public String model;
    public int vehicleId;

    public Appointment(int appointmentId, String color, String year, String make, String model, int vehicleId) {
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
    public int vehicleId;
    public String make;
    public String model;
    public String year;
    public int totalSold;
    public double profit;

    public ReportResponse (
            int VehicleId,
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
    public int packageId;

    public Package(
            String pName,
            int pId
    ) {
        name = pName;
        packageId = pId;
    }
}