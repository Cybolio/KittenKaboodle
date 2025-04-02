package util;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class KeyHandler {
    public boolean upPressed, downPressed, leftPressed, rightPressed;

    public void setupKeyBindings(JComponent component) {
        InputMap inputMap = component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = component.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("W"), "up");
        inputMap.put(KeyStroke.getKeyStroke("S"), "down");
        inputMap.put(KeyStroke.getKeyStroke("A"), "left");
        inputMap.put(KeyStroke.getKeyStroke("D"), "right");

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

        inputMap.put(KeyStroke.getKeyStroke("released W"), "upReleased");
        inputMap.put(KeyStroke.getKeyStroke("released S"), "downReleased");
        inputMap.put(KeyStroke.getKeyStroke("released A"), "leftReleased");
        inputMap.put(KeyStroke.getKeyStroke("released D"), "rightReleased");

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
    }
}