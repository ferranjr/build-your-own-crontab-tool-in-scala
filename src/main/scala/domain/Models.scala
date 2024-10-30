package domain

enum CronExpr:
  case AnyTime
  // Represents a frequency X
  case Every(value: Int)
  // Represents a range
  case Range(from: Int, to: Int)
  // Represents a range of labels
  case RangeLabels(from: String, to: String)
  // Represents a list of times
  case AtTimes(values: Seq[Int])
  // Represents a list of labels
  case AtTimesLabels(values: Seq[String])

sealed trait CronTime:
  def expr: CronExpr
  def min: Int
  def max: Int
  def labels: Map[String, Int] = Map()

  def units: Seq[Int] = expr match
    case CronExpr.AnyTime =>
      min.to(max)
    case CronExpr.Every(value) =>
      require(value < max && value > 1)
      min.to(max, value)
    case CronExpr.Range(from, to) =>
      require(from < to && from >= min & to <= max)
      from.to(to)
    case CronExpr.RangeLabels(fromLabel, toLabel) =>
      (
        for
          from <- labels.get(fromLabel)
          to <- labels.get(toLabel)
        yield from.to(to)
      ).getOrElse(throw new IllegalArgumentException(s"Invalid labels $fromLabel $toLabel"))
    case CronExpr.AtTimes(values) =>
      require(values.forall(v => v <= max && v >= min))
      values
    case CronExpr.AtTimesLabels(values) =>
      values.map(l => labels.getOrElse(l, throw new IllegalArgumentException(s"Invalid label $l")))


case class Minute(expr: CronExpr) extends CronTime {
  val min: Int = 0
  val max: Int = 59

  override val units: Seq[Int] = super.units
}

case class Hour(expr: CronExpr) extends CronTime {
  val min: Int = 0
  val max: Int = 23
  override val units: Seq[Int] = super.units
}

case class DayOfTheMonth(expr: CronExpr) extends CronTime {
  val min: Int = 1
  val max: Int = 31
  override val units: Seq[Int] = super.units
}

case class Month(expr: CronExpr) extends CronTime {
  val min: Int = 1
  val max: Int = 12

  override val labels: Map[String, Int] = Map(
    "JAN" -> 1,
    "FEB" -> 2,
    "MAR" -> 3,
    "APR" -> 4,
    "MAY" -> 5,
    "JUN" -> 6,
    "JUL" -> 7,
    "AUG" -> 8,
    "SEP" -> 9,
    "OCT" -> 10,
    "NOV" -> 11,
    "DEC" -> 12,
  )

  override val units: Seq[Int] = super.units
}

case class DayOfTheWeek(expr: CronExpr) extends CronTime {
  val min: Int = 0
  val max: Int = 6

  override def labels: Map[String, Int] = Map(
    "SUN" -> 0,
    "MON" -> 1,
    "TUE" -> 2,
    "WED" -> 3,
    "THU" -> 4,
    "FRI" -> 5,
    "SAT" -> 6,
  )

  override val units: Seq[Int] = super.units
}

