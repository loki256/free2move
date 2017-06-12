## Description
Simple akka-http service for Free2Move team.


## Compile, test and run

    sbt compile test      // compile and run UT
    sbt run               // Will run service on default port (8080)


## Configuring

To change settings either edit `src/main/resources/application.conf` or pass necessary settings as java props.
For example `sbt -Dhttp.port=9999 app.updateTime=5 run`


## Testing
(With default port)

  Check if service is ok: `curl "http://localhost:8080/ping"`

  Ask value by index: `curl "http://localhost:8080/12"`

  Ask value by index: `curl "http://localhost:8080/23131231"`

Service will return 200 http code in case of normal answer and code 400 otherwise.

Simple performance testing: `ab -c 20 -n 1000 "http://localhost:8080/123"`
It is usually required to "warm" akka for better performance with default thread pools
