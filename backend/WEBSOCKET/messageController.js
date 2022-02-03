
const AWS = require('aws-sdk');
const endPoint = process.env.ENDPOINT;
const client = new AWS.ApiGatewayManagementApi({endpoint : endPoint});
const cognito = new AWS.CognitoIdentityServiceProvider({apiVersion: '2016-04-18'});


const {Client} = require('pg');
const clientParams = {
  host: process.env.DBHOST,
  user: process.env.DBUSER,
  port: process.env.DBPORT,
  password: process.env.DBPASSWORD,
  database: process.env.DBNAME
};


const setConnection = async (id, sub) => {
    
    const db = new Client(clientParams);
    
    var params = {
      UserPoolId: process.env.USERPOOLID,
      Username: sub
    };
    
    var userAttributes = await (cognito.adminGetUser(params)).promise();
    var clientName = userAttributes.UserAttributes[2].Value;
   
    db.connect();
    
    try {
        await db.query('INSERT INTO ONLINEUSER VALUES($1, $2, $3)', [id, sub, clientName]);
    } catch (err) {
        console.log(err.stack);
    }
    
};


const deleteConnection = async (id) => {
    
    const db = new Client(clientParams);
    
    db.connect();
    
    try {
        await db.query('DELETE FROM ONLINEUSER WHERE ConnectionId = $1', [id]);
    } catch (err) {
        console.log(err.stack);
    }
    
};


const sendMessage = async (mId, body) => {
    
    const db = new Client(clientParams);

    db.connect();
    
    try {
        
        await db.query('INSERT INTO MESSAGE VALUES($1, $2, $3, $4)', [body.text, body.senddate, body.sendtime, body.sender, body.receiver]);
        let id = await db.query('SELECT connectionId FROM ONLINEUSER WHERE sub = $1', [body.receiver]);
        if(id != null) {
            
            body.sendername = await db.query('SELECT name FROM ONLINEUSER WHERE connectionId = $1', [mId]);
            
            await client.postToConnection({
                'ConnectionId': id,
                'Data': Buffer.from(JSON.stringify(body))
            }).promise();
           
        }
        
    } catch (err) {
        console.log(err.stack);
    }
    
};


exports.handler = async (event) => {
    
    if(event.requestContext) {
        
        const connectionId = event.requestContext.connectionId;
        const routeKey = event.requestContext.routeKey;
        
        let body = {};
        try{
            
            if(event.body)
                body = JSON.parse(event.body);
            
        }catch(err) {
            console.log(err);
        }
        
        switch(routeKey) {
            
            case '$connect':
            break;
            
            case '$disconnect':
                await deleteConnection(connectionId);
            break;
            
            case 'setConnectionId':
                await setConnection(connectionId, body.sub);
            break;
            
            case 'sendMessage':
                await sendMessage(connectionId, body);
            break;
            
            default:
            
        }
        
    }
    
    
    const response = {
        statusCode: 200,
        body: JSON.stringify('Hello from Lambda!'),
    };
    return response;
    
};
