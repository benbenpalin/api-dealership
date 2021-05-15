import static spark.Spark.*;
import com.google.gson.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class HelloWorld {
    public static void main(String[] args) {

        Gson gson = new Gson();

        // get report
        get("api/report", (req, res) -> {

            String startDate = req.queryParams("startDate");
            String endDate = req.queryParams("endDate");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("Select v.Vehicle_ID,make, model, year, count(v.vehicle_ID) AS Total_Sold, SUM(COST) - SUM(sales_price) AS Profit\n" +
                    "FROM car c, vehicle_type v, purchase p\n" +
                    "WHERE c.vehicle_ID = v.vehicle_Id\n" +
                    "AND p.car_ID = c.car_Id\n" +
                    "AND p.Date_Of_Purchase BETWEEN '" + startDate + "' and '" + endDate + "'\n" +
                    "GROUP BY v.Vehicle_ID");

            ArrayList<ReportRow> rows = new ArrayList<ReportRow>();

            while(rs.next()){
                ReportRow repres = new ReportRow(rs.getInt(1), rs.getString(2),rs.getString(3) ,rs.getString(4) , rs.getInt(5), rs.getDouble(6));
                rows.add(repres);
            }

            con.close();

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);



            ReportResponse response = new ReportResponse(rows);

           return gson.toJson(response, ReportResponse.class);
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

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            ResultSet rsIn=stmt.executeQuery("SELECT \n" +
                    "t.task_ID,\n" +
                    "concat(t.Name , ' ' , t.Task_type),\n" +
                    "t.Estd_Time\n" +
                    "FROM task t, recommends r\n" +
                    "WHERE  r.Package_ID = " + packageId +"\n" +
                    "AND t.task_ID = r.task_ID");

            ArrayList<PackageTask> inPackageTasks = new ArrayList<PackageTask>();

            while(rsIn.next()) {
                PackageTask task = new PackageTask(rsIn.getInt(1), rsIn.getString(2), rsIn.getInt(3));
                inPackageTasks.add(task);
            }

            Statement stmt2=con.createStatement();
            ResultSet rsNot=stmt2.executeQuery("SELECT \n" +
                    "t1.task_ID,\n" +
                    "concat(t1.Name , ' ' , t1.Task_type),\n" +
                    "t1.Estd_Time\n" +
                    "FROM task t1\n" +
                    "WHERE t1.task_ID NOT IN\n" +
                    "(SELECT \n" +
                    "t2.task_ID\n" +
                    "FROM task t2, recommends r\n" +
                    "WHERE  r.Package_ID = " + packageId +"\n" +
                    "AND t2.task_ID = r.task_ID)");


            ArrayList<PackageTask> notInPackageTasks = new ArrayList<PackageTask>();

            while(rsNot.next()) {
                PackageTask task = new PackageTask(rsNot.getInt(1), rsNot.getString(2), rsNot.getInt(3));
                notInPackageTasks.add(task);
            }

            con.close();

            TasksInPackageResponse response = new TasksInPackageResponse(inPackageTasks, notInPackageTasks);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, TasksInPackageResponse.class);
        });

        get("api/timeslots", (req, res) -> {
            String totalTime = req.queryParams("totalTime");
            String date = req.queryParams("date");

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            ResultSet rs=stmt.executeQuery("select \n" +
                    "Time_Slot_ID as 'Time Slot ID',\n" +
                    "Start_Time as 'Start Time',\n" +
                    "End_Time as 'End Time'\n" +
                    "from time_slot\n" +
                    "WHERE Date_Of = \'" + date + "\'\n" +
                    "AND HOUR(end_time) - Hour(Start_Time) >= " + totalTime);

            ArrayList<Timeslot> slots = new ArrayList<Timeslot>();

            while(rs.next()) {
                Timeslot slot = new Timeslot(rs.getInt(1), rs.getString(2), rs.getString(3));
                slots.add(slot);
            }

            con.close();

            TimeslotResponse response = new TimeslotResponse(slots);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, TimeslotResponse.class);
        });

        post("api/dropoff", (req, res) -> {
            DropoffRequest dropBody = gson.fromJson(req.body(), DropoffRequest.class);

            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");

            LocalTime now = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;
            String time  = now.format(formatter);

            Statement st=con.createStatement();
            st.executeUpdate("Update appointment Set Drop_Off = '" + time + "' WHERE Appointment_ID = " + dropBody.appointmentId);

            con.close();

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "200";
        });

        post("api/purchase", (req, res) -> {
            PurchaseRequest purchaseBody = gson.fromJson(req.body(), PurchaseRequest.class);

            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");

            int[] customerIds = new int[2];

            if(purchaseBody.customer.isNew) {
                NewCustomer info = purchaseBody.customer.newCustomers[0];

                Statement stmtCustomer1=con.createStatement();
                stmtCustomer1.executeUpdate("Insert Into customer(F_Name, M_Init, L_Name, Phone, Address)\n" +
                        "Values ('" + info.firstName + "', '" + info.middleInitial + "', '" + info.lastName + "', '" + info.phoneNumber + "', '" + info.streetAddress + " "  + info.city + ", " + info.state + " " + info.zipcode  +"')");

                Statement stmtId1=con.createStatement();
                ResultSet res1 = stmtId1.executeQuery("SELECT LAST_INSERT_ID();");

                while(res1.next()){
                    customerIds[0] = res1.getInt(1);
                }


                if(purchaseBody.customer.newCustomers.length == 2) {
                    NewCustomer info2 = purchaseBody.customer.newCustomers[1];

                    Statement stmtCustomer2=con.createStatement();
                    stmtCustomer2.executeUpdate("Insert Into customer(F_Name, M_Init, L_Name, Phone, Address)\n" +
                            "Values ('" + info2.firstName + "', '" + info2.middleInitial + "', '" + info2.lastName + "', '" + info2.phoneNumber + "', '" + info2.streetAddress + " "  + info2.city + ", " + info2.state + " " + info2.zipcode  +"')");

                    Statement stmtId2=con.createStatement();
                    ResultSet res2 = stmtId2.executeQuery("SELECT LAST_INSERT_ID();");

                    while(res2.next()){
                        customerIds[1] = res2.getInt(1);
                    }
                }
            } else {
                customerIds[0] = purchaseBody.customer.customerId[0];
                customerIds[1] = purchaseBody.customer.customerId[1];
            }

            for (int id : customerIds) {
                String idval = "(" + id +", " + purchaseBody.carId + ")\n";


                Statement stmtOwns=con.createStatement();
                stmtOwns.executeUpdate("Insert Into owns \n Values" + idval);
            }

            Statement stp=con.createStatement();
            stp.executeUpdate("Insert Into purchase(Car_ID, Date_Of_Purchase, Sales_Price) \n Values(" + purchaseBody.carId + ", CURDATE(), " + purchaseBody.salePrice + ")" );

            Statement stmtId1=con.createStatement();
            ResultSet res1 = stmtId1.executeQuery("SELECT LAST_INSERT_ID();");
            int purchaseId = 0;
            while(res1.next()){
                purchaseId = res1.getInt(1);
            }


            Statement stcust = con.createStatement();
            ResultSet rs = stcust.executeQuery("SELECT F_Name, L_Name \n" +
                    "FROM customer \n" +
                    "WHERE (Customer_ID = " + customerIds[0] + " OR Customer_ID = " + customerIds[1] + ")");

            ArrayList<String> customers = new ArrayList<String>();
            while(rs.next()){
                customers.add(rs.getString(1) + " " + rs.getString(2));
            }

            Statement stCar = con.createStatement();
            ResultSet rsC = stCar.executeQuery("SELECT Color, License_Plate_State, License_Plate_Number, Make, Model,Year \n" +
                    "FROM car, vehicle_type \n" +
                    "WHERE Car_ID = " + purchaseBody.carId + "\n" +
                    "AND Vehicle_Type.vehicle_ID = car.Vehicle_ID");

            LocalDate now = LocalDate.now();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String date  = now.format(formatter);

            ArrayList<PurchaseResponse> response = new ArrayList<PurchaseResponse>();

            while(rsC.next()){
                PurchaseResponse p = new PurchaseResponse(customers, purchaseId, date, rsC.getString(4),rsC.getString(5),rsC.getInt(6),rsC.getString(1),rsC.getString(3), rsC.getString(2));
                response.add(p);
            }


            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response.get(0), PurchaseResponse.class);
        });

        post("api/bookappointment", (req, res) -> {
            AppointmentRequest appointmentBody = gson.fromJson(req.body(), AppointmentRequest.class);

            int[] customerIds = new int[2];

            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");

            if(appointmentBody.customer.isNew) {
                NewCustomer info = appointmentBody.customer.newCustomers[0];

                Statement stmtCustomer1=con.createStatement();
                stmtCustomer1.executeUpdate("Insert Into customer(F_Name, M_Init, L_Name, Phone, Address)\n" +
                        "Values ('" + info.firstName + "', '" + info.middleInitial + "', '" + info.lastName + "', '" + info.phoneNumber + "', '" + info.streetAddress + " "  + info.city + ", " + info.state + " " + info.zipcode  +"')");

                Statement stmtId1=con.createStatement();
                ResultSet res1 = stmtId1.executeQuery("SELECT LAST_INSERT_ID();");

                while(res1.next()){
                    customerIds[0] = res1.getInt(1);
                }


                if(appointmentBody.customer.newCustomers.length == 2) {
                    NewCustomer info2 = appointmentBody.customer.newCustomers[1];

                    Statement stmtCustomer2=con.createStatement();
                    stmtCustomer2.executeUpdate("Insert Into customer(F_Name, M_Init, L_Name, Phone, Address)\n" +
                            "Values ('" + info2.firstName + "', '" + info2.middleInitial + "', '" + info2.lastName + "', '" + info2.phoneNumber + "', '" + info2.streetAddress + " "  + info2.city + ", " + info2.state + " " + info2.zipcode  +"')");

                    Statement stmtId2=con.createStatement();
                    ResultSet res2 = stmtId2.executeQuery("SELECT LAST_INSERT_ID();");

                    while(res2.next()){
                        customerIds[1] = res2.getInt(1);
                    }
                }
            }

            int carId = appointmentBody.car.carId;

            if (appointmentBody.car.isNew) {
                NewCar car = appointmentBody.car;

                Statement stmtCar=con.createStatement();
                stmtCar.executeUpdate("Insert Into car(Vehicle_ID, Color, Odometer, Is_In_Inventory, License_Plate_State, License_Plate_Number)\n" +
                        "Values ('" + car.vehicleId + "', '" + car.color + "', '" + car.odometer + "', '" + 0 + "', '" + car.licensePlateState + "', '"  + car.licensePlateNumber + "')");

                Statement stmtCarId=con.createStatement();
                ResultSet rescarId = stmtCarId.executeQuery("SELECT LAST_INSERT_ID();");

                while(rescarId.next()){
                    carId = rescarId.getInt(1);
                }

                for (int id : customerIds) {
                    String idval = "(" + id +", " + carId + ")\n";


                    Statement stmtOwns=con.createStatement();
                    stmtOwns.executeUpdate("Insert Into owns \n Values" + idval);
                }
            }

            Statement stApp=con.createStatement();
            stApp.executeUpdate("Insert Into appointment(Car_ID, Time_Slot_ID)\n" +
                    "Values(" + carId + ", " + appointmentBody.timeslotId + ")");

            Statement stmtId1=con.createStatement();
            ResultSet res1 = stmtId1.executeQuery("SELECT LAST_INSERT_ID();");

            int appointmentId = 0;

            while(res1.next()){
                appointmentId = res1.getInt(1);
            }


            for (int id : appointmentBody.tasks) {
                String tval = "(" + appointmentId +", " + id + ")\n";

                Statement StSched=con.createStatement();
                StSched.executeUpdate("Insert Into scheduled(Appointment_ID, Task_ID) \n Values" + tval);
            }

            con.close();

            AppointmentResponse response = new AppointmentResponse(appointmentId);

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return gson.toJson(response, AppointmentResponse.class);
        });

        post("api/addpart", (req, res) -> {
            AddPartRequest addpartBody = gson.fromJson(req.body(), AddPartRequest.class);

            String values = addpartBody.appointmentId + "," + addpartBody.partId;

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            stmt.executeUpdate("Insert Into was_replaced\n" +
                    "Values (" + values + ")");
            con.close();

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "200";
        });

        post("api/addtask", (req, res) -> {
            AddOrCompleteTask addtaskBody = gson.fromJson(req.body(), AddOrCompleteTask.class);

            String values = "(" + addtaskBody.appointmentId + ", " + addtaskBody.taskId + ", null, 'F', null)";

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            stmt.executeUpdate("INSERT INTO scheduled\n" +
                    "VALUES" + values);
            con.close();

            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "200";
        });

        post("api/completetask", (req, res) -> {
            AddOrCompleteTask completetaskBody = gson.fromJson(req.body(), AddOrCompleteTask.class);

            String testStatus;

            if (completetaskBody.isTest && (completetaskBody.testStatus.equals("Passed"))) {
                testStatus = ", Test_Failed=1";
            } else  if (completetaskBody.isTest && (completetaskBody.testStatus.equals("Failed"))) {
                testStatus = ", Test_Failed=1";
            } else  {testStatus = "";}

            String values = "Complete = 1" + testStatus;

            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            stmt.executeUpdate("Update scheduled \n" +
                    "SET " + values + "\n" +
                    "WHERE Appointment_ID = "+ completetaskBody.appointmentId +"\n" +
                    "AND Task_ID = " + completetaskBody.taskId);

            con.close();


            res.header("Access-Control-Allow-Origin", "*");
            res.header("Access-Control-Allow-Headers", "Content-Type");
            res.status(200);

            return "200";
        });

        post("api/completeappointment", (req, res) -> {
            CompleteAppointmentRequest completeAppointmentBody = gson.fromJson(req.body(), CompleteAppointmentRequest.class);

            int appointmentId = completeAppointmentBody.appointmentId;

           LocalTime now = LocalTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

            String time  = now.format(formatter);



            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con=DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/mydb","user","user");
            Statement stmt=con.createStatement();
            stmt.executeUpdate("Update appointment \n" +
                    "SET " + "Pick_Up = '" + time + "'\n" +
                    "WHERE Appointment_ID = "+ appointmentId);

            con.close();

            //TODO bill



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
    public String testStatus;

    public AddOrCompleteTask(int taskId, int appointmentId, boolean isTest, String testStatus) {
        this.taskId = taskId;
        this.appointmentId = appointmentId;
        this.isTest = isTest;
        this.testStatus = testStatus;
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

    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "customer=" + customer +
                ", car=" + car +
                ", packageId=" + packageId +
                ", tasks=" + Arrays.toString(tasks) +
                ", timeslotId=" + timeslotId +
                '}';
    }

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
    public  ArrayList<Timeslot> timeslots;

    public TimeslotResponse( ArrayList<Timeslot> timeslots) {
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
    public String zipcode;

    @Override
    public String toString() {
        return "NewCustomer{" +
                "firstName='" + firstName + '\'' +
                ", middleInitial='" + middleInitial + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", streetAddress='" + streetAddress + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipcode + '\'' +
                '}';
    }

    public NewCustomer(String firstName, String middleInitial, String lastName, String phoneNumber, String streetAddress, String city, String state, String zipcode) {
        this.firstName = firstName;
        this.middleInitial = middleInitial;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipcode = zipcode;
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
    public ArrayList<String> customerNames;
    public int purchaseId;
    public String dateOfSale;
    public String make;
    public String model;
    public int year;
    public String color;
    public String licensePlateNumber;
    public String licensePlateState;

    public PurchaseResponse(ArrayList<String> customerNames, int purchaseId, String dateOfSale, String make, String model, int year, String color, String licensePlateNumber, String licensePlateState) {
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
    public ArrayList<PackageTask> inPackage;
    public ArrayList<PackageTask> notInPackage;

    public TasksInPackageResponse(ArrayList<PackageTask> inPackage, ArrayList<PackageTask> notInPackage) {
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

class ReportResponse{
    ArrayList<ReportRow> rows;

    public ReportResponse(ArrayList<ReportRow> rows) {
        this.rows = rows;
    }
}

class ReportRow {
    public int vehicleId;
    public String make;
    public String model;
    public String year;
    public int totalSold;
    public double profit;

    public ReportRow(
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