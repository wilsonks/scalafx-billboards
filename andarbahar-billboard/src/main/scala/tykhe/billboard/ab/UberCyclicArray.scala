package tykhe.billboard.ab

class UberCyclicArray(size: Int) {
  private val start = 0
  private val stop = size

  private var index = 0
  def Previous(): Int = if(index == start) {index = stop; index} else { index = index - 1; index}
  def Current(): Int = index
  def Next(): Int = if(index == stop) {index = 0; index} else {index = index + 1; index}
}