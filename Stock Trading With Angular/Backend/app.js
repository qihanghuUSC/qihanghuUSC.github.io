// Copyright 2017 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

'use strict';


const API_KEY = "c81fn32ad3i8p98ikr9g";

const axios = require('axios');

// [START gae_node_request_example]
const express = require('express');

const app = express();

//handling corps problem
var cors = require('cors');
app.use(cors());



//moment.js to handle the time convertion
var moment = require('moment');
var dayjs = require('dayjs');
var relativeTime = require('dayjs/plugin/relativeTime');
dayjs.extend(relativeTime);


app.get('/', (req, res) => {
  res.status(200).send('Hello, world!').end();
});


//get data for autoComplete on Search input 
// url: /autoComplete?name=ticker
app.get('/auto', (req, res) => {
    var ticker = req.query['ticker'].toUpperCase();
    //console.log(ticker);
    let link = 'https://finnhub.io/api/v1/search?q=' + ticker + '&token=' + API_KEY;   
    console.log("auto: " + link);
    axios.get(link).then(response => {
        let data = response.data;
        let container = [];
        let index = 0;
        
        for (let i = 0; i < data['count']; i++){
            if ( data['result'][i]['type'] == "Common Stock") {
                if (!data['result'][i]['symbol'].includes(".")){
                    container[index] = data['result'][i];
                    index += 1;
                }
            }
        }
        
        console.log(container);
        res.send(container);
    }).catch(function (error) {
        console.log("Auto Error", error.message);
        res.send({'off': 1});
    });
    
});


//get company profile and price info for watchlist
app.get('/watchlist/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    
    let link_one = "https://finnhub.io/api/v1/stock/profile2?symbol="+ticker+"&token=" + API_KEY;
    let link_two = "https://finnhub.io/api/v1/quote?symbol="+ticker+"&token=" + API_KEY;
    
    const req_profile = axios.get(link_one);
    const req_price = axios.get(link_two);
    
    axios.all([req_profile, req_price]).then(axios.spread((...response) => {
        let data_profile = response[0].data;
        //price
        let data_price = response[1].data;
        
        
        data_price['c'] = Math.trunc(data_price['c']*100)/100;
        data_price['d'] = Math.trunc(data_price['d']*100)/100;
        data_price['dp'] = Math.trunc(data_price['dp']*100)/100;
        
        let content = {'ticker':data_profile['ticker'], 'name':data_profile['name'], 'c':data_price['c'], 'd':data_price['d'], 'dp':data_price['dp']};
        
       // console.log(link_one);
        //console.log(link_two);
        res.send(content);
    }))
    .catch(function (error) {
        console.log("watchlist Error", error.message);
        res.send({'off': 1});
    });
});


//get company profile info
app.get('/profile/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    //console.log(ticker);
    let link = "https://finnhub.io/api/v1/stock/profile2?symbol="+ticker+"&token=" + API_KEY;
    //console.log(link);
    console.log("profile: " + link);
    axios.get(link).then(response => {
        let data = response.data;
        //console.log("result: ");
        //console.log(data);
        //res.send({'off': 1});
        res.send(data);
    }).catch(function (error) {
        console.log("Profile Error", error.message);
        res.send({'off': 1});
    });
});


//get company latest price
app.get('/price/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    let link = "https://finnhub.io/api/v1/quote?symbol="+ticker+"&token=" + API_KEY;
    axios.get(link).then(response => {
        let data = response.data;
        
        let date = dayjs.unix(data['t']).subtract(7, 'hour').format('YYYY-MM-DD HH:mm:ss');
        let now = dayjs().subtract(7, 'hour').format('YYYY-MM-DD HH:mm:ss');
        let diff = dayjs(now).diff(dayjs(date), 'seconds');
        data['tdate'] = date;
        data['dp'] = Math.trunc(data['dp']*100)/100;
        data['now'] = now;
        if (diff > 60)
        {
            data['open'] = false;
        }
        else
        {
            data['open'] = true;
        }
        
        data['c'] = Math.trunc(data['c']*100)/100;
        data['d'] = Math.trunc(data['d']*100)/100;
        data['dp'] = Math.trunc(data['dp']*100)/100;
        data['h'] = Math.trunc(data['h']*100)/100;
        data['l'] = Math.trunc(data['l']*100)/100;
        data['o'] = Math.trunc(data['o']*100)/100;
        data['pc'] = Math.trunc(data['pc']*100)/100;
        
        res.send(data);
    }).catch(function (error) {
        console.log("price Error", error.message);
        res.send({'off': 1});
    });
});


