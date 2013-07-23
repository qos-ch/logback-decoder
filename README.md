logback-decoder
===============
*A log file analysis tool for logback output*

[![Snapshot](https://tony19.ci.cloudbees.com/job/logback-decoder/job/logback-decoder-ANALYZE/badge/icon)](https://tony19.ci.cloudbees.com/job/logback-decoder/job/logback-decoder-ANALYZE/)

Overview
--------
`logback-decoder` is a command-line tool for [log file analysis][1] of text output from the [`logback`][2] framework.

The current version is **0.1.0**. This project is in *alpha* development, and we're currently seeking volunteers! Please email the [development mailing list](http://logback.qos.ch/mailinglist.html) for more info.

Download
--------
 * [`logback-decoder-0.1.0.jar`][3]  (SHA1: `50cc6f0509e45fe8d92c4db256a3fb5b327a8d91`)

Usage
-----
`logback-decoder` is normally invoked by `java -jar logback-decoder-0.1.0.jar`. For clarity, the following examples use a wrapper script named `decode.sh`.

#### show help
```bash
$ ./decode.sh --help
usage: logback-decoder
 -D <property=value>      use value for given property
 -d,--debug               Enable debug mode
 -f,--input-file <path>   Log file to parse (default: stdin)
 -h,--help                Print this help message and exit
 -p,--layout <pattern>    Layout pattern to use (overrides file's pattern)
 -v,--version             Print version information and exit
    --verbose             Be verbose when printing information
```

#### parse log file
```bash
$ ./decode.sh -f foo.log
...
```

#### pipe in data from stdin
```bash
$ echo 2013-07-22 [main]: hello world | ./decode.sh -d -p '%d{yyyy-MM-dd} [%t]: %m%n'
[TRACE] regex: (?<date>\d{4}-\d{2}-\d{2})
[DEBUG] date = 2013-07-22
```

Build
-----
`logback-decoder` is built with Apache Maven 2+. Use these commands to create the executable JAR (with debug symbols).

    git clone git://github.com/qos-ch/logback-decoder.git
    cd logback-decoder
    ./makejar.sh

The jar would be in: `./target/logback-decoder-<version>.jar`

 [1]: http://en.wikipedia.org/wiki/Log_analysis
 [2]: http://logback.qos.ch
 [3]: https://bitbucket.org/tony19/logback-decoder/downloads/logback-decoder-0.1.0-SNAPSHOT.jar
