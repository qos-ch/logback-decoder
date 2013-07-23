logback-decoder
===============
*A log file analysis tool for logback output*

[![Snapshot](https://tony19.ci.cloudbees.com/job/logback-decoder/job/logback-decoder-ANALYZE/badge/icon)](https://tony19.ci.cloudbees.com/job/logback-decoder/job/logback-decoder-ANALYZE/)

Overview
--------
`logback-decoder` is a command-line tool for [log file analysis][1] of text output from the [`logback`][2] framework.

The current version is **0.1.0α**. This project is in *alpha* devellpment, and we're currently seeking volunteers! Please email the [development mailing list](http://logback.qos.ch/mailinglist.html) for more info.

Usage
-----

```bash
$ java -jar logback-decoder-0.1.0.jar --help
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

