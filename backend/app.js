
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
  
  var queryParams = 'SELECT * FROM ITINERARY ORDER BY iterID DESC LIMIT 8';
  
  var lastIter = req.query.iterid;
  if(lastIter != null){
    queryParams = 'SELECT * FROM ITINERARY WHERE ITERID < '+ lastIter +'ORDER BY iterID DESC LIMIT 8';
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
  
  var prefix = 'iter'+req.query.iterid+'/';
  
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
      response.count = data.KeyCount;
      
      for(var i = 0; i < data.Contents.length; ++i){
        
        var downloadParams = {
          Bucket: process.env.BUCKETNAME, 
          Key: data.Contents[i].Key
        };
        
        try{
          response['photo'+i] = (await (s3.getObject(downloadParams).promise())).Body.toString();
          response.lastkey = data.Contents[i].Key;
        }catch(error) {
          response.count--;
        }
        
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


app.post('/items/itinerary', function(req, res) {
  
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


app.post('/items/photos', async function(req, res) {
  
  var i = 0;
  var errno;
  const count = req.body.photo_count;
  
  for(; i < count; i++){
    
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
        errno = err.stack;
        i = count + 1;
      }
    }).promise();
    
  }
    
  if(i != count + 1)
    return res.json({Code: 200});
  else
    return res.json({Code: 400, Error: errno});
  
});


/****************************
* put methods *
****************************/


app.put('/items', function(req, res) {
  // Add your code here
  res.json({success: 'put call succeed!', url: req.url, body: req.body});
});


/****************************
* delete methods *
****************************/


app.delete('/items', function(req, res) {
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