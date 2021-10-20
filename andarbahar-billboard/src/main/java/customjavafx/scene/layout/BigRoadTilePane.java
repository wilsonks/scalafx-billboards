package customjavafx.scene.layout;


import customjavafx.scene.control.*;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.TilePane;

import java.util.ArrayList;

public class BigRoadTilePane extends TilePane {

    private int column = 0;
    private int row = -1;
    private int savedColumn = -1;
    private boolean shiftedNow = false;
    private int c0,c1,c2,c3,c4 = 0;

    public BigRoadTilePane() {
        super();
        super.setOrientation(Orientation.VERTICAL);
        super.setAlignment(Pos.TOP_LEFT);
    }

    private int getSizeLimit() {
        return (getPrefColumns() * getPrefRows());
    }

    private boolean childrenLimitReached() {
        return (getChildren().size() == getSizeLimit());
    }

    private boolean isCurrentWinRed() {
        switch (((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition())).getResult()) {
            case BANKER_WIN:
            case BANKER_WIN_FIRST:
            case BANKER_WIN_SECOND:
                return true;
            default:
                return false;
        }
    }

    private boolean isCurrentWinBlue() {
        switch (((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition())).getResult()) {
            case PLAYER_WIN:
            case PLAYER_WIN_FIRST:
            case PLAYER_WIN_SECOND:
                return true;
            default:
                return false;
        }
    }

    private boolean isNextWinRed(AndarBaharBeadRoadResult win) {
        switch (win) {
            case BANKER_WIN:
            case BANKER_WIN_FIRST:
            case BANKER_WIN_SECOND:
                return true;
            default:
                return false;
        }
    }

    private boolean isNextWinBlue(AndarBaharBeadRoadResult win) {
        switch (win) {
            case PLAYER_WIN:
            case PLAYER_WIN_FIRST:
            case PLAYER_WIN_SECOND:
                return true;
            default:
                return false;
        }
    }


    private void ClearLabel(AndarBaharBigRoadLabel t) {
        t.setResult(AndarBaharBigRoadResult.EMPTY);
        t.setText("");
    }

    private void Insert() {
        this.getChildren().add(new AndarBaharBigRoadLabel(AndarBaharBigRoadResult.EMPTY));
    }

    private int getSize() {
        return getCurrentPosition() + 1;
    }

    private int getCurrentPosition() {
        return (column * getPrefRows()) + row;
    }

    private void ShiftColumnNew() {
        int localSaveRow= row;
        int localSaveColumn = column;
        this.getChildren().remove(0, (getPrefRows()));
        for(int i=0; i < (getPrefRows());i++){
            Insert();
        }
        row = localSaveRow;
        column= localSaveColumn - 1;
        shiftedNow = true;
        if(savedColumn != -1) savedColumn--;
    }

    private void MoveForSameColor() {
        if ((getPrefRows() - (row + 1)) == 0) {
            if(savedColumn == -1) savedColumn = column;
            column++;

        } else {
            if (((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition() + 1)).getResult() == AndarBaharBigRoadResult.EMPTY){
                row++;
            }
            else {
                if(savedColumn == -1) savedColumn = column;
                column++;

            }
        }
        c0++;
    }

    private void MoveForDifferentColor() {
        if(savedColumn != -1) {
            column = savedColumn + 1;
            savedColumn = -1;
            row = 0;
        }
        else {
            column++;
            row = 0;

        }
        c4 = c3;c3 = c2;c2 = c1;c1 = c0;c0 = 1;
    }

    private void MoveToNextPositionFront(AndarBaharBeadRoadResult next) {
        if (getSize() != 0) {
            if (isCurrentWinRed()) {
                if (isNextWinRed(next)) {
                    MoveForSameColor();
                } else {
                    if (isNextWinBlue(next)) {
                        MoveForDifferentColor();
                    }
                }
            }
            else if (isCurrentWinBlue()) {
                if (isNextWinRed(next)) {
                    MoveForDifferentColor();
                } else {
                    if (isNextWinBlue(next)) {
                        MoveForSameColor();
                    }
                }
            }
        } else {
            row++;
            c0++;
        }
        if (getCurrentPosition() >= getSizeLimit()) {
            ShiftColumnNew();
        }
    }

