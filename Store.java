public class Store {
    private Rectangle base;
    private Triangle roof;
    private Rectangle door; // Nueva puerta para tiendas vacías
    private boolean isVisible;
    private String wallColor; // color de las paredes
    
    // Tamaños
    private int baseSize = 25;
    private int roofHeight = 15;
    private int roofWidth = 35;
    private int doorWidth = 8;  // Ancho de la puerta
    private int doorHeight = 12; // Alto de la puerta (desde el centro hasta abajo)
    
    // Array de colores disponibles para las paredes de las tiendas
    private static final String[] WALL_COLORS = {"brown", "ladrillo", "verdesito"};
    
    // Contador estático para asignar colores secuencialmente
    private static int colorIndex = 0;
    
    // Monedas
    private int coins;
    private int initialCoins;
    
    // Contador de veces que ha sido vaciada (PRIVADO - solo accesible desde SilkRoad)
    private int timesEmptied;
    
    public Store(int x, int y, int coins) {
        this.coins = coins;
        this.initialCoins = coins;
        this.timesEmptied = 0; // Inicializar contador
        
        // Asignar color automáticamente para las paredes
        this.wallColor = getNextWallColor();
        
        // Crear base con color diferente
        base = new Rectangle();
        base.changeSize(baseSize, baseSize);
        base.changeColor(wallColor); // Usar color asignado automáticamente
        base.moveHorizontal(x);
        base.moveVertical(y);
        
        // Crear techo (SIEMPRE del mismo color)
        roof = new Triangle();
        roof.changeSize(roofHeight, roofWidth);
        roof.changeColor("techo");
        roof.moveHorizontal(x + (baseSize / 2));
        roof.moveVertical(y - roofHeight);
        
        // Crear puerta (inicialmente invisible)
        door = new Rectangle();
        door.changeSize(doorHeight, doorWidth);
        door.changeColor("black"); // Puerta negra para simular que está cerrada
        // Centrar horizontalmente y posicionar desde el centro hacia abajo
        int doorX = x + (baseSize / 2) - (doorWidth / 2);
        int doorY = y + (baseSize / 2); // Desde el centro hacia abajo
        door.moveHorizontal(doorX);
        door.moveVertical(doorY);
        
        isVisible = false;

    }
    
    /**
     * Obtiene el siguiente color disponible de forma cíclica para las paredes
     */
    private static String getNextWallColor() {
        String selectedColor = WALL_COLORS[colorIndex];
        colorIndex = (colorIndex + 1) % WALL_COLORS.length;
        return selectedColor;
    }
    
    /**
     * Reinicia el contador de colores (útil al reiniciar el juego)
     */
    public static void resetColorIndex() {
        colorIndex = 0;
    }
    
    public void makeVisible() {
        isVisible = true;
        base.makeVisible();
        roof.makeVisible();
        
        // Mostrar puerta solo si la tienda está vacía
        if (coins == 0) {
            door.makeVisible();
        }
    }
    
    public void makeInvisible() {
        base.makeInvisible();
        roof.makeInvisible();
        door.makeInvisible(); // Ocultar puerta también
        isVisible = false;
    }
    
    public void moveHorizontal(int distance) {
        base.moveHorizontal(distance);
        roof.moveHorizontal(distance);
        door.moveHorizontal(distance); // Mover puerta también
    }
    
    public void moveVertical(int distance) {
        base.moveVertical(distance);
        roof.moveVertical(distance);
        door.moveVertical(distance); // Mover puerta también
    }
    
    public void changeColors(String baseColor, String roofColor) {
        base.changeColor(baseColor);
        roof.changeColor(roofColor);
    }
    
    // --- Métodos de monedas ---
    public int getCoins() {
        return coins;
    }
    
    public void setCoins(int value) {
        boolean wasEmpty = (coins == 0);
        boolean willBeEmpty = (value == 0);
        
        coins = value;
        
        // CONTADOR AUTOMÁTICO: Si la tienda se vacía, incrementar contador
        if (!wasEmpty && willBeEmpty) {
            timesEmptied++;
        }
        
        // NUEVO: Actualizar visualización de la puerta según el estado
        if (isVisible) {
            if (coins == 0) {
                door.makeVisible(); // Mostrar puerta si está vacía
            } else {
                door.makeInvisible(); // Ocultar puerta si tiene monedas
            }
        }
    }
    
    public void restock() {
        coins = initialCoins;
        // NO resetear timesEmptied - mantener historial
        
        // NUEVO: Actualizar visualización de la puerta al reabastecer
        if (isVisible) {
            door.makeInvisible(); // Ocultar puerta cuando se reabastece
        }
    }
    
    public int getInitialCoins() {
        return initialCoins;
    }
    
    /*
     * Obtiene el número de veces que esta tienda ha sido completamente vaciada
     */
    public int getTimesEmptied() {
        return timesEmptied;
    }
    
    /**
     * Obtiene el color de las paredes de esta tienda
     */
    public String getWallColor() {
        return wallColor;
    }
}