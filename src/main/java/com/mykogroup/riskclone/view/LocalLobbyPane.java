package com.mykogroup.riskclone.view;

import com.mykogroup.riskclone.Main;
import com.mykogroup.riskclone.model.LobbyPlayer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class LocalLobbyPane extends StackPane {
    private final List<LobbyPlayer> players = new ArrayList<>();
    private final GridPane playerGrid = new GridPane();
    private final Runnable onStart;
    private final Runnable onBack;

    private static final String[] DEFAULT_COLORS = {
            "#ef4444", "#3b82f6", "#10b981", "#f59e0b",
            "#8b5cf6", "#ec4899", "#14b8a6", "#f97316"
    };

    private static final String[] AI_NAMES = {
            "Jose Rizal", "Andres Bonifacio", "Magellan", "Lapu-lapu", "Antonio Luna",
            "Gabriela Silang", "Apolinario Mabini", "Emilio Jacinto", "Melchora Aquino",
            "Sultan Kudarat", "Ferdinand Magellan", "Juan Luna", "Emilio Aguinaldo"
    };

    public LocalLobbyPane(Runnable onStart, Runnable onBack) {
        this.onStart = onStart;
        this.onBack = onBack;

        // Background
        try {
            ImageView bgView = new ImageView(new Image(getClass().getResourceAsStream("/com/mykogroup/riskclone/assets/local-lobby-bg.png")));
            bgView.setFitWidth(1280);
            bgView.setFitHeight(720);
            getChildren().add(bgView);
        } catch (Exception e) {
            setStyle("-fx-background-color: #3d2b1f;");
        }

        VBox content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(120, 0, 0, 0)); // Lowered to avoid background header overlap

        // Grid
        playerGrid.setHgap(30);
        playerGrid.setVgap(20);
        playerGrid.setAlignment(Pos.CENTER);
        refreshGrid();

        // Bottom Buttons (Add Player, Add Bot)
        HBox addButtons = new HBox(30);
        addButtons.setAlignment(Pos.CENTER);

        Button addPlayerBtn = createIconButton("/com/mykogroup/riskclone/assets/add-player-btn.png", 220, 60);
        addPlayerBtn.setOnAction(e -> showAddPlayerModal(false));

        Button addBotBtn = createIconButton("/com/mykogroup/riskclone/assets/add-bot-btn.png", 220, 60);
        addBotBtn.setOnAction(e -> addBot());

        addButtons.getChildren().addAll(addPlayerBtn, addBotBtn);

        content.getChildren().addAll(playerGrid, addButtons);

        // Play Button (Right side)
        Button playBtn = createIconButton("/com/mykogroup/riskclone/assets/play-btn.png", 100, 150);
        StackPane.setAlignment(playBtn, Pos.CENTER_RIGHT);
        playBtn.setTranslateX(0); // Move against the edge
        playBtn.setOnAction(e -> {
            if (players.size() >= 4) {
                if (onStart != null) onStart.run();
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Minimum of 4 players required to start!");
                alert.show();
            }
        });

        // Back Button
        Button backBtn = new Button("BACK");
        try {
            String btnImgPath = getClass().getResource("/com/mykogroup/riskclone/assets/main-menu-btn.png").toExternalForm();
            backBtn.setStyle("-fx-background-image: url('" + btnImgPath + "'); " +
                             "-fx-background-size: 100% 100%; " +
                             "-fx-background-color: transparent; " +
                             "-fx-min-width: 140px; -fx-min-height: 40px; " +
                             "-fx-max-width: 140px; -fx-max-height: 40px; " +
                             "-fx-text-fill: white; " +
                             "-fx-alignment: center; " +
                             "-fx-padding: 0 0 5 0; " +
                             "-fx-cursor: hand;");
        } catch (Exception e) {
            backBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
        }
        
        if (Main.HEADER_FONT != null) backBtn.setFont(Font.font(Main.HEADER_FONT.getFamily(), 18));
        else if (Main.BODY_FONT != null) backBtn.setFont(Font.font(Main.BODY_FONT.getFamily(), FontWeight.BOLD, 16));
        
        backBtn.setOnAction(e -> onBack.run());
        StackPane.setAlignment(backBtn, Pos.TOP_LEFT);
        StackPane.setMargin(backBtn, new Insets(20));

        getChildren().addAll(content, playBtn, backBtn);
    }

    public List<LobbyPlayer> getPlayers() {
        return players;
    }

    private void addBot() {
        if (players.size() >= 8) return;
        
        Random rand = new Random();
        String name = AI_NAMES[rand.nextInt(AI_NAMES.length)];
        String avatar = "/com/mykogroup/riskclone/assets/Avatar" + (rand.nextInt(6) + 1) + ".png";
        
        // Find first available color
        List<String> takenColors = players.stream().map(LobbyPlayer::getColorHex).collect(Collectors.toList());
        String color = DEFAULT_COLORS[0];
        for (String c : DEFAULT_COLORS) {
            if (!takenColors.contains(c)) {
                color = c;
                break;
            }
        }
        
        players.add(new LobbyPlayer(name, avatar, color, true));
        refreshGrid();
    }

    private void refreshGrid() {
        playerGrid.getChildren().clear();
        for (int i = 0; i < 8; i++) {
            int row = i / 2;
            int col = i % 2;
            
            LobbyPlayer p = (i < players.size()) ? players.get(i) : null;
            final int index = i;
            PlayerCard card = new PlayerCard(p, () -> {
                players.remove(index);
                refreshGrid();
            });
            playerGrid.add(card, col, row);
        }
    }

    private void showAddPlayerModal(boolean isAi) {
        if (players.size() >= 8) return;

        Stage modal = new Stage(StageStyle.TRANSPARENT);
        modal.initModality(Modality.APPLICATION_MODAL);
        
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #2c3e50; -fx-border-color: #d4af37; -fx-border-width: 2; -fx-background-radius: 15; -fx-border-radius: 15;");
        root.setAlignment(Pos.CENTER);

        Label title = new Label("Add New Player");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter Player Name");
        nameField.setStyle("-fx-font-size: 16px;");

        // Avatar Picker
        Label avatarLabel = new Label("Select Avatar:");
        avatarLabel.setTextFill(Color.WHITE);
        HBox avatarBox = new HBox(10);
        avatarBox.setAlignment(Pos.CENTER);
        ToggleGroup avatarGroup = new ToggleGroup();
        String selectedAvatar[] = {"/com/mykogroup/riskclone/assets/Avatar1.png"};

        for (int i = 1; i <= 6; i++) {
            String path = "/com/mykogroup/riskclone/assets/Avatar" + i + ".png";
            ToggleButton btn = new ToggleButton();
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
            iv.setFitWidth(50);
            iv.setFitHeight(50);
            btn.setGraphic(iv);
            btn.setToggleGroup(avatarGroup);
            if (i == 1) btn.setSelected(true);
            btn.setOnAction(e -> selectedAvatar[0] = path);
            avatarBox.getChildren().add(btn);
        }

        // Color Picker
        Label colorLabel = new Label("Select Color:");
        colorLabel.setTextFill(Color.WHITE);
        FlowPane colorBox = new FlowPane(10, 10);
        colorBox.setAlignment(Pos.CENTER);
        colorBox.setPrefWrapLength(200);
        
        List<String> takenColors = players.stream().map(LobbyPlayer::getColorHex).collect(Collectors.toList());
        ToggleGroup colorGroup = new ToggleGroup();
        String selectedColor[] = {null};

        for (String hex : DEFAULT_COLORS) {
            ToggleButton btn = new ToggleButton();
            Circle c = new Circle(15, Color.web(hex));
            btn.setGraphic(c);
            btn.setToggleGroup(colorGroup);
            
            if (takenColors.contains(hex)) {
                btn.setDisable(true);
                btn.setOpacity(0.3);
            } else if (selectedColor[0] == null) {
                btn.setSelected(true);
                selectedColor[0] = hex;
            }
            
            btn.setOnAction(e -> selectedColor[0] = hex);
            colorBox.getChildren().add(btn);
        }

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.web("#ef4444"));
        errorLabel.setFont(Font.font(14));

        Button addBtn = new Button("ADD PLAYER");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        addBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText("Please enter a name!");
                nameField.setStyle("-fx-font-size: 16px; -fx-border-color: #ef4444; -fx-border-width: 2;");
                return;
            }
            players.add(new LobbyPlayer(name, selectedAvatar[0], selectedColor[0], isAi));
            modal.close();
            refreshGrid();
        });

        Button cancelBtn = new Button("CANCEL");
        cancelBtn.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 30;");
        cancelBtn.setOnAction(e -> modal.close());

        HBox actions = new HBox(20, cancelBtn, addBtn);
        actions.setAlignment(Pos.CENTER);

        root.getChildren().addAll(title, nameField, errorLabel, avatarLabel, avatarBox, colorLabel, colorBox, actions);
        
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        modal.setScene(scene);
        modal.showAndWait();
    }

    private Button createIconButton(String path, double w, double h) {
        Button btn = new Button();
        try {
            ImageView iv = new ImageView(new Image(getClass().getResourceAsStream(path)));
            iv.setFitWidth(w);
            iv.setFitHeight(h);
            btn.setGraphic(iv);
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
            btn.setCursor(javafx.scene.Cursor.HAND);
            
            // Hover effect
            btn.setOnMouseEntered(e -> btn.setOpacity(0.8));
            btn.setOnMouseExited(e -> btn.setOpacity(1.0));
        } catch (Exception e) {
            btn.setText(path.substring(path.lastIndexOf('/') + 1));
        }
        return btn;
    }
}
