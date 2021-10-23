package customjavafx.scene.layout;


import customjavafx.scene.control.AndarBaharBeadRoadLabel;
import customjavafx.scene.control.AndarBaharBeadRoadResult;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;

public class BeadRoadTilePane extends TilePane {

    private int column = 0;
    private int row = -1;


    private IntegerProperty count = new SimpleIntegerProperty(0);

    public IntegerProperty getCountProperty() {
        return count;
    }

    private IntegerProperty playerWinCount = new SimpleIntegerProperty(0);

    public IntegerProperty getPlayerWinCount() {
        return playerWinCount;
    }


    private IntegerProperty bankerWinCount = new SimpleIntegerProperty(0);

    public IntegerProperty getBankerWinCount() {
        return bankerWinCount;
    }


    /*
        Adding 1st and 2nd car win support
     */
    private IntegerProperty playerWinFirstCount = new SimpleIntegerProperty(0);

    public IntegerProperty getPlayerWinFirstCount() {
        return playerWinFirstCount;
    }


    private IntegerProperty bankerWinFirstCount = new SimpleIntegerProperty(0);

    public IntegerProperty getBankerWinFirstCount() {
        return bankerWinFirstCount;
    }

    private IntegerProperty playerWinSecondCount = new SimpleIntegerProperty(0);

    public IntegerProperty getPlayerWinSecondCount() {
        return playerWinSecondCount;
    }


    private IntegerProperty bankerWinSecondCount = new SimpleIntegerProperty(0);

    public IntegerProperty getBankerWinSecondCount() {
        return bankerWinSecondCount;
    }

    /*
        End of Adding 1st and 2nd car win support
     */

    private ListProperty<AndarBaharBeadRoadResult> beadRoadList = new SimpleListProperty<AndarBaharBeadRoadResult>(FXCollections.observableList(new ArrayList<AndarBaharBeadRoadResult>()));
    public ListProperty<AndarBaharBeadRoadResult> getBeadRoadListProperty() {
        return beadRoadList;
    }

    public BeadRoadTilePane() {
        setOrientation(Orientation.VERTICAL);
        setAlignment(Pos.TOP_LEFT);
    }

    private int sizeLimit() {
        return (getPrefColumns() * getPrefRows());
    }

    private int getSize() {
        return getPosition() + 1;
    }

    private int getPosition() {
        return (column * getPrefRows()) + row;
    }

    private boolean childrenLimitReached() {
        return (getChildren().size() == sizeLimit());
    }

    private void ResultAdded(AndarBaharBeadRoadResult res) {
        beadRoadList.add(res);
        count.setValue(count.getValue() + 1);
        switch (res) {
            case BANKER_WIN:
                bankerWinCount.setValue(bankerWinCount.getValue() + 1);
                break;
            case PLAYER_WIN:
                playerWinCount.setValue(playerWinCount.getValue() + 1);
                break;
            case BANKER_WIN_FIRST:
                bankerWinFirstCount.setValue(bankerWinFirstCount.getValue() + 1);
                break;
            case PLAYER_WIN_FIRST:
                playerWinFirstCount.setValue(playerWinFirstCount.getValue() + 1);
                break;
            case BANKER_WIN_SECOND:
                bankerWinSecondCount.setValue(bankerWinSecondCount.getValue() + 1);
                break;
            case PLAYER_WIN_SECOND:
                playerWinSecondCount.setValue(playerWinSecondCount.getValue() + 1);
                break;
            default:
                break;
        }
    }

    private boolean isCurrentWinRed() {
        switch (((AndarBaharBeadRoadLabel) getChildren().get(getPosition())).getResult()) {
            case BANKER_WIN:
            case BANKER_WIN_FIRST:
            case BANKER_WIN_SECOND:
                return true;
            default:
                return false;
        }
    }

    private boolean isCurrentWinBlue() {
        switch (((AndarBaharBeadRoadLabel) getChildren().get(getPosition())).getResult()) {
            case PLAYER_WIN:
            case PLAYER_WIN_FIRST:
            case PLAYER_WIN_SECOND:
                return true;
            default:
                return false;
        }
    }