    private void AddElement(AndarBaharBeadRoadResult next) {
        MoveToNextPositionFront(next);
        switch (next) {
            case BANKER_WIN:
                ((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition())).setResult(AndarBaharBigRoadResult.BANKER_WIN);
                break;

            case PLAYER_WIN:
                ((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition())).setResult(AndarBaharBigRoadResult.PLAYER_WIN);
                break;

            case BANKER_WIN_FIRST:
                ((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition())).setResult(AndarBaharBigRoadResult.BANKER_WIN_FIRST);
                break;

            case PLAYER_WIN_FIRST:
                ((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition())).setResult(AndarBaharBigRoadResult.PLAYER_WIN_FIRST);
                break;

            case BANKER_WIN_SECOND:
                ((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition())).setResult(AndarBaharBigRoadResult.BANKER_WIN_SECOND);
                break;

            case PLAYER_WIN_SECOND:
                ((AndarBaharBigRoadLabel) super.getChildren().get(getCurrentPosition())).setResult(AndarBaharBigRoadResult.PLAYER_WIN_SECOND);
                break;


            default:
                break;
        }
    }

    private Long getCount() {
        return getChildren()
                .stream()
                .map(x->(AndarBaharBigRoadLabel)x)
                .filter(t->t.getResult()!= AndarBaharBigRoadResult.EMPTY)
                .count();
    }

    private void ReArrangeElementsOld(BeadRoadTilePane bead) {
        getChildren().stream().map(x -> (AndarBaharBigRoadLabel)x ).forEach(t-> ClearLabel(t));
        row = -1;column = 0;savedColumn= -1;
        c0 = c1 = c2 = c3 = c4 = 0;
        bead.getChildren().stream().map(x->(AndarBaharBeadRoadLabel)x).forEach(t->{
            AddElement(t.getResult());
        });
    }

    private void ReArrangeElements(ListProperty<AndarBaharBeadRoadResult> beadRoadList) {
        getChildren().stream().map(x -> (AndarBaharBigRoadLabel)x ).forEach(t-> ClearLabel(t));
        row = -1;column = 0;savedColumn= -1;
        c0 = c1 = c2 = c3 = c4 = 0;
        beadRoadList.stream().forEach(t->{
            AddElement(t);
        });
    }


    public void AddElement(ListProperty<AndarBaharBeadRoadResult> beadRoadList) {
        Long ballsBefore = getCount();
        ReArrangeElements(beadRoadList);
        Long ballsAfter = getCount();


        if((ballsBefore < ballsAfter) || (shiftedNow)){
//            UpdateBigEyeRoadList(c0,c1,c2);
//            UpdateSmallRoadList(c0,c1,c2,c3);
//            UpdateCockroachRoadList(c0,c1,c2,c3,c4);
        }

        shiftedNow=false;

    }

    public void RemoveElement(ListProperty<AndarBaharBeadRoadResult> beadRoadList) {
        Long ballsBefore = getCount();
        ReArrangeElements(beadRoadList);
        Long ballsAfter = getCount();
        if (ballsAfter < ballsBefore) {
//            if(!bigEyeRoadList.isEmpty()) bigEyeRoadList.remove(bigEyeRoadList.size() - 1);
//            if(!smallRoadList.isEmpty()) smallRoadList.remove(smallRoadList.size() - 1);
//            if(!cockroachRoadList.isEmpty()) cockroachRoadList.remove(cockroachRoadList.size() - 1);
        }
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

    public void Reset(){
        getChildren().stream().map(t->(AndarBaharBigRoadLabel)t).forEach(t->{
            t.setResult(AndarBaharBigRoadResult.EMPTY);
        });

        column = 0;
        row= -1;
        savedColumn = -1;
        shiftedNow = false;
        c0 = 0;c1 =0;c2 = 0;c3=0;c4=0;

    }


    @Override
    protected void layoutChildren() {
        super.layoutChildren();
    }


}
