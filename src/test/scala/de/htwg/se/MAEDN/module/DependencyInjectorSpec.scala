package de.htwg.se.MAEDN.module

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import com.google.inject.{Injector, AbstractModule, Key}
import com.google.inject.name.Names
import scala.reflect.ClassTag

class DependencyInjectorSpec extends AnyWordSpec with Matchers {

  "DependencyInjector.getInjector" should {
    "return a non-null Injector" in {
      val inj = DependencyInjector.getInjector
      inj should not be null
    }
    "return the same instance on subsequent calls" in {
      val inj1 = DependencyInjector.getInjector
      val inj2 = DependencyInjector.getInjector
      inj1 shouldBe inj2
    }
  }

  "DependencyInjector.getInstance[T]" should {
    "return the Injector when asking for Injector" in {
      val inj = DependencyInjector.getInstance[Injector]
      inj shouldBe DependencyInjector.getInjector
    }
  }

  "DependencyInjector.getInstance(Class)" should {
    "return the Injector when asking for Injector.class" in {
      val inj = DependencyInjector.getInstance(classOf[Injector])
      inj shouldBe DependencyInjector.getInjector
    }
  }

  "DependencyInjector.createInjectorWith" should {
    "create a new injector which is not the default one" in {
      val custom = DependencyInjector.createInjectorWith()
      custom should not be DependencyInjector.getInjector
    }
    "include additional module bindings" in {
      val module = new AbstractModule {
        override def configure(): Unit = {
          bind(classOf[String])
            .annotatedWith(Names.named("foo"))
            .toInstance("bar")
        }
      }
      val custom = DependencyInjector.createInjectorWith(module)
      val value =
        custom.getInstance(Key.get(classOf[String], Names.named("foo")))
      value shouldBe "bar"
    }
  }

  "Injectable trait" should {
    "provide injector identical to DependencyInjector.getInjector" in {
      class PublicInjectable extends Injectable {
        def publicInjector: Injector = injector
      }
      val pi = new PublicInjectable
      pi.publicInjector shouldBe DependencyInjector.getInjector
    }

    "inject[T] delegates to DependencyInjector.getInstance" in {
      class PublicInjectable extends Injectable {
        def publicInject[T](implicit ct: ClassTag[T]): T = inject[T]
      }
      val pi = new PublicInjectable
      val injected = pi.publicInject[Injector]
      injected shouldBe DependencyInjector.getInstance[Injector]
    }
  }

}
