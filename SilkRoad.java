import java.util.ArrayList;
import java.awt.Point;

public class SilkRoad {
    // Atributos según el diagrama Astah
    private Road road;
    private ArrayList<Store> stores;
    private ArrayList<Integer> storeSegments;
    private ArrayList<Robot> robots;
    private ArrayList<Integer> robotSegments;
    
    // Nueva estructura con Segments (interna)
    private ArrayList<Segment> segments;
    
    // Tracking de costos de movimiento
    private int totalMovementCosts;
    
    // Barra de progreso
    private ProfitBar profitBar;
    
    // NUEVO: Historial de movimientos individuales para profitPerMove()
    private ArrayList<ArrayList<Integer>> robotMovementHistory;  // metros por movimiento
    private ArrayList<ArrayList<Integer>> robotCoinHistory;      // monedas recogidas por movimiento
    
    // Ajuste para centrar objetos dentro del cuadro de la carretera
    private static final int CELL_TO_OBJECT_OFFSET = 20;

    public SilkRoad(int length) {
        road = new Road(length);
        stores = new ArrayList<>();
        storeSegments = new ArrayList<>();
        robots = new ArrayList<>();
        robotSegments = new ArrayList<>();
        segments = new ArrayList<>();
        totalMovementCosts = 0;
        
        // Inicializar historial de movimientos
        robotMovementHistory = new ArrayList<>();
        robotCoinHistory = new ArrayList<>();
        
        // Inicializar barra de progreso
        profitBar = new ProfitBar();
    
        // Inicializar segmentos directamente
        int segmentCount = road.getSegmentCount();
        for (int i = 0; i < segmentCount; i++) {
            Point pos = road.getSegmentPosition(i);
            segments.add(new Segment(i, pos));
        }
    }

        public SilkRoad(int[][] days) {
        // Calcular la longitud mínima necesaria basada en las ubicaciones más altas
        int maxLocation = 0;
        for (int[] day : days) {
            if (day.length >= 2 && day[1] > maxLocation) {
                maxLocation = day[1];
            }
        }
        
        // Crear carretera con longitud suficiente (mínimo 10, máximo ubicación + 5)
        int roadLength = Math.max(10, maxLocation + 5);
        
        // Inicializar componentes básicos
        road = new Road(roadLength);
        stores = new ArrayList<>();
        storeSegments = new ArrayList<>();
        robots = new ArrayList<>();
        robotSegments = new ArrayList<>();
        segments = new ArrayList<>();
        totalMovementCosts = 0;
        
        // Inicializar historial de movimientos
        robotMovementHistory = new ArrayList<>();
        robotCoinHistory = new ArrayList<>();
        
        // Inicializar barra de progreso
        profitBar = new ProfitBar();
        
        // Inicializar segmentos directamente
        int segmentCount = road.getSegmentCount();
        for (int i = 0; i < segmentCount; i++) {
            Point pos = road.getSegmentPosition(i);
            segments.add(new Segment(i, pos));
        }
        
        // Procesar la configuración de días
        // Los días se numeran consecutivamente desde 1, ignorando el valor en days[i][0]
        for (int i = 0; i < days.length; i++) {
            int[] dayConfig = days[i];
            if (dayConfig.length < 4) continue;
            
            int day = i + 1;  // Día consecutivo: 1, 2, 3, 4...
            int location = dayConfig[1];
            int type = dayConfig[2];
            int value = dayConfig[3];
            
            
            if (location < 0 || location >= segments.size()) continue;
            System.out.println("Día " + day + ":");
            
            if (type == 1) {
                // Colocar tienda
                if (value > 0) {
                    placeStore(location, value);
                }
            } else if (type == 0) {
                // Colocar robot
                placeRobot(location);
            }
        }
        makeVisible();
    }

    
    // Método auxiliar PRIVADO para calcular total inicial de monedas en tiendas
    private int getTotalInitialCoinsInStores() {
        int total = 0;
        for (Store store : stores) {
            total += store.getInitialCoins();
        }
        return total;
    }
    
    // Método auxiliar PRIVADO para calcular monedas recolectadas
    private int getTotalCoinsCollected() {
        int total = 0;
        for (Robot robot : robots) {
            total += robot.getDailyCoins();
        }
        return total;
    }
    
    // =============================
    //    MÉTODOS EXACTOS DEL ASTAH
    // =============================
    
