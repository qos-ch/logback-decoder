Thank you for downloading logback-decoder, the logback log-file decoder.

The documentation can be found in the Wiki here:
 https://github.com/qos-ch/logback-decoder/wiki

Goal
====

The goal of this project is to convert one or more lines found in a log file (containing text) 
into a series of objects of type ILoggingEvent. Thus, FileAppender converts intances of ILoggingEvent
into one or more lines in a log file, a decoder performs the inverse operation.

It is assummed that the first line of the log file contains the pattern that was used to 
format the output. See http://logback.qos.ch/manual/encoders.html#outputPatternAsHeader for further details.

Building logback-decoder
========================

This project requires Maven 2.2.1 or later to build (3.x works as well).
Run the following command to build:

    $ mvn install

In case of problems
===================

In case of problems, please do not hesitate to post an e-mail message
on the logback-user@qos.ch mailing list.  However, please do not
directly e-mail logback developers. The answer to your question might
be useful to other users. Moreover, there are many knowledgeable users
on the logback-user mailing lists who can quickly answer your
questions. 

