
import better.files.File
import better.files.File._
import customjavafx.scene.control.BeadRoadResult
import javafx.beans.property.{ListProperty, SimpleListProperty, SimpleStringProperty, StringProperty}
import javafx.collections.FXCollections
import javafx.scene.input.KeyCode
import pureconfig.ConfigReader
import pureconfig.generic.auto._
import tykhe.billboard.baccarat.{TableHistory, TableSettings}

import scala.collection.JavaConverters._


class BaccaratModel {

  val historyDB: File = File(pureconfig.loadConfigOrThrow[String]("table.backup.history"))
  val settingsDB: File = File(pureconfig.loadConfigOrThrow[String]("table.backup.settings"))

  //Load KeyBoard Information from reference.conf
  val keysMap: Map[KeyCode, String] = pureconfig.loadConfigOrThrow[Map[KeyCode, String]]("keyboard.keys")
  implicit def keyCodeReader: pureconfig.ConfigReader[Map[KeyCode, String]] = {
    pureconfig.configurable.genericMapReader(s => Right(KeyCode.valueOf(s)))
  }

  implicit val beadRoadResultReader: ConfigReader[BeadRoadResult] = ConfigReader.fromNonEmptyString(s => Right(BeadRoadResult.valueOf(s)))
  lazy val coupsMap: Map[String, BeadRoadResult] = pureconfig.loadConfigOrThrow[Map[String, BeadRoadResult]]("keyboard.coups")


  //Define Data Elements & Getter Methods
  private val tableId: StringProperty = new SimpleStringProperty("")
  private val handBetMin: StringProperty = new SimpleStringProperty("")
  private val handBetMax: StringProperty = new SimpleStringProperty("")
  private val tieBetMin: StringProperty = new SimpleStringProperty("")
  private val tieBetMax: StringProperty = new SimpleStringProperty("")
  private val pairBetMin: StringProperty = new SimpleStringProperty("")
  private val pairBetMax: StringProperty = new SimpleStringProperty("")
  private val superSixBetMin: StringProperty = new SimpleStringProperty("")
  private val superSixBetMax: StringProperty = new SimpleStringProperty("")
  private val beadRoadList: ListProperty[BeadRoadResult] = new SimpleListProperty(FXCollections.observableArrayList[BeadRoadResult])
  private val language: StringProperty = new SimpleStringProperty("")
  private val theme: StringProperty = new SimpleStringProperty("")

  def tableIdProperty: StringProperty = tableId

  def handBetMinProperty: StringProperty = handBetMin

  def handBetMaxProperty: StringProperty = handBetMax

  def tieBetMinProperty: StringProperty = tieBetMin

  def tieBetMaxProperty: StringProperty = tieBetMax

  def pairBetMinProperty: StringProperty = pairBetMin

  def pairBetMaxProperty: StringProperty = pairBetMax

  def superSixBetMinProperty: StringProperty = superSixBetMin

  def superSixBetMaxProperty: StringProperty = superSixBetMax

  def languageProperty: StringProperty = language

  def themeProperty: StringProperty = theme

  def beadRoadListProperty: ListProperty[BeadRoadResult] = beadRoadList

  val languages: Array[String] = Array("English", "Hindi", "Punjabi", "Kannada")
//  var languageIndex: Int = languages.indexOf(language.get())
  var languageIndex: Int = 0

  val themes: Array[String] = Array("Theme1", "Theme2", "Theme3", "Theme4", "Theme5")
//  var themeIndex: Int = themes.indexOf(theme.get())
  var themeIndex: Int = 0


  //Load Data From Database
  def loadData(): TableHistory = {
    if (historyDB.exists) {
      historyDB.readDeserialized[TableHistory]()
    } else {
      historyDB.createIfNotExists(asDirectory = false, createParents = true)
      historyDB.writeSerialized(TableHistory(Seq.empty[BeadRoadResult]))
      historyDB.readDeserialized[TableHistory]()
    }
  }

  def loadHeader(): TableSettings = {
    //Load Data From Database
    if (settingsDB.exists) {
      settingsDB.readDeserialized[TableSettings]()
    } else {
      settingsDB.createIfNotExists(asDirectory = false, createParents = true)
      settingsDB.writeSerialized(pureconfig.loadConfigOrThrow[TableSettings]("table.signup.settings"))
      settingsDB.readDeserialized[TableSettings]()
    }
  }

  def saveHeader(): Unit = {
    settingsDB.writeSerialized(
      TableSettings(
        tableId.get(),
        handBetMin.get(),
        handBetMax.get(),
        tieBetMin.get(),
        tieBetMax.get(),
        pairBetMin.get(),
        pairBetMax.get(),
        superSixBetMin.get(),
        superSixBetMax.get(),
        language.get(),
        theme.get(),
      ))
  }

  def mediaCount: Int = getVideoFiles(dir = home).size

  def getVideoFiles(dir: File = home): List[String] = {
    dir.list.filter(f => f.extension.contains(".mp4") || f.extension.contains(".avi"))
      .map(f => f.path.toString)
      .toList
  }

  var mediaIndex = 0
  def getPromoMedia: String = {
    if (mediaCount != 0) {
      getVideoFiles().toArray.apply(mediaIndex)
    }
    else null
  }

  def nextPromoMedia(): Unit = {
    if (mediaCount != 0) {
      mediaIndex += 1
      mediaIndex = mediaIndex % mediaCount
    }
  }



  def selectNext(item: String = "Language"): Unit = {
    if(item == "Language") {
      languageIndex += 1
      languageIndex = languageIndex % languages.length
      language.set(languages(languageIndex))
    } else {
      themeIndex += 1
      themeIndex = themeIndex % themes.length
      theme.set(themes(themeIndex))
    }
  }

  def selectPrev(item: String = "Language"): Unit = {
    if(item == "Language") {
      if(languageIndex <= 0) languageIndex = languages.length

      languageIndex -= 1
      languageIndex = languageIndex % languages.length
      language.set(languages(languageIndex))
    } else {
      if(themeIndex <= 0) themeIndex = themes.length

      themeIndex -= 1
      themeIndex = themeIndex % themes.length
      theme.set(themes(themeIndex))
    }
  }


  def saveData(): Unit = {
    historyDB.writeSerialized(TableHistory(beadRoadList.asScala.toList.filter(x => x != BeadRoadResult.EMPTY)))
  }


}
