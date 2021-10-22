import customjavafx.scene.control._
import customjavafx.scene.layout._
import fx2.io.FxReader
import javafx.beans.value.{ChangeListener, ObservableValue}
import javafx.collections.ObservableList
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout.{BorderPane, HBox, Pane, StackPane, VBox}
import javafx.scene.media.{Media, MediaPlayer, MediaView}
import scalafx.animation.PauseTransition
import scalafx.geometry.Pos
import scalafx.scene.media.AudioClip
import scalafx.util.Duration
import scalafxml.core.macros.sfxml
import sodium.syntax._
import tykhe.billboard.ab.{TableHistory, TableSettings}

import java.io.{File => JFile}
import java.util.{Locale, ResourceBundle}


@sfxml(additionalControls = List("customjavafx.scene.control", "customjavafx.scene.layout"))
class AppController(
  val root: StackPane,

  val gameBox: Pane,
  val gameHeaderBox: HBox,
  val lastWinResultLabel: AndarBaharLastWinResultLabel,
  val lastGame: AndarBaharLastWinResultLabel,

  val tableId: Label,
  val firstBetMin: Label,
  val firstBetMax: Label,
  val secondBetMin: Label,
  val secondBetMax: Label,

  val andarCount: Label,
  val baharCount: Label,
  val andarFirstCount: Label,
  val baharFirstCount: Label,
  val andarSecondCount: Label,
  val baharSecondCount: Label,

  val andarTrend: Label,
  val baharTrend: Label,
  val andarFirstTrend: Label,
  val baharFirstTrend: Label,
  val andarSecondTrend: Label,
  val baharSecondTrend: Label,

  val limits: Label,
  val trends: Label,
  val last: Label,
  val stats: Label,
  val symbols: Label,
  val min: Label,
  val max: Label,
  val firstBet: Label,
  val secondBet: Label,
                   
  val symbolsA: Label,
  val symbolsB: Label,
  val symbolsA1: Label,
  val symbolsB1: Label,
  val symbolsA2: Label,
  val symbolsB2: Label,

  val footingText: Label,

  val menuPane: BorderPane,
  val promoPane: Pane,
  val promoMediaView: MediaView,

  val tTableId: TextField,
  val tFirstBetMin: TextField,
  val tFirstBetMax: TextField,
  val tSecondBetMin: TextField,
  val tSecondBetMax: TextField,
  val language: Button,
  val theme: Button,

  val lTableId: Button,
  val lFirstBetMin: Button,
  val lFirstBetMax: Button,
  val lSecondBetMin: Button,
  val lSecondBetMax: Button,
  val lLanguage: Button,
  val lTheme: Button,

  val info: BorderPane,
  val beadRoad: BeadRoadTilePane,
  val bigRoad: BigRoadTilePane,
  val dynamicResult: BorderPane
)(implicit display: fx2.io.Display, res: Option[ResourceBundle], reader: FxReader[AndarBaharBeadRoadResult]) {


  //Instantiate model
  val model = new AppModel()
  val data: TableHistory = model.loadData()
  val header: TableSettings = model.loadHeader()

  dynamicResult.setVisible(false)
  beadRoad.setVisible(false)

  //Bind the UI Controls -> Model Properties
  tableId.textProperty().bindBidirectional(model.tableIdProperty)
  firstBetMin.textProperty().bindBidirectional(model.firstBetMinProperty)
  firstBetMax.textProperty().bindBidirectional(model.firstBetMaxProperty)
  secondBetMin.textProperty().bindBidirectional(model.secondBetMinProperty)
  secondBetMax.textProperty().bindBidirectional(model.secondBetMaxProperty)


  beadRoad.getBeadRoadListProperty.bind(model.beadRoadListProperty)

  beadRoad.Initialize(6, 11)
  bigRoad.Initialize(10, 34)

  tableId.textProperty().bindBidirectional(tTableId.textProperty())
  firstBetMin.textProperty().bindBidirectional(tFirstBetMin.textProperty())
  firstBetMax.textProperty().bindBidirectional(tFirstBetMax.textProperty())
  secondBetMin.textProperty().bindBidirectional(tSecondBetMin.textProperty())
  secondBetMax.textProperty().bindBidirectional(tSecondBetMax.textProperty())

  tableId.setText(header.tableId)
  firstBetMin.setText(header.firstBetMin)
  firstBetMax.setText(header.firstBetMax)
  secondBetMin.setText(header.secondBetMin)
  secondBetMax.setText(header.secondBetMax)

  language.setText(header.language)
  theme.setText(header.theme)

  model.languageProperty.bindBidirectional(language.textProperty())
  model.themeProperty.bindBidirectional(theme.textProperty())

  model.themeProperty.get() match {
    case "Theme1" => {
      root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
      root.getStyleClass().add("Theme1")
    }
    case "Theme2" => {
      root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
      root.getStyleClass().add("Theme2")
    }
    case "Theme3" => {

      root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
      root.getStyleClass().add("Theme3")
    }
    case "Theme4" => {
      root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
      root.getStyleClass().add("Theme4")
    }
    case "Theme5" => {

      root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
      root.getStyleClass().add("Theme5")
    }

    case _ => {

    }
  }

  val tList = Array(
    tTableId,
    tFirstBetMin,
    tFirstBetMax,
    tSecondBetMin,
    tSecondBetMax,
    language,
    theme)

  val lList = Array(
    lTableId,
    lFirstBetMin,
    lFirstBetMax,
    lSecondBetMin,
    lSecondBetMax,
    lLanguage,
    lTheme)

  val lastWinPause: PauseTransition = new PauseTransition(Duration(3000))
  var media: Media = null
  var mediaPlayer: MediaPlayer = null
  var menuOn = false
  var infoOn = false
  var editOn = false
  var mIndex: Int = 0

  var promoOn = false
  if (java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(java.awt.event.KeyEvent.VK_NUM_LOCK)) {
    menuPane.toFront()
    lList(mIndex).requestFocus()
    menuOn = true
  }

  def lastWinUpdates(): Unit = {
    var lastWin = beadRoad.LastWinAudio()

    language.textProperty().get() match {
      case "Hindi" =>
        lastWin = s"/sounds/Hindi/${lastWin}"
        beadRoad.LastWin() match {
          case "bWin" => lastWinResultLabel.setText("बहार\n जीत गया")
          case "pWin" => lastWinResultLabel.setText("आंदर\n जीत गया")
        }

      case "Kannada" =>
        lastWin = s"/sounds/Kannada/${lastWin}"
        beadRoad.LastWin() match {
          case "bWin" => lastWinResultLabel.setText("ಬಹಾರ್\n ಗೆಲ್ಲುತ್ತದೆ")
          case "pWin" => lastWinResultLabel.setText("ಅಂದರ್\n ಗೆಲ್ಲುತ್ತಾನೆ")
        }

      case "Telugu" =>
        lastWin = s"/sounds/Telugu/${lastWin}"
        beadRoad.LastWin() match {
          case "bWin" => lastWinResultLabel.setText("బహార్\n గెలిచింది")
          case "pWin" => lastWinResultLabel.setText("అందర్\n గెలిచాడు")
        }

      case "Tamil" =>
        lastWin = s"/sounds/Tamil/${lastWin}"
        beadRoad.LastWin() match {
          case "bWin" => lastWinResultLabel.setText("பஹார்\n வென்றது")
          case "pWin" => lastWinResultLabel.setText("அந்தர்\n வென்றார்")
        }

      case "Punjabi" =>
        lastWin = s"/sounds/Punjabi/${lastWin}"
        beadRoad.LastWin() match {
          case "bWin" => lastWinResultLabel.setText("ਬਹਾਰ\n ਜਿੱਤ ਗਿਆ")
          case "pWin" => lastWinResultLabel.setText("ਅੰਡਰ\n ਜਿੱਤ ਗਿਆ")
        }

      case _ =>
        lastWin = s"/sounds/English/${lastWin}"
        beadRoad.LastWin() match {
          case "bWin" => lastWinResultLabel.setText("Bahar\n Wins")
          case "pWin" => lastWinResultLabel.setText("Andar\n Wins")
        }

    }

    new AudioClip(getClass.getResource(lastWin).toExternalForm).play(1)

    lastWinResultLabel.setResult(beadRoad.LastWinResult())

    dynamicResult.setVisible(true)
    lastWinPause.stop()
    lastWinPause.play()
  }

  def playPromo(): Unit = {
    if (model.getPromoMedia != null) {
      val f = new JFile(model.getPromoMedia)
      media = new Media(f.toURI.toString)
      mediaPlayer = new MediaPlayer(media)
      mediaPlayer.setCycleCount(-1)
      promoMediaView.setMediaPlayer(mediaPlayer)
      mediaPlayer.play()
    } else {
      println("Error: Promo Videos Not Found in!")
    }
  }

  def stopPromo(): Unit = {
    if (model.getPromoMedia != null) mediaPlayer.dispose()

  }

  // callback registration
  sodium.Stream.shift(reader.valueProperty().updates).foreach { result =>
    if(AndarBaharBeadRoadResult.EMPTY != result) {
      beadRoad.AddElement(result)
      model.saveData()
    }
  }
  // start service after callback registration to ensure no updates are missed
  reader.start()

  beadRoad.getCountProperty
    .addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t1: Number, t2: Number): Unit = {
        if (t2.intValue() > 0) {
          if (t2.longValue() > t1.longValue()) {
            bigRoad.AddElement(beadRoad.getBeadRoadListProperty)

          } else {
            bigRoad.RemoveElement(beadRoad.getBeadRoadListProperty)
          }

        } else {
          bigRoad.Reset()
          lastGame.setResult(AndarBaharBeadRoadResult.EMPTY)
          lastGame.setText("")
        }
      }
    })

  beadRoad.getBankerWinCount
    .addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t1: Number, t2: Number): Unit = {
        if (t2.intValue() > 0) {
          lastWinUpdates()

          baharCount.setText(String.valueOf(t2.intValue()))

          val baharTrendValue = (t2.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarTrendValue = (beadRoad.getPlayerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharFirstTrendValue = (beadRoad.getBankerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharSecondTrendValue = (beadRoad.getBankerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarFirstTrendValue = (beadRoad.getPlayerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarSecondTrendValue = (beadRoad.getPlayerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;

          baharTrend.setText(String.valueOf(baharTrendValue).concat("%"))
          andarTrend.setText(String.valueOf(andarTrendValue).concat("%"))
          baharFirstTrend.setText(String.valueOf(baharFirstTrendValue).concat("%"))
          andarFirstTrend.setText(String.valueOf(andarFirstTrendValue).concat("%"))
          baharSecondTrend.setText(String.valueOf(baharSecondTrendValue).concat("%"))
          andarSecondTrend.setText(String.valueOf(andarSecondTrendValue).concat("%"))

        } else {
          baharCount.setText("0")
          baharTrend.setText("0%")
        }
      }
    })

  beadRoad.getPlayerWinCount
    .addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t1: Number, t2: Number): Unit = {
        if (t2.intValue() > 0) {
          lastWinUpdates()

          andarCount.setText(String.valueOf(t2.intValue()))

          val baharTrendValue = (beadRoad.getBankerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarTrendValue = (t2.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharFirstTrendValue = (beadRoad.getBankerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharSecondTrendValue = (beadRoad.getBankerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarFirstTrendValue = (beadRoad.getPlayerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarSecondTrendValue = (beadRoad.getPlayerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;

          baharTrend.setText(String.valueOf(baharTrendValue).concat("%"))
          andarTrend.setText(String.valueOf(andarTrendValue).concat("%"))
          baharFirstTrend.setText(String.valueOf(baharFirstTrendValue).concat("%"))
          andarFirstTrend.setText(String.valueOf(andarFirstTrendValue).concat("%"))
          baharSecondTrend.setText(String.valueOf(baharSecondTrendValue).concat("%"))
          andarSecondTrend.setText(String.valueOf(andarSecondTrendValue).concat("%"))

        } else {
          andarCount.setText("0")
          andarTrend.setText("0%")
        }
      }
    })


  beadRoad.getBankerWinFirstCount
    .addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t1: Number, t2: Number): Unit = {
        if (t2.intValue() > 0) {
          lastWinUpdates()

          baharFirstCount.setText(String.valueOf(t2.intValue()))

          val baharTrendValue = (beadRoad.getBankerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarTrendValue = (beadRoad.getPlayerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharFirstTrendValue = (t2.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharSecondTrendValue = (beadRoad.getBankerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarFirstTrendValue = (beadRoad.getPlayerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarSecondTrendValue = (beadRoad.getPlayerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;

          baharTrend.setText(String.valueOf(baharTrendValue).concat("%"))
          andarTrend.setText(String.valueOf(andarTrendValue).concat("%"))
          baharFirstTrend.setText(String.valueOf(baharFirstTrendValue).concat("%"))
          andarFirstTrend.setText(String.valueOf(andarFirstTrendValue).concat("%"))
          baharSecondTrend.setText(String.valueOf(baharSecondTrendValue).concat("%"))
          andarSecondTrend.setText(String.valueOf(andarSecondTrendValue).concat("%"))

        } else {
          baharFirstCount.setText("0")
          baharFirstTrend.setText("0%")
        }
      }
    })

  beadRoad.getPlayerWinFirstCount
    .addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t1: Number, t2: Number): Unit = {
        if (t2.intValue() > 0) {
          lastWinUpdates()

          andarFirstCount.setText(String.valueOf(t2.intValue()))


          val baharTrendValue = (beadRoad.getBankerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarTrendValue = (beadRoad.getPlayerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharFirstTrendValue = (beadRoad.getBankerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharSecondTrendValue = (t2.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarFirstTrendValue = (beadRoad.getPlayerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarSecondTrendValue = (beadRoad.getPlayerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;

          baharTrend.setText(String.valueOf(baharTrendValue).concat("%"))
          andarTrend.setText(String.valueOf(andarTrendValue).concat("%"))
          baharFirstTrend.setText(String.valueOf(baharFirstTrendValue).concat("%"))
          andarFirstTrend.setText(String.valueOf(andarFirstTrendValue).concat("%"))
          baharSecondTrend.setText(String.valueOf(baharSecondTrendValue).concat("%"))
          andarSecondTrend.setText(String.valueOf(andarSecondTrendValue).concat("%"))

        } else {
          andarFirstCount.setText("0")
          andarFirstTrend.setText("0%")
        }
      }
    })


  beadRoad.getBankerWinSecondCount
    .addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t1: Number, t2: Number): Unit = {
        if (t2.intValue() > 0) {
          lastWinUpdates()

          baharSecondCount.setText(String.valueOf(t2.intValue()))


          val baharTrendValue = (beadRoad.getBankerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarTrendValue = (beadRoad.getPlayerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharFirstTrendValue = (beadRoad.getBankerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharSecondTrendValue = (beadRoad.getBankerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarFirstTrendValue = (t2.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarSecondTrendValue = (beadRoad.getPlayerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;

          baharTrend.setText(String.valueOf(baharTrendValue).concat("%"))
          andarTrend.setText(String.valueOf(andarTrendValue).concat("%"))
          baharFirstTrend.setText(String.valueOf(baharFirstTrendValue).concat("%"))
          andarFirstTrend.setText(String.valueOf(andarFirstTrendValue).concat("%"))
          baharSecondTrend.setText(String.valueOf(baharSecondTrendValue).concat("%"))
          andarSecondTrend.setText(String.valueOf(andarSecondTrendValue).concat("%"))

        } else {
          baharSecondCount.setText("0")
          baharSecondTrend.setText("0%")
        }
      }
    })

  beadRoad.getPlayerWinSecondCount
    .addListener(new ChangeListener[Number] {
      override def changed(observableValue: ObservableValue[_ <: Number], t1: Number, t2: Number): Unit = {
        if (t2.intValue() > 0) {
          lastWinUpdates()

          andarSecondCount.setText(String.valueOf(t2.intValue()))


          val baharTrendValue = (beadRoad.getBankerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarTrendValue = (beadRoad.getPlayerWinCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharFirstTrendValue = (beadRoad.getBankerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharSecondTrendValue = (beadRoad.getBankerWinSecondCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarFirstTrendValue = (beadRoad.getPlayerWinFirstCount.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val andarSecondTrendValue = (t2.intValue() * 100)/beadRoad.getCountProperty.intValue() ;

          baharTrend.setText(String.valueOf(baharTrendValue).concat("%"))
          andarTrend.setText(String.valueOf(andarTrendValue).concat("%"))
          baharFirstTrend.setText(String.valueOf(baharFirstTrendValue).concat("%"))
          andarFirstTrend.setText(String.valueOf(andarFirstTrendValue).concat("%"))
          baharSecondTrend.setText(String.valueOf(baharSecondTrendValue).concat("%"))
          andarSecondTrend.setText(String.valueOf(andarSecondTrendValue).concat("%"))

        } else {
          andarSecondCount.setText("0")
          andarSecondTrend.setText("0%")
        }
      }
    })



  theme.textProperty().addListener(new ChangeListener[String] {
    override def changed(observableValue: ObservableValue[_ <: String], t1: String, t2: String): Unit = {
      t2 match {
        case "Theme1" => {
          root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
          root.getStyleClass().add("Theme1")
        }
        case "Theme2" => {
          root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
          root.getStyleClass().add("Theme2")
        }
        case "Theme3" => {

          root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
          root.getStyleClass().add("Theme3")
        }
        case "Theme4" => {
          root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
          root.getStyleClass().add("Theme4")
        }
        case "Theme5" => {

          root.getStyleClass().removeAll("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
          root.getStyleClass().add("Theme5")
        }

        case _ => {

        }
      }
      model.saveHeader()
    }
  })

  language.textProperty().addListener(new ChangeListener[String] {
    override def changed(observableValue: ObservableValue[_ <: String], t1: String, t2: String): Unit = {
      lastGame.setResult(beadRoad.LastWinResult())
      lastWinResultLabel.setResult(beadRoad.LastWinResult())

      t2 match {
        case "English" => {
          limits.setText("LIMITS")
          trends.setText("TRENDS")
          last.setText("LAST WIN")
          stats.setText("STATS")
          symbols.setText("SYMBOLS")

          min.setText("Min")
          max.setText("Max")
          firstBet.setText("1st Bet")
          secondBet.setText("2nd Bet")

          symbolsA.setText("Andar Win")
          symbolsB.setText("Bahar Win")
          symbolsA1.setText("1st Card Win")
          symbolsB1.setText("1st Card Win")
          symbolsA2.setText("2nd Card Win")
          symbolsB2.setText("2nd Card Win")

          lastGame.setText(res.get.getString(beadRoad.LastWin()))


        }



        case "Hindi" => {
          limits.setText("सीमाएं")
          trends.setText("प्रवृत्तियों")
          last.setText("पिछली जीत")
          stats.setText("अंक-विवरन")
          symbols.setText("प्रतीक")

          min.setText("न्यूनतम")
          max.setText("अधिकतम")
          firstBet.setText("पहली शर्त")
          secondBet.setText("दूसरा दांव")


          symbolsA.setText("अंदर जीत")
          symbolsB.setText("बहार जीत")
          symbolsA1.setText("पहला कार्ड जीत")
          symbolsB1.setText("पहला कार्ड जीत")
          symbolsA2.setText("दूसरा कार्ड जीत")
          symbolsB2.setText("दूसरा कार्ड जीत")

          beadRoad.LastWin() match {
            case "bWin" => lastGame.setText("बहार\n जीत गया")
            case "pWin" => lastGame.setText("आंदर\n जीत गया")
          }

        }
        case "Punjabi" => {
          limits.setText("ਲਿਮਿਟਸ")
          trends.setText("ਰੁਝਾਨ")
          last.setText("ਪਿਛਲੀ ਜਿੱਤ")
          stats.setText("ਅੰਕੜੇ")
          symbols.setText("ਸਿੰਬਲਸ")

          min.setText("ਘੱਟੋ ਘੱਟ")
          max.setText("ਅਧਿਕਤਮ")
          firstBet.setText("ਪਹਿਲੀ ਬਾਜ਼ੀ")
          secondBet.setText("2 ਵੀਂ ਬਾਜ਼ੀ")


          symbolsA.setText("ਅੰਡਰ ਜਿੱਤ")
          symbolsB.setText("ਬਹਾਰ ਜੀਤ")
          symbolsA1.setText("ਪਹਿਲਾ ਕਾਰਡ ਜਿੱਤਿਆ")
          symbolsB1.setText("ਪਹਿਲਾ ਕਾਰਡ ਜਿੱਤਿਆ")
          symbolsA2.setText("ਦੂਜਾ ਕਾਰਡ ਜਿੱਤ")
          symbolsB2.setText("ਦੂਜਾ ਕਾਰਡ ਜਿੱਤ")

          beadRoad.LastWin() match {
            case "bWin" => lastGame.setText("ਬਹਾਰ\n ਜਿੱਤ ਗਿਆ")
            case "pWin" => lastGame.setText("ਅੰਡਰ\n ਜਿੱਤ ਗਿਆ")
          }

        }
        case "Kannada" => {
          limits.setText("ಮಿತಿಗಳು")
          trends.setText("ಟ್ರೆಂಡ್ಸ್")
          last.setText("ಕೊನೆಯ ಗೆಲುವು")
          stats.setText("ಅಂಕಿಅಂಶಗಳು")
          symbols.setText("ಸಿಂಬಲ್ಸ್")

          min.setText("ಕನಿಷ್ಠ")
          max.setText("ಗರಿಷ್ಠ")
          firstBet.setText("1 ನೇ ಪಂತ")
          secondBet.setText("2 ನೇ ಪಂತ")


          symbolsA.setText("ಅಂದರ್ ಗೆಲ್ಲುತ್ತಾನೆ")
          symbolsB.setText("ಬಹಾರ್ ಗೆಲ್ಲುತ್ತದೆ")
          symbolsA1.setText("1 ನೇ ಕಾರ್ಡ್ ಗೆಲುವು")
          symbolsB1.setText("1 ನೇ ಕಾರ್ಡ್ ಗೆಲುವು")
          symbolsA2.setText("2 ನೇ ಕಾರ್ಡ್ ಗೆಲುವು")
          symbolsB2.setText("2 ನೇ ಕಾರ್ಡ್ ಗೆಲುವು")

          beadRoad.LastWin() match {
            case "bWin" => lastGame.setText("ಬಹಾರ್\n ಗೆಲ್ಲುತ್ತದೆ")
            case "pWin" => lastGame.setText("ಅಂದರ್\n ಗೆಲ್ಲುತ್ತಾನೆ")
          }

        }

        case "Telugu" => {
          limits.setText("పరిమితులు")
          trends.setText("పోకడలు")
          last.setText("చివరి విజయం")
          stats.setText("గణాంకాలు")
          symbols.setText("చిహ్నాలు")

          min.setText("కనీస")
          max.setText("గరిష్టత")
          firstBet.setText("1 వ పందెం")
          secondBet.setText("2 వ పందెం")

          symbolsA.setText("అందర్ విజయం")
          symbolsB.setText("బహార్ విజయం")
          symbolsA1.setText("మొదటి కార్డు విజయం")
          symbolsB1.setText("మొదటి కార్డు విజయం")
          symbolsA2.setText("రెండవ కార్డు విజయం")
          symbolsB2.setText("రెండవ కార్డు విజయం")

          beadRoad.LastWin() match {
            case "bWin" => lastGame.setText("బహార్\n గెలిచింది")
            case "pWin" => lastGame.setText("అందర్\n గెలిచాడు")
          }

        }

        case "Tamil" => {
          limits.setText("வரம்புகள்")
          trends.setText("போக்குகள்")
          last.setText("கடைசி வெற்றி")
          stats.setText("புள்ளி விவரம்")
          symbols.setText("சின்னங்கள்")

          min.setText("குறைந்தபட்ச")
          max.setText("அதிகபட்சம்")
          firstBet.setText("முதல் பந்தயம்")
          secondBet.setText("இரண்டாவது பந்தயம்")

          symbolsA.setText("அந்தர் வெற்றி")
          symbolsB.setText("பஹார் வெற்றி")
          symbolsA1.setText("முதல் அட்டை")
          symbolsB1.setText("முதல் அட்டை")
          symbolsA2.setText("2 வது அட்டை")
          symbolsB2.setText("2 வது அட்டை")

          beadRoad.LastWin() match {
            case "bWin" => lastGame.setText("அந்தர்\n வெற்றி")
            case "pWin" => lastGame.setText("பஹார்\n வெற்றி")
          }

        }

        case _ => {

        }
      }
      model.saveHeader()
    }
  })
  
  def focusSame(): Unit = {
    lList(mIndex).requestFocus()
  }

  def focusBack(): Unit = {
    if (mIndex <= 0) mIndex = lList.length
    else {
      mIndex -= 1
      mIndex = mIndex  % lList.length
    }
    lList(mIndex).requestFocus()
  }

  def focusNext(): Unit = {
    mIndex += 1
    mIndex = mIndex  % lList.length
    lList(mIndex).requestFocus()
  }



  (display.root
    .handle(KeyEvent.KEY_RELEASED)
    .map(_.getCode)
    .filter(key => model.keysMap.contains(key))
    .transform(Option.empty[String]) {
      case (KeyCode.ENTER, _) if promoOn             => stopPromo(); model.nextPromoMedia(); playPromo(); (None, None)

      case (KeyCode.ENTER, _) if menuOn && editOn    => lList(mIndex).requestFocus(); editOn = false; (None, None)
      case (KeyCode.ENTER, _) if menuOn              => tList(mIndex).requestFocus(); editOn = true; (None, None)
      case (KeyCode.ENTER, result)                   => gameBox.requestFocus(); (result, None)
      case (KeyCode.NUMPAD2, _) if menuOn && !editOn => focusNext(); (None, None)
      case (KeyCode.NUMPAD8, _) if menuOn && !editOn => focusBack(); (None, None)
      case (KeyCode.NUMPAD4, _) if menuOn && editOn =>  model.selectPrev(lList(mIndex).textProperty().get()); (None, None)
      case (KeyCode.NUMPAD6, _) if menuOn && editOn => model.selectNext(lList(mIndex).textProperty().get()); (None, None)
      case (KeyCode.NUM_LOCK, _) if menuOn => menuPane.toBack(); gameBox.requestFocus(); model.saveHeader(); menuOn = false; (None, None)
      case (KeyCode.NUM_LOCK, _) => menuPane.toFront(); focusSame(); menuOn = true; (None, None)

      case (KeyCode.DIVIDE, _) if promoOn => stopPromo(); promoPane.toBack(); gameBox.requestFocus(); promoOn = false; (None, None)
      case (KeyCode.DIVIDE, _) => promoPane.toFront(); playPromo(); promoPane.requestFocus(); promoOn = true; (None, None)
      case (KeyCode.MULTIPLY, _) if infoOn                   => info.toBack(); gameBox.requestFocus(); infoOn = false; (None, None)
      case (KeyCode.MULTIPLY, _)                             => info.toFront(); info.requestFocus(); infoOn = true; (None, None)
      case (key, result) if result.isEmpty                   => (None, Some(model.keysMap(key)))
      case (key, result) if result.get eq model.keysMap(key) => (None, None)
      case (key, result) if result.get.contains(model.keysMap(key)) => (None, Some(result.get.replaceAll(model.keysMap(key), "")))
      case (key, result) => (None, Some((result.get + model.keysMap(key)).toCharArray.sorted.mkString))
    } unNone)
    .map(model.coupsMap.get)
    .filter(result => result.isDefined)
    .map(result => result.get)
    .foreach { result =>
      {
        result match {
          case AndarBaharBeadRoadResult.EXIT  =>
            display.exit()
          case AndarBaharBeadRoadResult.UNDO  =>
            beadRoad.RemoveElement()
          case AndarBaharBeadRoadResult.CLEAR =>
            beadRoad.Reset()
          case AndarBaharBeadRoadResult.LANGUAGE_NEXT =>
            model.selectNext("Language");
          case AndarBaharBeadRoadResult.LANGUAGE_PREV =>
            model.selectPrev("Language");
          case AndarBaharBeadRoadResult.LANGUAGE_AUTO =>
            println(timerA.getStatus);

          case _ => {
            beadRoad.AddElement(result)
          }
        }
        model.saveData()
      }
    }


  lastWinPause.setOnFinished { e =>
    dynamicResult.setVisible(false)
    lastGame.setResult(beadRoad.LastWinResult())
    lastWinResultLabel.setResult(beadRoad.LastWinResult())

    language.textProperty().get() match {
      case "English" => {
        lastGame.setText(res.get.getString(beadRoad.LastWin()))
      }

      case "Hindi" => {
        beadRoad.LastWin() match {
          case "bWin" => lastGame.setText("बहार\n जीत गया")
          case "pWin" => lastGame.setText("आंदर\n जीत गया")
        }

      }
      case "Punjabi" => {
        beadRoad.LastWin() match {
          case "bWin" => lastGame.setText("ਬਹਾਰ\n ਜਿੱਤ ਗਿਆ")
          case "pWin" => lastGame.setText("ਅੰਡਰ\n ਜਿੱਤ ਗਿਆ")
        }

      }
      case "Kannada" => {

        beadRoad.LastWin() match {
          case "bWin" => lastGame.setText("ಬಹಾರ್\n ಗೆಲ್ಲುತ್ತದೆ")
          case "pWin" => lastGame.setText("ಅಂದರ್\n ಗೆಲ್ಲುತ್ತಾನೆ")
        }

      }

      case "Telugu" => {

        beadRoad.LastWin() match {
          case "bWin" => lastGame.setText("బహార్\n గెలిచింది")
          case "pWin" => lastGame.setText("అందర్\n గెలిచాడు")
        }

      }

      case "Tamil" => {
        beadRoad.LastWin() match {
          case "bWin" => lastGame.setText("அந்தர்\n வெற்றி")
          case "pWin" => lastGame.setText("பஹார்\n வெற்றி")
        }

      }

      case _ => {
        lastGame.setText(res.get.getString(beadRoad.LastWin()))
      }
    }
  }

  //Load the saved results
  data.results.foreach { result =>
    beadRoad.AddElement(result)
  }

  footingText.setText("Powered By Tykhe Gaming Pvt Ltd.")

  val timerA = new PauseTransition(Duration(3000))

  // When the timer goes off, show the alert.
  timerA.onFinished = {_ =>
    println("Select Next Language")
    model.selectNext("Language");
    timerA.playFromStart()
  }

  // Start the timer for the first time.
  timerA.play


  display.root.setOnCloseRequest(_ => {
    display.exit()
  })
}
