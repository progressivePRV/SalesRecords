const { response } = require("express");
const express = require("express");
const { request } = require("http");
const mongo = require('mongodb');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const { requestBody, validationResult, body, header, param, query } = require('express-validator');
const csv = require('csv-parser')
const fs = require('fs')

const route = express.Router();

const MongoClient = mongo.MongoClient;
const uri = "mongodb+srv://rojatkaraditi:AprApr_2606@test.z8ya6.mongodb.net/project8DB?retryWrites=true&w=majority";
var client;
var collection;
var usersCollection;
const tokenSecret = "wFq9+ssDbT#e2H9^";
var decoded={};
var token;
var pageLimit = 50;

var connectToDb = function(req,res,next){
    client = new MongoClient(uri, { useNewUrlParser: true, useUnifiedTopology: true});
    client.connect(err => {
      if(err){
          closeConnection();
          return res.status(400).json({"error":"Could not connect to database: "+err});
      }
      usersCollection = client.db("project8DB").collection("users");
      collection = client.db("project8DB").collection("sales");
      console.log("connected to database");
    next();
    });
};

var verifyToken = function(req,res,next){
    var headerValue = req.header("Authorization");
    if(!headerValue){
        //closeConnection();
        return res.status(400).json({"error":"Authorization header needs to be provided for using API","errorCode":103});
    }

    var authData = headerValue.split(' ');

    if(authData && authData.length==2 && authData[0]==='Bearer'){
        token = authData[1];
        try {
            decoded = jwt.verify(token, tokenSecret);
            next();
          } catch(err) {
            //closeConnection();
            return res.status(400).json({"error":err,"errorCode":104});
          }
    }
    else {
        //closeConnection();
        return res.status(400).json({"error":"Appropriate authentication information needs to be provided","errorCode":105})
    }

};

var closeConnection = function(){
    client.close();
};

route.use('/profile',verifyToken);
route.use('/sales',verifyToken);
route.use(connectToDb);

//do not use. To be removed before submission
// route.post('/saveData',(request,response)=>{
//     var results = [];
//     var items =[];
//     fs.createReadStream('data.csv')
//   .pipe(csv())
//   .on('data', (data) => {
//     var totalCost = data.UnitsSold*data.UnitCost;
//     var date= data.OrderDate;
//     var str = date.split('/');
//     if(str[0].length==1){
//         str[0] = 0+str[0];
//     }
//     if(str[1].length==1){
//         str[1] = 0+str[1];
//     }
//     var newDate = str[2]+"/"+str[0]+"/"+str[1]
//     var item ={
//         'item_type' : data.ItemType,
//         'order_date' : newDate,
//         'units_sold' : data.UnitsSold,
//         'unit_cost': data.UnitCost,
//         'total' : totalCost.toFixed(2)
//     };
//     items.push(item);
//   })
//   .on('end', () => {
//     collection.insertMany(items,(err,res)=>{
//         if(err){
//             closeConnection();
//             return response.send('error');
//         }
//         closeConnection();
//         return response.send('Done');
//     });
//   });

//  });

route.get('/sales',[
    query('page','Page number required to process this request').notEmpty().trim(),
    query('page','page should be valid number greater than 0').isInt({gt:0}),
    query('filter','filter value can only be item_type, order_date, units_sold, unit_cost or total').optional().isIn(['item_type','order_date','units_sold','unit_cost','total']),
    query('sortBy','sortBy value can only be item_type, order_date, units_sold, unit_cost or total').optional().isIn(['item_type','order_date','units_sold','unit_cost','total']),
    query('sortOrder','sortBy value can only be asc,desc').optional().isIn(['asc','desc'])
],(request,response)=>{
    const err = validationResult(request);
    if(!err.isEmpty()){
        closeConnection();
        return response.status(400).json({"error":err});
    }

    var pageNo = request.query.page;
    var filterQuery={};
    var sortQuery={};

    if(request.query.filter){
        if(!request.query.filterValue){
            var errors =[];
            var errVal = {
                "value": "filter value",
                "msg": "filter value should be provided if filter is provided",
                "param": "filterValue",
                "location": "query"
            }
            errors.push(errVal);
            var error = {
                'errors':errors
            };

            var result = {
                'error' : error
            }

            closeConnection();
            return response.status(400).json(result);
        }

        var rule = {"$regex": ".*"+request.query.filterValue+".*", "$options": "i"}

        filterQuery={
            [request.query.filter] : rule
        }
    }

    if(request.query.sortBy){
        if(!request.query.sortOrder){
            var errors =[];
            var errVal = {
                "value": "sort order",
                "msg": "sort order should be provided if sortBy is provided",
                "param": "sortOrder",
                "location": "query"
            }
            errors.push(errVal);
            var error = {
                'errors':errors
            };

            var result = {
                'error' : error
            }

            closeConnection();
            return response.status(400).json(result);
        }

        var sortOrder = request.query.sortOrder;
        var sortVal = 0;
        if(sortOrder=='asc'){
            sortVal = 1;
        }
        else if(sortOrder=='desc'){
            sortVal = -1;
        }

        sortQuery = {
            [request.query.sortBy] : sortVal
        }
    }

    collection.find(filterQuery)
    .sort(sortQuery)
    .skip((pageNo-1)*pageLimit)
    .limit(pageLimit)
    .toArray((err,res)=>{
        if(err){
            closeConnection();
            return response.status(400).json({'error':err,"errorCode":101});
        }
        if(res.length<=0){
            closeConnection();
            return response.status(400).json({'error':'no records found',"errorCode":102});
        }
        closeConnection();
        return response.status(200).json(res);
    });
});

