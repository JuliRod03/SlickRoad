import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

/**
 * Canvas es una clase que permite dibujar figuras simples en una ventana.
 * 
 * @author Michael Kolling and David J. Barnes 
 * @version 2016.02.29
 */
public class Canvas
{
    // Nota: La implementación podría variar dependiendo de la versión que tengas
    private static Canvas canvasSingleton;
    
    public static Canvas getCanvas()
    {
        if(canvasSingleton == null) {
            canvasSingleton = new Canvas("BlueJ Shapes Demo", 700, 700, 
                                         Color.white);
        }
        canvasSingleton.setVisible(true);
        return canvasSingleton;
    }

    //  ----- Campos de instancia -----

    private JFrame frame;
    private CanvasPane canvas;
    private Graphics2D graphic;
    private Color backgroundColor;
    private Image canvasImage;
    private java.util.List<Object> objects;
    private HashMap<Object, ShapeDescription> shapes;
    
    /**
     * Crea un Canvas.
     * @param title    título de la ventana
     * @param width    ancho de la ventana
     * @param height   alto de la ventana
     * @param bgColour color de fondo de la ventana
     */
    private Canvas(String title, int width, int height, Color bgColour)
    {
        frame = new JFrame();
        canvas = new CanvasPane();
        frame.setContentPane(canvas);
        frame.setTitle(title);
        canvas.setPreferredSize(new Dimension(width, height));
        backgroundColor = bgColour;
        frame.pack();
        objects = new ArrayList<Object>();
        shapes = new HashMap<Object, ShapeDescription>();
    }

    /**
     * Hace la ventana visible o invisible.
     * @param visible  true para hacer visible, false para invisible.
     */
    public void setVisible(boolean visible)
    {
        if(graphic == null) {
            // primera vez: instancia la imagen off-screen y llena con
            // color de fondo
            Dimension size = canvas.getSize();
            canvasImage = canvas.createImage(size.width, size.height);
            graphic = (Graphics2D)canvasImage.getGraphics();
            graphic.setColor(backgroundColor);
            graphic.fillRect(0, 0, size.width, size.height);
            graphic.setColor(Color.black);
        }
        frame.setVisible(visible);
    }

    /**
     * Dibuja la figura dada en el canvas.
     * @param  referenceObject  un objeto para definir identidad para esta figura
     * @param  color            el color de la figura
     * @param  shape            un objeto que define la figura a dibujar
     */
    public void draw(Object referenceObject, String color, Shape shape)
    {
        objects.remove(referenceObject);   // remueve si ya estaba
        objects.add(referenceObject);      // añade al final
        shapes.put(referenceObject, new ShapeDescription(shape, color));
        redraw();
    }

    /**
     * Borra la figura dada del canvas.
     * @param  referenceObject  el objeto forma que será borrado 
     */
    public void erase(Object referenceObject)
    {
        objects.remove(referenceObject);   
        shapes.remove(referenceObject);
        redraw();
    }

    /**
     * Espera un número específico de milisegundos antes de finalizar.
     * @param  milliseconds  el número de millisegundos a esperar
     */
    public void wait(int milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        } 
        catch (Exception e)
        {
            // ignora la excepción
        }
    }

    /**
     * Redibuja todas las figuras actualmente en el Canvas.
     */
    private void redraw()
    {
        erase();
        for(Object shape : objects) 
        {
            shapes.get(shape).draw(graphic);
        }
        canvas.repaint();
    }
    
    /**
     * Borra todo el canvas.
     */
    private void erase()
    {
        Color original = graphic.getColor();
        graphic.setColor(backgroundColor);
        Dimension size = canvas.getSize();
        graphic.fill(new java.awt.Rectangle(0, 0, size.width, size.height));
        graphic.setColor(original);
    }


    /************************************************************************
     * Clase interna CanvasPane - el componente paintable actual contenido en el
     * Canvas frame. Es esencialmente un JPanel con capacidades añadidas de
     * pintar una imagen bitmap.
     */
    private class CanvasPane extends JPanel
    {
        public void paint(Graphics g)
        {
            g.drawImage(canvasImage, 0, 0, null);
        }
    }
    
    /************************************************************************
     * Clase interna CanvasPane - almacena y maneja información sobre una 
     * figura que debe ser dibujada.
     */
    private class ShapeDescription
    {
        private Shape shape;
        private String colorString;

        public ShapeDescription(Shape shape, String color)
        {
            this.shape = shape;
            colorString = color;
        }

        public void draw(Graphics2D graphic)
        {
            setForegroundColor(colorString);
            graphic.fill(shape);
        }

        /**
         * Set the foreground colour of the Canvas.
         * @param  newColour   the new colour for the foreground of the Canvas 
         */
        private void setForegroundColor(String colorString)
        {
            if(colorString.equals("red")) {
                graphic.setColor(new Color(235, 25, 25));
            } else if(colorString.equals("black")) {
                graphic.setColor(Color.black);
            } else if(colorString.equals("blue")) {
                graphic.setColor(new Color(30, 75, 220));
            } else if(colorString.equals("yellow")) {
                graphic.setColor(new Color(255, 230, 0));
            } else if(colorString.equals("green")) {
                graphic.setColor(new Color(80, 160, 60));
            } else if(colorString.equals("magenta")) {
                graphic.setColor(Color.magenta);
            } else if(colorString.equals("white")) {
                graphic.setColor(Color.white);
            }else if(colorString.equals("brown")) { 
                graphic.setColor(new Color(229, 217, 182));
            }else if(colorString.equals("techo")) { 
                graphic.setColor(new Color(78, 59, 49));
            }else if(colorString.equals("arena")) { 
                graphic.setColor(new Color(216, 184, 99));
            }else if(colorString.equals("grey")) { 
                graphic.setColor(new Color(128, 128, 128));
            }else if(colorString.equals("azulmetal")) { 
                graphic.setColor(new Color(51, 60, 135));    
            }else if(colorString.equals("verdemetal")) { 
                graphic.setColor(new Color(28, 84, 45)); 
            }else if(colorString.equals("ladrillo")) { 
                graphic.setColor(new Color(208, 73, 28)); 
            }else if(colorString.equals("verdesito")) { 
                graphic.setColor(new Color(189, 236, 182)); 
                
            } else {
                graphic.setColor(Color.black);
            }
        }
    }
}