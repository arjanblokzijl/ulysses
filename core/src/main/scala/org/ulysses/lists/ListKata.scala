package org.ulysses.lists

/**
 * User: arjan
 */


trait Optionn[+A] {
  self =>
  // Church Encoding (abstract method)

  def cata[X](ding: A => X, dong: => X): X

  // Look ma, no case classes!
  def map[B](f: A => B) = new Optionn[B] {
    def cata[X](ding: B => X, dong: => X) = self.cata(ding compose f, dong)
  }

  def flatMap[B](f: A => Optionn[B]) = new Optionn[B] {
    def cata[X](ding: B => X, dong: => X) =
      self.cata(f(_).cata(ding, dong), dong)
  }

  def mmap[B](o: Optionn[A => B]) = o.flatMap(f => map(f))

  override def toString() = cata("Some(" + _.toString + ")", "None")
}

object Nonen extends Optionn[Nothing] {
  def cata[X](ding: Nothing => X, dong: => X): X = dong

  def ::[B](x: B) = new Optionn[B] {
    def cata[X](ding: B => X, dong: => X): X = ding(x)
  }
}

trait Listt[+A] {
  self =>

  def cata[X](ding: (A, Listt[A]) => X, dong: => X): X

  def map[B](f: A => B): Listt[B] = new Listt[B] {
    def cata[X](ding: (B, Listt[B]) => X, dong: => X) = self.cata((head, tail) => ding(f(head), tail.map(f)), dong)
  }

  def flatMap[B](f: A => Listt[B]): Listt[B] = new Listt[B] {
    def cata[X](ding: (B, Listt[B]) => X, dong: => X): X =
      self.cata[X]((head, tail) => (f(head) mplus tail.flatMap(f)).cata[X](ding, dong), dong)
  }

  def mmap[B](o: Listt[A => B]) = o.flatMap(f => map(f))

  def foldl[X](z: X)(op: (X, A) => X): X = self.cata((head, tail) => tail.foldl(op(z, head))(op), z)

  def foldr[X](z: X)(op: (A, X) => X): X = self.cata((head, tail) => op(head, tail.foldr(z)(op)), z)

  def mplus[B >: A](l: Listt[B]): Listt[B] = new Listt[B] {
    def cata[X](ding: (B, Listt[B]) => X, dong: => X): X = {
      self.cata((head, tail) => ding(head, tail mplus l), l.cata(ding, dong))
    }
  }

  def ::[B >: A](x: B) = new Listt[B] {
    def cata[X](ding: (B, Listt[B]) => X, dong: => X) = ding(x, self)
  }

  def foreach(f: A => Unit): Unit = cata((h, t) => {f(h); t.foreach(f)}, ())

  override def toString() = "Listt[" + cata((h, t) => t.foldl(h.toString)((p, n) => p + "," + n.toString), "") + "]"
}

object Nil extends Listt[Nothing] {
  def cata[X](ding: (Nothing, Listt[Nothing]) => X, dong: => X): X = dong
}
