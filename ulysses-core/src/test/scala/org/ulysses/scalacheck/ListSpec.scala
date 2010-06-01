package org.ulysses.scalacheck

import org.scalacheck.Prop._
import org.specs._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
/**
 * User: arjan
 * Date: May 28, 2010
 * Time: 5:26:25 PM
 */

class ListSpec extends Specification with Sugar with ScalaCheck {
  import ListChecks._

  "myListSum" should {
    "equal scala list.sum" in {
      val lEq = (l1: List[Int]) => l1.sum == mySumList(l1)
      forAll(lEq) must pass
    }
  }

//  "a person " should {
//    "equal any other with the same name and age" in {
//      val pEq = {(p1: Person, p2: Person) => {(p1.first == p2.first && p1.last == p2.last && p1.age == p2.age) == (p1 == p2)}}
//
//      forAll {
//        (p1: Person, p2: Person) =>
////          val eq = (p1.first == p2.first && p1.last == p2.last && p1.age == p2.age)
//          val eq = (p1.ssn == p2.ssn)
//          println("checking: " + p1.age + " " + p2.age + " are eq: " + eq + " p1 == p2 " + (p1 == p2))
//
//          if (eq && p1 != p2) false
//          else if (!eq && p1 == p2) false
//          else true
//      } must pass
//    }
//  }

  "a point " should {
    "equal any other point with the same x and y coordinate" in {
      val pEq = (p1: Point, p2: Point) => {
        println("Checking p1: " + p1 + " p2: " + p2 + " p1 == p2: " + (p1 == p2))
        if (p1.x == p2.x && p1.y == p2.y) p1 == p2 else p1 != p2
      }
      forAll(pEq) must pass
    }
  }

  val pointGen = for{
     x <- arbitrary[Int]
     y <- arbitrary[Int]
  } yield new Point(x, y)

  implicit val arbPoint: Arbitrary[Point] = Arbitrary(pointGen)

  val personGen = for{
    f <- arbitrary[String]
    l <- arbitrary[String]
    a <- arbitrary[Int]
  } yield new Person(f, l, a)

  implicit val arbPerson: Arbitrary[Person] = Arbitrary(personGen)
}

sealed trait Color
case object Red extends Color
case object Green extends Color
case object Blue extends Color

class Point(val x: Int, val y: Int) {
//  override def equals(other: Any) = other match {
//    case that: Point => this.x == that.x && this.y == that.y
//    case _ => false
//  }

  override def toString = "Point with coords x: " + this.x + "y: " + this.y
}

class Person(val first: String, val last: String, val ssn: Int) {
//  override def equals(other: Any) = other match {
//    case that: Person => (this.first != that.first && this.last == that.last && this.age == that.age)
//    case _ => false
//  }

  override def hashCode() = (this.ssn).hashCode

  override def toString() = this.first + this.last + this.ssn
}
