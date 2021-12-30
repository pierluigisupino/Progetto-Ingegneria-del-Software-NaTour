/*
Copyright 2017 - 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at
http://aws.amazon.com/apache2.0/
or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
*/
var express = require('express')
var bodyParser = require('body-parser')
var awsServerlessExpressMiddleware = require('aws-serverless-express/middleware')

const { Client } = require('pg');
const { decryptSecret } = require("utils");

var host;
var user;
var port;
var password;
var database;
var poolId;

var client;

const AWS = require('aws-sdk')
const s3 = new AWS.S3()
const cognito = new AWS.CognitoIdentityServiceProvider({
    apiVersion: '2016-04-18'
})

var app = express()
app.use(express.json({
    limit: '50mb'
}));
app.use(express.urlencoded({
    limit: '50mb'
}));
app.use(bodyParser.json())
app.use(awsServerlessExpressMiddleware.eventContext())

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*")
    res.header("Access-Control-Allow-Headers", "*")
    next()
});

app.get('/items/user', async function(req, res) {
  
  await checkCredentials()
  
    var params = {
        UserPoolId: poolId,
        Username: req.query.id
    };
    cognito.adminGetUser(params, function(err, data) {
        if (err)
            res.json({
                Error: err
            })
        else
            var x = data.UserAttributes[2];
        res.json({
            Success: x.Value
        })
    });

});

app.post('/items/user', async function(req, res) {
  
  await checkCredentials()
  
  client.connect();
  
  var query = 'INSERT INTO ENDUSER VALUES (\'' + req.body.email + '\',\'' + req.body.name + '\')';
  
  client.query(query, (err, suc) => {
    if (err) {
      client.end();
      res.json(err);
    } else {
      client.end();
      res.json(suc);
    }
  })
});

app.post('/items/itineraries', async function(req, res) {
  
  await checkCredentials()

  var startPoint = JSON.parse(req.body.startPoint);

  client.connect();

  var query = 'INSERT INTO ITINERARY(iterName, description, difficulty, hours, minutes, startPoint, creator, waypoints) VALUES (\'' + req.body.name + '\',\'' + req.body.iterDescription + '\',\'' + req.body.difficulty + '\',\'' + req.body.hours + '\',\'' + req.body.minutes + '\',\'(' + startPoint.Latitude + ',' + startPoint.Longitude + ')\',\'' + req.body.creator + '\',array[\'' + req.body.waypoints + '\']::json[]) RETURNING iterID';

  client.query(query, (err, suc) => {
    if (err) {
      client.end();
      res.json({
        Error: err
      });
    } else {
      var iterID = suc.rows[0].iterid;
      var bucketName = 'natour-images-' + iterID;

      var bucketParams = {
        Bucket: bucketName,
        CreateBucketConfiguration: {
          LocationConstraint: 'eu-west-3'
        },
      };

      s3.createBucket(bucketParams, function(err, data) {
        if (err) {
          client.query('DELETE FROM ITINERARY WHERE IterID = ' + iterID, function(error, succeed) {});
          client.end();
          res.json({
            error: err.stack
          });
        } else {
          client.end();
          res.json(iterID);
        }
      });
    }
  });
});

app.post('/items/photos', function(req, res) {

    var iterID = req.body.iterID;
    var bucketName = 'natour-images-' + iterID;
    var count = req.body.photo_count;

    for (let i = 0; i < count; i++) {
        var photoBody = 'photo' + i;
        var uploadParams = {
            Bucket: bucketName,
            Key: photoBody, //TODO: it will be number of file in the bucket+1
            Body: req.body[photoBody],
            ContentEncoding: 'base64'
        };

        s3.putObject(uploadParams, (err, dataUp) => {
            if (err) {
                res.json({
                    error: err.stack
                });
                i = count;
            }
        });
    }
});

app.listen(3000, function() {
    console.log("App started")
});

async function checkCredentials(){
  if(typeof host == 'undefined' || typeof user == 'undefined' || typeof port == 'undefined' || typeof port == 'undefined' || typeof password == 'undefined' || typeof database == 'undefined' || typeof poolId == 'undefined'){
    await getCredentials()
  }
}

async function getCredentials(){
  
  host = await decryptSecret("host")
  user = await decryptSecret("user")
  port = await decryptSecret("port")
  password = await decryptSecret("password")
  database = await decryptSecret("database")
  poolId = await decryptSecret("poolId")
  
  if(typeof client == 'undefined') await createClient()

}

async function createClient(){
  
  client = new Client({
    host: host,
    user: user,
    port: port,
    password: password,
    database: database
  })
}

module.exports = app