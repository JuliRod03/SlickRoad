public class Robot {
    private Circle head;
    private Rectangle body;
    private Rectangle leftLeg, rightLeg;
    private boolean isVisible;
    private int x, y; // posición actual del cuerpo
    private int initialSegment; // segmento donde nació
    private int dailyCoins;     // monedas recolectadas en el día actual
    private String color;       // color del robot
    
    // Array de colores disponibles para los robots
    private static final String[] ROBOT_COLORS = {
        "grey", "azulmetal", "verdemetal"};
    
    // Contador estático para asignar colores secuencialmente
    private static int colorIndex = 0;
    
    // Tamaños estándar
    private int headSize = 15;
    private int bodyHeight = 20;
    private int bodyWidth = 15;
    private int legHeight = 7;
    private int legWidth = 4;
    
    public Robot(int x, int y, int initialSegment) {
        this.x = x;
        this.y = y;
        this.initialSegment = initialSegment;
        this.dailyCoins = 0;
        
        // Asignar color automáticamente
        this.color = getNextColor();
        
        // Cuerpo
        body = new Rectangle();
        body.changeSize(bodyHeight, bodyWidth);
        body.changeColor(color);
        body.moveHorizontal(x);
        body.moveVertical(y);
        
        // Cabeza
        head = new Circle();
        head.changeSize(headSize);
        head.changeColor(color);
        int headX = x + (bodyWidth / 2) - (headSize / 2);
        int headY = y - headSize;
        head.moveHorizontal(headX);
        head.moveVertical(headY);
        
        // Piernas
        leftLeg = new Rectangle();
        leftLeg.changeSize(legHeight, legWidth);
        leftLeg.changeColor(color);
        leftLeg.moveHorizontal(x + 2);
        leftLeg.moveVertical(y + bodyHeight);
        
        rightLeg = new Rectangle();
        rightLeg.changeSize(legHeight, legWidth);
        rightLeg.changeColor(color);
        rightLeg.moveHorizontal(x + bodyWidth - legWidth - 3);
        rightLeg.moveVertical(y + bodyHeight);
        
        isVisible = false;

    }
    
    /**
     * Obtiene el siguiente color disponible de forma cíclica
     */
    private static String getNextColor() {
        String selectedColor = ROBOT_COLORS[colorIndex];
        colorIndex = (colorIndex + 1) % ROBOT_COLORS.length;
        return selectedColor;
    }
    
    /**
     * Reinicia el contador de colores (útil al reiniciar el juego)
     */
    public static void resetColorIndex() {
        colorIndex = 0;
    }
    
    // ---- Dibujar ----
    public void makeVisible() {
        isVisible = true;
        head.makeVisible();
        body.makeVisible();
        leftLeg.makeVisible();
        rightLeg.makeVisible();
    }
    
    public void makeInvisible() {
        head.makeInvisible();
        body.makeInvisible();
        leftLeg.makeInvisible();
        rightLeg.makeInvisible();
        isVisible = false;
    }
    
    // ---- Movimiento ----
    public void moveHorizontal(int distance) {
        head.moveHorizontal(distance);
        body.moveHorizontal(distance);
        leftLeg.moveHorizontal(distance);
        rightLeg.moveHorizontal(distance);
        x += distance;
    }
    
    public void moveVertical(int distance) {
        head.moveVertical(distance);
        body.moveVertical(distance);
        leftLeg.moveVertical(distance);
        rightLeg.moveVertical(distance);
        y += distance;
    }
    
    public void moveTo(int newX, int newY) {
        int dx = newX - x;
        int dy = newY - y;
        moveHorizontal(dx);
        moveVertical(dy);
    }
    
    // ---- Monedas (SIN LÍMITE) ----
    public void resetDay() {
        dailyCoins = 0;
    }
    
    public int getDailyCoins() { 
        return dailyCoins; 
    }
    
    public void addCoins(int amount) {
        // ELIMINADO el límite de 200 monedas
        dailyCoins += amount;
    }
    
    public boolean canCollectMore() {
        // SIEMPRE puede recolectar más (sin límite)
        return true;
    }
    
    public int getRemainingCapacity() {
        // CAPACIDAD ILIMITADA
        return Integer.MAX_VALUE;
    }
    
    public int getInitialSegment() { 
        return initialSegment; 
    }
    
    public int getX() { 
        return x; 
    }
    
    public int getY() { 
        return y; 
    }
    
    public String getColor() {
        return color;
    }
}