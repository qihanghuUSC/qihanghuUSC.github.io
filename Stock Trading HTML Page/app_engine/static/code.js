//"use strict";

var domain = 'https://pythonbackend-8484.wl.r.appspot.com/';  //need to be changed after deployed
            var valid_pre_result = 0;
            var count = 0;
            
            document.getElementById("section_two").style.display = "none";
            
            async function getInfoAPI(domain, ticker)
            {
                var profile = domain + 'company/' + ticker;
                var summary = domain + 'stock_summary/' + ticker;
                var trend = domain + 'recommendation/' + ticker;
                var news = domain + 'news/' + ticker;
                var chart = domain + 'chart/' + ticker;
                
                //profile
                let resp = await fetch(profile);
                let prof = await resp.json();
                console.log("profile:  ok: " + resp.ok + "  status: " + resp.status);
                
                
                //summary
                let resp_s = await fetch(summary);
                let sum = await resp_s.json();
                console.log("sum:  ok: " + resp_s.ok + "  status: " + resp_s.status);
                
                //recommendaton trend
                let resp_r = await fetch(trend);
                let recom = await resp_r.json();
                console.log("recom:  ok: " + resp_r.ok + "  status: " + resp_r.status);
                
                //news
                let resp_n = await fetch(news);
                let news_list = await resp_n.json();
                console.log("news:  ok: " + resp_n.ok + "  status: " + resp_n.status);
                
                //chart
                let resp_c = await fetch(chart);
                let chart_data = await resp_c.json();
                console.log("chart:  ok: " + resp_c.ok + "  status: " + resp_c.status);
                
                console.log(profile);
                console.log(summary);
                console.log(trend);
                console.log(news)
                console.log(chart);
                
                return [prof, sum, recom, news_list, chart_data];
            }
            
            function getSearchContent()
            {
                try{
                    var d = document.getElementById("error_mess");
                    d.remove();
                }
                catch(e)
                {
                    
                }
                
                var valid = document.querySelector("#search_content").reportValidity();
                
                if (valid) 
                {
                    
                    var ticker = document.getElementById("search_content").value;
                    
                    
                    //var data = getInfoAPI(domain, ticker); //getProfileAPI(profile)
                    //document.getElementById("section_two").style.display = "none";

                    //document.getElementById("test").innerHTML = profile;
                    
                    //console.log(data);
                    
                    
                    getInfoAPI(domain, ticker).then(([prof, sum, recom, news_list, chart]) => {
                        console.log(prof);
                        console.log(sum);
                        console.log(recom);
                        console.log(news_list);
                        console.log(chart);
                        if (Object.keys(prof) == 0)
                            {
                                console.log("inside error");
                                document.getElementById("error").innerHTML = "<div id='error_mess'></div>";
                                document.getElementById("error_mess").innerHTML = "Error : No record has been found, please enter a valid symbol";
                                document.getElementById("error_mess").style.cssText="position: relative; text-align: center; background-color: gainsboro; height: 40px; width: 60%; margin-left: 200px; font-family: 'Arial'; line-height: 40px; border-radius: 10px;"
                                document.getElementById("section_two").style.display = "none";
                                valid_pre_result = 0;
                            }
                        else
                            {
                                build(prof, sum, recom, news_list, chart);
                                tabSelection();
                            }
                        //console.log(data);
                        //console.log(Object.keys(data));
                        //console.log(Object.keys(data) == 0);
                        //console.log(data.weburl);
                        
                    });
                    
                }
                
                
                event.preventDefault();      
                
            }
            
            function build(profile, sum, trend, news, chart)
            {
                document.getElementById("section_two").style.display = "contents";
                
                
                //company tab
                var image = document.createElement('img');
                image.src = profile.logo;
                image.className = "profile_image";
                
                console.log(image);
                document.getElementById("i").innerHTML = "";
                document.getElementById("i").append(image);
                document.getElementById("cn").innerHTML = profile.name;
                document.getElementById("sts").innerHTML = profile.ticker;
                document.getElementById("sec").innerHTML = profile.exchange;
                document.getElementById("cid").innerHTML = profile.ipo;
                document.getElementById("c").innerHTML = profile.finnhubIndustry;
                
                
                //summary tab
                document.getElementById("sts_ss").innerHTML = profile.ticker;
                document.getElementById("td_ss").innerHTML = sum.t;
                document.getElementById("pcp_ss").innerHTML = sum.pc;
                document.getElementById("op_ss").innerHTML = sum.o;
                document.getElementById("hp_ss").innerHTML = sum.h;
                document.getElementById("lp_ss").innerHTML = sum.l;
                document.getElementById("c_ss").innerHTML = sum.d;
                document.getElementById("cp_ss").innerHTML = sum.dp;
                
                
                
                if (sum.d > 0) //get svg file for Change
                {
                    var v_up = document.createElement('img');
                    v_up.src = "img/GreenArrowUp.png";
                    v_up.alt = "image_here";
                    v_up.className = "arrow_up_and_down";
                    document.getElementById("c_ss").append(v_up);
                    
                }
                else if (sum.d < 0)
                {
                    var v_down = document.createElement('img');
                    v_down.className = "arrow_up_and_down";
                    v_down.src = "img/RedArrowDown.png";
                    v_down.alt = "image_here";
                    document.getElementById("c_ss").append(v_down);
                    console.log("inside down change");
                }
                
                
                if (sum.dp > 0) //get svg file for Change Percent
                {
                    var value_up = document.createElement('img');
                    value_up.src = "img/GreenArrowUp.png";
                    value_up.className = "arrow_up_and_down";
                    value_up.alt = "image_here";
                    document.getElementById("cp_ss").append(value_up);
                    console.log("inside dp up change");
                }
                else if (sum.dp < 0)
                {
                    var value_down = document.createElement('img');
                    value_down.className = "arrow_up_and_down";
                    value_down.src = "img/RedArrowDown.png";
                    value_down.alt = "image_here";
                    document.getElementById("cp_ss").append(value_down);
                }
                
                //recommendation trend
                
                if (Object.keys(trend) == 0)
                {
                    document.getElementById("item_one_trend_sec").innerHTML = 'N.A.';
                    document.getElementById("item_two_trend_sec").innerHTML = 'N.A.';
                    document.getElementById("item_three_trend_sec").innerHTML = 'N.A.';
                    document.getElementById("item_four_trend_sec").innerHTML = 'N.A.';
                    document.getElementById("item_five_trend_sec").innerHTML = 'N.A.';
                }
                else
                {
                    document.getElementById("item_one_trend_sec").innerHTML = trend.strongSell;
                    document.getElementById("item_two_trend_sec").innerHTML = trend.sell;
                    document.getElementById("item_three_trend_sec").innerHTML = trend.hold;
                    document.getElementById("item_four_trend_sec").innerHTML = trend.buy;
                    document.getElementById("item_five_trend_sec").innerHTML = trend.strongBuy;
                }
                
                
                
                //news
                var index = ["one", "two", "three", "four", "five"];
                console.log(news.length);
                for (let i = 0; i < index.length; i++)
                {
                    console.log("before index: "+ i + ": \nchart length: "+ chart.length);
                    
                    if (i < news.length)
                    {
                        console.log("index: "+ i + ": \n");
                        console.log(news[i]);
                        //set image
                        let img = document.createElement('img');
                        img.src = news[i].image;
                        img.className = "news_image";
                        document.getElementById("news_image_"+index[i]).innerHTML = "";
                        document.getElementById("news_image_"+index[i]).append(img);
                        document.getElementById("news_image_"+index[i]).className = "news_image";
                        document.getElementById("news_image_"+index[i]).alt = "image";

                        //set bold
                        document.getElementById("news_bold_"+index[i]).innerHTML = news[i].headline;

                        //set datetime
                        document.getElementById("news_label_"+index[i]).innerHTML = news[i].datetime;

                        //set url
                        document.getElementById("news_a_"+index[i]).href = news[i].url;
                        
                        document.getElementById("news_"+index[i]).style.display = 'content';
                    }
                    else
                    {
                        document.getElementById("news_"+index[i]).style.display = 'none';
                    }
                    
                    
                }
                
                
                //chart
                console.log(chart);
                var date = new Date(chart[0][0][0]);
                console.log(date.getUTCDate())

                var m = date.getMonth() + 1;
                
                console.log(chart[0][0]);
                console.log(date);
                console.log(m);

                var month;
                var da;
                if (m < 10)
                {
                    month = "-0" + m;
                }
                else
                {
                    month = "-" + m;
                }

                if (date.getUTCDate() < 10)
                {
                    da = "-0" + date.getUTCDate();
                }
                else
                {
                    da = "-" + date.getUTCDate();
                }

                var title_date = date.getFullYear() + month + da;
                
                console.log(date.getFullYear());
                
                Highcharts.stockChart('container', {


                    title: {
                      text: 'Stock Price '+ profile.ticker + ' ' + title_date
                    },

                    subtitle: {
                      text: '<a href="https://finnhub.io/" target="_blank" style="text-decoration: underline">Source: Finnhub</a>'
                    },

                    xAxis: {
                        
                    },
                    
                  yAxis: [{title: {text: 'Stock Price'}, opposite: false},{title: {text: 'Volumn'}}],
                   

                    rangeSelector: {
                          allButtonsEnabled: true,
                          enabled: true,
                          buttons: [{
                            type: 'day',
                            count: 7,
                            text: '7d',
                          }, {
                              type: 'day',
                              count: 15,
                              text: '15d',
                          }, {
                            type: 'month',
                            count: 1,
                            text: '1m'
                          }, {
                            type: 'month',
                            count: 3,
                            text: '3m'
                          }, {
                            type: 'month',
                            count: 6,
                            text: '6m'
                          }],
                          selected: 0,
                          inputEnabled: false
                        },
                  
                    plotOptions: {
                        series: {
                            pointWidth: 3,
                            pointPlacement: 'on'
                        }
                    },

                    series: [ 
                             {
                      name: 'Stock Price',
                      type: 'area',
                      yAxis: 0,
                      data: chart[0],
                      gapSize: 5,
                      tooltip: {
                        valueDecimals: 2
                      }, 
                    
                      fillColor: {
                        linearGradient: {
                          x1: 0,
                          y1: 0,
                          x2: 0,
                          y2: 1
                        },
                        stops: [
                          [0, Highcharts.getOptions().colors[0]],
                          [1, Highcharts.color(Highcharts.getOptions().colors[0]).setOpacity(0).get('rgba')]
                        ]
                      },
                      threshold: null
                    }, 
                        
                    {
                      name: 'Volume',
                      type: 'column',
                      yAxis: 1,
                      data: chart[1]
                    },
                            ]
                  });
                
                
                
            }
            
            function tabSelection()
            {
                if (!count)
                {
                    document.getElementById("default_open").click();
                }
                else
                {
                    if (!valid_pre_result)
                    {
                        document.getElementById("default_open").click();
                    }
                }
                valid_pre_result = 1;
                count = 1;
            }
            
            function clearSearchContent()
            {
                valid_pre_result = 0;
                document.getElementById("search_content").value = "";
                document.getElementById("section_two").style.display = "none";
                try{
                    var d = document.getElementById("error_mess");
                    d.remove();
                }
                catch(e)
                {
                    
                }
                event.preventDefault();
                
            }
            
            
            
            
            function openPage(pageName, element)
            {
                var content_list = document.getElementsByClassName("content_page");
                for (let i = 0; i < content_list.length; i++)
                {
                    content_list[i].style.display = "none";
                }
                
                document.getElementById(pageName).style.display = "block";
                
            }
            //document.getElementById("default_open").click();