//get company peers
app.get('/peers/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    let link = "https://finnhub.io/api/v1/stock/peers?symbol="+ticker+"&token=" + API_KEY;
    console.log(link);
    axios.get(link, { headers : { 'Content-Type': 'application/json;charset=utf8'}
        }).then(response => {
        let content = []
        let data = response.data;
        //console.log(data);
        
        for (let i = 0; i < data.length; i++)
        {
            if (!data[i].includes(".") && data[i] != "" )
            {
                console.log("|" , data[i] , "|");
                content.push(data[i]);
            }
        }
        
        
        
        
        console.log(content);
        res.send(content);
    }).catch(function (error) {
        console.log("peers Error", error.message);
        res.send({'off': 1});
    });
});

//https://finnhub.io/api/v1/stock/candle?symbol=AAPL&resolution=5&from=1631022248&to=1631627048&token=c81fn32ad3i8p98ikr9g
// get stock chart data in summary
app.get('/summaryChart/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    var now = dayjs();
    var pre = now.subtract(7, 'day');
    let link = "https://finnhub.io/api/v1/stock/candle?symbol="+ticker+"&resolution=5&from=" + pre.unix() + "&to=" + now.unix() + "&token=" + API_KEY;
    //console.log(link);
    //console.log(now.unix());
    //console.log(now.format('YYYY-MM-DD HH:mm:ss'));
    //console.log(pre.format('YYYY-MM-DD HH:mm:ss'));
    
    axios.get(link).then(response => {
        let data = response.data;
        //console.log(link);
        console.log("\n\nchart in summary:");
        console.log(link);
        console.log(data);
        
        
        
        if (data['s'] == 'no_data')
        {
            res.send([]);
        }
        else
        {
            let current = dayjs.unix(data['t'][data['t'].length-1])
            let before = current.subtract(6, 'hour');

            //console.log(current.format('YYYY-MM-DD HH:mm:ss'));
            //console.log(before.format('YYYY-MM-DD HH:mm:ss'));

            let link_second = "https://finnhub.io/api/v1/stock/candle?symbol="+ticker+"&resolution=5&from=" + before.unix() + "&to=" + current.unix() + "&token=" + API_KEY;
            
            //console.log("\n\nchart in summary second:");
            //console.log(link_second);
            
            axios.get(link_second).then(repl => {
                let d = repl.data;
                let content = []
                for (let i = 0; i < d['t'].length; i++)
                {
                    let time = d['t'][i] * 1000;
                    content.push([time, d['c'][i]]);
                }
                res.send(content);
            }).catch(function (error) {
        console.log("sum chart second Error", error.message);
                res.send({'off': 1});
    });
        }
        
        
    }).catch(function (error) {
        console.log("sum chart first Error", error.message);
        res.send({'off': 1});
    });
});


//get historical data for chart tab
app.get('/chart/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    var now = dayjs();
    var pre = now.subtract(2, 'year');
    let link = "https://finnhub.io/api/v1/stock/candle?symbol="+ticker+"&resolution=D&from=" + pre.unix() + "&to=" + now.unix() + "&token=" + API_KEY;
    console.log(link);
    axios.get(link).then(response => {
        let data = response.data;
        let content = [];
        let ohlc = [];
        let volu = [];
        
        for (let i = 0; i < data['t'].length; i++)
        {
            let o = Math.trunc(data['o'][i]*100)/100;
            let h = Math.trunc(data['h'][i]*100)/100;
            let l = Math.trunc(data['l'][i]*100)/100;
            let c = Math.trunc(data['c'][i]*100)/100;
            
            
            ohlc.push([data['t'][i] * 1000,
                       o,
                       h,
                       l,
                       c]);
            
            volu.push([data['t'][i] * 1000,
                       data['v'][i]]);
        }
        
        content.push(ohlc);
        content.push(volu);
        
        //console.log(response.data);
        res.send(content);
    }).catch(function (error) {
        console.log("chart Error", error.message);
        res.send({'off': 1});
    });
});


