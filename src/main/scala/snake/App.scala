package snake
import com.thoughtworks.binding.{Binding, dom}
import com.thoughtworks.binding.Binding.{BindingSeq, Var, Vars, Constants}
import scala.scalajs.js.timers._
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.scalajs.dom.{Event, KeyboardEvent, window, document}
import org.scalajs.dom.ext.{KeyCode, LocalStorage}
import org.scalajs.dom.raw.{HTMLInputElement, Node}
import scala.util.Random

case class Dot(x: Int, y: Int)

object App {
  
  val _debug = (str: String) => println("[Scala.js Snake] " + str)

  // consts
  val stepInterval: Double = 200 // ms
  val stageWidth = 50
  val stageHeight = 50
  val cellSize = 7
  val initSnakeSize = 5

  // game state
  var direction = 1 // 0 left, 1 up, 2 right, 3 down
  val gameState = Var(0) // 0 init, 1 playing, 2 game over
  val snake = Vars.empty[Dot] 
  val foods = Vars.empty[Dot]
  var intervalHandle: Option[SetIntervalHandle] = None

  def init() {
    _debug("Init Game...")
    direction = 1
    snake.value.clear()
    foods.value.clear()
    intervalHandle match {
      case Some(value) => clearInterval(value)
      case None => Unit
    }
    for (i <- 1 to initSnakeSize) {
      snake.value += Dot(stageWidth / 2, stageHeight / 2 + i)
    }
    for (i <- 1 to Random.nextInt(3) + 1) {
      generateFood
    }
  }
  
  def start() {
    _debug("Start Game...")
    init()
    gameState.value = 1
    intervalHandle = Some(setInterval(stepInterval) { moveForward })
  }

  def moveForward() {
    _debug("moveForward")
    val head = snake.value.head
    val next = direction match {
      case 0 => Dot(head.x - 1, head.y)
      case 1 => Dot(head.x, head.y - 1)
      case 2 => Dot(head.x + 1, head.y)
      case 3 => Dot(head.x, head.y + 1)
    }
    if (snake.value.contains(next) || head.x > stageWidth || head.x < 0 || head.y > stageHeight || head.y < 0) {
      clearInterval(intervalHandle.get)
      gameState.value = 2
      _debug("game over" + next.x + next.y)
    }
    if (foods.value.contains(next)) {
      foods.value -= next
      generateFood
    } else {
      snake.value.remove(snake.value.size -1)
    }
    snake.value.prepend(next)
  }

  def generateFood() {
    val food = Dot(Random.nextInt(stageWidth), Random.nextInt(stageHeight))
    if (snake.value.contains(food)) 
      generateFood
    else {
      foods.value += food
    }
  }

  def onKeypress(e: KeyboardEvent) {
    if (e.keyCode < 37 || e.keyCode > 40) {
      return
    }
    val newDirection = e.keyCode - 37
    if ((newDirection - direction).abs == 2) {
      return
    }
    direction = newDirection
    _debug("Set direction " + direction)
  }

  @dom 
  def table: Binding[Node] = {
    <div class="stage" style={s"width:${cellSize * stageWidth}px;height:${cellSize * stageHeight}px;"}>

      <div class="button-wrapper">
        {
          if (gameState.bind == 2) {
            <h1 class="game-over">Game Over</h1>
          } else {
            <!-- empty content -->
          }
        }
        {
          if (gameState.bind != 1) {
            <button id="start-btn" onclick={ e: Event => start }>
              Start
            </button>
          } else {
            <!-- empty content -->
          }
        }
      </div>
      {
        for (s <- snake) yield <i class="dot" style={s"width:${cellSize}px;height:${cellSize}px;top:${s.y * cellSize}px;left:${s.x * cellSize}px"}/>
      }
      {
        for (f <- foods) yield <i class="dot food" style={s"width:${cellSize}px;height:${cellSize}px;top:${f.y * cellSize}px;left:${f.x * cellSize}px"}/>
      }
    </div>
  }

  @JSExport
  def main(args: Array[String]): Unit = {
    _debug("Hello playa, welcome to my snake game powered by scala.js!")
    dom.render(document.getElementById("app"), table)
    document.addEventListener("keydown", onKeypress)
  }
}
