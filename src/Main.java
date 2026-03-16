import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Main {

    public static void main(String[] args) {

        // ================= UC1 =================
        System.out.println("=================================");
        System.out.println("        BOOK MY STAY APP         ");
        System.out.println("   Hotel Booking System v1.0     ");
        System.out.println("=================================\n");

        // ================= UC2 =================
        System.out.println("Room Availability:\n");

        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        single.displayRoomDetails();
        System.out.println("Available: " + singleAvailable + "\n");

        doubleRoom.displayRoomDetails();
        System.out.println("Available: " + doubleAvailable + "\n");

        suite.displayRoomDetails();
        System.out.println("Available: " + suiteAvailable + "\n");

        // ================= UC3 =================
        System.out.println("\n===== UC3: Centralized Room Inventory =====");

        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        inventory.updateAvailability("Single Room", -1);

        System.out.println("\nAfter booking one Single Room:");
        inventory.displayInventory();


        // ================= UC4 =================
        System.out.println("\n===== UC4: Search Available Rooms =====");

        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        System.out.println("Available Rooms:");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.roomType);

            if (available > 0) {
                room.displayRoomDetails();
                System.out.println("Available: " + available + "\n");
            }
        }
        // ================= UC5 =================
        System.out.println("\n===== UC5: Booking Request Queue =====");

        // Create booking queue
        Queue<Reservation> bookingQueue = new LinkedList<>();

        // Guests submit booking requests
        bookingQueue.add(new Reservation("Rahul", "Single Room"));
        bookingQueue.add(new Reservation("Anita", "Double Room"));
        bookingQueue.add(new Reservation("Vikram", "Suite Room"));

        // Display queued requests
        System.out.println("Booking Requests in Queue:");

        for (Reservation r : bookingQueue) {
            System.out.println("Guest: " + r.guestName + " requested " + r.roomType);
            }
        System.out.println("\nTotal Requests Waiting: " + bookingQueue.size());
        // ================= UC6 =================
        System.out.println("\n===== UC6: Process Booking Requests =====");

        // Map to track allocated rooms by type
                Map<String, Set<String>> allocatedRooms = new HashMap<>();

        // Process queue
                while (!bookingQueue.isEmpty()) {

                    Reservation request = bookingQueue.poll(); // FIFO

                    String roomType = request.roomType;

                    int available = inventory.getAvailability(roomType);

                    if (available > 0) {

                        // Generate room ID
                        String roomId = roomType.replace(" ", "").substring(0,3).toUpperCase()
                                + "-" + (available);

                        // Get or create set for room type
                        allocatedRooms.putIfAbsent(roomType, new HashSet<>());

                        Set<String> roomSet = allocatedRooms.get(roomType);

                        // Ensure uniqueness
                        if (!roomSet.contains(roomId)) {

                            roomSet.add(roomId);

                            // update inventory
                            inventory.updateAvailability(roomType, -1);

                            System.out.println("Reservation Confirmed:");
                            System.out.println("Guest: " + request.guestName);
                            System.out.println("Room Type: " + roomType);
                            System.out.println("Room ID: " + roomId + "\n");

                        }

                    } else {

                        System.out.println("Reservation Failed for " + request.guestName +
                                " (No rooms available)\n");
                    }
                }

        // show final inventory
                System.out.println("Updated Inventory After Booking:");
                inventory.displayInventory();
        // ================= UC7 =================
                System.out.println("\n===== UC7: Add-On Services =====");

        // Map reservationID -> services
                Map<String, List<Service>> reservationServices = new HashMap<>();

        // Example reservation ID from UC6
                String reservationId = "SIN-4";

        // Guest selects services
                List<Service> services = new ArrayList<>();
                services.add(new Service("Breakfast", 20));
                services.add(new Service("Airport Pickup", 40));

        // Map services to reservation
                reservationServices.put(reservationId, services);

        // Display selected services
                System.out.println("Services for Reservation " + reservationId + ":");

                double totalCost = 0;

                for (Service s : reservationServices.get(reservationId)) {
                    System.out.println(s.serviceName + " - $" + s.price);
                    totalCost += s.price;
                }

                System.out.println("Total Add-On Cost: $" + totalCost);
        // ================= UC8 =================
                System.out.println("\n===== UC8: Booking History & Reports =====");

        // List to store confirmed bookings
                List<Reservation> bookingHistory = new ArrayList<>();

        // Example confirmed reservations (from UC6 logic)
                bookingHistory.add(new Reservation("Rahul", "Single Room"));
                bookingHistory.add(new Reservation("Anita", "Double Room"));
                bookingHistory.add(new Reservation("Vikram", "Suite Room"));

        // Admin views booking history
                System.out.println("Confirmed Booking History:");

                for (Reservation r : bookingHistory) {
                    System.out.println("Guest: " + r.guestName + " | Room Type: " + r.roomType);
                }

        // Generate simple report
                System.out.println("\nTotal Confirmed Bookings: " + bookingHistory.size());
        // ================= UC9 =================
        System.out.println("\n===== UC9: Booking Validation & Error Handling =====");

        try {

            String requestedRoom = "Luxury Room"; // invalid room type example
            int available = inventory.getAvailability(requestedRoom);

            // validation
            if (available == 0) {
                throw new InvalidBookingException("Invalid or unavailable room type: " + requestedRoom);
            }

            System.out.println("Booking request validated successfully.");

        } catch (InvalidBookingException e) {

            System.out.println("Booking Failed: " + e.getMessage());
        }

        System.out.println("System continues running safely.");
        // ================= UC10 =================
                System.out.println("\n===== UC10: Booking Cancellation =====");

        // Stack to track released room IDs
                Stack<String> releasedRooms = new Stack<>();

        // Example reservation cancellation
                String cancelRoomType = "Single Room";
                String cancelRoomId = "SIN-4";

                System.out.println("Cancellation requested for Room ID: " + cancelRoomId);

        // Validate reservation exists
                if (allocatedRooms.containsKey(cancelRoomType) &&
                        allocatedRooms.get(cancelRoomType).contains(cancelRoomId)) {

                    // remove from allocated set
                    allocatedRooms.get(cancelRoomType).remove(cancelRoomId);

                    // push to rollback stack
                    releasedRooms.push(cancelRoomId);

                    // restore inventory
                    inventory.updateAvailability(cancelRoomType, 1);

                    System.out.println("Reservation Cancelled Successfully.");
                    System.out.println("Released Room ID: " + cancelRoomId);

                } else {

                    System.out.println("Cancellation Failed: Reservation does not exist.");
                }

        // show updated inventory
                System.out.println("\nInventory After Cancellation:");
                inventory.displayInventory();
        // ================= UC11 =================
                System.out.println("\n===== UC11: Concurrent Booking Simulation =====");

        // shared booking queue
                Queue<Reservation> concurrentQueue = new LinkedList<>();

                concurrentQueue.add(new Reservation("Amit", "Single Room"));
                concurrentQueue.add(new Reservation("Priya", "Single Room"));
                concurrentQueue.add(new Reservation("Rohit", "Double Room"));
                concurrentQueue.add(new Reservation("Neha", "Suite Room"));

        // thread pool
                ExecutorService executor = Executors.newFixedThreadPool(3);

        // process requests concurrently
                for (int i = 0; i < 4; i++) {

                    executor.execute(() -> processBooking(concurrentQueue, inventory));
                }

                executor.shutdown();
    }
    static synchronized void processBooking(Queue<Reservation> queue, RoomInventory inventory) {

        if (queue.isEmpty()) {
            return;
        }

        Reservation r = queue.poll();

        if (r == null) return;

        int available = inventory.getAvailability(r.roomType);

        if (available > 0) {

            inventory.updateAvailability(r.roomType, -1);

            System.out.println(
                    Thread.currentThread().getName() +
                            " confirmed booking for " +
                            r.guestName +
                            " (" + r.roomType + ")"
            );

        } else {

            System.out.println(
                    "No rooms available for " + r.guestName
            );
        }
        // ================= UC12 =================
                System.out.println("\n===== UC12: Persistence & Recovery =====");

                List<Reservation> persistenceHistory = new ArrayList<>();

                persistenceHistory.add(new Reservation("Rahul", "Single Room"));
                persistenceHistory.add(new Reservation("Anita", "Double Room"));

        // save state
                PersistenceService.saveState(inventory, persistenceHistory);

        // simulate restart
                Object[] restored = PersistenceService.loadState();

                if (restored != null) {

                    RoomInventory restoredInventory = (RoomInventory) restored[0];
                    List<Reservation> restoredHistory = (List<Reservation>) restored[1];

                    System.out.println("\nRecovered Inventory:");
                    restoredInventory.displayInventory();

                    System.out.println("\nRecovered Booking History:");

                    for (Reservation res : restoredHistory) {   // renamed variable
                        System.out.println(res.guestName + " booked " + res.roomType);
                    }
                }
    }


    // ================= UC2 Supporting Classes =================

    abstract static class Room {

        String roomType;
        int beds;
        double price;

        Room(String roomType, int beds, double price) {
            this.roomType = roomType;
            this.beds = beds;
            this.price = price;
        }

        void displayRoomDetails() {
            System.out.println("Room Type: " + roomType);
            System.out.println("Beds: " + beds);
            System.out.println("Price per night: $" + price);
        }
    }

    static class SingleRoom extends Room {
        SingleRoom() {
            super("Single Room", 1, 100);
        }
    }

    static class DoubleRoom extends Room {
        DoubleRoom() {
            super("Double Room", 2, 180);
        }
    }

    static class SuiteRoom extends Room {
        SuiteRoom() {
            super("Suite Room", 3, 300);
        }
    }

    // ================= UC3 Supporting Class =================

    static class RoomInventory implements Serializable {

        private HashMap<String, Integer> availability;

        RoomInventory() {
            availability = new HashMap<>();

            availability.put("Single Room", 5);
            availability.put("Double Room", 3);
            availability.put("Suite Room", 2);
        }

        int getAvailability(String roomType) {
            return availability.getOrDefault(roomType, 0);
        }

        void updateAvailability(String roomType, int change) {
            int current = availability.getOrDefault(roomType, 0);
            availability.put(roomType, current + change);
        }

        void displayInventory() {
            System.out.println("Current Room Inventory:");
            for (String roomType : availability.keySet()) {
                System.out.println(roomType + " -> " + availability.get(roomType));
            }
        }
    }
    // ================= UC5 Supporting Class =================

    static class Reservation implements Serializable {

        String guestName;
        String roomType;

        Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }
    }
    // ================= UC7 Supporting Class =================

    static class Service {

        String serviceName;
        double price;

        Service(String serviceName, double price) {
            this.serviceName = serviceName;
            this.price = price;
        }
    }
    // ================= UC9 Supporting Class =================

    static class InvalidBookingException extends Exception {

        InvalidBookingException(String message) {
            super(message);
        }
    }
    static class PersistenceService {

        static void saveState(RoomInventory inventory, List<Reservation> history) {

            try {

                ObjectOutputStream out =
                        new ObjectOutputStream(new FileOutputStream("hotel_state.dat"));

                out.writeObject(inventory);
                out.writeObject(history);

                out.close();

                System.out.println("System state saved successfully.");

            } catch (Exception e) {

                System.out.println("Error saving system state.");
            }
        }

        static Object[] loadState() {

            try {

                ObjectInputStream in =
                        new ObjectInputStream(new FileInputStream("hotel_state.dat"));

                RoomInventory inventory = (RoomInventory) in.readObject();
                List<Reservation> history = (List<Reservation>) in.readObject();

                in.close();

                System.out.println("System state restored successfully.");

                return new Object[]{inventory, history};

            } catch (Exception e) {

                System.out.println("No previous state found. Starting fresh.");
                return null;
            }
        }
    }
}