route.post("/signup",[
    body("firstName","firstName cannot be empty").notEmpty().trim().escape(),
    body("firstName","firstName can have only alphabets").isAlpha().trim().escape(),
    body("lastName","lastName cannot be empty").notEmpty().trim().escape(),
    body("lastName","lastName can have only alphabets").isAlpha().trim().escape(),
    body("gender","gender cannot be empty").notEmpty().trim().escape(),
    body("gender","gender can have only alphabets").isAlpha().trim().escape(),
    body("gender","gender can only be Male or Female").isIn(["Male","Female"]),
    body("email","email cannot be empty").notEmpty().trim().escape(),
    body("email","invalid email format").isEmail(),
    body("password","password cannot be empty").notEmpty().trim(),
    body("password","password should have atleast 6 and at max 20 characters").isLength({min:6,max:20})
],(request,response)=>{
    const err = validationResult(request);
    if(!err.isEmpty()){
        closeConnection();
        return response.status(400).json({"error":err});
    }
    try{
        let pwd = request.body.password;
        var hash = bcrypt.hashSync(pwd,10);
        var newUser = request.body;
        newUser.password=hash;
        usersCollection.insertOne(newUser,(err,res)=>{
            var result={};
            var responseCode = 200;
            if(err){
                result={"error":err,"errorCode":101};
                responseCode=400;
            }
            else{
                //console.log(res);
    
                if(res.ops.length>0){
                    var usr = {
                        "_id":res.ops[0]._id
                    }
                    console.log(usr);
                    usr.exp = Math.floor(Date.now() / 1000) + (60 * 60);
                    var token = jwt.sign(usr, tokenSecret);
                    result=res.ops[0];
                    delete result.password;
                    result.token=token;
                }
                
            }
            closeConnection();
            return response.status(responseCode).json(result);
        });
    
    }
    catch(error){
        closeConnection();
        return response.status(400).json({"error":error.toString(),'errorCode':113});
    }
});

route.get("/login",[
    header("Authorization","Authorization header required to login").notEmpty().trim()
],(request,response)=>{

    const err = validationResult(request);
    if(!err.isEmpty()){
        closeConnection();
        return response.status(400).json({"error":err});
    }
    
    try{
        var data = request.header('Authorization');
        //console.log(data);
        var authData = data.split(' ');

        if(authData && authData.length==2 && authData[0]==='Basic'){
            let buff = new Buffer(authData[1], 'base64');
            let loginInfo = buff.toString('ascii').split(":");
            var result ={};

            if(loginInfo!=undefined && loginInfo!=null && loginInfo.length==2){
                var query = {"email":loginInfo[0]};
                usersCollection.find(query).toArray((err,res)=>{
                    var responseCode = 400;
                    if(err){
                        result = {"error":err,'errorCode':101};
                    }
                    else if(res.length<=0){
                        result={"error":"no such user present",'errorCode':102};
                    }
                    else{
                        var user = res[0];
                        if(bcrypt.compareSync(loginInfo[1],user.password)){
                            result=user;
                            delete result.password;
                            user={'_id' : user._id};
                            user.exp = Math.floor(Date.now() / 1000) + (60 * 60);
                            var token = jwt.sign(user, tokenSecret);
                            result.token=token;
                            responseCode=200;
                        }
                        else{
                            result={"error":"Username or password is incorrect",'errorCode':110};
                        }
                    }
                    closeConnection();
                    return response.status(responseCode).json(result);

                });
            }
            else{
                closeConnection();
                return response.status(400).json({"error":"credentials not provided for login",'errorCode':111});
            }
        }
        else{
            closeConnection();
            return response.status(400).json({"error":"Desired authentication type and value required for login",'errorCode':112})
        }
    }
    catch(error){
        closeConnection();
        return response.status(400).json({"error":error.toString(),'errorCode':113});
    }

});

route.get("/profile",(request,response)=>{
    try{
        var query = {"_id":new mongo.ObjectID(decoded._id)};
        var result={};
        var responseCode = 400;
        usersCollection.find(query,{ projection: { password: 0 } }).toArray((err,res)=>{
            if(err){
                result = {"error":err,'errorCode':101};
            }
            else{
                if(res.length<=0){
                    result={"error":"no user found with id "+decoded._id,'errorCode':102};
                }
                else{
                    result = res[0];
                    responseCode=200;
                }
            }
            closeConnection();
            return response.status(responseCode).json(result);
        });
    }
    catch(error){
        closeConnection();
        return response.status(400).json({"error":error.toString(),'errorCode':113});
    }
});

module.exports = route;