    private void ResultRemoved(AndarBaharBeadRoadResult res) {
        count.setValue(count.getValue() - 1);
        switch (res) {
            case BANKER_WIN:
                bankerWinCount.setValue(bankerWinCount.getValue() - 1);
                break;

            case PLAYER_WIN:
                playerWinCount.setValue(playerWinCount.getValue() - 1);
                break;

            case BANKER_WIN_FIRST:
                bankerWinFirstCount.setValue(bankerWinFirstCount.getValue() - 1);
                break;

            case PLAYER_WIN_FIRST:
                playerWinFirstCount.setValue(playerWinFirstCount.getValue() - 1);
                break;

            case BANKER_WIN_SECOND:
                bankerWinSecondCount.setValue(bankerWinSecondCount.getValue() - 1);
                break;

            case PLAYER_WIN_SECOND:
                playerWinSecondCount.setValue(playerWinSecondCount.getValue() - 1);
                break;

            default:
                break;
        }
    }


    private void Update(AndarBaharBeadRoadResult res) {
        MovePositionFront();
        ((AndarBaharBeadRoadLabel) getChildren().get(getPosition())).setResult(res);
    }

    private void Insert() {
        AndarBaharBeadRoadLabel temp = new AndarBaharBeadRoadLabel(AndarBaharBeadRoadResult.EMPTY);
        temp.setResult(AndarBaharBeadRoadResult.EMPTY);
        getChildren().add(temp);
    }

    private void MovePositionFront() {
        if (row == (getPrefRows() - 1)) {
            column++;
            row = 0;
        } else {
            row++;
        }

    }


    public void RemoveElement() {
        if (getPosition() >= 0) {
            AndarBaharBeadRoadResult tmp = ((AndarBaharBeadRoadLabel) getChildren().get(getPosition())).getResult();
            ((AndarBaharBeadRoadLabel) getChildren().get(getPosition())).setResult(AndarBaharBeadRoadResult.EMPTY);
            beadRoadList.remove(beadRoadList.size() - 1);
            getChildren().stream().map(t -> (AndarBaharBeadRoadLabel) t).forEach(t -> {
                t.setResult(AndarBaharBeadRoadResult.EMPTY);
            });
            column = 0;
            row = -1;
            beadRoadList.stream().forEach(t->{
                if (getSize() >= sizeLimit()) {
                    ShiftElement();
                }
                Update(t);
            });
            ResultRemoved(tmp);
        }
    }

    private void ShiftElement() {
        int localSaveRow= row;
        int localSaveColumn = column;
        this.getChildren().remove(0, 1);
        Insert();
        row = localSaveRow - 1;
        column= localSaveColumn;
    }

    public String LastWinAudio() {
        if (isCurrentWinRed()) return "bahar-won.mp3";
        else return "andar-won.mp3";
    }

    public boolean isEmpty() {
        return getSize() > 0;
    }

    public AndarBaharBeadRoadResult LastWinResult() {
        if(getSize() > 0) {
            return ((AndarBaharBeadRoadLabel) getChildren().get(getPosition())).getResult();
        } else {
            return AndarBaharBeadRoadResult.EMPTY;
        }
    }

    public String LastWin() {
        if ((getSize() > 0) && isCurrentWinRed()) return "bWin";
        else if ((getSize() > 0) && isCurrentWinBlue()) return "pWin";
        else {
            return "";
        }
    }

    public void AddElement(AndarBaharBeadRoadResult res) {
        if (getSize() >= sizeLimit()) {
            ShiftElement();
        }
        Update(res);
        ResultAdded(res);
    }

    public void Initialize(int row, int column) {
        this.setPrefRows(row);
        this.setPrefColumns(column);
        while (!childrenLimitReached()) {
            Insert();
        }
        this.column = 0;
        this.row = -1;
    }

    public void Reset() {
        getChildren().stream().map(t -> (AndarBaharBeadRoadLabel) t).forEach(t -> {
            t.setResult(AndarBaharBeadRoadResult.EMPTY);
        });
        beadRoadList.clear();
        bankerWinCount.setValue(0);
        playerWinCount.setValue(0);
        bankerWinFirstCount.setValue(0);
        playerWinFirstCount.setValue(0);
        bankerWinSecondCount.setValue(0);
        playerWinSecondCount.setValue(0);
        count.setValue(0);
        column = 0;
        row = -1;
    }


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }

}