    public void placeStore(int location, int tenges) {
        if (location < 0 || location >= segments.size()) {
            System.out.println("Ubicación inválida para tienda: " + location);
            return;
        }
        
        // Verificar que no haya ya una tienda en esa ubicación
        if (storeSegments.contains(location)) {
            System.out.println("Ya hay una tienda en el segmento " + location);
            return;
        }
        
        // Verificar que no haya un robot en esa ubicación
        if (robotSegments.contains(location)) {
            System.out.println("No se puede colocar tienda en segmento " + location + " porque ya hay un robot.");
            return;
        }
        
        Point pos = segments.get(location).getPosition();
        int x = pos.x + CELL_TO_OBJECT_OFFSET;
        int y = pos.y + CELL_TO_OBJECT_OFFSET;

        Store store = new Store(x, y, tenges);
        
        // Agregar a las estructuras del diagrama Astah
        stores.add(store);
        storeSegments.add(location);
        
        // Agregar al segmento
        segments.get(location).placeStore(store);
        
        store.makeVisible();
        
        // Actualizar barra de progreso
        profitBar.updateMaxProfit(getTotalInitialCoinsInStores());
        profitBar.updateCurrentProfit(getTotalCoinsCollected());
        
        System.out.println("Tienda colocada en segmento " + location + " con " + tenges + " tenges.");
    }
    
    public void removeStore(int location) {
        if (location < 0 || location >= segments.size()) {
            System.out.println("Ubicación inválida: " + location);
            return;
        }
        
        Segment segment = segments.get(location);
        if (!segment.hasStore()) {
            System.out.println("No hay tienda en el segmento " + location);
            return;
        }
        
        Store store = segment.removeStore();
        store.makeInvisible();
        
        // Remover de las estructuras del diagrama Astah
        int idx = storeSegments.indexOf(location);
        if (idx != -1) {
            stores.remove(idx);
            storeSegments.remove(idx);
        }
        
        // Actualizar barra de progreso
        profitBar.updateMaxProfit(getTotalInitialCoinsInStores());
        profitBar.updateCurrentProfit(getTotalCoinsCollected());
        
        System.out.println("Tienda eliminada del segmento " + location + ".");
    }
    
    public void placeRobot(int location) {
        if (location < 0 || location >= segments.size()) {
            System.out.println("Ubicación inválida para robot: " + location);
            return;
        }
        
        // Verificar que no haya ya un robot en esa ubicación
        if (robotSegments.contains(location)) {
            System.out.println("Ya hay un robot en el segmento " + location);
            return;
        }
        
        // Verificar que no haya una tienda en esa ubicación
        if (storeSegments.contains(location)) {
            System.out.println("No se puede colocar robot en segmento " + location + " porque ya hay una tienda.");
            return;
        }
        
        Point pos = segments.get(location).getPosition();
        int x = pos.x + CELL_TO_OBJECT_OFFSET;
        int y = pos.y + CELL_TO_OBJECT_OFFSET;

        Robot robot = new Robot(x, y, location);
        
        // Agregar a las estructuras del diagrama Astah
        robots.add(robot);
        robotSegments.add(location);
        
        // NUEVO: Inicializar historial para este robot
        robotMovementHistory.add(new ArrayList<Integer>());
        robotCoinHistory.add(new ArrayList<Integer>());
        
        // Agregar al segmento
        segments.get(location).placeRobot(robot);
        
        robot.makeVisible();
        System.out.println("Robot colocado en segmento " + location + ".");
    }
    
    public void removeRobot(int location) {
        if (location < 0 || location >= segments.size()) {
            System.out.println("Ubicación inválida: " + location);
            return;
        }
        
        Segment segment = segments.get(location);
        if (!segment.hasRobot()) {
            System.out.println("No hay robot en el segmento " + location);
            return;
        }
        
        Robot robot = segment.removeRobot();
        robot.makeInvisible();
        
        // Remover de las estructuras del diagrama Astah
        int idx = robotSegments.indexOf(location);
        if (idx != -1) {
            robots.remove(idx);
            robotSegments.remove(idx);
            
            // NUEVO: Remover historial correspondiente
            robotMovementHistory.remove(idx);
            robotCoinHistory.remove(idx);
        }
        
        System.out.println("Robot eliminado del segmento " + location + ".");
    }
    
