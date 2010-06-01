package org.ulysses.scalacheck

/**
 * User: arjan
 * Date: May 28, 2010
 * Time: 6:28:47 PM
 */

object ListChecks {
    def mySumList(l: List[Int]) = mySumList2(0, l)

    def mySumList2(acc: Int, l: List[Int]): Int = l match {
      case Nil => acc
      case _ => mySumList2(acc + l.head, l tail)
    }
}