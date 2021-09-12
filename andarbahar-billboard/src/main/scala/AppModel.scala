import better.files.File
import better.files.File._
import customjavafx.scene.control.AndarBaharBeadRoadResult
import javafx.beans.property.{ListProperty, SimpleListProperty, SimpleStringProperty, StringProperty}
import javafx.collections.FXCollections
import javafx.scene.input.KeyCode
import pureconfig.ConfigReader
import pureconfig.generic.auto._
import tykhe.billboard.ab.{TableHistory, TableSettings}
import scala.collection.JavaConverters._

class AppModel {

  val dataDB: File = File(pureconfig.loadConfigOrThrow[String]("table.backup.history"))
  val headerDB: File = File(pureconfig.loadConfigOrThrow[String]("table.backup.settings"))

  //Load KeyBoard Information from reference.conf
  val keysMap: Map[KeyCode, String] = pureconfig.loadConfigOrThrow[Map[KeyCode, String]]("keyboard.keys")
  implicit def keyCodeReader: pureconfig.ConfigReader[Map[KeyCode, String]] = {
    pureconfig.configurable.genericMapReader(s => Right(KeyCode.valueOf(s)))
  }

  implicit val AndarBaharBeadRoadResultReader: ConfigReader[AndarBaharBeadRoadResult] = ConfigReader.fromNonEmptyString(s => Right(AndarBaharBeadRoadResult.valueOf(s)))
  lazy val coupsMap: Map[String, AndarBaharBeadRoadResult] = pureconfig.loadConfigOrThrow[Map[String, AndarBaharBeadRoadResult]]("keyboard.coups")

  //Define Data Elements & Getter Methods
  private val tableId: StringProperty = new SimpleStringProperty("")
  private val firstBetMin: StringProperty = new SimpleStringProperty("")
  private val firstBetMax: StringProperty = new SimpleStringProperty("")
  private val secondBetMin: StringProperty = new SimpleStringProperty("")
  private val secondBetMax: StringProperty = new SimpleStringProperty("")
  private val language: StringProperty = new SimpleStringProperty("")
  private val theme: StringProperty = new SimpleStringProperty("")

  private val beadRoadList: ListProperty[AndarBaharBeadRoadResult] = new SimpleListProperty(FXCollections.observableArrayList[AndarBaharBeadRoadResult])


  def tableIdProperty: StringProperty = tableId

  def firstBetMinProperty: StringProperty = firstBetMin

  def firstBetMaxProperty: StringProperty = firstBetMax

  def secondBetMinProperty: StringProperty = secondBetMin

  def secondBetMaxProperty: StringProperty = secondBetMax

  def languageProperty: StringProperty = language

  def themeProperty: StringProperty = theme

  def beadRoadListProperty: ListProperty[AndarBaharBeadRoadResult] = beadRoadList


  val languages: Array[String] = Array("English", "Hindi", "Punjabi", "Kannada")
  var languageIndex: Int = languages.indexOf(language.get())

  val themes: Array[String] = Array("Orange", "Red", "Green")
  var themeIndex: Int = themes.indexOf(theme.get())

  var header: TableSettings = null
  //Load Data From Database
  def loadData(): TableHistory = {
    if (dataDB.exists) {
      dataDB.readDeserialized[TableHistory]()
    } else {
      dataDB.createIfNotExists(asDirectory = false, createParents = true)
      dataDB.writeSerialized(TableHistory(Seq.empty[AndarBaharBeadRoadResult]))
      dataDB.readDeserialized[TableHistory]()
    }
  }

  def loadHeader(): TableSettings = {
    //Load Data From Database
    header =
    if (headerDB.exists) {
      headerDB.readDeserialized[TableSettings]()
    } else {
      headerDB.createIfNotExists(asDirectory = false, createParents = true)
      headerDB.writeSerialized(pureconfig.loadConfigOrThrow[TableSettings]("table.signup.settings"))
      headerDB.readDeserialized[TableSettings]()
    }

    header
  }

  def saveHeader(): Unit = {
    headerDB.writeSerialized(
      TableSettings(
        tableId.get(),
        firstBetMin.get(),
        firstBetMax.get(),
        secondBetMin.get(),
        secondBetMax.get(),
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
    dataDB.writeSerialized(TableHistory(beadRoadList.asScala.toList.filter(x => x != AndarBaharBeadRoadResult.EMPTY)))
  }


}

