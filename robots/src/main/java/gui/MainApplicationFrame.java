package gui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.*;

import log.Logger;

public class MainApplicationFrame extends JFrame {
    private final JDesktopPane desktopPane = new JDesktopPane();
    Locale locale = new Locale("ru", "RU");
    ResourceBundle bundle = ResourceBundle.getBundle("recources", locale);

    @Override
    public void dispose() {
        super.dispose();
        saveWindows();
    }

    private void saveWindows() {
        for (var frame : desktopPane.getAllFrames()) {
            if (frame instanceof FrameState) {
                ((FrameState) frame).saveWindow();
            }
        }
    }

    private void restoreState() {
        for (var frame : desktopPane.getAllFrames())
            if (frame instanceof FrameState)
                ((FrameState) frame).restoreWindow();
    }

    public MainApplicationFrame() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
                screenSize.width  - inset*2,
                screenSize.height - inset*2);

        setContentPane(desktopPane);

        addWindow(new LogWindow(Logger.getDefaultLogSource()));
        addWindow(createGameWindow());
        for (var frame : desktopPane.getAllFrames())
            if (frame instanceof FrameState)
                ((FrameState) frame).restoreWindow();
        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        }
//    public static void initializeUI() {
//        try {
//            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        MainApplicationFrame frame = new MainApplicationFrame();
//        int inset = 50;
//        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//        frame.setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
//        frame.setContentPane(frame.desktopPane);
//        frame.setJMenuBar(frame.generateMenuBar());
//        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
//        frame.restoreState();
//        frame.setVisible(true);
//        frame.pack();
//        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
//    }

    private GameWindow createGameWindow() {
        int gameWindowWidth = 400;
        int gameWindowHeight = 400;

        GameWindow gameWindow = new GameWindow();
        gameWindow.setSize(gameWindowWidth, gameWindowHeight);
        return gameWindow;
    }

    private void addWindow(JInternalFrame frame) {
        desktopPane.add(frame);
        frame.setVisible(true);
    }

    private JMenuItem createMenuItem(String label, int mnemonic, String acceleratorKey, ActionListener actionListener) {
        JMenuItem menuItem = new JMenuItem(label, mnemonic);
        if (acceleratorKey != null && !acceleratorKey.isEmpty()) {
            menuItem.setAccelerator(KeyStroke.getKeyStroke(acceleratorKey));
        }
        menuItem.addActionListener(actionListener);
        return menuItem;
    }

    private JMenu createMenu(String label, int mnemonic) {
        JMenu menu = new JMenu(label);
        menu.setMnemonic(mnemonic);
        return menu;
    }

    private JMenuBar generateMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu documentMenu = createMenu(bundle.getString("menu"), KeyEvent.VK_D);

        documentMenu.add(createMenuItem(bundle.getString("new_game_field"), KeyEvent.VK_N, "alt N", (event) -> addWindow(createGameWindow())));
        documentMenu.add(createMenuItem(bundle.getString("log_window"), KeyEvent.VK_L, "alt L", (event) -> addWindow(new LogWindow(Logger.getDefaultLogSource()))));
        documentMenu.add(createMenuItem(bundle.getString("exit"), KeyEvent.VK_Q, "alt Q", (event) -> confirmExit()));
        menuBar.add(documentMenu);

        JMenu viewMenu = createMenu(bundle.getString("display_mode"), KeyEvent.VK_V);
        viewMenu.add(createMenuItem(bundle.getString("system_diagram"), KeyEvent.VK_S, "ctrl S", (event) -> setLookAndFeel(UIManager.getSystemLookAndFeelClassName())));
        viewMenu.add(createMenuItem(bundle.getString("universal_scheme"), KeyEvent.VK_U, "ctrl U", (event) -> setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName())));
        menuBar.add(viewMenu);

        JMenu testMenu = createMenu(bundle.getString("tests"), KeyEvent.VK_T);
        testMenu.add(createMenuItem(bundle.getString("message_in_the_log"), KeyEvent.VK_M, "alt M", (event) -> Logger.debug(bundle.getString("new_str"))));
        menuBar.add(testMenu);

        return menuBar;
    }


    private void setLookAndFeel(String className) {
        try {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                bundle.getString("exit?"), bundle.getString("exit-yes"),
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            int confirm2 = JOptionPane.showConfirmDialog(this,
                    bundle.getString("exit-save"), bundle.getString("exit-save_q"),
                    JOptionPane.YES_NO_OPTION);
            if (confirm2 == JOptionPane.YES_OPTION) {
                saveWindows();
            }else if (confirm2 == JOptionPane.NO_OPTION) {
                // Создаем панель с изображением и текстом
                JPanel panel = new JPanel(new BorderLayout());
                ImageIcon icon = new ImageIcon("./robots/src/resources/photos/chechen.jpg"); // Укажите путь к вашей картинке
                JLabel label = new JLabel(bundle.getString("ramzan_1"), icon, JLabel.CENTER);
                panel.add(label, BorderLayout.CENTER);

                // Добавляем панель в диалоговое окно
                int confirm3 = JOptionPane.showConfirmDialog(this,
                        panel, bundle.getString("ramzan"),
                        JOptionPane.YES_NO_OPTION);
                if (confirm3 == JOptionPane.YES_OPTION) saveWindows();
                setVisible(false);
                Arrays.asList(desktopPane.getAllFrames()).forEach(JInternalFrame::dispose);
                dispose();
            }
            setVisible(false);
            Arrays.asList(desktopPane.getAllFrames()).forEach(JInternalFrame::dispose);
            dispose();
            }
    }
}