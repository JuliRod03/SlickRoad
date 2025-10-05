import java.awt.Point;

public class Segment {
    private int index;
    private Point position;
    private Store store;
    private Robot robot;
    private boolean occupied;
    
    public Segment(int index, Point position) {
        this.index = index;
        this.position = position;
        this.store = null;
        this.robot = null;
        this.occupied = false;
    }
    
    // =============================
    //        TIENDAS
    // =============================
    
    public boolean placeStore(Store store) {
        if (occupied) {
            return false; // Segmento ocupado
        }
        this.store = store;
        this.occupied = true;
        return true;
    }
    
    public Store removeStore() {
        Store removedStore = this.store;
        this.store = null;
        updateOccupiedStatus();
        return removedStore;
    }
    
    public boolean hasStore() {
        return store != null;
    }
    
    public Store getStore() {
        return store;
    }
    
    // =============================
    //        ROBOTS
    // =============================
    
    public boolean placeRobot(Robot robot) {
        if (occupied) {
            return false; // Segmento ocupado
        }
        this.robot = robot;
        this.occupied = true;
        return true;
    }
    
    public Robot removeRobot() {
        Robot removedRobot = this.robot;
        this.robot = null;
        updateOccupiedStatus();
        return removedRobot;
    }
    
    public boolean hasRobot() {
        return robot != null;
    }
    
    public Robot getRobot() {
        return robot;
    }
    
    // =============================
    //        UTILIDADES
    // =============================
    
    private void updateOccupiedStatus() {
        occupied = (store != null) || (robot != null);
    }
    
    public boolean isOccupied() {
        return occupied;
    }
    
    public boolean isEmpty() {
        return !occupied;
    }
    
    public int getIndex() {
        return index;
    }
    
    public Point getPosition() {
        return position;
    }
    
    public String getStatus() {
        if (store != null && robot != null) {
            return "Store + Robot"; // No debería pasar, pero por seguridad
        } else if (store != null) {
            return "Store (" + store.getCoins() + " coins)";
        } else if (robot != null) {
            return "Robot (" + robot.getDailyCoins() + " coins)";
        } else {
            return "Empty";
        }
    }
    
    public String toString() {
        return "Segment " + index + ": " + getStatus();
    }
}