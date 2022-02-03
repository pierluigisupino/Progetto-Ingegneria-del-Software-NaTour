
const AWS = require('aws-sdk');
const client = new AWS.ApiGatewayManagementApi({endpoint : process.env.ENDPOINT});

const sendMessage = async (id, body) => {
    
    try{
        
        await client.postToConnection({
            'ConnectionId': id,
            'Data': Buffer.from(JSON.stringify(body))
        }).promise();
        
    }catch(err) {
        console.log(err);
    }
 
};


exports.handler = async (event) => {
    
    if(event.requestContext) {
        
        const connectionId = event.requestContext.connectionId;
        const routeKey = event.requestContext.routeKey;
        
        let body = {};
        try{
            if(event.body){
                body = JSON.parse(event.body);
            }
        }catch(err) {
            
        }
        
        switch(routeKey) {
            
            case '$connect':
                await sendMessage(connectionId, {message: 'Hi'});
            break;
            
            case '$disconnect':
            break;
            
            case 'sendMessage':
                await sendMessage(connectionId, {message: 'Received'});
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
