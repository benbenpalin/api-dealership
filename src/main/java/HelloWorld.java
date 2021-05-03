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

        get("api/timeslot", (req, res) -> {
            String totalTime = req.queryParams("totalTime");
            String date = req.queryParams("date");

            Timeslot slot1 = new Timeslot(1, "1:00PM", "2:00 PM");
            Timeslot slot2 = new Timeslot(2, "2:00PM", "3:00 PM");
            Timeslot[] tasksInPackage = {slot1, slot2};


            Timeslot[] timeslots = {slot1, slot2};

            TimeslotResponse response = new TimeslotResponse(timeslots);


            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, TimeslotResponse.class);
        });

        post("api/dropoff", (req, res) -> {
            DropoffRequest dropBody = gson.fromJson(req.body(), DropoffRequest.class);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "";
        });

        post("api/purchase", (req, res) -> {
            PurchaseRequest purchaseBody = gson.fromJson(req.body(), PurchaseRequest.class);

            String[] customers = {"Barack", "Michel"};

            PurchaseResponse response = new PurchaseResponse(customers, 1, "5/2/2020","Toyota", "Camry", "2015", "Blue", "1234567", "DC");

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, PurchaseResponse.class);
        });

        post("api/makeappointment", (req, res) -> {
            AppointmentRequest appointmentBody = gson.fromJson(req.body(), AppointmentRequest.class);

            AppointmentResponse response = new AppointmentResponse(1);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, AppointmentResponse.class);
        });

        post("api/addpart", (req, res) -> {
            AddPartRequest addpartBody = gson.fromJson(req.body(), AddPartRequest.class);



            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "";
        });

        post("api/addtask", (req, res) -> {
            AddOrCompleteTask addtaskBody = gson.fromJson(req.body(), AddOrCompleteTask.class);



            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "";
        });

        post("api/completetask", (req, res) -> {
            AddOrCompleteTask completetaskBody = gson.fromJson(req.body(), AddOrCompleteTask.class);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "";
        });

        post("api/completeappointment", (req, res) -> {
            CompleteAppointmentRequest completeAppointmentBody = gson.fromJson(req.body(), CompleteAppointmentRequest.class);

            String[] customers = {"Barack", "Michel"};

            FinishedTest test1 = new FinishedTest("Brake Test", 10, 100, "passed");
            FinishedTest test2 = new FinishedTest("Engine Test", 10, 100, "failed");

            FinishedTest[] tests = {test1, test2};

            FinishedReplacement rep1 = new FinishedReplacement("Oil Change", 15, 1, "oil", 75);
            FinishedReplacement rep2 = new FinishedReplacement("Enginer Replace", 75, 750, "enginer", 1000);

            FinishedReplacement[] reps= {rep1, rep2};
            CompleteAppointmentResponse response = new CompleteAppointmentResponse(customers, tests, reps, "10:00 AM", "2:00 PM");

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, CompleteAppointmentResponse.class);
        });

    }
}

class CompleteAppointmentRequest{
    public int appointmentId;
    public String pickUpTime;

    public CompleteAppointmentRequest(int appointmentId, String pickUpTime) {
        this.appointmentId = appointmentId;
        this.pickUpTime = pickUpTime;
    }
}

class CompleteAppointmentResponse{
    public String[] customerNames;
    public FinishedTest[] tests;
    public FinishedReplacement[] replacements;
    public String dropOff;
    public String pickUp;

    public CompleteAppointmentResponse(String[] customerNames, FinishedTest[] tests, FinishedReplacement[] replacements, String dropOff, String pickUp) {
        this.customerNames = customerNames;
        this.tests = tests;
        this.replacements = replacements;
        this.dropOff = dropOff;
        this.pickUp = pickUp;
    }
}

class FinishedTest {
    public String taskName;
    public int timeToComplete;
    public int laborCost;
    public String testStatus;

    public FinishedTest(String taskName, int timeToComplete, int laborCost, String testStatus) {
        this.taskName = taskName;
        this.timeToComplete = timeToComplete;
        this.laborCost = laborCost;
        this.testStatus = testStatus;
    }
}

