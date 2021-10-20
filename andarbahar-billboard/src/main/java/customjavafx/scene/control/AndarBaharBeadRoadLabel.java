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
    private static final PseudoClass PSEUDO_CLASS_EMPTY = PseudoClass.getPseudoClass("empty");
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN = PseudoClass.getPseudoClass("baharWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN = PseudoClass.getPseudoClass("andarWin");
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN_FIRST = PseudoClass.getPseudoClass("baharFirstWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN_FIRST = PseudoClass.getPseudoClass("andarFirstWin");
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN_SECOND = PseudoClass.getPseudoClass("baharSecondWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN_SECOND = PseudoClass.getPseudoClass("andarSecondWin");

    private ObjectProperty<AndarBaharBeadRoadResult> result = new ObjectPropertyBase<AndarBaharBeadRoadResult>() {
        @Override
        protected void invalidated() {
            switch (get()) {
                case EMPTY:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;

                case BANKER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;

                case PLAYER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;

                case BANKER_WIN_FIRST:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;

                case PLAYER_WIN_FIRST:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;

                case BANKER_WIN_SECOND:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;

                case PLAYER_WIN_SECOND:
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, true);
                    break;

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
