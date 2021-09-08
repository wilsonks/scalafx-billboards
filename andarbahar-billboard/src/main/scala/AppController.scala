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
import java.util.ResourceBundle


@sfxml(additionalControls = List("customjavafx.scene.control", "customjavafx.scene.layout"))
class AppController(
  val root: StackPane,
  val gameHiddenBox: VBox,
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
  val andarTrend: Label,
  val baharTrend: Label,

  val menu: BorderPane,
  val promoPane: Pane,
  val promoMediaView: MediaView,

  val tTableId: TextField,
  val tFirstBetMin: TextField,
  val tFirstBetMax: TextField,
  val tSecondBetMin: TextField,
  val tSecondBetMax: TextField,

  val lTableId: Button,
  val lFirstBetMin: Button,
  val lFirstBetMax: Button,
  val lSecondBetMin: Button,
  val lSecondBetMax: Button,

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
  gameHiddenBox.setVisible(false)
  beadRoad.setVisible(false)

  //Bind the UI Controls -> Model Properties
  tableId.textProperty().bindBidirectional(model.tableIdProperty)
  firstBetMin.textProperty().bindBidirectional(model.firstBetMinProperty)
  firstBetMax.textProperty().bindBidirectional(model.firstBetMaxProperty)
  secondBetMin.textProperty().bindBidirectional(model.secondBetMinProperty)
  secondBetMax.textProperty().bindBidirectional(model.secondBetMaxProperty)

  beadRoad.getBeadRoadListProperty.bind(model.beadRoadListProperty)

  beadRoad.Initialize(6, 11)
  bigRoad.Initialize(10, 29)

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

  val tList = Array(
    tTableId,
    tFirstBetMin,
    tFirstBetMax,
    tSecondBetMin,
    tSecondBetMax)

  val lList = Array(
    lTableId,
    lFirstBetMin,
    lFirstBetMax,
    lSecondBetMin,
    lSecondBetMax)

  val lastWinPause: PauseTransition = new PauseTransition(Duration(3000))
  var media: Media = null
  var mediaPlayer: MediaPlayer = null
  var menuOn = false
  var infoOn = false
  var editOn = false
  var mIndex: Int = 0

  var promoOn = false
  if (java.awt.Toolkit.getDefaultToolkit.getLockingKeyState(java.awt.event.KeyEvent.VK_NUM_LOCK)) {
    menu.toFront()
    lList(mIndex).requestFocus()
    menuOn = true
  }

  def lastWinUpdates(): Unit = {
    new AudioClip(getClass.getResource(beadRoad.LastWinAudio()).toExternalForm).play(1)
    lastWinResultLabel.setResult(beadRoad.LastWinResult())
    lastWinResultLabel.setText(res.get.getString(beadRoad.LastWin()))
    lastWinResultLabel.setAlignment(Pos.Center)
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
          val andarTrendValue = 100 - baharTrendValue

          baharTrend.setText(String.valueOf(baharTrendValue).concat("%"))
          andarTrend.setText(String.valueOf(andarTrendValue).concat("%"))
        } else {
          baharCount.setText("")
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

          val andarTrendValue = (t2.intValue() * 100)/beadRoad.getCountProperty.intValue() ;
          val baharTrendValue = 100 - andarTrendValue

          baharTrend.setText(String.valueOf(baharTrendValue).concat("%"))
          andarTrend.setText(String.valueOf(andarTrendValue).concat("%"))

        } else {
          andarCount.setText("")
          andarTrend.setText("0%")
        }
      }
    })

  
  def focusSame(): Unit = {
    lList(mIndex).requestFocus()
  }

  def focusBack(): Unit = {
    if (mIndex == 0) mIndex = 8
    else {
      mIndex = (mIndex - 1) % 9
    }
    lList(mIndex).requestFocus()
  }



  (display.root
    .handle(KeyEvent.KEY_RELEASED)
    .map(_.getCode)
    .filter(key => model.keysMap.contains(key))
    .transform(Option.empty[String]) {
      case (KeyCode.ENTER, _) if promoOn             => stopPromo(); model.nextPromoMedia(); playPromo(); (None, None)
      case (KeyCode.ENTER, _) if menuOn && editOn    => lList(mIndex).requestFocus(); editOn = !editOn; (None, None)
      case (KeyCode.ENTER, _) if menuOn              => tList(mIndex).requestFocus(); editOn = !editOn; (None, None)
      case (KeyCode.ENTER, result)                   => gameBox.requestFocus(); (result, None)
      case (KeyCode.NUMPAD2, _) if menuOn && !editOn => focusNext(); (None, None)
      case (KeyCode.NUMPAD8, _) if menuOn && !editOn => focusBack(); (None, None)
      case (KeyCode.NUM_LOCK, _) if menuOn =>
        menu.toBack(); gameBox.requestFocus(); model.saveHeader(); menuOn = false; (None, None)
      case (KeyCode.NUM_LOCK, _) => menu.toFront(); focusSame(); menuOn = true; (None, None)
      case (KeyCode.DIVIDE, _) if promoOn =>
        stopPromo(); promoPane.toBack(); gameBox.requestFocus(); promoOn = false; (None, None)
      case (KeyCode.DIVIDE, _) =>
        promoPane.toFront(); playPromo(); promoPane.requestFocus(); promoOn = true; (None, None)
      case (KeyCode.MULTIPLY, _) if infoOn                   => info.toBack(); gameBox.requestFocus(); infoOn = false; (None, None)
      case (KeyCode.MULTIPLY, _)                             => info.toFront(); info.requestFocus(); infoOn = true; (None, None)
      case (key, result) if result.isEmpty                   => (None, Some(model.keysMap(key)))
      case (key, result) if result.get eq model.keysMap(key) => (None, None)
      case (key, result) if result.get.contains(model.keysMap(key)) =>
        (None, Some(result.get.replaceAll(model.keysMap(key), "")))
      case (key, result) => (None, Some((result.get + model.keysMap(key)).toCharArray.sorted.mkString))
    } unNone)
    .map(model.coupsMap.get)
    .filter(result => result.isDefined)
    .map(result => result.get)
    .foreach { result =>
      {
        result match {
          case AndarBaharBeadRoadResult.EXIT  => display.exit()
          case AndarBaharBeadRoadResult.UNDO  => beadRoad.RemoveElement()
          case AndarBaharBeadRoadResult.CLEAR => beadRoad.Reset()
          case AndarBaharBeadRoadResult.THEME => {
            println(s"root Before  ${gameBox.getStyleClass.sorted()}")
            gameBox.getStyleClass.removeAll("theme1", "theme2")
            gameHeaderBox.getStyleClass.removeAll("theme1", "theme2")
            gameBox.getStyleClass.add("theme2")
            gameHeaderBox.getStyleClass.add("theme2")
            println(s"root After ${gameBox.getStyleClass.sorted()}")

          }
          case _ => {
            beadRoad.AddElement(result)
          }
        }
        model.saveData()
      }
    }

  def focusNext(): Unit = {
    mIndex = (mIndex + 1) % 9
    lList(mIndex).requestFocus()
  }

  lastWinPause.setOnFinished { e =>
    dynamicResult.setVisible(false)
    lastGame.setResult(beadRoad.LastWinResult())
    lastGame.setText(res.get.getString(beadRoad.LastWin()))
  }

  //Load the saved results
  data.results.foreach { result =>
    beadRoad.AddElement(result)
  }

//  footing.setText("Powered By Tykhe Gaming Pvt. Ltd.")



  display.root.setOnCloseRequest(_ => {
    display.exit()
  })
}
