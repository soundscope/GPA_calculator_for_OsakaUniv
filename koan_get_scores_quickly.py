#! /usr/bin/python 
# -*- coding: utf-8 -*- 
#===============================================================================
# Date created  : 2022-02-03T05:43:38+09:00
# Date modified : 2022-02-03T09:07:03+09:00
# Author        : soundscope
# Description   : get score from KOAN by python quickly, MIT License
# Note          : you have to make cookies file from under script in advance
# https://github.com/soundscope/GPA_calculator_for_OsakaUniv/blob/master/koan_auto_login.py
# Disclaimer    : NO WARRANTY; Everything is at your own risk.
#===============================================================================
import requests
import re
import pickle
def get_score(session):
    url_flow = 'https://koan.osaka-u.ac.jp/campusweb/campussquare.do?_flowId=SIW0001300-flow'
    res = session.get(url_flow)
    url_flow2 = 'https://koan.osaka-u.ac.jp/campusweb/campussquare.do'
    tmp = re.search(r'name="_flowExecutionKey"\s\s*value=([^>]*)>', res.text)
    flow_key = tmp.groups()[0].strip('"')
    json_data = {
            "_flowExecutionKey": flow_key,
            "_eventId": "display",
            "dummy": "",
            "spanType": "0",   #if this spanType is 0
            "nendo": "2021",   #you need not to change
            "gakkiKbnCd": "6", #you need not to change
            }
    score_data = session.post(url_flow2, data=json_data, cookies=session.cookies, headers=headers)
    with open('score_data.html', 'w') as f: f.write(score_data.text)
    print('finished getting scores!  > score_data.html')
    quit()
def load_cookies(session):
    try: 
        with open('cookies.pkl', 'rb') as f: session.cookies = pickle.load(f)
    except FileNotFoundError:
        print('error cannot find cookies file')
headers = {"Accept-Language": "ja-JP,en-US;q=0.7,en-GB;q=0.3"}
s = requests.session()
load_cookies(s)
get_score(s)

