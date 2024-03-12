package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import log.Logger;

import java.util.Locale;
import java.util.ResourceBundle;

public class MainApplicationFrame extends JFrame {
    Locale locale = new Locale("ru", "RU");
    ResourceBundle bundle = ResourceBundle.getBundle("recources", locale);
    private final JDesktopPane desktopPane = new JDesktopPane();

    public MainApplicationFrame() {
        initializeUI();
    }

    private void initializeUI() {
        int inset = 50;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset, screenSize.width - inset * 2, screenSize.height - inset * 2);
        setContentPane(desktopPane);

        addWindow(createLogWindow(), 10, 10);
        GameWindow gameWindow = createGameWindow();
        addWindow(gameWindow, 100, 100);

        setJMenuBar(generateMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        logWindow.setLocation(10, 10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(bundle.getString("protocol"));
        return logWindow;
    }

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

    private void addWindow(JInternalFrame frame, int x, int y) {
        desktopPane.add(frame);
        frame.setLocation(x, y);
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

        JMenuItem newGameItem = createMenuItem(bundle.getString("new_game_field"), KeyEvent.VK_N, "alt N", (event) -> addWindow(createGameWindow()));
        documentMenu.add(newGameItem);

        JMenuItem logItem = createMenuItem(bundle.getString("log_window"), KeyEvent.VK_L, "alt L", (event) -> addWindow(new LogWindow(Logger.getDefaultLogSource())));
        documentMenu.add(logItem);

        JMenuItem exitItem = createMenuItem(bundle.getString("exit"), KeyEvent.VK_Q, "alt Q", (event) -> confirmExit());
        documentMenu.add(exitItem);

        menuBar.add(documentMenu);

        JMenu viewMenu = createMenu(bundle.getString("display_mode"), KeyEvent.VK_V);
        viewMenu.add(createLookAndFeelMenuItem(bundle.getString("system_diagram"), UIManager.getSystemLookAndFeelClassName()));
        viewMenu.add(createLookAndFeelMenuItem(bundle.getString("universal_scheme"), UIManager.getCrossPlatformLookAndFeelClassName()));
        menuBar.add(viewMenu);

        JMenu testMenu = createMenu(bundle.getString("tests"), KeyEvent.VK_T);
        testMenu.add(createTestMenuItem(bundle.getString("message_in_the_log"), () -> Logger.debug(bundle.getString("new_str"))));
        menuBar.add(testMenu);

        return menuBar;
    }

    private JMenuItem createLookAndFeelMenuItem(String label, String className) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener((event) -> setLookAndFeel(className));
        return item;
    }

    private JMenuItem createTestMenuItem(String label, Runnable action) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener((event) -> action.run());
        return item;
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
        String message = bundle.getString("exit?");
        UIManager.put("OptionPane.yesButtonText", bundle.getString("yes_button_text"));
        UIManager.put("OptionPane.noButtonText", bundle.getString("no_button_text"));
        int confirmation = JOptionPane.showConfirmDialog(this, message, bundle.getString("exit-yes"), JOptionPane.YES_NO_OPTION);
        if (confirmation == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApplicationFrame frame = new MainApplicationFrame();
            frame.setVisible(true);
        });
    }
}