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
  
  client.query('SELECT * FROM ITINERARY', (err, data) => {
    
    client.end();
    
    if(err) 
      return res.json({Error: err.stack});
    else 
      return res.json({Result: data.rows});
    
  });
  
});


app.get('/items/users', function(req, res) {
  
  var params = {
    UserPoolId: process.env.USERPOOLID,
    Username: req.query.id
  };
    
  cognito.adminGetUser(params, function(err, data) {
    if (err)
      return res.json({Error: err.stack});
    else
      return res.json(data.UserAttributes[2].Value);
  });
  
});


/****************************
* post methods *
****************************/


app.post('/items/itineraries', function(req, res) {
  
  const client = new Client(clientParams);
  var startPoint = JSON.parse(req.body.startPoint);
  var waypoints = req.body.waypoints;
  
  if(waypoints != null){
    waypoints = JSON.parse(waypoints);
  }
  
  client.connect();
  
  var query = 'INSERT INTO ITINERARY(iterName, description, difficulty, hours, minutes, startPoint, waypoints, creatorID, shareDate) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9) RETURNING IterID';
  
  client.query(query, [req.body.name, req.body.iterDescription, req.body.difficulty, req.body.hours, req.body.minutes, 
  '('+startPoint.Latitude+','+startPoint.Longitude+')', waypoints, req.body.creator, req.body.date] ,(err, suc) => {
    
    client.end();
    
    if(err) 
      return res.json({Error: err.stack});
    else 
      return res.json(suc.rows[0].iterid);
    
  });
  
});


app.post('/items/photos', async function(req, res) {
  
  var i = 0;
  const count = req.body.photo_count;
  
  for(i; i < count; i++){
    
    var filename = Math.random().toString(36).slice(2);
    var photoBody = 'photo'+i;
    
    var uploadParams = {
      Bucket: process.env.BUCKETNAME, 
      Key: 'iter'+req.body.iterID+'/'+filename, 
      Body: req.body[photoBody],
      ContentEncoding: 'base64'
    };
    
    await s3.putObject(uploadParams, (err, dataUp) => {
      if (err){
        i = count + 1;
        res.json({Error: 400});
      }
    }).promise();
  }
  
    
  if( i != count + 1){
    res.json({Success: 200})
  }
  
});


/****************************
* put methods *
****************************/


app.put('/items', function(req, res) {
  // Add your code here
  res.json({success: 'put call succeed!', url: req.url, body: req.body})
});

app.put('/items/*', function(req, res) {
  // Add your code here
  res.json({success: 'put call succeed!', url: req.url, body: req.body})
});


/****************************
* delete methods *
****************************/


app.delete('/items', function(req, res) {
  // Add your code here
  res.json({success: 'delete call succeed!', url: req.url});
});

app.delete('/items/*', function(req, res) {
  // Add your code here
  res.json({success: 'delete call succeed!', url: req.url});
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
