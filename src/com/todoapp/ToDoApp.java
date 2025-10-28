package com.todoapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;

public class ToDoApp {
    private static final String DATA_FILE = "tasks.txt"; // saved in project working dir
    private DefaultListModel<String> model = new DefaultListModel<>();
    private JList<String> list;
    private JTextField input;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ToDoApp().createAndShowGUI());
    }

    private void createAndShowGUI() {
        loadTasks();

        JFrame frame = new JFrame("To-Do App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(10, 10));

        // Top: input + add button
        JPanel top = new JPanel(new BorderLayout(5, 5));
        input = new JTextField();
        JButton addButton = new JButton("Add");
        top.add(input, BorderLayout.CENTER);
        top.add(addButton, BorderLayout.EAST);

        // Center: task list
        model = model; // already initialized
        list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(list);

        // Bottom: remove / clear
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton removeButton = new JButton("Remove");
        JButton clearButton = new JButton("Clear All");
        bottom.add(removeButton);
        bottom.add(clearButton);

        frame.add(top, BorderLayout.NORTH);
        frame.add(scroll, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);

        // Actions
        addButton.addActionListener(e -> addTask());
        input.addActionListener(e -> addTask()); // Enter key adds

        removeButton.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx != -1) model.remove(idx);
        });

        clearButton.addActionListener(e -> {
            if (model.getSize() > 0 && JOptionPane.showConfirmDialog(frame,
                    "Clear all tasks?", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                model.clear();
            }
        });

        // Delete key removes selected
        list.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    int idx = list.getSelectedIndex();
                    if (idx != -1) model.remove(idx);
                }
            }
        });

        // Save tasks on close
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                saveTasks();
            }
        });

        frame.setSize(420, 520);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        input.requestFocusInWindow();
    }

    private void addTask() {
        String text = input.getText().trim();
        if (!text.isEmpty()) {
            model.addElement(text);
            input.setText("");
        }
    }

    private void loadTasks() {
        Path p = Paths.get(DATA_FILE);
        if (Files.exists(p)) {
            try {
                List<String> lines = Files.readAllLines(p);
                for (String l : lines) if (!l.trim().isEmpty()) model.addElement(l);
            } catch (IOException e) {
                System.err.println("Unable to load tasks: " + e.getMessage());
            }
        }
    }

    private void saveTasks() {
        Path p = Paths.get(DATA_FILE);
        try (BufferedWriter bw = Files.newBufferedWriter(p)) {
            for (int i = 0; i < model.size(); i++) bw.write(model.get(i) + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Unable to save tasks: " + e.getMessage());
        }
    }
}