// get social sentiment data
app.get('/social/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    //console.log(ticker);
    //https://finnhub.io/api/v1/stock/social-sentiment?symbol=<TICKER>&from=2022-01- 01&token=<API_KEY>
    let link = "https://finnhub.io/api/v1/stock/social-sentiment?symbol="+ticker+"&from=2022-01-01&token=" + API_KEY;
    console.log(link);
    axios.get(link).then(response => {
        let red = response.data['reddit'];
        let twi = response.data['twitter'];
        let content = [];
        let redPM = 0;
        let redNM = 0;
        let twiPM = 0;
        let twiNM = 0;
        
        
        for (let i  = 0; i < red.length; i++)
        {
            redPM += red[i]['positiveMention'];
            redNM += red[i]['negativeMention'];
        }
        
        let redData = [];
        redData.push(redPM);
        redData.push(redNM);
        redData.push(redNM + redPM);
        content.push(redData);
        
        for (let i  = 0; i < twi.length; i++)
        {
            twiPM += twi[i]['positiveMention'];
            twiNM += twi[i]['negativeMention'];
        }
        let twiData = [];
        twiData.push(twiPM);
        twiData.push(twiNM);
        twiData.push(twiNM + twiPM);
        content.push(twiData);
        
        console.log(content);
        // content[0] - reddit  //PM, NM, M
        // conrent[1] - twitter
                
        
        res.send(content);
    }).catch(function (error) {
        console.log("social Error", error.message);
        res.send({'off': 1});
    });;
});


app.get('/insightsRT/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    //https://finnhub.io/api/v1/stock/recommendation?symbol=TSLA&token=c81fn32ad3i8p98ikr9g
    //https://finnhub.io/api/v1/stock/earnings?symbol=MSFT&token=c81fn32ad3i8p98ikr9g
    let link_rec = "https://finnhub.io/api/v1/stock/recommendation?symbol="+ticker + "&token=" + API_KEY;
    console.log(link_rec);
    
    axios.get(link_rec).then(response => {
        let rec_data = response.data;
        console.log(rec_data);
        //console.log(rec_data[0]['period']);
        console.log();
        
        
        //modify recommendation data
        let b = [];
        let h = [];
        let s = [];
        let sb = [];
        let ss = [];
        let t = [];
        
        try
        {
            for (let i = 0; i < rec_data.length; i++)
            {
                b.push(rec_data[i]['buy']);
                h.push(rec_data[i]['hold']);
                s.push(rec_data[i]['sell']);
                sb.push(rec_data[i]['strongBuy']);
                ss.push(rec_data[i]['strongSell']);
                t.push(rec_data[i]['period']);
            }
        }
        catch(error)
        {
            console.log("insight RT data Error", error.message);
        }
        
        
        let rec_content = {"b":b, "h":h, "s":s, "sb": sb, "ss":ss, "t":t};
        console.log("\n\ndata: ");
        //console.log(rec_data);
        //console.log(ear_data);
        
        res.send(rec_content)
        
        }).catch(function (error) {
        console.log("insight RT Error", error.message);
        res.send({'off': 1});
    });;
});


app.get('/insightsEPS/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    //https://finnhub.io/api/v1/stock/recommendation?symbol=TSLA&token=c81fn32ad3i8p98ikr9g
    //https://finnhub.io/api/v1/stock/earnings?symbol=MSFT&token=c81fn32ad3i8p98ikr9g
    let link_ear = "https://finnhub.io/api/v1/stock/earnings?symbol="+ticker + "&token=" + API_KEY;
    
    axios.get(link_ear).then(response => {
        let ear_data = response.data;
        console.log(ear_data);
        
        
        let actual = [];
        let estimate = [];
        let surprise = [];
        let date = [];
        
        for (let i = 0; i < ear_data.length; i++)
        {
            if (ear_data[i]['actual'] == null)
            {
                actual.push(0);
            }
            else
            {
                actual.push(ear_data[i]['actual']);
            }
            
            if (ear_data[i]['estimate'] == null)
            {
                estimate.push(0);
            }
            else
            {
                estimate.push(ear_data[i]['estimate']);
            }
            
            if (ear_data[i]['surprise'] == null)
            {
                surprise.push(0);
            }
            else
            {
                surprise.push(ear_data[i]['surprise'].toFixed(2));
            }
            
            date.push(ear_data[i]['period']+"<br>Surprise: "+surprise[i]);
            
        }
        
        let ear_content = {"a":actual, "e":estimate, "t":date};
        //console.log(rec_data);
        //console.log(ear_data);
        
        res.send(ear_content)
        
        }).catch(function (error) {
        console.log("insight EPS Error", error.message);
        res.send({'off': 1});
    });;
});


