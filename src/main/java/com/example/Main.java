package com.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.UUID;

import com.google.cloud.dialogflow.v2.*;

public class Main {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private SessionsClient sessionsClient;
    private SessionName session;

    public Main() {
        initializeGUI();
        initializeDialogflow();
    }

    private void initializeGUI() {
        frame = new JFrame("Chatbot Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);

        inputField = new JTextField();
        inputField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = inputField.getText();
                displayMessage("You: " + userInput);

                String dialogflowResponse = sendUserInputToDialogflow(userInput);
                displayMessage("Chatbot: " + dialogflowResponse);

                inputField.setText("");
            }
        });

        frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        frame.add(inputField, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    private void initializeDialogflow() {
        try {
            sessionsClient = SessionsClient.create();
            String sessionId = UUID.randomUUID().toString();
            session = SessionName.of("small-talk-jequ", sessionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String sendUserInputToDialogflow(String userInput) {
        try {
            TextInput.Builder textInput = TextInput.newBuilder().setText(userInput).setLanguageCode("en-US");
            QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

            DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);
            return response.getQueryResult().getFulfillmentText();
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while processing your request.";
        }
    }

    private void displayMessage(String message) {
        chatArea.append(message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}
