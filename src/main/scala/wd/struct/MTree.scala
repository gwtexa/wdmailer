package wd.struct


/**
 * Multiway tree
 */
case class MTree[+T](value: T, children: List[MTree[T]]) extends Iterable[T] {
  import MTree._
  /**
   * Constructor for the leaf
   */
  def this(value: T) = this(value, List())
  
  def isLeaf(): Boolean = children.isEmpty
  
  def asString(indent: String = ""): String = {
    indent + value + "\n" + (children map (_.asString(indent + Indent))).mkString
  }
  
  def iterator: Iterator[T] = {
    def nodeList(subtree: MTree[T]): List[T] = {
      List(subtree.value) ++ (subtree.children flatMap (nodeList(_)))
    }
    nodeList(this).iterator
  }
  
    
}

object MTree {
  val Indent = "    "
  def apply[T](value: T) = new MTree(value)
}

object M extends App {
  val m = MTree(11, List(MTree(22), MTree(33), MTree(44, List(MTree(55), MTree(66))), MTree(77)))
  println(m)
  println(m.asString())
}