class FinishedReplacement {
    public String taskName;
    public int timeToComplete;
    public int laborCost;
    public String partName;
    public double costOfPart;

    public FinishedReplacement(String taskName, int timeToComplete, int laborCost, String partName, double costOfPart) {
        this.taskName = taskName;
        this.timeToComplete = timeToComplete;
        this.laborCost = laborCost;
        this.partName = partName;
        this.costOfPart = costOfPart;
    }
}

class AddOrCompleteTask {
    public int taskId;
    public int appointmentId;

    public AddOrCompleteTask(int taskId, int appointmentId) {
        this.taskId = taskId;
        this.appointmentId = appointmentId;
    }
}

class AddPartRequest{
    public int appointmentId;
    public int partId;

    public AddPartRequest(int appointmentId, int partId) {
        this.appointmentId = appointmentId;
        this.partId = partId;
    }
}

class NewCar{
    public boolean isNew;
    public int carId;
    public int vehicleId;
    public String licensePlateNumber;
    public String licensePlateState;
    public String color;
    public String odometer;

    public NewCar(boolean isNew, int carId, int vehicleId, String licensePlateNumber, String licensePlateState, String color, String odometer) {
        this.isNew = isNew;
        this.carId = carId;
        this.vehicleId = vehicleId;
        this.licensePlateNumber = licensePlateNumber;
        this.licensePlateState = licensePlateState;
        this.color = color;
        this.odometer = odometer;
    }
}

class AppointmentRequest{
    public CustomerInfo customer;
    public NewCar car;
    public int pakcageId;
    public String[] tasks;
    public int timeslotId;

    public AppointmentRequest(CustomerInfo customer, NewCar car, int pakcageId, String[] tasks, int timeslotId) {
        this.customer = customer;
        this.car = car;
        this.pakcageId = pakcageId;
        this.tasks = tasks;
        this.timeslotId = timeslotId;
    }
}

class AppointmentResponse{
    public int appointmentId;

    public AppointmentResponse(int appointmentId) {
        this.appointmentId = appointmentId;
    }
}

class TimeslotRequest{
    public String totalTime;
    public String date;

    public TimeslotRequest(String totalTime, String date) {
        this.totalTime = totalTime;
        this.date = date;
    }
}

class Timeslot {
    public int timeslotId;
    public String startTime;
    public String endTime;

    public Timeslot(int timeslotId, String startTime, String endTime) {
        this.timeslotId = timeslotId;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}

class TimeslotResponse{
    public Timeslot[] timeslots;

    public TimeslotResponse(Timeslot[] timeslots) {
        this.timeslots = timeslots;
    }
}

class NewCustomer{
    public String firstName;
    public String middleInitial;
    public String lastName;
    public String phoneNumber;
    public String streetAddress;
    public String city;
    public String state;
    public String zipCode;

    public NewCustomer(String firstName, String middleInitial, String lastName, String phoneNumber, String streetAddress, String city, String state, String zipCode) {
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }
}
// TODO add carID and salePrice to purchase
class PurchaseRequest{
    public CustomerInfo customer;
    public String carId;
    public double salePrice;

    public PurchaseRequest(CustomerInfo customer, String carId, double salePrice) {
        this.customer = customer;
        this.carId = carId;
        this.salePrice = salePrice;
    }
}

class CustomerInfo {
    public boolean isNew;
    public String[] customerId[];
    public NewCustomer[] newCustomers;

    public CustomerInfo(boolean isNew, String[][] customerId, NewCustomer[] newCustomers) {
        this.isNew = isNew;
        this.customerId = customerId;
        this.newCustomers = newCustomers;
    }
}

class PurchaseResponse {
    public String[] customerNames;
    public int purchaseId;
    public String dateOfSale;
    public String make;
    public String model;
    public String year;
    public String color;
    public String licensePlateNumber;
    public String licensePlateState;

    public PurchaseResponse(String[] customerNames, int purchaseId, String dateOfSale, String make, String model, String year, String color, String licensePlateNumber, String licensePlateState) {
        this.customerNames = customerNames;
        this.purchaseId = purchaseId;
        this.dateOfSale = dateOfSale;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.licensePlateNumber = licensePlateNumber;
        this.licensePlateState = licensePlateState;
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