package com.mykogroup.riskclone.view;

import com.mykogroup.riskclone.Main;
import com.mykogroup.riskclone.model.LobbyPlayer;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class PlayerCard extends StackPane {
    private static final double CARD_WIDTH = 340;
    private static final double CARD_HEIGHT = 80;

    public PlayerCard(LobbyPlayer player, boolean isHost, Runnable onDelete) {
        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);

        String bgPath = (player == null) ? "/com/mykogroup/riskclone/assets/empty-player-card.png"
                                         : "/com/mykogroup/riskclone/assets/nonempty-player-card.png";

        try {
            ImageView bgView = new ImageView(new Image(getClass().getResourceAsStream(bgPath)));
            bgView.setFitWidth(CARD_WIDTH);
            bgView.setFitHeight(CARD_HEIGHT);
            getChildren().add(bgView);
        } catch (Exception e) {
            setStyle("-fx-border-color: white; -fx-border-style: dashed; -fx-background-color: rgba(0,0,0,0.2);");
        }

        if (player != null) {
            AnchorPane content = new AnchorPane();
            content.setPrefSize(CARD_WIDTH, CARD_HEIGHT);
            content.setMaxSize(CARD_WIDTH, CARD_HEIGHT);

            // Avatar Container
            StackPane avatarContainer = new StackPane();
            Circle colorCircle = new Circle(28);
            colorCircle.setFill(Color.web(player.color));
            
            try {
                ImageView avatarView = new ImageView(new Image(getClass().getResourceAsStream(player.avatarPath)));
                avatarView.setFitWidth(50);
                avatarView.setFitHeight(50);
                Circle clip = new Circle(25, 25, 25);
                avatarView.setClip(clip);
                avatarContainer.getChildren().addAll(colorCircle, avatarView);
            } catch (Exception e) {
                avatarContainer.getChildren().add(colorCircle);
            }
            
            AnchorPane.setLeftAnchor(avatarContainer, 20.0);
            AnchorPane.setTopAnchor(avatarContainer, (CARD_HEIGHT - 56) / 2.0);

            // Host Badge
            if (isHost) {
                Label hostBadge = new Label("DATU");
                hostBadge.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: black; -fx-font-weight: bold; -fx-font-size: 10px; -fx-padding: 2 5; -fx-background-radius: 3;");
                if (Main.BODY_FONT != null) hostBadge.setFont(Font.font(Main.BODY_FONT.getFamily(), FontWeight.BOLD, 10));
                
                AnchorPane.setRightAnchor(hostBadge, 10.0);
                AnchorPane.setBottomAnchor(hostBadge, 5.0);
                content.getChildren().add(hostBadge);
            }

            // Name Label
            Label nameLabel = new Label(player.displayName);
            if (Main.HEADER_FONT != null) nameLabel.setFont(Font.font(Main.HEADER_FONT.getFamily(), 28));
            else nameLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
            nameLabel.setTextFill(Color.WHITE);
            nameLabel.setAlignment(Pos.CENTER);
            
            AnchorPane.setLeftAnchor(nameLabel, 0.0);
            AnchorPane.setRightAnchor(nameLabel, 0.0);
            AnchorPane.setTopAnchor(nameLabel, (CARD_HEIGHT - 40) / 2.0);

            // Delete Button (Circle with X)
            StackPane deleteBtn = new StackPane();
            Circle deleteCircle = new Circle(12, Color.web("#ef4444"));
            Label xLabel = new Label("×");
            xLabel.setTextFill(Color.WHITE);
            xLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            xLabel.setTranslateY(-1);
            
            deleteBtn.getChildren().addAll(deleteCircle, xLabel);
            deleteBtn.setCursor(javafx.scene.Cursor.HAND);
            deleteBtn.setOnMouseClicked(e -> {
                if (onDelete != null) onDelete.run();
            });
            
            // Hover effect for delete button
            deleteBtn.setOnMouseEntered(e -> {
                deleteCircle.setRadius(14);
                deleteBtn.setScaleX(1.1);
                deleteBtn.setScaleY(1.1);
            });
            deleteBtn.setOnMouseExited(e -> {
                deleteCircle.setRadius(12);
                deleteBtn.setScaleX(1.0);
                deleteBtn.setScaleY(1.0);
            });
            
            AnchorPane.setRightAnchor(deleteBtn, 8.0);
            AnchorPane.setTopAnchor(deleteBtn, 8.0);

            content.getChildren().addAll(avatarContainer, nameLabel);
            if (onDelete != null) content.getChildren().add(deleteBtn);
            getChildren().add(content);
        }
    }
}
