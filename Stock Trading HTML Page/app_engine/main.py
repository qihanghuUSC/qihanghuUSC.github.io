# Copyright 2018 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# [START gae_python38_app]
# [START gae_python3_app]
from flask import Flask, send_from_directory, current_app
from flask import jsonify
from flask_cors import CORS, cross_origin
import requests
from datetime import *
from dateutil.relativedelta import *
import calendar
import json
import copy
import pytz


# If `entrypoint` is not defined in app.yaml, App Engine will look for an app
# called `app` in `main.py`.
app = Flask(__name__, static_url_path='')
cors = CORS(app)
app.config['CORS_HEADERS'] = 'Content-Type'

ticker = ""
key = "c81fn32ad3i8p98ikr9g"

#@app.route('/')
#def hello():
#    """Return a friendly HTTP greeting."""
#    return 'Hello World!'
   
   
@app.route('/')
def hello_world():
    print("inside /")
    return send_from_directory('static','Page.html')

@app.route('/company/<string:symbol>', methods=['GET'])
@cross_origin()
def company(symbol):
    URL = "https://finnhub.io/api/v1/stock/profile2?symbol=" + symbol.upper() + "&token=" + key
    
    r = requests.get(url=URL)
    data = r.json()
    data
    return data

@app.route('/stock_summary/<string:symbol>', methods=['GET'])
@cross_origin()
def stock_summary(symbol):
    URL = "https://finnhub.io/api/v1/quote?symbol=" + symbol.upper() + "&token=" + key
    r = requests.get(url=URL)
    data = r.json()
    data['t'] = datetime.utcfromtimestamp(data['t']).strftime('%d %B, %Y')
    return data

@app.route('/recommendation/<string:symbol>', methods=['GET'])
@cross_origin()
def recommendation(symbol):
    URL = "https://finnhub.io/api/v1/stock/recommendation?symbol=" + symbol.upper() + "&token=" + key
    r = requests.get(url=URL)
    data = r.json()
    #print("\n\nrecommend: ", data)
    try:
        return data[0]
    except:
        return {};
    

@app.route('/news/<string:symbol>', methods=['GET'])
@cross_origin()
def news(symbol):
    today = date.today()
    before = today + relativedelta(days=-30)
    
    URL = "https://finnhub.io/api/v1/company-news?symbol=" + symbol.upper() + "&from=" + str(before) + "&to=" + str(today) + "&token=" + key

    r = requests.get(url=URL)
    data = r.json()
    
    
    count = 0
    container = []
    label_list = ["image", "headline", "datetime", "url"]
    for element in data:
        if element is None:
            continue
        dic = {}
        keys = element.keys()
        index = 0
        
        if count >= 5:
            break
            
        for label in label_list:
            if label in keys:
                if element[label] is not None:
                    if label == "datetime":
                        year = datetime.fromtimestamp(int(element[label])).strftime('%Y')
                        day = datetime.fromtimestamp(int(element[label])).strftime('%d')
                        month = datetime.fromtimestamp(int(element[label])).strftime('%m')
                        
                        month = datetime.strptime(month, '%m').strftime("%B")
                        
                        dic[label] = day + " " + month + ", " + year
                    else:
                        dic[label] = element[label]
                    index += 1
            else:
                break
                
        if index == 4:
            container.append(copy.deepcopy(dic))
            count += 1
            
    return jsonify(container)

@app.route('/chart/<string:symbol>', methods=['GET'])
@cross_origin()
def chart(symbol):
    
    today = datetime.now(pytz.utc).date()
    before = today + relativedelta(months=-6 ,days=-1)
    
      
    today = int(datetime.timestamp(datetime.strptime(str(today), "%Y-%m-%d")))
    before = int(datetime.timestamp(datetime.strptime(str(before), "%Y-%m-%d")))
      
      
    
    URL = "https://finnhub.io/api/v1/stock/candle?symbol=" + symbol.upper() + "&resolution=D&from=" + str(before) + "&to=" + str(today) + "&token=" + key
      
    r = requests.get(url=URL)
    data = r.json()
    
    #t = data["t"][len(data["t"])-1]
    
    #print("\n\nt: ", t, datetime.utcfromtimestamp(t), "\n\n")
      
    if data["s"] == "no_data":
        return {}
    else:
        time_and_price = []
        time_and_volumn = []
        date_time = []
        for i in range(len(data["t"])):
            time = data["t"][i]*1000
            time_and_price.append( [time, data["c"][i]] )
            
            time_and_volumn.append( [time, data["v"][i]] )
        
        return jsonify([time_and_price, time_and_volumn])
    
    


if __name__ == '__main__':
    # This is used when running locally only. When deploying to Google App
    # Engine, a webserver process such as Gunicorn will serve the app. You
    # can configure startup instructions by adding `entrypoint` to app.yaml.
    app.run(host='127.0.0.1', port=8080, debug=True)
# [END gae_python3_app]
# [END gae_python38_app]
