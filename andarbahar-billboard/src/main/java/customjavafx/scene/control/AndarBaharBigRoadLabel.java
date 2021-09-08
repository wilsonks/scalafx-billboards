package customjavafx.scene.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.css.PseudoClass;
import javafx.scene.control.Label;

public class AndarBaharBigRoadLabel extends Label {

    public AndarBaharBigRoadLabel() {
        super();
        this.getStyleClass().add("BigRoadLabel");
        this.setResult(AndarBaharBigRoadResult.EMPTY);
    }

    public AndarBaharBigRoadLabel(AndarBaharBigRoadResult result) {
        super();
        this.getStyleClass().add("BigRoadLabel");
        this.setResult(result);
    }

    private static final PseudoClass PSEUDO_CLASS_EMPTY = PseudoClass.getPseudoClass("empty");
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN = PseudoClass.getPseudoClass("bankerWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN = PseudoClass.getPseudoClass("playerWin");


    private ObjectProperty<AndarBaharBigRoadResult> result = new ObjectPropertyBase<AndarBaharBigRoadResult>() {
        @Override
        protected void invalidated() {
            switch (get()) {
                case EMPTY:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    break;
                case BANKER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    break;
                case PLAYER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, true);

                default:
            }
        }

        @Override
        public Object getBean() {
            return AndarBaharBigRoadLabel.this;
        }

        @Override
        public String getName() {
            return "BigRoadLabel";
        }

    };

    public AndarBaharBigRoadResult getResult() {
        return result.get();
    }

    public ObjectProperty<AndarBaharBigRoadResult> resultProperty() {
        return result;
    }

    public void setResult(AndarBaharBigRoadResult result) {
        this.result.set(result);
    }
}
