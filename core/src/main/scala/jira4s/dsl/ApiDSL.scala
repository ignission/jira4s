package jira4s.dsl

import cats.free.Free

object ApiDSL {
  type ApiADT[A] = HttpADT[A]
  type ApiProgram[A] = Free[ApiADT, A]
}