    public void moveRobot(int location, int meters) {
        // Buscar el robot en la ubicación especificada
        int idx = robotSegments.indexOf(location);
        if (idx == -1) {
            System.out.println("No hay robot en el segmento " + location);
            return;
        }

        // Calcular la nueva ubicación (ubicación actual + metros a avanzar)
        int newLocation = location + meters;
        if (newLocation >= segments.size()) {
            System.out.println("El robot no puede avanzar " + meters + " metros desde el segmento " + location + 
                             " porque excedería el límite de la carretera (máximo segmento: " + (segments.size()-1) + ")");
            return;
        }
        
        // Obtener el robot y moverlo visualmente a la nueva posición
        Robot robot = robots.get(idx);
        Point pos = segments.get(newLocation).getPosition();
        int x = pos.x + CELL_TO_OBJECT_OFFSET;
        int y = pos.y + CELL_TO_OBJECT_OFFSET;
        robot.moveTo(x, y);
        
        // Actualizar la ubicación en robotSegments
        robotSegments.set(idx, newLocation);
        
        // Actualizar los segmentos internos (remover del actual, colocar en el nuevo)
        segments.get(location).removeRobot();
        segments.get(newLocation).placeRobot(robot);

        // NUEVO: Inicializar monedas recogidas en este movimiento
        int coinsCollectedInThisMove = 0;
        
        // Ver si hay tienda en el nuevo segmento
        if (segments.get(newLocation).hasStore()) {
            Store store = segments.get(newLocation).getStore();
            System.out.println("Robot llegó a tienda en segmento " + newLocation + "Hay" + store.getCoins());
            
            if (store.getCoins() > 0 && robot.canCollectMore()) {
                int available = store.getCoins();
                int canCollect = Math.min(available, robot.getRemainingCapacity());

                if (canCollect > 0) {
                    robot.addCoins(canCollect);
                    store.setCoins(store.getCoins() - canCollect);
                    
                    // NUEVO: Registrar monedas recogidas en este movimiento específico
                    coinsCollectedInThisMove = canCollect;
                    
                    // Actualizar barra de progreso después de recoger monedas
                    profitBar.updateMaxProfit(getTotalInitialCoinsInStores());
                    profitBar.updateCurrentProfit(getTotalCoinsCollected());
                }
            }
        }
        
        // NUEVO: Guardar historial de este movimiento
        robotMovementHistory.get(idx).add(Math.abs(meters));  // Metros movidos (siempre positivo)
        robotCoinHistory.get(idx).add(coinsCollectedInThisMove);  // Monedas recogidas en este movimiento
        
        // Registrar el costo del movimiento
        totalMovementCosts += Math.abs(meters);
        
    }

