package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class KeyboardPanel extends JPanel {
    private final Map<String, JButton> keyButtonMap;
    private final JButton enterButton, deleteButton;

    public KeyboardPanel(ActionListener listener) {
        setLayout(new GridLayout(3, 9, 5, 5));

        keyButtonMap = new HashMap<>();
        String[] keys = {
                "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L",
                "Z", "X", "C", "V", "B", "N", "M"
        };

        for (String key : keys) {
            JButton button = createButton(key, listener);
            keyButtonMap.put(key, button);
            add(button);
        }

        enterButton = createButton("ENTER", listener);
        deleteButton = createButton("DELETE", listener);

        add(enterButton);
        add(deleteButton);
    }

    private JButton createButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(50, 50));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(230, 230, 230));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
        button.addActionListener(listener);
        return button;
    }

    public JButton getEnterButton() {
        return enterButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getKeyButton(String key) {
        return keyButtonMap.get(key);
    }

    public void setKeyButtonColor(String key, Color color) {
        JButton button = keyButtonMap.get(key);
        if (button != null) {
            button.setBackground(color);
        }
    }
}
