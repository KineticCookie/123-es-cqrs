# BTC wallet statistics

## Architecture

![Architecture diagram](/docs/img.png)

## APIs
`Producer` service exposes `POST /add` endpoint which accepts jsons. E.g. `{ "datetime": "2019-10-05T17:48:01+01:00", "amount": 10 }`

`Stats` service exposes `GET /get` endpoint which accepts jsons. E.g. `{ "startDatetime": "2019-10-05T00:00:01+00:00", "endDatetime": "2019-10-06T18:48:02+00:00" }`
## How-to`s

Test: `sbt test`  
Build docker images: `sbt docker:publishLocal`  
Run: `docker-compose up -d`

Send data to `localhost:8081/add`  
Get data from `localhost:8080/get`

## Notes
Docker container uses init scripts in order to create neccessary tables.