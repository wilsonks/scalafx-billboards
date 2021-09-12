package customjavafx.scene.control;

import com.sun.org.apache.xpath.internal.operations.And;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;

public class AndarBaharLastWinResultLabel extends Label {

    public AndarBaharLastWinResultLabel() {
        this.getStyleClass().add("LastWinResultLabel");
        this.setResult();
    }

    public AndarBaharLastWinResultLabel(AndarBaharBeadRoadResult result) {
        this.getStyleClass().add("LastWinResultLabel");
        this.setResult(result);
    }

    //Add the Individual States
    private static final PseudoClass PSEUDO_CLASS_NONE = PseudoClass.getPseudoClass("none");
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN = PseudoClass.getPseudoClass("bankerWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN = PseudoClass.getPseudoClass("playerWin");

    private ObjectProperty<AndarBaharBeadRoadResult> result = new ObjectPropertyBase<AndarBaharBeadRoadResult>() {
        @Override
        protected void invalidated() {
            switch (get()) {
                case EMPTY:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_NONE, true);
                    break;

                case BANKER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_NONE, false);
                    break;

                case PLAYER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_NONE, false);
                    break;
                default:
            }
        }

        @Override
        public Object getBean() {
            return AndarBaharLastWinResultLabel.this;
        }

        @Override
        public String getName() {
            return "LastWinResultLabel";
        }
    };

    public AndarBaharBeadRoadResult getResult() {
        return result.get();
    }

    public void setResult() {
        this.result.set(AndarBaharBeadRoadResult.EMPTY);
    }
    public void setResult(AndarBaharBeadRoadResult result) {
        this.result.set(result);
    }

    public ObjectProperty<AndarBaharBeadRoadResult> resultProperty() {
        return result;
    }
}
