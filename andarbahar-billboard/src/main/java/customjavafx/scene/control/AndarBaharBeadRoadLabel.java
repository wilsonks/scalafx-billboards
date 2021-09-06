package customjavafx.scene.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;

public class AndarBaharBeadRoadLabel extends Label {

    public AndarBaharBeadRoadLabel() {
        this.getStyleClass().add("BeadRoadLabel");
    }

    public AndarBaharBeadRoadLabel(AndarBaharBeadRoadResult result) {
        this.getStyleClass().add("BeadRoadLabel");
        this.setResult(result);
    }

    //Add the Individual States
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN = PseudoClass.getPseudoClass("bankerWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN = PseudoClass.getPseudoClass("playerWin");

    private ObjectProperty<AndarBaharBeadRoadResult> result = new ObjectPropertyBase<AndarBaharBeadRoadResult>() {
        @Override
        protected void invalidated() {
            switch (get()) {
                case EMPTY:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    break;

                case BANKER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    break;

                case PLAYER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, true);
                default:
            }
        }

        @Override
        public Object getBean() {
            return AndarBaharBeadRoadLabel.this;
        }

        @Override
        public String getName() {
            return "BeadRoadLabel";
        }
    };

    public AndarBaharBeadRoadResult getResult() {
        return result.get();
    }

    public void setResult(AndarBaharBeadRoadResult result) {
        this.result.set(result);
    }

    public ObjectProperty<AndarBaharBeadRoadResult> resultProperty() {
        return result;
    }
}
