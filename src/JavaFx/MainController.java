package JavaFx;



import java.net.URL;
import java.util.ResourceBundle;
import gameModel.Game;
import gameModel.Grill;
import gameModel.MainGrill;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class MainController implements Initializable {
    @FXML
    private Label timerLabel = new Label();

    @FXML
    private Label statusLabel = new Label();

    @FXML
    private ComboBox grillSizeChoiceBox = new ComboBox();

    @FXML
    private ComboBox levelChoiceBox = new ComboBox();

    @FXML
    private Button checkBtn = new Button();

    @FXML
    private AnchorPane sudokuBoard = new AnchorPane();

    @FXML
    private Label hintLabel = new Label();

    @FXML
    private Button hintBtn = new Button();

    private Game newGame;
    private int sizeOfGrid;
    private GridPane[][] gridPanes;


    @FXML
    private void startGame(ActionEvent event) throws Exception {
        statusLabel.setVisible(false);
        hintLabel.setVisible(true);
        checkBtn.setDisable(false);
        hintBtn.setDisable(false);
        hintLabel.setTextFill(Color.GREEN);

        switch((String)grillSizeChoiceBox.getValue()){
            case("3x3"):
                sizeOfGrid=3;
                gridPanes = new GridPane[sizeOfGrid][sizeOfGrid];
                FXMLLoader loader3x3 = new FXMLLoader(getClass().getResource("3x3.fxml"));
                sudokuBoard.getChildren().setAll((AnchorPane)loader3x3.load());

                Controller3x3 controller3x3 = loader3x3.getController();
                gridPanes = controller3x3.getGrills();
                break;

            case("2x2"):
                sizeOfGrid=2;
                gridPanes = new GridPane[sizeOfGrid][sizeOfGrid];
                FXMLLoader loader2x2 = new FXMLLoader(getClass().getResource("2x2.fxml"));
                sudokuBoard.getChildren().setAll((AnchorPane)loader2x2.load());

                Controller2x2 controller2x2 = loader2x2.getController();
                gridPanes = controller2x2.getGrills();
                break;
        }


        String level = (String) levelChoiceBox.getValue();

        if(newGame != null)
        {
            newGame.getGameTime().stopTime();
        }

        newGame = new Game(level, sizeOfGrid);
        newGame.getGameTime().setTimerLabel(timerLabel);
        newGame.getGameTime().startTime();

        hintLabel.setText("Hint: " + newGame.getHint());


        clearGrids();
        setGrids();

    }

    @FXML
    private void exitGame(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void checkGame(ActionEvent event) {

        setGreenAndRedField();
        disableAllTextField();
        hintBtn.setDisable(true);

        if(newGame.checkWin())
        {
            statusLabel.setVisible(true);
            statusLabel.setTextFill(Color.GREEN);
            statusLabel.setText("You WIN!");
            newGame.getGameTime().stopTime();
        }
        else
        {
            statusLabel.setVisible(true);
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("You LOSE!");
            newGame.getGameTime().stopTime();
        }
    }

    @FXML private void getHint(ActionEvent event) {

        setGreenAndRedField();

        newGame.setHint(newGame.getHint()-1);

        if(newGame.getHint() == 0)
        {
            hintBtn.setDisable(true);
            hintLabel.setTextFill(Color.RED);
        }
        else if(newGame.getHint() < 2)
        {
            hintLabel.setTextFill(Color.ORANGERED);
        }
        else if(newGame.getHint() < 4)
        {
            hintLabel.setTextFill(Color.ORANGE);
        }

        hintLabel.setText("Hint: " + newGame.getHint());

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        grillSizeChoiceBox.setDisable(false);

        grillSizeChoiceBox.setValue("3x3");
        grillSizeChoiceBox.getItems().addAll("3x3","2x2");
        levelChoiceBox.setValue("easy");
        levelChoiceBox.getItems().addAll("easy","med","hard");

        timerLabel.setAlignment(Pos.CENTER);
        timerLabel.setFont(new Font("Arial", 20));
        timerLabel.setText("00:00");

    }

    private void setGrids()
    {
        for(int grillX=0;grillX<newGame.getGrillToGame().getSizeOfGrill();grillX++)
        {
            for(int grillY=0;grillY<newGame.getGrillToGame().getSizeOfGrill();grillY++)
            {
                for(int grillFieldX=0;grillFieldX<newGame.getGrillToGame().getSizeOfGrill();grillFieldX++)
                {
                    for(int grillFieldY=0;grillFieldY<newGame.getGrillToGame().getSizeOfGrill();grillFieldY++)
                    {
                        Label label = new Label(String.valueOf(newGame.getGrillToGame().getGrills()[grillX][grillY].getGrillFields()[grillFieldX][grillFieldY].getNumber()));
                        label.setMaxWidth(Double.MAX_VALUE);
                        label.setAlignment(Pos.CENTER);
                        label.setFont(new Font("Arial", 20));

                        if(!label.getText().equals("0")) {
                            gridPanes[grillX][grillY].add(label, grillFieldY, grillFieldX);
                        }
                        else
                        {
                            TextField textField = new TextField();
                            textField.setMaxWidth(Double.MAX_VALUE);
                            textField.setMaxHeight(Double.MAX_VALUE);
                            textField.setAlignment(Pos.CENTER);
                            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                               if(isNumeric(newValue) && !textField.getText().equals("0")) {
                                   if (textField.getText().length() > 1)
                                   {
                                       String text= textField.getText().substring(0, 1);
                                       textField.setText(text);
                                   }
                               } else {
                                   textField.setText("");
                               }
                            });

                            textField.setFont(new Font("Arial", 20));
                            gridPanes[grillX][grillY].add(textField, grillFieldY, grillFieldX);
                        }
                    }
                }
            }
        }

    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


    private void clearGrids()
    {
        for(int grillX=0;grillX<gridPanes.length;grillX++)
        {
            for(int grillY=0;grillY<gridPanes.length;grillY++)
            {
                for(int grillFieldX=0;grillFieldX<gridPanes.length;grillFieldX++)
                {
                    for(int grillFieldY=0;grillFieldY<gridPanes.length;grillFieldY++)
                    {
                        Node node = gridPanes[grillX][grillY].getChildren().get(0);
                        gridPanes[grillX][grillY].getChildren().clear();
                        gridPanes[grillX][grillY].getChildren().add(0,node);
                    }
                }
            }
        }
    }

    private void setGridTo(MainGrill grills)
    {
        for(int grillX=0;grillX<sizeOfGrid;grillX++) {
            for (int grillY = 0; grillY < sizeOfGrid; grillY++) {
                grills.getGrills()[grillX][grillY] = getGrillsFrom(gridPanes[grillX][grillY]);
            }
        }
    }

    private Grill getGrillsFrom(GridPane grid) {
        Grill toReturn = new Grill(newGame.getSizeOfGrill());
        ObservableList<Node> childrens = grid.getChildren();
        String textFieldString = "javafx.scene.control.TextField";
        String groupString = "javafx.scene.Group";

        int i=0;
        int j=0;

        for (Node node : childrens) {

            if(textFieldString.equals(node.getClass().getCanonicalName()))
            {
                if("".equals(((TextField)node).getText()))
                {
                    toReturn.getGrillFields()[i][j].setNumber(0);
                    j++;
                }
                else {
                    toReturn.getGrillFields()[i][j].setNumber(Integer.valueOf(((TextField) node).getText()));
                    j++;
                }
            }
            else
            {
                if(!groupString.equals(node.getClass().getCanonicalName()))
                {
                    toReturn.getGrillFields()[i][j].setNumber(Integer.valueOf(((Label)node).getText()));
                    j++;
                }
            }

            if(j>newGame.getSizeOfGrill()-1)
            {
                i++;
                j=0;
            }
        }
        return toReturn;
    }

    private void disableAllTextField()
    {
        String textFieldString = "javafx.scene.control.TextField";

        for(int gridX=0; gridX<newGame.getSizeOfGrill(); gridX++)
        {
            for(int gridY=0; gridY<newGame.getSizeOfGrill(); gridY++)
            {
                ObservableList<Node> childrens = gridPanes[gridX][gridY].getChildren();
                    for (Node node : childrens) {
                        if(textFieldString.equals(node.getClass().getCanonicalName())) {
                                node.setDisable(true);
                            }
                    }
            }
        }
    }

    private void setGreenAndRedField()
    {

        setGridTo(newGame.getUsersGrill());

        int[][] tableValidate = newGame.getValidateTable();

        String textFieldString = "javafx.scene.control.TextField";
        String groupString = "javafx.scene.Group";

        for(int gridX=0; gridX<newGame.getSizeOfGrill(); gridX++)
        {
            for(int gridY=0; gridY<newGame.getSizeOfGrill(); gridY++)
            {
                ObservableList<Node> childrens = gridPanes[gridX][gridY].getChildren();
                int i=0;
                int j=0;

                for (Node node : childrens) {
                    if(textFieldString.equals(node.getClass().getCanonicalName())) {
                        if(tableValidate[gridX*newGame.getSizeOfGrill()+i][gridY*newGame.getSizeOfGrill()+j] == 0)
                        {
                            node.setStyle("-fx-control-inner-background: #ff0000");
                        }
                        else if(tableValidate[gridX*newGame.getSizeOfGrill()+i][gridY*newGame.getSizeOfGrill()+j] == 1)
                        {
                            node.setStyle("-fx-control-inner-background: #00ff00");
                            node.setDisable(true);

                            newGame.setGrillToGame(newGame.getUsersGrill());
                        }
                        else if(tableValidate[gridX*newGame.getSizeOfGrill()+i][gridY*newGame.getSizeOfGrill()+j] == 2){
                            node.setStyle("-fx-control-inner-background: white");
                        }
                        j++;
                    }
                    else
                    {
                        if(!groupString.equals(node.getClass().getCanonicalName()))
                        {
                            j++;
                        }
                    }


                    if(j>newGame.getSizeOfGrill()-1)
                    {
                        i++;
                        j=0;
                    }
                }
            }
        }
    }


}