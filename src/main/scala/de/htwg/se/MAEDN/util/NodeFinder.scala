package de.htwg.se.MAEDN.util

import javafx.scene.{Node, Parent}
import scala.jdk.CollectionConverters._

object NodeFinder {

  /** Recursively search for a node by its fx:id */
  def findNodeById(root: Parent, id: String): Option[Node] = {
    if (id.equals(root.getId)) return Some(root)

    // Search through all children (both Parent and leaf nodes)
    root.getChildrenUnmodifiable.asScala.foreach { child =>
      if (id.equals(child.getId)) return Some(child)

      // If child is a Parent, search recursively
      child match {
        case parent: Parent =>
          findNodeById(parent, id) match {
            case Some(found) => return Some(found)
            case None        =>
          }
        case _ => // Leaf node, already checked above
      }
    }

    None
  }

  /** Recursively search for a node by its style class */
  def findNodeByStyleClass(root: Parent, styleClass: String): Option[Node] = {
    if (root.getStyleClass.contains(styleClass)) return Some(root)

    // Search through all children (both Parent and leaf nodes)
    root.getChildrenUnmodifiable.asScala.foreach { child =>
      if (child.getStyleClass.contains(styleClass)) return Some(child)

      // If child is a Parent, search recursively
      child match {
        case parent: Parent =>
          findNodeByStyleClass(parent, styleClass) match {
            case Some(found) => return Some(found)
            case None        =>
          }
        case _ => // Leaf node, already checked above
      }
    }

    None
  }

  /** Find all nodes with a specific fx:id (useful for debugging) */
  def findAllNodesById(root: Parent, id: String): List[Node] = {
    val results = scala.collection.mutable.ListBuffer[Node]()

    def searchRecursively(node: Node): Unit = {
      if (id.equals(node.getId)) results += node

      node match {
        case parent: Parent =>
          parent.getChildrenUnmodifiable.asScala.foreach(searchRecursively)
        case _ =>
      }
    }

    searchRecursively(root)
    results.toList
  }

  /** Find all nodes with a specific style class */
  def findAllNodesByStyleClass(root: Parent, styleClass: String): List[Node] = {
    val results = scala.collection.mutable.ListBuffer[Node]()

    def searchRecursively(node: Node): Unit = {
      if (node.getStyleClass.contains(styleClass)) results += node

      node match {
        case parent: Parent =>
          parent.getChildrenUnmodifiable.asScala.foreach(searchRecursively)
        case _ =>
      }
    }

    searchRecursively(root)
    results.toList
  }
}
