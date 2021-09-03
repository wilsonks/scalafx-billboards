package fx2.io

import cats.effect.{Concurrent, ContextShift, IO, SyncIO}
import fs2.concurrent.SignallingRef
import javafx.application.Platform
import javafx.concurrent.{Service, Task}
import javafx.fxml.JavaFXBuilderFactory
import javafx.geometry.Rectangle2D
import javafx.scene.{Cursor, Scene}
import javafx.stage.{Modality, Screen, Stage, StageStyle}
import javafx.util.Callback
import scalafx.application.JFXApp
import scalafxml.core._

import java.util.ResourceBundle
import scala.concurrent.ExecutionContextExecutor

sealed trait Display {

  def exit(): Unit

  def load(window: Display.Window): Scene

  def open(window: Display.Window): Stage

  def root: Stage
}

object Display {

  import fx2.io.syntax._

  def launch(window: Window)(args: Array[String]): IO[Unit] =
    IO.async[Unit](done => new LaunchJFXApp(window, done).main(args))

  def resolve(head: ControllerDependencyResolver, tail: ControllerDependencyResolver*): ControllerDependencyResolver =
    tail.foldLeft(head)(_ ++ _)

  
  trait App {

    implicit def ECE: ExecutionContextExecutor = scala.concurrent.ExecutionContext.global

    def launch(window: Display.Window)(args: List[String]): IO[Unit] = Display.launch(window)(args.toArray)
  }

  class Reader[I](source: fs2.Stream[IO, I], halt: SignallingRef[IO, Boolean])(
    implicit ECE: ExecutionContextExecutor,
    F: Concurrent[IO],
    CS: ContextShift[IO])
    extends Service[sodium.Stream[I]] {

    import fx2.io.syntax._

    setExecutor(ECE)

    def createTask(): Task[sodium.Stream[I]] = () => io.unsafeRunSync()

    private def io: IO[sodium.Stream[I]] =
      for {
        _ <- halt.set(true)
        _ <- halt.set(false)
        reader = new sodium.StreamSink[I]
        _ <- source.interruptWhen(halt).through(reader.sink).compile.drain.start
      } yield reader
  }

  case class Window( fxml: String,
                     screenId: Int = 0,
                     style: StageStyle = StageStyle.UNDECORATED,
                     title: String = "",
                     alwaysOnTop: Boolean = false,
                     resizable: Boolean = false,
                     maximized: Boolean = false,
                     iconified: Boolean = false,
                     fullscreen: Boolean = true,
                     autoShow: Boolean = true,
                     modality: Modality = Modality.WINDOW_MODAL,
                     cursor: Cursor = Cursor.NONE,
                     position: Position = Position(Some(0.0),Some(0.0)),
                     bounds: Bounds = Bounds(Dimension(Some(1920.0),None,None),Dimension(Some(1080.0),None,None)),
                     stylesheets: List[String] = Nil,
                     resources: Option[ResourceBundle] = None,
                     resolver: ControllerDependencyResolver = NoDependencyResolver) {

    def init(stage: Stage, scene: Scene): Unit = {
      stage.setTitle(title)
      stage.setAlwaysOnTop(alwaysOnTop)
      stage.setResizable(resizable)
      stage.setMaximized(maximized)
      stage.setIconified(iconified)

      scene.getStylesheets.addAll(stylesheets: _*)
      stage.setScene(scene)

      val screen: Rectangle2D = Screen.getScreens.get(screenId).getBounds
      val width = bounds.width.exact.getOrElse(1920.0)
      val height = bounds.height.exact.getOrElse(1080.0)
      val x = position.x.getOrElse(0.0)
      val y = position.y.getOrElse(0.0)
      if(fullscreen) {
        stage.setX(screen.getMinX)
        stage.setY(screen.getMinY)
        stage.setFullScreen(true)
      } else {
        stage.setX(screen.getMinX + x)
        stage.setY(screen.getMinY + y)
        stage.setWidth(width)
        stage.setHeight(height)
      }

      if (autoShow) stage.show()
      Screen.getScreens.forEach(screen => {
        println(s"Screen= ${screen.getBounds}")
      })
    }
  }

  case class Position(x: Option[Double] = None, y: Option[Double] = None)

  case class Bounds(width: Dimension = Dimension(), height: Dimension = Dimension())

  case class Dimension(exact: Option[Double] = None, min: Option[Double] = None, max: Option[Double] = None)

  private class Manager(val root: Stage) extends Display {
    self =>

    root.setOnCloseRequest(_ => exit())

    def exit(): Unit = Platform.exit()

    def open(window: Window): Stage = {
      val stage = new JFXApp.PrimaryStage
      val scene = load(window)
      window.init(stage, scene)
      stage
    }

    def load(window: Window): Scene = {
      val proxy: Callback[Class[_], Object] = (param: Class[_]) => FxmlProxyGenerator(param, self.fxResolver ++ window.resolver)

      val loader = new javafx.fxml.FXMLLoader(
        getClass.getClassLoader.getResource(window.fxml),
        window.resources.orNull,
        new JavaFXBuilderFactory(),
        proxy
      ) {

        //noinspection JavaAccessorMethodOverriddenAsEmptyParen
        override def getController[T](): T = super.getController[ControllerAccessor]().as[T]
      }
      loader.load()
      new Scene(loader.getRoot[javafx.scene.Parent])
    }
  }

  // JFXApp needs to be subclassed
  private class LaunchJFXApp(window: Window, done: Either[Throwable, Unit] => Unit) extends JFXApp {

    stage = new JFXApp.PrimaryStage

    val opened: Boolean = SyncIO {
      if (stage.delegate.getStyle != window.style) stage.delegate.initStyle(window.style)
      val display: Display = new Manager(stage.delegate)
      val scene: Scene = display.load(window)
      scene.setCursor(window.cursor)
      window.init(stage.delegate, scene)
      true
    }.handleErrorWith(ex => SyncIO(done(Left(ex))).map(_ => false))
      .unsafeRunSync()

    override def stopApp(): Unit = if (opened) done(Right(()))
  }

}