    /*
     * Mueve todos los robots automáticamente hacia las tiendas más cercanas con monedas
     * Cada robot va a UNA tienda diferente, asignando el robot más cercano a cada tienda
     */
    public void moveRobots() {
        System.out.println("=== Movimiento automático de robots ===");
        
        // Obtener estado actual
        int[][] robotsData = robots();
        int[][] storesData = stores();
        
        if (robotsData.length == 0) {
            System.out.println("No hay robots para mover");
            return;
        }
        
        if (storesData.length == 0) {
            System.out.println("No hay tiendas disponibles");
            return;
        }
        
        // Crear listas de robots y tiendas disponibles
        ArrayList<Integer> availableRobots = new ArrayList<>();
        ArrayList<Integer> availableStores = new ArrayList<>();
        
        // Llenar lista de robots disponibles
        for (int i = 0; i < robotsData.length; i++) {
            availableRobots.add(i);
        }
        
        // Llenar lista de tiendas con monedas disponibles
        for (int j = 0; j < storesData.length; j++) {
            if (storesData[j][1] > 0) { // Solo tiendas con monedas
                availableStores.add(j);
            }
        }
        
        if (availableStores.isEmpty()) {
            System.out.println("No hay tiendas con monedas disponibles");
            return;
        }
        
        // ASIGNACIÓN INTELIGENTE: Un robot por tienda
        while (!availableRobots.isEmpty() && !availableStores.isEmpty()) {
            int bestRobotIndex = -1;
            int bestStoreIndex = -1;
            int minDistance = Integer.MAX_VALUE;
            
            // Encontrar la combinación robot-tienda con menor distancia
            for (int i = 0; i < availableRobots.size(); i++) {
                int robotIndex = availableRobots.get(i);
                int robotLocation = robotsData[robotIndex][0];
                
                for (int j = 0; j < availableStores.size(); j++) {
                    int storeIndex = availableStores.get(j);
                    int storeLocation = storesData[storeIndex][0];
                    
                    int distance = Math.abs(storeLocation - robotLocation);
                    
                    if (distance < minDistance) {
                        minDistance = distance;
                        bestRobotIndex = i;
                        bestStoreIndex = j;
                    }
                }
            }
            
            // Mover el robot más cercano a su tienda asignada
            if (bestRobotIndex != -1 && bestStoreIndex != -1) {
                int robotIndex = availableRobots.get(bestRobotIndex);
                int storeIndex = availableStores.get(bestStoreIndex);
                
                int robotLocation = robotsData[robotIndex][0];
                int storeLocation = storesData[storeIndex][0];
                int storeCoins = storesData[storeIndex][1];
                
                int distance = Math.abs(storeLocation - robotLocation);
                
                if (distance > 0) {
                    int direction = storeLocation > robotLocation ? distance : -distance;
                    
                    System.out.println("Plan: Robot en segmento " + robotLocation + 
                                     " → Tienda en segmento " + storeLocation + 
                                     " (distancia: " + distance + ", monedas: " + storeCoins + ")");
                    
                    moveRobot(robotLocation, direction);
                } else {
                    System.out.println("Robot en segmento " + robotLocation + " ya está en su tienda asignada");
                }
                
                // Remover robot y tienda de las listas disponibles
                availableRobots.remove(bestRobotIndex);
                availableStores.remove(bestStoreIndex);
            } else {
                break; // No se pudo encontrar asignación
            }
        }
        
        // Informar sobre robots sin asignar
        if (!availableRobots.isEmpty()) {
            System.out.println("Robots sin tienda asignada: " + availableRobots.size());
            for (int robotIndex : availableRobots) {
                int robotLocation = robotsData[robotIndex][0];
                System.out.println("  Robot en segmento " + robotLocation + " se queda en su lugar");
            }
        }
    }
    
    public void returnRobots() {
        // Devolver robots a su posición inicial
        for (int i = 0; i < robots.size(); i++) {
            Robot robot = robots.get(i);
            int currentLocation = robotSegments.get(i);
            int initialLocation = robot.getInitialSegment();
            
            // Remover del segmento actual
            segments.get(currentLocation).removeRobot();
            
            // Colocar en segmento inicial
            segments.get(initialLocation).placeRobot(robot);
            robotSegments.set(i, initialLocation);
            
            // Mover visualmente
            Point pos = segments.get(initialLocation).getPosition();
            int x = pos.x + CELL_TO_OBJECT_OFFSET;
            int y = pos.y + CELL_TO_OBJECT_OFFSET;
            robot.moveTo(x, y);
        }
        
        // NUEVO: Limpiar historial de movimientos del día
        for (int i = 0; i < robotMovementHistory.size(); i++) {
            robotMovementHistory.get(i).clear();
            robotCoinHistory.get(i).clear();
        }
        
        // Resetear costos de movimiento del día
        totalMovementCosts = 0;
        
        System.out.println("Robots a posición inicial");
    }
    
    public void resupplyStores() {
        for (Store s : stores) {
            s.restock();
        }
        
        // Actualizar barra de progreso después de reabastecer
        profitBar.updateMaxProfit(getTotalInitialCoinsInStores());
        profitBar.updateCurrentProfit(getTotalCoinsCollected());
        
        System.out.println("Tiendas reabastecidas.");
    }
    
    public void reboot() {
        // Hacer invisibles los objetos actuales
        for (Store s : stores) s.makeInvisible();
        for (Robot r : robots) r.makeInvisible();
        
        // Limpiar estructuras del diagrama Astah
        stores.clear();
        storeSegments.clear();
        robots.clear();
        robotSegments.clear();
        
        // NUEVO: Limpiar historial de movimientos
        robotMovementHistory.clear();
        robotCoinHistory.clear();
        
        // Limpiar segmentos
        for (Segment segment : segments) {
            segment.removeStore();
            segment.removeRobot();
        }
        
        // Resetear costos de movimiento
        totalMovementCosts = 0;
    
        // Resetear barra de progreso
        profitBar.reset();
    

        Robot.resetColorIndex();
        Robot.resetColorIndex();

    }

    
    public int profit() {
        int totalEarned = 0;
        for (Robot r : robots) {
            totalEarned += r.getDailyCoins();
        }
        // Profit = ganancias - costos de movimiento
        return totalEarned - totalMovementCosts;
    }
    
