package util;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class KeyHandler {
    public boolean upPressed, downPressed, leftPressed, rightPressed, enterPressed, interactPressed;
    private boolean ePressed = false; // Added ePressed

    public void setupKeyBindings(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "up");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "down");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "left");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "right");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, false), "interact");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enter");

        actionMap.put("up", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upPressed = true;
            }
        });
        actionMap.put("down", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downPressed = true;
            }
        });
        actionMap.put("left", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPressed = true;
            }
        });
        actionMap.put("right", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPressed = true;
            }
        });
        actionMap.put("interact", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ePressed = true; // Set ePressed to true when 'E' is pressed
                interactPressed = true;
            }
        });
        actionMap.put("enter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterPressed = true;
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "upReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "downReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "leftReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "rightReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, true), "interactReleased");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "enterReleased");

        actionMap.put("upReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                upPressed = false;
            }
        });
        actionMap.put("downReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                downPressed = false;
            }
        });
        actionMap.put("leftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                leftPressed = false;
            }
        });
        actionMap.put("rightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rightPressed = false;
            }
        });
        actionMap.put("interactReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ePressed = false; // Reset ePressed when 'E' is released
                interactPressed = false;
            }
        });
        actionMap.put("enterReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enterPressed = false;
            }
        });
    }

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isDownPressed() {
        return downPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isEnterPressed() {
        return enterPressed;
    }

    public boolean isInteractPressed() {
        return interactPressed;
    }

    public boolean isEPressed() { // Added isEPressed() getter
        return ePressed;
    }

    public void setEPressed(boolean pressed) { // Added setEPressed() setter
        this.ePressed = pressed;
    }
}

