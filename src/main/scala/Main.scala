import utils.Showable.given

@main
def cronTabTool(args: String*): Unit =
  if (args.isEmpty)
    println(
      """
        |Please provide a cronJob string input, as example:
        |   `*/15 0 1,15 * 1-5 /usr/bin/find`
        |""".stripMargin)
  else
    val input = args(0)
    parser.CronTabParser.parse(input)
      .fold(
        error => println(s"There was an error on the input provided \"$input\": $error"),
        result => println(result.show)
      )
