
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
  
  client.query(queryParams, async (err, data) => {
    
    client.end();
    
    if(err) 
      return res.json({Error: err.stack});
    else{
      
      const response = data.rows;
      for(var i = 0; i < response.length; ++i) {
        
        var params = {
          UserPoolId: process.env.USERPOOLID,
          Username: response[i].creatorid
        };
  
        try {
          
          var userAttributes = await (cognito.adminGetUser(params)).promise();
          response[i].creatorname = userAttributes.UserAttributes[2].Value;
          
        }catch(e) {
          
          if((e.stack).includes('UserNotFoundException'))
            response[i].creatorname = 'Unknown';
          else
            return res.json({Error: e.stack});
            
        }  
        
      }
      
      return res.json({Result: response});
      
    }
  });
  
});


app.get('/items/photos', function(req, res) {
  
  const prefix = 'iter'+req.query.iterid+'/';
  
  var listParams = {
    Bucket: process.env.BUCKETNAME,
    Prefix: prefix,
    MaxKeys: 10,
    StartAfter: req.query.lastkey
  };
  
  
  s3.listObjectsV2(listParams, function(err, data) {
    
    if (err)
      return res.json({Error: err.stack});
      
    else{
      
      var urls = new Object();
      var lastPhotoKey;
      
      for(var i = 0; i < data.KeyCount; ++i) {
        
        var getParams = {
          Bucket: process.env.BUCKETNAME,
          Key: data.Contents[i].Key,
          Expires: 60
        };
        
        try{
          urls['url'+i] = s3.getSignedUrl('getObject', getParams);
        }catch(err){
          res.json({Error: err.stack});
        }
        
        lastPhotoKey = data.Contents[i].Key;
        
      }
      
      return res.json({KeyCount: data.KeyCount, Urls: urls, LastKey: lastPhotoKey});
      
    }
  });
  
});


app.get('/items/admin', function(req, res) {
  
  var params = {
    UserPoolId: process.env.USERPOOLID,
    Username: req.query.uid
  };
  
  cognito.adminListGroupsForUser(params, function(err, data) {
    
    if (err)
      return res.json({Error: err.stack}); 
      
    else
    
      var groups = data.Groups;
    
      for(var i = 0; i < groups.length; ++i) {
        
        if(groups[i].GroupName == "AdminGroup")
          return res.json({isAdmin: true});
          
      }
    
      return res.json({isAdmin: false});
           
  });
    
});


app.get('/items/chats', function(req, res) {
  
  const client = new Client(clientParams);
  
  client.connect();
  
  const query = 'SELECT DISTINCT sender AS userID\
                 FROM Message \
                 WHERE receiver = $1 \
                 UNION \
                 SELECT DISTINCT receiver AS userID \
                 FROM Message \
                 WHERE sender = $1';
                
                                      
  client.query(query, [req.query.userid], async (err, data) => {
    
    if(err)
      return res.json({Error: err.stack});
    else{
      
      const result = data.rows;
      for(var i = 0; i < result.length; ++i) {
        
        var params = {
          UserPoolId: process.env.USERPOOLID,
          Username: result[i].userid
        };
  
        try {
          
          var userAttributes = await (cognito.adminGetUser(params)).promise();
          result[i].name = userAttributes.UserAttributes[2].Value;
          
        }catch(e) {
          
          if((e.stack).includes('UserNotFoundException'))
            result[i].name = 'Unknown';
          else
            return res.json({Error: e.stack});
            
        } 
      }
      
      return res.json({Result: result});
      
    }
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
  
  const client = new Client(clientParams);
  
  client.connect();
  
  const query = 'UPDATE ITINERARY SET iterName = $1, description = $2, difficulty = $3, hours = $4, minutes = $5, updateDate = $6 WHERE iterid = $7';
  
  client.query(query, [req.body.name, req.body.iterDescription, req.body.difficulty, req.body.hours, req.body.minutes, req.body.updateDate, req.body.iterid], (err, suc) => {
    
    client.end();
    
    if(err)
      return res.json({Code:500, Error: err.stack});
    else
      return res.json({Code: 200});
      
  });
  
});


app.put('/items/photo', function(req, res) {
  
  const prefix = 'iter'+req.body.iterID+'/';
  var urls = new Object();
  
  for(var i = 0; i < req.body.photoCount; ++i){
    
    var filename = prefix + Math.random().toString(36).slice(2);
    
    var uploadParams = {
      Bucket: process.env.BUCKETNAME,
      Key: filename
    };
    
    try{
      urls['url'+i] = s3.getSignedUrl('putObject', uploadParams);
    }catch(err){
      return res.json({Error: err.stack});
    }
    
  }
  
  return res.json({Urls: urls});
  
});


/****************************
* delete methods *
****************************/


app.delete('/items/itineraries/:id', function(req, res) {
  
  const client = new Client(clientParams);
  const iterid = Number(req.params.id);
  
  client.connect();
  
  client.query('DELETE FROM ITINERARY WHERE IterID = '+iterid, (err, suc) => {
    
    client.end();
    
    if(err)
      return res.json({Code:500, Error: err.stack});
    else
      return res.json({Code: 200});
    
  });
  
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