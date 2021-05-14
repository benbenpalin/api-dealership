import static spark.Spark.*;
import com.google.gson.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class HelloWorld {
    public static void main(String[] args) {

        Gson gson = new Gson();

        // get report
        get("api/report", (req, res) -> {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection con=DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/mydb","user","user");
                Statement stmt=con.createStatement();
                ResultSet rs=stmt.executeQuery("select * from appointment");
                while(rs.next())
                    System.out.println(rs.getInt(1));

                con.close();
            } catch (Exception e){System.out.println(e);}


            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            ReportResponse repres = new ReportResponse(1, "bmw","x5" ,"2020" , 10, 12.0);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

           return gson.toJson(repres, ReportResponse.class);
        });

        get("api/packages", (req, res) -> {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select \n" +
                    "\n" +
                    "Name ,\n" +
                    "Package_ID as `Package ID`\n" +
                    "\n" +
                    "from package;");

            ArrayList<Package> packages = new ArrayList<Package>();

            while(rs.next()) {
                Package pack = new Package(rs.getString(1), rs.getInt(2));
                packages.add(pack);
            }
            con.close();

            PackagesResponse packagesResponse = new PackagesResponse(packages);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(packagesResponse, PackagesResponse.class);
        });

        get("api/appointments", (req, res) -> {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select \n" +
                    "\n" +
                    "Appointment_ID as 'Appointment ID',\n" +
                    "Color,\n" +
                    "Year,\n" +
                    "Make,\n" +
                    "Model,\n" +
                    "car.Vehicle_ID as 'Vehicle ID'\n" +
                    "\n" +
                    "from appointment\n" +
                    "inner join Car ON car.Car_ID = appointment.Car_ID\n" +
                    "inner join vehicle_type ON vehicle_type.Vehicle_ID = car.Vehicle_ID\n" +
                    ";");

            ArrayList<Appointment> appointments = new ArrayList<Appointment>();

            while(rs.next()) {
                Appointment app = new Appointment(rs.getInt(1), rs.getString(2), rs.getString(3).substring(0,4), rs.getString(4), rs.getString(5), rs.getInt(6));
                appointments.add(app);
            }
            con.close();

            AppointmentsResponse appointmentsResponse = new AppointmentsResponse(appointments);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(appointmentsResponse, AppointmentsResponse.class);
        });

        get("api/vehicletypes", (req, res) -> {

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select\n" +
                    "\n" +
                    "Make,\n" +
                    "Model,\n" +
                    "Year,\n" +
                    "vehicle_Id as `Vehicle ID`\n" +
                    "\n" +
                    "from vehicle_type;");

            ArrayList<VehicleType> vehicles = new ArrayList<VehicleType>();

            while(rs.next()) {
                VehicleType vehicle = new VehicleType(rs.getString(1), rs.getString(2), rs.getString(3).substring(0,4), rs.getInt(4));
                vehicles.add(vehicle);
            }
            con.close();

            VehicleTypeResponse vehicleTypeResponse = new VehicleTypeResponse(vehicles);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(vehicleTypeResponse, VehicleTypeResponse.class);
        });

        get("api/appointmenttasks", (req, res) -> {

            String appointmentId = req.queryParams("appointmentId");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            ResultSet rsTest=stmt.executeQuery("select\n" +
                    "scheduled.Task_ID as 'Task ID',\n" +
                    "concat(t1.Name , ' ' , t1.Task_type) as 'Task Name' ,\n" +
                    "failure_requires.Part_Replacement_ID,\n" +
                    "concat(t2.Name , ' ' , t2.Task_type) as 'Replacement_Name'\n" +
                    "from scheduled, task t1, task t2, failure_requires\n" +
                    "WHERE t1.Task_ID = scheduled.Task_ID\n" +
                    "AND failure_requires.Test_Task_ID = scheduled.Task_ID\n" +
                    "AND failure_requires.Part_Replacement_ID = t2.Task_ID\n" +
                    "AND t1.Task_Type = 'Test' \n" +
                    "AND Appointment_ID = " + appointmentId);

            ArrayList<TestTask> tests = new ArrayList<TestTask>();

            while(rsTest.next()) {
                TestTask test = new TestTask(rsTest.getInt(1), rsTest.getString(2), rsTest.getInt(3), rsTest.getString(4));
                tests.add(test);
            }

            Statement stmt2=con.createStatement();
            ResultSet rsRep=stmt2.executeQuery("Select \n" +
                    "s.task_ID,\n" +
                    "t.Name,\n" +
                    "p.part_ID,\n" +
                    "p.Name,\n" +
                    "p.Cost_Of_Part\n" +
                    "FROM scheduled s, appointment a, task t, car c, used_in u, part p\n" +
                    "WHERE s.appointment_ID = " + appointmentId + "\n" +
                    "AND t.Task_Type = 'Replacement'\n" +
                    "AND t.Task_ID = s.task_ID\n" +
                    "AND s.Appointment_ID = a.Appointment_ID\n" +
                    "AND a.car_ID = c.Car_ID\n" +
                    "AND c.Vehicle_ID = u.Vehicle_ID\n" +
                    "AND t.task_ID = p.Task_ID\n" +
                    "AND p.part_ID = u.part_ID");


            ArrayList<ReplacementTask> reps = new ArrayList<ReplacementTask>();

            while(rsRep.next()) {
                ReplacementTask rep = new ReplacementTask(rsRep.getInt(1), rsRep.getString(2), rsRep.getInt(3), rsRep.getString(4), rsRep.getInt(5));
                reps.add(rep);
            }

            con.close();

            AppointmentTasksResponse appTasksRes = new AppointmentTasksResponse(tests, reps);


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

        get("api/timeslots", (req, res) -> {
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

            return "200";
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

        post("api/bookappointment", (req, res) -> {
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

            return "200";
        });

        post("api/addtask", (req, res) -> {
            AddOrCompleteTask addtaskBody = gson.fromJson(req.body(), AddOrCompleteTask.class);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "200";
        });

        post("api/completetask", (req, res) -> {
            AddOrCompleteTask completetaskBody = gson.fromJson(req.body(), AddOrCompleteTask.class);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "200";
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
            CompleteAppointmentResponse response = new CompleteAppointmentResponse(customers, tests, reps, "10:00 AM", "2:00 PM", "05/09/2021");

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, CompleteAppointmentResponse.class);
        });

    }
}

class CompleteAppointmentRequest{
    public int appointmentId;

    public CompleteAppointmentRequest(int appointmentId, String pickUpTime) {
        this.appointmentId = appointmentId;
    }
}

class CompleteAppointmentResponse{
    public String[] customerNames;
    public FinishedTest[] tests;
    public FinishedReplacement[] replacements;
    public String dropOff;
    public String pickUp;
    public String date;

    public CompleteAppointmentResponse(String[] customerNames, FinishedTest[] tests, FinishedReplacement[] replacements, String dropOff, String pickUp, String date) {
        this.customerNames = customerNames;
        this.tests = tests;
        this.replacements = replacements;
        this.dropOff = dropOff;
        this.pickUp = pickUp;
        this.date = date;
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
    public boolean isTest;
    public String testPassed;

    public AddOrCompleteTask(int taskId, int appointmentId, boolean isTest, String testPassed) {
        this.taskId = taskId;
        this.appointmentId = appointmentId;
        this.isTest = isTest;
        this.testPassed = testPassed;
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
    public int odometer;

    public NewCar(boolean isNew, int carId, int vehicleId, String licensePlateNumber, String licensePlateState, String color, int odometer) {
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
    public int packageId;
    public int[] tasks;
    public int timeslotId;

    public AppointmentRequest(CustomerInfo customer, NewCar car, int packageId, int[] tasks, int timeslotId) {
        this.customer = customer;
        this.car = car;
        this.packageId = packageId;
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
    public int carId;
    public double salePrice;

    public PurchaseRequest(CustomerInfo customer, int carId, double salePrice) {
        this.customer = customer;
        this.carId = carId;
        this.salePrice = salePrice;
    }
}

class CustomerInfo {
    public boolean isNew;
    public int[] customerId;
    public NewCustomer[] newCustomers;

    public CustomerInfo(boolean isNew, int[] customerId, NewCustomer[] newCustomers) {
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
    public int costOfPart;

    public ReplacementTask(int taskId, String taskName, int partId, String partName, int costOfPart) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.partId = partId;
        this.partName = partName;
        this.costOfPart = costOfPart;
    }
}

class AppointmentTasksResponse{
    public ArrayList<TestTask> tests;
    public ArrayList<ReplacementTask> partReplacements;

    public AppointmentTasksResponse(ArrayList<TestTask> tests, ArrayList<ReplacementTask> partReplacements) {
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
    public ArrayList<VehicleType> vehicleTypes;

    public VehicleTypeResponse(ArrayList<VehicleType> vehicleTypes) {
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
    public ArrayList<Appointment> appointments;

    public AppointmentsResponse(ArrayList<Appointment> appointments) {
        this.appointments = appointments;
    }
}

class DropoffRequest {
    int appointmentId;

    public DropoffRequest(int appointmentId) {
        this.appointmentId = appointmentId;
    }
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
    public ArrayList<Package> packages;
    public PackagesResponse(
            ArrayList<Package> pkgs
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