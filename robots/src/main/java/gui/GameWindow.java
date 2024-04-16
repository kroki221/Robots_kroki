package gui;

import java.awt.BorderLayout;
import model.Robot;
import java.awt.*;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends AbstarctWindowState
{
    private final GameVisualizer m_visualizer;
    public GameWindow(Robot robot)
    {
        super("Игровое поле", true, true, true);
        m_visualizer = new GameVisualizer(robot);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
