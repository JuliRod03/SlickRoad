import java.util.Random;
import java.util.ArrayList;

public class Game {
    private SilkRoad silkRoad;
    private int dayCounter;
    private int totalCollected; // tenges acumulados globales
    private Random random;
    
    // Barra de progreso
    private ProfitBar profitBar;
    
    public Game(int roadLength) {
        silkRoad = new SilkRoad(roadLength);
        dayCounter = 0;
        totalCollected = 0;
        random = new Random();
        
        // Inicializar barra de progreso
        profitBar = new ProfitBar();
    }
    
    /*
     * Mostrar la carretera y todo lo que ya exista
     */
    public void start() {
        silkRoad.makeVisible();
        
        // Mostrar barra de progreso
        profitBar.makeVisible();
        updateProfitBar();
        
        System.out.println("Juego iniciado. Día " + dayCounter);
    }

    /*
     * Avanza un día: robots vuelven al inicio, tiendas se reabastecen, se puede añadir algo nuevo
     */
    public void nextDay() {
        dayCounter++;
        System.out.println("===== Día " + dayCounter + " =====");
        
        // Usar los métodos de SilkRoad
        silkRoad.returnRobots();
        silkRoad.resupplyStores();
        
        // Actualizar barra después de reabastecer
        updateProfitBar();
        
        System.out.println("Robots regresaron a su inicio y tiendas reabastecidas.");
        
        // Al final del día, se adiciona un nuevo objeto aleatorio
        addRandomObject();
    }
    
    /*
     * Agrega aleatoriamente una tienda o un robot
     */
    private void addRandomObject() {
        ArrayList<Integer> freeSegments = getFreeSegments();
        if (freeSegments.isEmpty()) {
            System.out.println("No hay espacio para nuevos objetos.");
            return;
        }
        
        int randomSegment = freeSegments.get(random.nextInt(freeSegments.size()));
        boolean crearTienda = random.nextBoolean(); // 50% tienda, 50% robot
        
        if (crearTienda) {
            int tenges = 10 + random.nextInt(91); // Entre 10 y 100 tenges
            silkRoad.placeStore(randomSegment, tenges);
        } else {
            silkRoad.placeRobot(randomSegment);
        }
        
        // Actualizar barra después de agregar objeto
        updateProfitBar();
    }
    
    /*
     * Mueve un robot y maneja el costo global
     */
    public void moveRobot(int location, int meters) {
        // Verificar que hay suficientes tenges para el movimiento
        if (totalCollected < meters) {
            System.out.println("No hay suficientes tenges para el movimiento. Necesarios: " + meters + ", Disponibles: " + totalCollected);
            return;
        }
        
        // Obtener tenges antes del movimiento
        int tengesBeforeMove = getDailyCollected();
        
        // Realizar el movimiento
        silkRoad.moveRobot(location, meters);
        
        // Pagar costo de movimiento
        totalCollected -= meters;
        
        // Obtener tenges después del movimiento (para ver si recogió algo)
        int tengesAfterMove = getDailyCollected();
        int tengesCollected = tengesAfterMove - tengesBeforeMove;
        
        // Sumar los tenges recolectados al total global
        totalCollected += tengesCollected;
        
        // Actualizar barra después del movimiento
        updateProfitBar();
        
        System.out.println("Movimiento completado. Costo: " + meters + " tenges. Total acumulado: " + totalCollected);
    }
    
    /*
     * Reinicia el juego completamente
     */
    public void resetGame() {
        silkRoad.reboot();
        dayCounter = 0;
        totalCollected = 0;
        
        // Resetear barra de progreso
        profitBar.reset();
        
        System.out.println("Juego reiniciado completamente.");
    }
    
    // =============================
    //   MÉTODOS PARA LA BARRA
    // =============================
    
    /*
     * Calcula el total de monedas disponibles en todas las tiendas
     */
    private int getTotalCoinsInStores() {
        int[][] storesData = silkRoad.stores();
        int total = 0;
        for (int i = 0; i < storesData.length; i++) {
            total += storesData[i][1]; // [1] contiene los tenges
        }
        return total;
    }
    
    /*
     * Actualiza la barra de progreso
     */
    private void updateProfitBar() {
        int maxPossible = getTotalCoinsInStores();
        int currentProfit = silkRoad.profit();
        
        profitBar.updateMaxProfit(maxPossible);
        profitBar.updateCurrentProfit(currentProfit);
    }
    
    // =============================
    //   MÉTODOS AUXILIARES
    // =============================
    
    /*
     * Obtiene el total de tenges recolectados por todos los robots en el día actual
     */
    public int getDailyCollected() {
        int[][] robotsData = silkRoad.robots();
        int total = 0;
        for (int i = 0; i < robotsData.length; i++) {
            total += robotsData[i][1]; // [1] contiene los tenges recolectados
        }
        return total;
    }
    
    /*
     * Verifica si un segmento está ocupado
     */
    public boolean isSegmentOccupied(int location) {
        // Verificar en tiendas
        int[][] storesData = silkRoad.stores();
        for (int i = 0; i < storesData.length; i++) {
            if (storesData[i][0] == location) { // [0] contiene la ubicación
                return true;
            }
        }
        
        // Verificar en robots
        int[][] robotsData = silkRoad.robots();
        for (int i = 0; i < robotsData.length; i++) {
            if (robotsData[i][0] == location) { // [0] contiene la ubicación
                return true;
            }
        }
        
        return false;
    }
    
