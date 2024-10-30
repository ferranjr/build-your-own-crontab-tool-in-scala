package parser

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ParserSpec
  extends AnyFlatSpec
   with Matchers
   with EitherValues {

  "sat" should "capture the char if condition is true" in {
    Parser.sat(_.isDigit)("122").value shouldBe ('1', "22")
  }

  "letter" should "capture the char if is a lower case letter" in {
    Parser.letter("aloha").value shouldBe ('a',"loha")
  }

  it should "capture the char if is a upper case letter" in {
    Parser.letter("Aloha").value shouldBe ('A',"loha")
  }

  "word" should "parser a group of `letter`s as a string" in {
    Parser.word("Hello world").value shouldBe ("Hello", " world")
  }

  "many" should "capture 0 or more items as a seq" in {
    Parser.many(Parser.letter)("Foo123").value shouldBe (Seq('F','o','o'), "123")
    Parser.many(Parser.letter)("123").value shouldBe (Seq(), "123")
  }

  "many1" should "capture 1 or more items as a seq" in {
    Parser.many1(Parser.letter)("Foo123").value shouldBe (Seq('F','o','o'), "123")
    Parser.many1(Parser.letter)("123").left.value shouldBe ParserError.ZeroFailure
  }

  "nat" should "capture a positive non floating number as an int" in {
    Parser.nat("1982").value shouldBe (1982, "")
  }

  "int" should "capture a positive and negative non floating numbers as an int" in {
    Parser.int("1982").value shouldBe (1982, "")
    Parser.int("-25").value shouldBe (-25, "")
  }

  "ints" should "parse a comma separated list of ints" in {
    Parser.ints("1,2,3,4,5").value shouldBe (Seq(1, 2, 3, 4, 5), "")
  }

  "sepby1" should "capture list of elements separated by `sep` parser" in {
    Parser.sepby1(Parser.int, Parser.char(','))("1,2,3,4,5").value shouldBe (Seq(1,2,3,4,5), "")
    Parser.sepby1(Parser.int, Parser.char(','))("1").value shouldBe (Seq(1), "")
  }

  "bracket" should "capture elements wrapped in a bracket" in {
    Parser.bracket(
      Parser.char('('),
      Parser.ints,
      Parser.char(')')
    )("(1,2,3)").value shouldBe (Seq(1,2,3), "")
  }
}
