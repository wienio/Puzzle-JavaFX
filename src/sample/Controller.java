package sample;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Controller {

    @FXML
    private Label timeLabel;
    @FXML
    private AnchorPane panel;

    private List<Tile> tilesList;
    private long time;
    private Timeline timeline;
    private long record;

    private Tile first = null;
    private Tile second = null;

    private Rectangle firstClicked;
    private Rectangle secondClicked;
    private Rectangle strokeRect;

    @FXML
    private void startButtonHandle () {
        time=0;
        Collections.shuffle(tilesList);
        for (int i = 0 ; i<tilesList.size() ; ++i) {
            Tile tile = tilesList.get(i);
            int num = tile.getNum();
            tile.setFill(new ImagePattern(SwingFXUtils.toFXImage(tilesList.get(num).getPart(),null)));
        }

        timeline = new Timeline(new KeyFrame(
                Duration.millis(2),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent ae) {
                       updateTime();
                    }
                }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void initialize() {
        this.tilesList = SubImage.getTiles(new File("out\\production\\Puzzle\\assets\\image2.jpg"));
        try {
            Scanner scanner = new Scanner(new File("record.txt"));
            if (new File("record.txt").length() == 0) {
                record=-1;
            }
            else {
                record = scanner.nextLong();
            }
        } catch (FileNotFoundException e) {
            record = -1;
        }

        for (Tile tile: tilesList) {
            tile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (first == null) {
                        first = (Tile) event.getSource();
                        firstClicked.setLayoutX(first.getLayoutX());
                        firstClicked.setLayoutY(first.getLayoutY());
                        strokeRect.setFill(Color.RED);
                        strokeRect.setLayoutX(first.getLayoutX()-3);
                        strokeRect.setLayoutY(first.getLayoutY()-3);
                    }
                    else if (second == null) {
                        second = (Tile) event.getSource();
                        secondClicked.setLayoutX(second.getLayoutX());
                        secondClicked.setLayoutY(second.getLayoutY());

                        if(validMove()) {
                        PathTransition ptr = getPathTransition(first,second);
                        PathTransition ptr2 = getPathTransition(second,first);
                        ParallelTransition pt = new ParallelTransition(ptr,ptr2);
                        strokeRect.setFill(Color.TRANSPARENT);
                        pt.play();
                        pt.setOnFinished(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                if(validMove()) {
                                    swap();
                                }
                                if(endGame()) {
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            timeline.stop();
                                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                            alert.setTitle("Koniec gry!!");
                                            alert.setHeaderText("Gratulacje, wygrałeś!");
                                            try {
                                                if (record == -1 ) {
                                                    BufferedWriter out = new BufferedWriter(new FileWriter("record.txt"));
                                                    alert.setContentText("Twój wynik to: " + time + " ms");
                                                    out.write(Long.toString(time));
                                                    out.close();
                                                }
                                                else if (record > time) {
                                                    BufferedWriter out = new BufferedWriter(new FileWriter("record.txt"));
                                                    alert.setContentText("Pobiłeś swój rekord o: " + (record - time) + " ms" + "\nTwój wynik to: " + time + " ms");
                                                    out.write(Long.toString(time));
                                                    out.close();
                                                }
                                                else {
                                                    alert.setContentText("Niestety, nie udało Ci się pobić rekordu.\nTwój wynik to: " + time + " ms");
                                                }
                                            } catch (IOException e) {
                                            }
                                            alert.showAndWait();
                                        }
                                    });
                                }

                            }
                        });
                        }
                    }
                }
            });
        }

        firstClicked = new Rectangle(140,140,Color.TRANSPARENT);
        secondClicked = new Rectangle(140,140,Color.TRANSPARENT);
        strokeRect = new Rectangle(146,146,Color.TRANSPARENT);
        panel.getChildren().add(firstClicked);
        panel.getChildren().add(secondClicked);
        panel.getChildren().add(strokeRect);
        panel.getChildren().addAll(tilesList);
    }

    private void swap () {
        int indexFirst = tilesList.indexOf(first);
        int indexSecond = tilesList.indexOf(second);
        Collections.swap(tilesList, indexFirst, indexSecond);

        double sx,sy,fx,fy;

        fx=first.getLayoutX();
        fy=first.getLayoutY();
        sx=second.getLayoutX();
        sy=second.getLayoutY();

        first.setTranslateX(0);
        first.setTranslateY(0);
        second.setTranslateX(0);
        second.setTranslateY(0);

        first.setLayoutX(sx);
        first.setLayoutY(sy);
        second.setLayoutX(fx);
        second.setLayoutY(fy);
        first = null;
        second = null;
    }

    private boolean validMove() {
        if ( Math.abs( first.getLayoutX() - second.getLayoutX() ) == 151 && Math.abs( first.getLayoutY() - second.getLayoutY() ) == 147 ) {
            strokeRect.setFill(Color.TRANSPARENT);
            first=null;
            second=null;
            return false;
        }
        if (( Math.abs( first.getLayoutX() - second.getLayoutX() ) == 0 || Math.abs ( first.getLayoutX() - second.getLayoutX() ) == 151 ) && ( Math.abs( first.getLayoutY() - second.getLayoutY() ) == 0 || Math.abs( first.getLayoutY() - second.getLayoutY() ) == 147 ) ) return true;

        strokeRect.setFill(Color.TRANSPARENT);
        first=null;
        second=null;
        return false;
    }

    private boolean endGame() {
        for (int i = 0 ; i < tilesList.size() ; ++i) {
            if (tilesList.get(i).getNum() != i) {
                return false;
            }
        }
        return true;
    }

    private void updateTime() {
        long second = TimeUnit.MILLISECONDS.toSeconds(time);
        long minute = TimeUnit.MILLISECONDS.toMinutes(time);
        long hour = TimeUnit.MILLISECONDS.toHours(time);
        long millis = time - TimeUnit.SECONDS.toMillis(second);
        String timeString = String.format("%02d:%02d:%02d:%d",hour,minute%60,second%60,millis);
        timeLabel.setText(timeString);
        time+=2;
    }

    private PathTransition getPathTransition(Tile first, Tile second) {
        PathTransition ptr = new PathTransition();
        Path path = new Path();
        path.getElements().clear();
        path.getElements().add(new MoveToAbs(first));
        path.getElements().add(new LineToAbs(first, second.getLayoutX(),second.getLayoutY()));
        ptr.setPath(path);
        ptr.setNode(first);
        return ptr;
    }

    public static class MoveToAbs extends MoveTo {
        public MoveToAbs(Node node) {
            super(node.getLayoutBounds().getWidth()/2, node.getLayoutBounds().getHeight()/2);
        }
    }

    public static class LineToAbs extends LineTo {
        public LineToAbs (Node node, double x, double y) {
            super (x-node.getLayoutX() + node.getLayoutBounds().getWidth()/2, y- node.getLayoutY() + node.getLayoutBounds().getHeight() /2 );
        }
    }
}
