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

    public PlayerCard(LobbyPlayer player, Runnable onDelete) {
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
            colorCircle.setFill(Color.web(player.getColorHex()));
            
            try {
                ImageView avatarView = new ImageView(new Image(getClass().getResourceAsStream(player.getAvatarPath())));
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

            // Name Label
            Label nameLabel = new Label(player.getName());
            if (Main.HEADER_FONT != null) nameLabel.setFont(Font.font(Main.HEADER_FONT.getFamily(), 22));
            else nameLabel.setFont(Font.font("System", FontWeight.BOLD, 22));
            nameLabel.setTextFill(Color.WHITE);
            
            AnchorPane.setLeftAnchor(nameLabel, 90.0);
            AnchorPane.setTopAnchor(nameLabel, (CARD_HEIGHT - 32) / 2.0);

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
            
            AnchorPane.setRightAnchor(deleteBtn, 8.0);
            AnchorPane.setTopAnchor(deleteBtn, 8.0);

            content.getChildren().addAll(avatarContainer, nameLabel, deleteBtn);
            getChildren().add(content);
        }
    }
}
