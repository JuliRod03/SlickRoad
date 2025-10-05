import java.awt.Point;
import java.util.ArrayList;

public class Road {
    private int length;
    private boolean isVisible;
    private ArrayList<Rectangle> roadSegments;
    private ArrayList<Point> segmentPositions;  // posiciones reales de cada cuadrito
    
    // --- CONSTANTES DE ESCALA ---
    private static final int CELL_SIZE = 50;   // Tamaño del cuadro amarillo
    private static final int CELL_GAP = 65;    // Distancia entre cuadros
    private static final int OFFSET = 270;     // Margen inicial
    private static final int LINE_THICKNESS = CELL_SIZE / 15; // Grosor relativo de líneas

    public Road(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("La longitud debe ser mayor que 0");
        }
        this.length = length;
        this.isVisible = false;
        this.roadSegments = new ArrayList<Rectangle>();
        this.segmentPositions = new ArrayList<Point>();
        createRoadPath();
    }

    private void createRoadPath() {
        roadSegments.clear();
        segmentPositions.clear(); // limpiar también posiciones

        int segmentsNeeded = length;  // NUEVO: 1 segmento por metro 
        int segmentsCreated = 0;

        int x = 0;
        int y = 0;
        int previousX = 0;
        int previousY = 0;

        createSegmentAt(x, y);
        segmentsCreated++;

        int pasos = 1;

        while (segmentsCreated < segmentsNeeded) {
            // DERECHA
            for (int i = 0; i < pasos && segmentsCreated < segmentsNeeded; i++) {
                previousX = x;
                previousY = y;
                x++;
                createStraightLine(previousX, previousY, x, y);
                createSegmentAt(x, y);
                segmentsCreated++;
            }

            // ABAJO
            for (int i = 0; i < pasos && segmentsCreated < segmentsNeeded; i++) {
                previousX = x;
                previousY = y;
                y++;
                createStraightLine(previousX, previousY, x, y);
                createSegmentAt(x, y);
                segmentsCreated++;
            }

            pasos++;

            // IZQUIERDA
            for (int i = 0; i < pasos && segmentsCreated < segmentsNeeded; i++) {
                previousX = x;
                previousY = y;
                x--;
                createStraightLine(previousX, previousY, x, y);
                createSegmentAt(x, y);
                segmentsCreated++;
            }

            // ARRIBA
            for (int i = 0; i < pasos && segmentsCreated < segmentsNeeded; i++) {
                previousX = x;
                previousY = y;
                y--;
                createStraightLine(previousX, previousY, x, y);
                createSegmentAt(x, y);
                segmentsCreated++;
            }

            pasos++;
        }
    }

    private void createSegmentAt(int gridX, int gridY) {
        Rectangle segment = new Rectangle();
        segment.changeSize(CELL_SIZE, CELL_SIZE);
        segment.changeColor("arena");

        int pixelX = gridX * CELL_GAP + OFFSET;
        int pixelY = gridY * CELL_GAP + OFFSET;

        segment.moveHorizontal(pixelX);
        segment.moveVertical(pixelY);

        roadSegments.add(segment);
        segmentPositions.add(new Point(pixelX, pixelY)); // Guardar posición
    }

    private void createStraightLine(int fromX, int fromY, int toX, int toY) {
        Rectangle line = new Rectangle();

        int fromPixelX = fromX * CELL_GAP + OFFSET + CELL_SIZE / 2;
        int fromPixelY = fromY * CELL_GAP + OFFSET + CELL_SIZE / 2;
        int toPixelX   = toX   * CELL_GAP + OFFSET + CELL_SIZE / 2;
        int toPixelY   = toY   * CELL_GAP + OFFSET + CELL_SIZE / 2;

        if (fromY == toY) {
            line.changeSize(LINE_THICKNESS, CELL_GAP);
            int centerX = (fromPixelX + toPixelX) / 2;
            line.moveHorizontal(centerX - CELL_GAP / 2);
            line.moveVertical(fromPixelY - LINE_THICKNESS / 2);
        } else {
            line.changeSize(CELL_GAP, LINE_THICKNESS);
            int centerY = (fromPixelY + toPixelY) / 2;
            line.moveHorizontal(fromPixelX - LINE_THICKNESS / 2);
            line.moveVertical(centerY - CELL_GAP / 2);
        }

        line.changeColor("black");
        roadSegments.add(line);
    }

    public int getLength() {
        return length;
    }

    public void makeVisible() {
        isVisible = true;
        draw();
    }

    public void makeInvisible() {
        erase();
        isVisible = false;
    }

    private void draw() {
        if (isVisible) {
            for (Rectangle segment : roadSegments) {
                segment.makeVisible();
            }
        }
    }

    private void erase() {
        for (Rectangle segment : roadSegments) {
            segment.makeInvisible();
        }
    }

    public void finish() {
        makeInvisible();
    }

    public int getSegmentCount() {
        return segmentPositions.size();
    }

    // Obtener coordenadas de un segmento
    public Point getSegmentPosition(int index) {
        if (index >= 0 && index < segmentPositions.size()) {
            return segmentPositions.get(index);
        }
        return null;
    }
}