appender("STDOUT", ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = '%-4relative %-5level {%X} %-30c{30} - %msg%n'
  }
}

root(DEBUG, ['STDOUT'])
