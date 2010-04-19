package org.ulysses.stm

import se.scalablesolutions.akka.actor.Actor

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Nov 28, 2009
 * Time: 4:00:17 PM
 * To change this template use File | Settings | File Templates.
 */

class Bank  {
//  makeTransactionRequired
  
  def transferTo(fromId:Long, toId:Long) {
    println("Transferring from " + fromId + " to " + toId)
  }
}