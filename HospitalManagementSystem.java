import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HospitalManagementSystem {

    // Define SimpleDateFormat to parse dates
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // Define lists and maps for data storage
    private static final List<Patient> patients = new ArrayList<>();
    private static final List<Appointment> appointments = new ArrayList<>();
    private static final Map<String, EHR> ehrRecords = new HashMap<>();
    private static final Map<String, Billing> billings = new HashMap<>();
    private static final List<InventoryItem> inventory = new ArrayList<>();
    private static final List<Staff> staff = new ArrayList<>();

    public static void main(String[] args) {
        loadData();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            showMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    registerPatient(scanner);
                    break;
                case 2:
                    scheduleAppointment(scanner);
                    break;
                case 3:
                    updateEHR(scanner);
                    break;
                case 4:
                    manageBilling(scanner);
                    break;
                case 5:
                    manageInventory(scanner);
                    break;
                case 6:
                    manageStaff(scanner);
                    break;
                case 7:
                    saveData();
                    System.out.println("Data saved successfully.");
                    break;
                case 8:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice.");
                    break;
            }
        }
    }

    // Display the main menu
    private static void showMenu() {
        System.out.println("Hospital Management System");
        System.out.println("1. Register Patient");
        System.out.println("2. Schedule Appointment");
        System.out.println("3. Update EHR");
        System.out.println("4. Manage Billing");
        System.out.println("5. Manage Inventory");
        System.out.println("6. Manage Staff");
        System.out.println("7. Save Data");
        System.out.println("8. Exit");
        System.out.print("Enter your choice: ");
    }

    // Register a new patient
    private static void registerPatient(Scanner scanner) {
        System.out.print("Enter patient ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.print("Enter patient contact info: ");
        String contactInfo = scanner.nextLine();
        patients.add(new Patient(id, name, contactInfo));
        ehrRecords.put(id, new EHR(id));
        billings.put(id, new Billing(id));
        System.out.println("Patient registered successfully.");
    }

    // Schedule a new appointment
    private static void scheduleAppointment(Scanner scanner) {
        System.out.print("Enter patient ID: ");
        String patientId = scanner.nextLine();
        if (!ehrRecords.containsKey(patientId)) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.print("Enter appointment date (yyyy-MM-dd): ");
        Date date;
        try {
            date = DATE_FORMAT.parse(scanner.nextLine());
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
            return;
        }
        System.out.print("Enter doctor's name: ");
        String doctor = scanner.nextLine();
        System.out.print("Enter reason for appointment: ");
        String reason = scanner.nextLine();
        appointments.add(new Appointment(patientId, date, doctor, reason));
        System.out.println("Appointment scheduled successfully.");
    }

    // Update electronic health record
    private static void updateEHR(Scanner scanner) {
        System.out.print("Enter patient ID: ");
        String patientId = scanner.nextLine();
        if (!ehrRecords.containsKey(patientId)) {
            System.out.println("Patient not found.");
            return;
        }
        System.out.print("Enter date of record (yyyy-MM-dd): ");
        Date date;
        try {
            date = DATE_FORMAT.parse(scanner.nextLine());
        } catch (ParseException e) {
            System.out.println("Invalid date format.");
            return;
        }
        System.out.print("Enter record details: ");
        String record = scanner.nextLine();
        ehrRecords.get(patientId).addRecord(date, record);
        System.out.println("EHR updated successfully.");
    }

    // Manage billing
    private static void manageBilling(Scanner scanner) {
        System.out.print("Enter patient ID: ");
        String patientId = scanner.nextLine();
        if (!billings.containsKey(patientId)) {
            System.out.println("Patient not found.");
            return;
        }
        Billing billing = billings.get(patientId);
        System.out.print("Enter charge description: ");
        String description = scanner.nextLine();
        System.out.print("Enter charge amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        billing.addCharge(description, amount);
        System.out.println("Charge added successfully. Total amount: $" + billing.getTotalAmount());
    }

    // Manage inventory
    private static void manageInventory(Scanner scanner) {
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        InventoryItem item = findInventoryItem(itemName);
        if (item == null) {
            System.out.print("Enter quantity: ");
            int quantity = scanner.nextInt();
            System.out.print("Enter price: ");
            double price = scanner.nextDouble();
            scanner.nextLine(); // Consume newline
            inventory.add(new InventoryItem(itemName, quantity, price));
            System.out.println("Inventory item added successfully.");
        } else {
            System.out.print("Enter new quantity: ");
            int newQuantity = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            item.setQuantity(newQuantity);
            System.out.println("Inventory updated successfully.");
        }
    }

    // Find an inventory item by name
    private static InventoryItem findInventoryItem(String itemName) {
        for (InventoryItem item : inventory) {
            if (item.getItemName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    // Manage staff
    private static void manageStaff(Scanner scanner) {
        System.out.print("Enter staff ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter staff name: ");
        String name = scanner.nextLine();
        System.out.print("Enter staff role: ");
        String role = scanner.nextLine();
        staff.add(new Staff(id, name, role));
        System.out.println("Staff registered successfully.");
    }

    // Save data to file
    private static void saveData() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("hospitalData.dat"))) {
            out.writeObject(patients);
            out.writeObject(appointments);
            out.writeObject(ehrRecords);
            out.writeObject(billings);
            out.writeObject(inventory);
            out.writeObject(staff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load data from file
    private static void loadData() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("hospitalData.dat"))) {
            List<Patient> loadedPatients = (List<Patient>) in.readObject();
            List<Appointment> loadedAppointments = (List<Appointment>) in.readObject();
            Map<String, EHR> loadedEHRRecords = (Map<String, EHR>) in.readObject();
            Map<String, Billing> loadedBillings = (Map<String, Billing>) in.readObject();
            List<InventoryItem> loadedInventory = (List<InventoryItem>) in.readObject();
            List<Staff> loadedStaff = (List<Staff>) in.readObject();

            patients.addAll(loadedPatients);
            appointments.addAll(loadedAppointments);
            ehrRecords.putAll(loadedEHRRecords);
            billings.putAll(loadedBillings);
            inventory.addAll(loadedInventory);
            staff.addAll(loadedStaff);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Patient class
    private static class Patient implements Serializable {
        private String id;
        private String name;
        private String contactInfo;

        public Patient(String id, String name, String contactInfo) {
            this.id = id;
            this.name = name;
            this.contactInfo = contactInfo;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getContactInfo() {
            return contactInfo;
        }

        @Override
        public String toString() {
            return "Patient ID: " + id + ", Name: " + name + ", Contact Info: " + contactInfo;
        }
    }

    // Appointment class
    private static class Appointment implements Serializable {
        private String patientId;
        private Date date;
        private String doctor;
        private String reason;

        public Appointment(String patientId, Date date, String doctor, String reason) {
            this.patientId = patientId;
            this.date = date;
            this.doctor = doctor;
            this.reason = reason;
        }

        public String getPatientId() {
            return patientId;
        }

        public Date getDate() {
            return date;
        }

        public String getDoctor() {
            return doctor;
        }

        public String getReason
