package gui;

import model.Robot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import java.util.Observer;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class GameVisualizer extends JPanel implements Observer
{
    private volatile double m_robotPositionX = 100;
    private volatile double m_robotPositionY = 100;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = 150;
    private volatile int m_targetPositionY = 100;

    private final Robot robot;
    ImageIcon robotImage = new ImageIcon("./robots/src/resources/photos/chechen.jpg");

    public GameVisualizer(Robot robot)
    {
        Timer m_timer = new Timer("events generator", true);

        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                robot.update(m_targetPositionX, m_targetPositionY);
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setTargetPosition(e.getPoint());
                repaint();
            }
        });
        setDoubleBuffered(true);

        this.robot = robot;
        robot.addObserver(this);
    }

    protected void setTargetPosition(Point p) {
        double exp = 7f/6f;
        m_targetPositionX = (int)(p.x*exp);
        m_targetPositionY = (int)(p.y*exp);
    }

    protected void onRedrawEvent() {
        EventQueue.invokeLater(this::repaint);
    }

    @Override
    public void update(Observable o, Object arg){
        if (o.equals(robot))
            if (arg.equals("robot moved")){
                m_robotPositionX = robot.getRobotPositionX();
                m_robotPositionY = robot.getRobotPositionY();
                m_robotDirection = robot.getRobotDirection();
            }
    }

    private static int round(double value) {
        return (int)(value + 0.5);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D)g;
        drawRobot(g2d, round(m_robotPositionX), round(m_robotPositionY), m_robotDirection);
        drawTarget(g2d, m_targetPositionX, m_targetPositionY);
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private void drawRobot(Graphics2D g, int x, int y, double direction) {
        // Создаем центр робота
        int robotCenterX = round(x);
        int robotCenterY = round(y);

        // Создаем объект преобразования для поворота изображения в соответствии с направлением робота
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);

        // Сохраняем текущее преобразование графики
        AffineTransform originalTransform = g.getTransform();

        // Применяем преобразование для вращения графики
        g.setTransform(t);

        // Отрисовываем изображение робота
        int robotWidth = robotImage.getIconWidth();
        int robotHeight = robotImage.getIconHeight();

        // Рисуем изображение робота в нужном месте и поворачиваем его
        g.drawImage(robotImage.getImage(), robotCenterX - robotWidth / 2, robotCenterY - robotHeight / 2, null);

        // Восстанавливаем исходное преобразование графики
        g.setTransform(originalTransform);
    }

    private void drawTarget(Graphics2D g, int x, int y) {
        AffineTransform t = AffineTransform.getRotateInstance(0, 0, 0);
        g.setTransform(t);
        g.setColor(Color.GREEN);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}