    public int[][] stores() {
        // Crear matriz con [ubicación, tenges] para cada tienda
        int[][] result = new int[stores.size()][2];
        
        // Crear lista temporal para ordenar por ubicación
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < stores.size(); i++) {
            indices.add(i);
        }
        
        // Ordenar índices por ubicación (de menor a mayor)
        indices.sort((a, b) -> Integer.compare(storeSegments.get(a), storeSegments.get(b)));
        
        // Llenar la matriz ordenada
        for (int i = 0; i < indices.size(); i++) {
            int idx = indices.get(i);
            result[i][0] = storeSegments.get(idx);      // ubicación
            result[i][1] = stores.get(idx).getCoins();  // tenges
        }
        
        return result;
    }
    
    public int[][] robots() {
        // Crear matriz con [ubicación, tenges_recolectados] para cada robot
        int[][] result = new int[robots.size()][2];
        
        // Crear lista temporal para ordenar por ubicación
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < robots.size(); i++) {
            indices.add(i);
        }
        
        // Ordenar índices por ubicación (de menor a mayor)
        indices.sort((a, b) -> Integer.compare(robotSegments.get(a), robotSegments.get(b)));
        
        // Llenar la matriz ordenada
        for (int i = 0; i < indices.size(); i++) {
            int idx = indices.get(i);
            result[i][0] = robotSegments.get(idx);           // ubicación actual
            result[i][1] = robots.get(idx).getDailyCoins();  // tenges recolectados
        }
        
        return result;
    }


    public int[][] emptiedStores() {
        ArrayList<int[]> emptiedList = new ArrayList<>();
        
        // Revisar todas las tiendas
        for (int i = 0; i < stores.size(); i++) {
            Store store = stores.get(i);
            int location = storeSegments.get(i);
            int timesEmptied = store.getTimesEmptied();
            
            // Si la tienda ha sido vaciada al menos una vez (sin importar su estado actual)
            if (timesEmptied > 0) {
                emptiedList.add(new int[]{location, timesEmptied});
            }
        }
        
        // Convertir a arreglo y ordenar por ubicación de menor a mayor
        int[][] result = emptiedList.toArray(new int[emptiedList.size()][]);
        
        // Ordenar por ubicación (columna 0) de menor a mayor
        java.util.Arrays.sort(result, (a, b) -> Integer.compare(a[0], b[0]));
        
        return result;
    }

    
    public int[][] profitPerMove() {
        System.out.println("Ganancias :)");
        
        if (robots.size() == 0) {
            System.out.println("No hay robots");
            return new int[0][3]; // Sin robots, matriz vacía
        }
        
        // Verificar si hay historial
        boolean hasMovements = false;
        for (int i = 0; i < robotMovementHistory.size(); i++) {
            if (robotMovementHistory.get(i).size() > 0) {
                hasMovements = true;
                // Mostrar detalles con ubicaciones
                Robot robot = robots.get(i);
                int currentLocation = robot.getInitialSegment(); // Empezar desde ubicación inicial
                
                for (int j = 0; j < robotMovementHistory.get(i).size(); j++) {
                    int meters = robotMovementHistory.get(i).get(j);
                    int coins = robotCoinHistory.get(i).get(j);
                    
                    // Calcular a dónde llegó en este movimiento
                    currentLocation += meters; // Asumir movimientos hacia adelante
                    
                }
            }
        }
        
        if (!hasMovements) {
            System.out.println("PROBLEMA: No hay historial de movimientos");
            System.out.println("Posible causa: Los robots se movieron antes de inicializar el historial");
            
            // FALLBACK: Usar método anterior basado en posición inicial vs actual
            System.out.println("Usando método fallback...");
            ArrayList<int[]> profitList = new ArrayList<>();
            
            for (int i = 0; i < robots.size(); i++) {
                int currentLocation = robotSegments.get(i);
                Robot robot = robots.get(i);
                int initialLocation = robot.getInitialSegment();
                int totalCoins = robot.getDailyCoins();
                int totalMeters = Math.abs(currentLocation - initialLocation);
                
                System.out.println("Robot " + i + " - Ubicación inicial: " + initialLocation + 
                                 ", llegó a ubicación: " + currentLocation + 
                                 ", monedas recogidas: " + totalCoins);
                
                // DINÁMICO: Solo crear columnas necesarias (ubicación + ganancia_única)
                // Si no hay historial detallado, asumir UN SOLO movimiento con toda la ganancia
                int[] robotProfits = new int[2]; // Solo ubicación + ganancia_total
                robotProfits[0] = currentLocation;
                robotProfits[1] = totalCoins - totalMeters; // Ganancia neta total
                
                profitList.add(robotProfits);
            }
            
            // Convertir a matriz
            int[][] result = new int[profitList.size()][2]; // Solo 2 columnas
            for (int i = 0; i < profitList.size(); i++) {
                result[i] = profitList.get(i);
            }
            
            // Ordenar por ubicación
            java.util.Arrays.sort(result, (a, b) -> Integer.compare(a[0], b[0]));
            
            for (int i = 0; i < result.length; i++) {
                System.out.print("Fila " + i + ": [");
                for (int j = 0; j < result[i].length; j++) {
                    System.out.print(result[i][j]);
                    if (j < result[i].length - 1) System.out.print(", ");
                }
                System.out.println("]");
            }
            
            return result;
        }
        
        // Encontrar el máximo número de movimientos para dimensionar la matriz
        int maxMovements = 0;
        for (int i = 0; i < robotMovementHistory.size(); i++) {
            int movements = robotMovementHistory.get(i).size();
            if (movements > maxMovements) {
                maxMovements = movements;
            }
        }
        
        ArrayList<int[]> profitList = new ArrayList<>();
        
        // Procesar cada robot
        for (int i = 0; i < robots.size(); i++) {
            int currentLocation = robotSegments.get(i);
            
            // Crear array: [ubicación, ganancia_mov1, ganancia_mov2, ...]
            int[] robotProfits = new int[maxMovements + 1];
            robotProfits[0] = currentLocation; // Primera columna es ubicación actual
            
            // Calcular ganancia por cada movimiento realizado
            ArrayList<Integer> movements = robotMovementHistory.get(i);
            ArrayList<Integer> coins = robotCoinHistory.get(i);
            
            for (int moveIndex = 0; moveIndex < movements.size(); moveIndex++) {
                int metersMovedInThisMove = movements.get(moveIndex);
                int coinsCollectedInThisMove = coins.get(moveIndex);
                
                // Ganancia = monedas recogidas - costo del movimiento
                int profitThisMove = coinsCollectedInThisMove - metersMovedInThisMove;
                
                // Guardar en la columna correspondiente (moveIndex + 1 porque columna 0 es ubicación)
                robotProfits[moveIndex + 1] = profitThisMove;
                
            }
            
            // Llenar movimientos no realizados con 0
            for (int moveIndex = movements.size(); moveIndex < maxMovements; moveIndex++) {
                robotProfits[moveIndex + 1] = 0;
            }
            
            profitList.add(robotProfits);
        }
        
        // Convertir a matriz
        int[][] result = new int[profitList.size()][maxMovements + 1];
        for (int i = 0; i < profitList.size(); i++) {
            result[i] = profitList.get(i);
        }
        
        // Ordenar por ubicación (columna 0) de menor a mayor
        java.util.Arrays.sort(result, (a, b) -> Integer.compare(a[0], b[0]));
        
        for (int i = 0; i < result.length; i++) {
            System.out.print("robot " + i + ": [");
            for (int j = 0; j < result[i].length; j++) {
                System.out.print(result[i][j]);
                if (j < result[i].length - 1) System.out.print(", ");
            }
            System.out.println("]");
        }
        
        return result;
    }
    
    public void makeVisible() {
        road.makeVisible();
        for (Store s : stores) s.makeVisible();
        for (Robot r : robots) r.makeVisible();
        
        // Mostrar barra de progreso
        profitBar.makeVisible();
    }
    
    public void makeInvisible() {
        road.makeInvisible();
        for (Store s : stores) s.makeInvisible();
        for (Robot r : robots) r.makeInvisible();
        
        // Ocultar barra de progreso
        profitBar.makeInvisible();
    }
    
    public void finish() {
        road.makeInvisible();
        for (Store s : stores) s.makeInvisible();
        for (Robot r : robots) r.makeInvisible();
        
        // Ocultar barra de progreso
        profitBar.makeInvisible();
        
        System.out.println("SilkRoad terminado.");
    }
    
    public boolean ok() {
        return road != null && stores != null && robots != null && segments != null;
    }
}