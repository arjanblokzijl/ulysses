package org.ulysses.stm

/**
 * Created by IntelliJ IDEA.
 * User: arjan
 * Date: Nov 28, 2009
 * Time: 3:57:29 PM
 * To change this template use File | Settings | File Templates.
 */

case class TransferMoneyCommand(val amount:Double, val accountIdFrom: Int, val accountIdTo:Int, val bankName:Int)