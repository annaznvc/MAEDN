package de.htwg.se.MAEDN.module

import com.google.inject.Guice
import com.google.inject.Injector
import scala.reflect.ClassTag

/** DependencyInjector provides a centralized way to create objects using
  * dependency injection. This class prevents raw object creation throughout the
  * project by providing a controlled way to instantiate objects with their
  * dependencies properly injected.
  */
object DependencyInjector {
  // The Guice injector configured with our MAEDN module
  private val injector: Injector = {
    try {
      Guice.createInjector(MAEDNModule())
    } catch {
      case e: Exception =>
        println(s"Error creating injector: ${e.getMessage}")
        throw e
    }
  }

  /** Gets an instance of the specified type with all dependencies injected.
    * This is the primary method for object creation in the project.
    *
    * @tparam T
    *   The type of object to create
    * @return
    *   An instance of T with all dependencies injected
    *
    * Example usage: val controller =
    * DependencyInjector.getInstance[IController] val tui =
    * DependencyInjector.getInstance[TUI]
    */
  def getInstance[T](implicit classTag: ClassTag[T]): T = {
    injector.getInstance(classTag.runtimeClass.asInstanceOf[Class[T]])
  }

  /** Gets an instance of the specified class with all dependencies injected.
    * Alternative method when you have the Class object directly.
    *
    * @param clazz
    *   The class to instantiate
    * @tparam T
    *   The type of object to create
    * @return
    *   An instance of T with all dependencies injected
    */
  def getInstance[T](clazz: Class[T]): T = {
    injector.getInstance(clazz)
  }

  /** Gets the underlying Guice injector for advanced use cases. Generally, you
    * should prefer using getInstance methods.
    *
    * @return
    *   The configured Guice injector
    */
  def getInjector: Injector = injector

  /** Creates a new injector with additional modules if needed. This is useful
    * for testing or when you need to override certain bindings.
    *
    * @param additionalModules
    *   Additional Guice modules to include
    * @return
    *   A new injector with the additional modules
    */
  def createInjectorWith(
      additionalModules: com.google.inject.Module*
  ): Injector = {
    val allModules = Seq(MAEDNModule()) ++ additionalModules
    Guice.createInjector(allModules *)
  }
}

/** Injectable trait that can be mixed into classes that need dependency
  * injection. Classes extending this trait can access the dependency injector
  * easily.
  */
trait Injectable {

  /** Gets an instance with dependency injection from the injector
    */
  protected def inject[T](implicit classTag: ClassTag[T]): T = {
    DependencyInjector.getInstance[T]
  }

  /** Gets the injector instance for advanced operations
    */
  protected def injector: Injector = DependencyInjector.getInjector
}
