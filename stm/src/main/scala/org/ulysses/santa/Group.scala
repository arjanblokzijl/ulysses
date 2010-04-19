package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging

import se.scalablesolutions.akka.stm.TransactionalRef;
import se.scalablesolutions.akka.stm.TransactionalState;
import se.scalablesolutions.akka.stm.Transaction.Local._
import se.scalablesolutions.akka.util.Logging;

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 17, 2010
 * Time: 8:34:36 AM
 * To change this template use File | Settings | File Templates.
 */

case class Group(capacity:Int, ref:TransactionalRef[(Int, Gate, Gate)]) extends Logging {

}

object Group {
  def newGroup(capacity: Int)(implicit tfn: String): Group = {
    val ref = TransactionalState.newRef[(Int, Gate, Gate)]
    atomic {
      ref.swap(capacity, Gate.newGate(capacity), Gate.newGate(capacity))
      
      Group(capacity, ref)
    }
  }
}

