    
public class ProfitBar {
    private Rectangle background;
    private Rectangle progressBar;
    private Rectangle border;
    private boolean isVisible;
    
    // Dimensiones de la barra (más grande y visible)
    private static final int BAR_WIDTH = 400;  // Más larga
    private static final int BAR_HEIGHT = 45;  // Más ancha
    private static final int BORDER_THICKNESS = 2;
    
    // Posición de la barra (arriba del canvas, bien visible)
    private static final int BAR_X = 100;
    private static final int BAR_Y = 10;
    
    // Valores para calcular el progreso
    private int currentProfit;
    private int maxPossibleProfit;
    
    public ProfitBar() {
        currentProfit = 0;
        maxPossibleProfit = 0;
        isVisible = false;
        
        createBarComponents();
    }
    
    private void createBarComponents() {
        // Borde de la barra (negro) - crear primero para que esté atrás
        border = new Rectangle();
        border.changeSize(BAR_HEIGHT + 2 * BORDER_THICKNESS, BAR_WIDTH + 2 * BORDER_THICKNESS);
        border.changeColor("black");
        border.moveHorizontal(BAR_X - BORDER_THICKNESS);
        border.moveVertical(BAR_Y - BORDER_THICKNESS);
        
        // Fondo de la barra (gris claro)
        background = new Rectangle();
        background.changeSize(BAR_HEIGHT, BAR_WIDTH);
        background.changeColor("white");
        background.moveHorizontal(BAR_X);
        background.moveVertical(BAR_Y);
        
        // Barra de progreso (verde, inicialmente con ancho 0)
        progressBar = new Rectangle();
        progressBar.changeSize(BAR_HEIGHT - 2, 0); // Slightly smaller than background
        progressBar.changeColor("green");
        progressBar.moveHorizontal(BAR_X + 1);
        progressBar.moveVertical(BAR_Y + 1);
    }
    
    /**
     * Actualiza la ganancia máxima posible basada en el número total de monedas disponibles
     * @param totalCoinsInStores Total de monedas en todas las tiendas
     */
    public void updateMaxProfit(int totalCoinsInStores) {
        this.maxPossibleProfit = totalCoinsInStores;
        updateProgressBar();
    }
    
    /**
     * Actualiza la ganancia actual
     * @param profit Ganancia actual (monedas recolectadas - costos de movimiento)
     */
    public void updateCurrentProfit(int profit) {
        this.currentProfit = Math.max(0, profit); // Evitar valores negativos en la visualización
        updateProgressBar();
    }
    
    /**
     * Actualiza el ancho de la barra de progreso basado en el progreso actual
     */
    private void updateProgressBar() {
        if (maxPossibleProfit <= 0) {
            // Si no hay ganancia máxima posible, la barra está vacía
            progressBar.changeSize(BAR_HEIGHT - 2, 0);
            return;
        }
        
        // Calcular el porcentaje de progreso
        double progressRatio = (double) currentProfit / maxPossibleProfit;
        progressRatio = Math.min(1.0, Math.max(0.0, progressRatio)); // Limitar entre 0% y 100%
        
        // Calcular el nuevo ancho de la barra
        int newWidth = (int) (progressRatio * (BAR_WIDTH - 2));
        
        // Actualizar el tamaño de la barra de progreso
        progressBar.changeSize(BAR_HEIGHT - 2, newWidth);
    }
    
    // (Método updateBarColor eliminado)
    
    /**
     * Muestra la barra de progreso
     */
    public void makeVisible() {
        if (!isVisible) {
            border.makeVisible();
            background.makeVisible();
            progressBar.makeVisible();
            isVisible = true;
        }
    }
    
    /**
     * Oculta la barra de progreso
     */
    public void makeInvisible() {
        if (isVisible) {
            border.makeInvisible();
            background.makeInvisible();
            progressBar.makeInvisible();
            isVisible = false;
        }
    }
    
    /**
     * Reinicia la barra (progreso a 0)
     */
    public void reset() {
        currentProfit = 0;
        maxPossibleProfit = 0;
        updateProgressBar();
    }
    
    /**
     * Obtiene el progreso actual como porcentaje
     * @return Porcentaje de progreso (0-100)
     */
    public double getProgressPercentage() {
        if (maxPossibleProfit <= 0) return 0.0;
        return (double) currentProfit / maxPossibleProfit * 100.0;
    }
    
    /**
     * Verifica si la barra está llena (100% de progreso)
     * @return true si el progreso es del 100%
     */
    public boolean isComplete() {
        return maxPossibleProfit > 0 && currentProfit >= maxPossibleProfit;
    }
    
    // Getters para debugging
    public int getCurrentProfit() {
        return currentProfit;
    }
    
    public int getMaxPossibleProfit() {
        return maxPossibleProfit;
    }
}