// get data for the recommendation trend chart and the EPS chart in Insight tab
app.get('/insights/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    //https://finnhub.io/api/v1/stock/recommendation?symbol=TSLA&token=c81fn32ad3i8p98ikr9g
    //https://finnhub.io/api/v1/stock/earnings?symbol=MSFT&token=c81fn32ad3i8p98ikr9g
    let link_rec = "https://finnhub.io/api/v1/stock/recommendation?symbol="+ticker + "&token=" + API_KEY;
    let link_ear = "https://finnhub.io/api/v1/stock/earnings?symbol="+ticker + "&token=" + API_KEY;
    //console.log(link_rec);
    //console.log(link_ear);
    
    const req_rec = axios.get(link_rec);
    const req_ear = axios.get(link_ear);
    
    //res.send(content);
    
    axios.all([req_rec, req_ear]).then(axios.spread((...responses) => {
        let rec_data = responses[0].data;
        let ear_data = responses[1].data;
        console.log(rec_data);
        //console.log(rec_data[0]['period']);
        console.log();
        
        
        //modify recommendation data
        let b = [];
        let h = [];
        let s = [];
        let sb = [];
        let ss = [];
        let t = [];
        
        try
        {
            for (let i = 0; i < rec_data.length; i++)
            {
                b.push(rec_data[i]['buy']);
                h.push(rec_data[i]['hold']);
                s.push(rec_data[i]['sell']);
                sb.push(rec_data[i]['strongBuy']);
                ss.push(rec_data[i]['strongSell']);
                t.push(rec_data[i]['period'].substring(0,7));
            }
        }
        catch(error)
        {
            
        }
        
        
        let rec_content = {"b":b, "h":h, "s":s, "sb": sb, "ss":ss, "t":t};
        console.log("\n\ndata: ");
        //console.log(rec_data);
        //console.log(ear_data);
        
        
        let actual = [];
        let estimate = [];
        let surprise = [];
        let date = [];
        
        for (let i = 0; i < ear_data.length; i++)
        {
            if (ear_data[i]['actual'] == null)
            {
                actual.push(0);
            }
            else
            {
                actual.push(ear_data[i]['actual']);
            }
            
            if (ear_data[i]['estimate'] == null)
            {
                estimate.push(0);
            }
            else
            {
                estimate.push(ear_data[i]['estimate']);
            }
            
            if (ear_data[i]['surprise'] == null)
            {
                surprise.push(0);
            }
            else
            {
                surprise.push(ear_data[i]['surprise']);
            }
            
            date.push(ear_data[i]['period']+"<br>Surprise: "+surprise[i]);
            
        }
        
        let ear_content = {"a":actual, "e":estimate, "t":date};
        
        res.send([rec_content, ear_content])
        
        })
    ).catch(function (error) {
        console.log("insight Error", error.message);
        res.send({'off': 1});
    });;
});


// get news data 
app.get('/news/:ticker', (req, res) => {
    var ticker = req.params.ticker.toUpperCase();
    var now = dayjs();
    var pre = now.subtract(7, 'day');
    //https://finnhub.io/api/v1/company-news?symbol=MSFT&from=2021-09-01&to=2021-09-09&token=c81fn32ad3i8p98ikr9g
    let link = "https://finnhub.io/api/v1/company-news?symbol="+ticker+ "&from=" + pre.format('YYYY-MM-DD') + "&to=" + now.format('YYYY-MM-DD') + "&token=" + API_KEY;
    console.log(link);
    //console.log(now.format('YYYY-MM-DD'));
    //console.log(pre.format('YYYY-MM-DD'));
    //console.log(typeof pre.format('YYYY-MM-DD'));
    
    //console.log(typeof dayjs.unix(1648503000).format('MMMM DD, YYYY'));
    
    axios.get(link).then(response => {
        let data = response.data;
        let index = 0;
        let content = [];
        for (let i = 0; i < data.length; i++)
        {
            if (data[i]['image'] != "" && data[i]['source'] != "" && data[i]['datetime'] != "" && data[i]['headline'] != "" && data[i]['summary'] != "" && data[i]['url'] != "")
            {
                let item = data[i];
                let date = item['datetime'];
                item['datetime'] = dayjs.unix(item['datetime']).format('MMMM DD, YYYY');
                item['time_df'] = dayjs.unix(date).fromNow();
                content.push(item);
                index += 1;
            }
            
            if (index == 20) 
            {
                break;
            }
        }
        
        res.send(content);
        
    }).catch(function (error) {
        res.send({'off': 1});
    });
});

// Start the server
const PORT = parseInt(process.env.PORT) || 8080;
app.listen(PORT, () => {
  console.log(`App listening on port ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});
// [END gae_node_request_example]

module.exports = app;
