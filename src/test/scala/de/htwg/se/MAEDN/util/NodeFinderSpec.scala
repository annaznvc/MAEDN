package de.htwg.se.MAEDN.util

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import javafx.scene.{Node, Parent}
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.{VBox, HBox}
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import org.scalatest.BeforeAndAfterAll

class NodeFinderSpec extends AnyWordSpec with Matchers with BeforeAndAfterAll {

  override def beforeAll(): Unit = {
    // Initialize JavaFX toolkit
    new JFXPanel()
    Platform.runLater(() => {})
    Thread.sleep(100) // Give JavaFX time to initialize
  }

  def createTestHierarchy(): VBox = {
    val root = new VBox()
    root.setId("root")
    root.getStyleClass.add("root-container")

    val button1 = new Button("Button 1")
    button1.setId("button1")
    button1.getStyleClass.add("primary-button")

    val button2 = new Button("Button 2")
    button2.setId("button2")
    button2.getStyleClass.add("secondary-button")

    val nestedContainer = new HBox()
    nestedContainer.setId("nested")
    nestedContainer.getStyleClass.add("nested-container")

    val label1 = new Label("Label 1")
    label1.setId("label1")
    label1.getStyleClass.add("info-label")

    val label2 = new Label("Label 2")
    label2.setId("duplicateId") // Intentionally duplicate ID for testing
    label2.getStyleClass.add("info-label")

    val label3 = new Label("Label 3")
    label3.setId("duplicateId") // Intentionally duplicate ID for testing
    label3.getStyleClass.add("warning-label")

    nestedContainer.getChildren.addAll(label1, label2, label3)
    root.getChildren.addAll(button1, button2, nestedContainer)

    root
  }

  "NodeFinder" when {
    "using findNodeById" should {
      "find a node by its ID at root level" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeById(root, "button1")

        result shouldBe defined
        result.get.getId shouldBe "button1"
        result.get shouldBe a[Button]
      }

      "find a node by its ID in nested structure" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeById(root, "label1")

        result shouldBe defined
        result.get.getId shouldBe "label1"
        result.get shouldBe a[Label]
      }

      "find the root node by its own ID" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeById(root, "root")

        result shouldBe defined
        result.get shouldBe root
      }

      "return None for non-existent ID" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeById(root, "nonexistent")

        result shouldBe empty
      }

      "return the first matching node for duplicate IDs" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeById(root, "duplicateId")

        result shouldBe defined
        result.get.getId shouldBe "duplicateId"
        // Should find the first label with duplicate ID
        result.get shouldBe a[Label]
      }
    }

    "using findNodeByStyleClass" should {
      "find a node by its style class at root level" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeByStyleClass(root, "root-container")

        result shouldBe defined
        result.get shouldBe root
      }

      "find a node by its style class in nested structure" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeByStyleClass(root, "info-label")

        result shouldBe defined
        result.get shouldBe a[Label]
        result.get.getStyleClass should contain("info-label")
      }

      "return None for non-existent style class" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeByStyleClass(root, "nonexistent-class")

        result shouldBe empty
      }

      "find the first matching node for shared style classes" in {
        val root = createTestHierarchy()
        val result = NodeFinder.findNodeByStyleClass(root, "info-label")

        result shouldBe defined
        result.get.getStyleClass should contain("info-label")
      }
    }

    "using findAllNodesById" should {
      "find all nodes with a specific ID" in {
        val root = createTestHierarchy()
        val results = NodeFinder.findAllNodesById(root, "duplicateId")

        results should have length 2
        results.foreach(_.getId shouldBe "duplicateId")
        results.foreach(_ shouldBe a[Label])
      }

      "return single node in list for unique ID" in {
        val root = createTestHierarchy()
        val results = NodeFinder.findAllNodesById(root, "button1")

        results should have length 1
        results.head.getId shouldBe "button1"
        results.head shouldBe a[Button]
      }

      "return empty list for non-existent ID" in {
        val root = createTestHierarchy()
        val results = NodeFinder.findAllNodesById(root, "nonexistent")

        results shouldBe empty
      }

      "include root node if it matches ID" in {
        val root = createTestHierarchy()
        val results = NodeFinder.findAllNodesById(root, "root")

        results should have length 1
        results.head shouldBe root
      }
    }

    "using findAllNodesByStyleClass" should {
      "find all nodes with a specific style class" in {
        val root = createTestHierarchy()
        val results = NodeFinder.findAllNodesByStyleClass(root, "info-label")

        results should have length 2
        results.foreach(_.getStyleClass should contain("info-label"))
        results.foreach(_ shouldBe a[Label])
      }

      "return single node in list for unique style class" in {
        val root = createTestHierarchy()
        val results =
          NodeFinder.findAllNodesByStyleClass(root, "primary-button")

        results should have length 1
        results.head.getStyleClass should contain("primary-button")
        results.head shouldBe a[Button]
      }

      "return empty list for non-existent style class" in {
        val root = createTestHierarchy()
        val results =
          NodeFinder.findAllNodesByStyleClass(root, "nonexistent-class")

        results shouldBe empty
      }

      "include root node if it matches style class" in {
        val root = createTestHierarchy()
        val results =
          NodeFinder.findAllNodesByStyleClass(root, "root-container")

        results should have length 1
        results.head shouldBe root
      }
    }

    "handling edge cases" should {

      "handle nodes with multiple style classes" in {
        val root = new VBox()
        val multiClassButton = new Button("Multi Class")
        multiClassButton.getStyleClass.addAll("class1", "class2", "class3")
        root.getChildren.add(multiClassButton)

        NodeFinder.findNodeByStyleClass(root, "class2") shouldBe defined
        NodeFinder.findAllNodesByStyleClass(root, "class1") should have length 1
      }
    }
  }
}
