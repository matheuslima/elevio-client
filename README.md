# Elevio API client

This project expects you have scala and sbt in your path. Start defining two environment variables:
ELEVIO_KEY (application key) and ELEVIO_TOKEN (application token). Then run:
sbt compile
sbt run. You should see something similar to:
```
[info] Running Server 
Server online at http://localhost:8080/
Press RETURN to stop...
```
This client defines 3 endpoints:

- Get all articles:
```
GET /articles?page={page}
[
    {"title": "Title article 1"}
]
```

- Get an article passing an id:
```
GET /article/{id}
{
    "title": "Title article 1"
}
```

- Search articles using a given term:
```
GET /search/{term}
[
    {"title": "Title article 1"}
]
```

To run the tests, execute the command
```
sbt test
```