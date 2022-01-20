
var express = require('express')
var bodyParser = require('body-parser')
var awsServerlessExpressMiddleware = require('aws-serverless-express/middleware')

const {Client} = require('pg')
const clientParams = {
  host: process.env.DBHOST,
  user: process.env.DBUSER,
  port: process.env.DBPORT,
  password: process.env.DBPASSWORD,
  database: process.env.DBNAME
} 

const AWS = require('aws-sdk')
AWS.config.update({region: 'eu-west-3'})
const s3 = new AWS.S3({apiVersion: '2006-03-01'})
const cognito = new AWS.CognitoIdentityServiceProvider({apiVersion: '2016-04-18'})

// declare a new express app
var app = express()
app.use(express.json({limit: '50mb'}))
app.use(express.urlencoded({limit: '50mb'}))
app.use(bodyParser.json())
app.use(awsServerlessExpressMiddleware.eventContext())

// Enable CORS for all methods
app.use(function(req, res, next) {
  res.header("Access-Control-Allow-Origin", "*")
  res.header("Access-Control-Allow-Headers", "*")
  next()
})


/**********************
 * get methods *
 **********************/
 
 
app.get('/items/itineraries', function(req, res) {
  
  const client = new Client(clientParams);
  client.connect();
  
  const limitRows = 8;
  var queryParams = 'SELECT * FROM ITINERARY ORDER BY iterID DESC LIMIT '+limitRows;
  
  var lastIter = req.query.iterid;
  if(lastIter != null){
    queryParams = 'SELECT * FROM ITINERARY WHERE ITERID < '+ lastIter +'ORDER BY iterID DESC LIMIT '+limitRows;
  }
  
  client.query(queryParams, (err, data) => {
    
    client.end();
    
    if(err) 
      return res.json({Error: err.stack});
    else 
      return res.json({Result: data.rows});
    
  });
  
});


app.get('/items/photos', function(req, res) {
  
  const prefix = 'iter'+req.query.iterid+'/';
  const limitSize = 10000000;
  
  var listParams = {
    Bucket: process.env.BUCKETNAME,
    Prefix: prefix,
    MaxKeys: 10,
    StartAfter: req.query.lastkey
  };
  
  
  s3.listObjectsV2(listParams, async function(err, data) {
    
    if (err)
      return res.json({Error: err.stack});
    
    else{
      
      var response = new Object();
      response.count = 0;
      var accumulatedSize = 0;
      
      for(var i = 0; i < data.Contents.length; ++i){
        
        accumulatedSize += data.Contents[i].Size;
        if(accumulatedSize > limitSize)
          break;
        
        var downloadParams = {
          Bucket: process.env.BUCKETNAME, 
          Key: data.Contents[i].Key
        };
        
        try{
          response['photo'+i] = (await (s3.getObject(downloadParams).promise())).Body.toString();
          response.lastkey = data.Contents[i].Key;
          response.count++;
        }catch(error) {}
        
      }
      
      return res.json({Result: response});
    
    } 
  });
  
});


app.get('/items/user', function(req, res) {
  
  var params = {
    UserPoolId: process.env.USERPOOLID,
    Username: req.query.uid
  };
    
  cognito.adminGetUser(params, function(err, data) {
    
    if (err)
      return res.json({Error: err.stack});
    else
      return res.json({Name: data.UserAttributes[2].Value});
      
  });
  
});


/****************************
* post methods *
****************************/


app.post('/items/itineraries', function(req, res) {
  
  const client = new Client(clientParams);
  const startPoint = JSON.parse(req.body.startPoint);
  var waypoints = req.body.waypoints;
  
  if(waypoints != null)
    waypoints = JSON.parse(waypoints);
  
  client.connect();
  
  const query = 'INSERT INTO ITINERARY(iterName, description, difficulty, hours, minutes, startPoint, waypoints, creatorID, shareDate) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9) RETURNING IterID';
  
  client.query(query, [req.body.name, req.body.iterDescription, req.body.difficulty, req.body.hours, req.body.minutes, 
  '('+startPoint.Latitude+','+startPoint.Longitude+')', waypoints, req.body.creator, req.body.date] ,(err, suc) => {
    
    client.end();
    
    if(err) 
      return res.json({Error: err.stack});
    else 
      return res.json(suc.rows[0].iterid);
    
  });
  
});



/****************************
* put methods *
****************************/

app.put('/items/feedback', function(req,res) {
  
  const client = new Client(clientParams);
  
  client.connect();
  
  const query = 'UPDATE ITINERARY SET difficulty = $1, hours = $2, minutes = $3 WHERE iterid = $4';
  
  client.query(query, [req.body.difficulty, req.body.hours, req.body.minutes, req.body.iterid], (err, suc) => {
    
    client.end();
    
    if(err)
      return res.json({Code:500, Error: err.stack});
    else
      return res.json({Code: 200});
      
  });
  
});


app.put('/items/itineraries', function(req, res) {
  //PUT BY ADMIN
});


app.put('/items/photo', function(req, res) {
  
  const prefix = 'iter'+req.body.iterID+'/';
  const filename = prefix+Math.random().toString(36).slice(2);
  
  var uploadParams = {
    Bucket: process.env.BUCKETNAME, 
    Key: filename, 
    Body: req.body.photo,
    ContentEncoding: 'base64'
  };
    
  s3.putObject(uploadParams, (err, dataUp) => {
      
    if (err)
      return res.json({Code: 500, Error: err.stack});
    else
      return res.json({Code: 200});
      
  });
  
});


/****************************
* delete methods *
****************************/


app.delete('/items/itineraries', function(req, res) {
  //DELETE ITINERARY ON RDS & DIRECTORY FROM S3
});



/****************************
****************************/

app.listen(3000, function() {
    console.log("App started")
});

// Export the app object. When executing the application local this does nothing. However,
// to port it to AWS Lambda we will create a wrapper around that will load the app from
// this file
module.exports = app