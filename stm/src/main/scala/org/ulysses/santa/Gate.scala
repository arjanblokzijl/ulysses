package org.ulysses.santa

import se.scalablesolutions.akka.util.Logging
import se.scalablesolutions.akka.stm.TransactionalRef;
import se.scalablesolutions.akka.stm.TransactionalState;
import se.scalablesolutions.akka.stm.Transaction.Local._

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Jan 16, 2010
 * Time: 10:43:13 AM
 * To change this template use File | Settings | File Templates.
 */

case class Gate(capacity: Int, remaining: TransactionalRef[Int]) extends Logging {
  def passGate(implicit tfn: String): Unit = {
    atomic {
      val n_left = remaining.get.getOrElse(0)
      log.debug("passGate found n_left " + n_left)
      if (n_left <= 0) throw new RuntimeException("No capacity left in Gate")
      remaining.swap(n_left - 1)
    }
  }

  def getRemaining(implicit tfn: String): Int = {
    var res = 0
    atomic {
      res = remaining.get.getOrElse(0)
    }
    res
  }

  def operateGate(implicit tfn: String): Unit = {
    resetGate
    waitForFull
    log.debug("Finished operating gate")
  }

  private def waitForFull(implicit tfn: String): Unit = {
    if (!isFull) {
      try {Thread.sleep(10)} catch {case e: InterruptedException => {}}
      waitForFull
    }
  }

  private def resetGate(implicit tfn: String): Unit = {
    log.debug("swap capacity to value " + capacity)
    atomic {  
      remaining.swap(capacity)
      
    }
  }

  private def isFull(implicit tfn: String): Boolean = {
    atomic {
      val n_left: Int = remaining.get.getOrElse(0)
      log.debug("Found n_left: " + n_left)
      if (n_left > 0) {
        log.debug("Re-trying transaction")
        false
      } else {
        true
      }
    }
  }
}

object Gate {
  def newGate(capacity: Int)(implicit tfn: String): Gate = {
    val ref = TransactionalState.newRef[Int]
    atomic {
      ref.swap(capacity)
      Gate(capacity, ref)
    }
  }
}