    /*
     * Obtiene lista de segmentos libres
     */
    public ArrayList<Integer> getFreeSegments() {
        ArrayList<Integer> freeSegments = new ArrayList<>();
        
        // Necesitamos saber cuántos segmentos hay en total
        // Por simplicidad, asumiremos un rango basado en el tamaño de la carretera
        int maxSegments = getEstimatedSegmentCount();
        
        for (int i = 0; i < maxSegments; i++) {
            if (!isSegmentOccupied(i)) {
                freeSegments.add(i);
            }
        }
        return freeSegments;
    }
    
    /*
     * Estima el número de segmentos basado en las tiendas y robots existentes
     */
    private int getEstimatedSegmentCount() {
        int maxLocation = 0;
        
        // Buscar la ubicación más alta en tiendas
        int[][] storesData = silkRoad.stores();
        for (int i = 0; i < storesData.length; i++) {
            if (storesData[i][0] > maxLocation) {
                maxLocation = storesData[i][0];
            }
        }
        
        // Buscar la ubicación más alta en robots
        int[][] robotsData = silkRoad.robots();
        for (int i = 0; i < robotsData.length; i++) {
            if (robotsData[i][0] > maxLocation) {
                maxLocation = robotsData[i][0];
            }
        }
        
        // Devolver el máximo + un buffer de segmentos adicionales
        return Math.max(maxLocation + 10, 20); // Mínimo 20 segmentos
    }
    
    // =============================
    //   MÉTODOS DE ACCESO A SILKROAD
    // =============================
    
    public void placeStore(int location, int tenges) {
        silkRoad.placeStore(location, tenges);
        // Actualizar barra después de colocar tienda
        updateProfitBar();
    }
    
    public void removeStore(int location) {
        silkRoad.removeStore(location);
        // Actualizar barra después de remover tienda
        updateProfitBar();
    }
    
    public void placeRobot(int location) {
        silkRoad.placeRobot(location);
        // No necesita actualizar barra (no cambia el máximo posible)
    }
    
    public void removeRobot(int location) {
        silkRoad.removeRobot(location);
        // No necesita actualizar barra (no cambia el máximo posible)
    }
    
    public void removeRandomStore() {
        int[][] storesData = silkRoad.stores();
        if (storesData.length == 0) {
            System.out.println("No hay tiendas para remover.");
            return;
        }
        
        int randomIndex = random.nextInt(storesData.length);
        int locationToRemove = storesData[randomIndex][0];
        silkRoad.removeStore(locationToRemove);
        
        // Actualizar barra después de remover tienda
        updateProfitBar();
        
        System.out.println("Tienda removida aleatoriamente del segmento " + locationToRemove);
    }
    
    public void removeRandomRobot() {
        int[][] robotsData = silkRoad.robots();
        if (robotsData.length == 0) {
            System.out.println("No hay robots para remover.");
            return;
        }
        
        int randomIndex = random.nextInt(robotsData.length);
        int locationToRemove = robotsData[randomIndex][0];
        silkRoad.removeRobot(locationToRemove);
        System.out.println("Robot removido aleatoriamente del segmento " + locationToRemove);
    }
    
    // =============================
    //   GETTERS Y UTILIDADES
    // =============================
    
    public int getDay() { 
        return dayCounter; 
    }
    
    public int getStoreCount() { 
        return silkRoad.stores().length; // Ahora es matriz, usar .length
    }
    
    public int getRobotCount() { 
        return silkRoad.robots().length; // Ahora es matriz, usar .length
    }
    
    public int getTotalCollected() {
        return totalCollected;
    }
    
    /*
     * Método para obtener progreso de la barra
     */
    public double getProfitProgress() {
        return profitBar.getProgressPercentage();
    }
    
    /*
     * Método para actualizar manualmente la barra (para ver cambios paso a paso)
     */
    public void refreshProfitBar() {
        updateProfitBar();
        System.out.println("Barra actualizada - Progreso: " + String.format("%.1f", getProfitProgress()) + "%");
    }
    
    public void printState() {
        System.out.println("--- Estado actual del juego ---");
        System.out.println("Día: " + dayCounter);
        System.out.println("Tenges diarios (robots): " + getDailyCollected());
        System.out.println("Tenges totales acumulados: " + totalCollected);
        System.out.println("Tiendas activas: " + getStoreCount());
        System.out.println("Robots activos: " + getRobotCount());
        
        // Mostrar progreso de la barra
        System.out.println("Progreso de ganancia: " + String.format("%.1f", getProfitProgress()) + "%");
        
        // Mostrar detalles de tiendas
        int[][] storesData = silkRoad.stores();
        System.out.println("Tiendas:");
        for (int i = 0; i < storesData.length; i++) {
            System.out.println("  Segmento " + storesData[i][0] + ": " + storesData[i][1] + " tenges");
        }
        
        // Mostrar detalles de robots
        int[][] robotsData = silkRoad.robots();
        System.out.println("Robots:");
        for (int i = 0; i < robotsData.length; i++) {
            System.out.println("  Segmento " + robotsData[i][0] + ": " + robotsData[i][1] + " tenges recolectados");
        }
        
        System.out.println("Segmentos libres: " + getFreeSegments().size());
    }
    
    public void finish() {
        silkRoad.finish();
        
        // Ocultar barra de progreso
        profitBar.makeInvisible();
        
        System.out.println("Juego terminado.");
    }
    
}