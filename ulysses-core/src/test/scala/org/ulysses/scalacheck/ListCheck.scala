package org.ulysses.scalacheck

import org.scalacheck.Prop._


/**
 * User: arjan
 * Date: May 28, 2010
 * Time: 5:26:25 PM
 */

class ListCheck {
  import ListChecks._

  def sumListShouldWork = {
    val lEq = (l1: List[Int]) => l1.sum == mySumList(l1)
    forAll(lEq) check
  }
}