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
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN = PseudoClass.getPseudoClass("baharWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN = PseudoClass.getPseudoClass("andarWin");
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN_FIRST = PseudoClass.getPseudoClass("baharFirstWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN_FIRST = PseudoClass.getPseudoClass("andarFirstWin");
    private static final PseudoClass PSEUDO_CLASS_BANKER_WIN_SECOND = PseudoClass.getPseudoClass("baharSecondWin");
    private static final PseudoClass PSEUDO_CLASS_PLAYER_WIN_SECOND = PseudoClass.getPseudoClass("andarSecondWin");


    private ObjectProperty<AndarBaharBigRoadResult> result = new ObjectPropertyBase<AndarBaharBigRoadResult>() {
        @Override
        protected void invalidated() {
            switch (get()) {
                case EMPTY:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;
                case BANKER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;
                case PLAYER_WIN:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;
                case BANKER_WIN_FIRST:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;
                case PLAYER_WIN_FIRST:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;
                case BANKER_WIN_SECOND:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_FIRST, false);
                    pseudoClassStateChanged(PSEUDO_CLASS_BANKER_WIN_SECOND, true);
                    pseudoClassStateChanged(PSEUDO_CLASS_PLAYER_WIN_SECOND, false);
                    break;
                case PLAYER_WIN_SECOND:
                    pseudoClassStateChanged(PSEUDO_CLASS_EMPTY, false);
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
