package domain

import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers


class ModelsSpec
  extends AnyFlatSpec
    with Matchers 
    with EitherValues {

  "Minute" should "accept CronJobs All and produce right values" in {
    Minute(CronExpr.AnyTime).units shouldBe Range(0, 59).inclusive.toList
  }

  it should "fail to create a negative frequency `every`" in {
    assertThrows[IllegalArgumentException](Minute(CronExpr.Every(-1)).units)
  }

  it should "fail to create a frequency `every` of 0" in {
    assertThrows[IllegalArgumentException](Minute(CronExpr.Every(0)).units)
  }

  it should "fail to create a frequency `every` of more than max" in {
    assertThrows[IllegalArgumentException](Minute(CronExpr.Every(60)).units)
  }

  it should "work to create a frequency of X minutes if is between the min and max" in {
    Minute(CronExpr.Every(5)).units shouldBe Range(0, 59, 5).toList
  }

  "Hour" should "accept CronJobs All and produce right values" in {
    Hour(CronExpr.AnyTime).units shouldBe Range(0, 23).inclusive.toList
  }

  it should "fail to create a negative frequency `every`" in {
    assertThrows[IllegalArgumentException](Hour(CronExpr.Every(-1)).units)
  }

  it should "fail to create a frequency `every` of 0" in {
    assertThrows[IllegalArgumentException](Hour(CronExpr.Every(0)).units)
  }

  it should "fail to create a frequency `every` of more than max" in {
    assertThrows[IllegalArgumentException](Hour(CronExpr.Every(25)).units)
  }

  it should "work to create a frequency of X Hours if is between the min and max" in {
    Hour(CronExpr.Every(5)).units shouldBe Range(0, 23, 5).toList
  }
}
