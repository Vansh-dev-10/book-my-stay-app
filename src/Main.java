import java.util.HashMap;

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

    static class RoomInventory {

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
}