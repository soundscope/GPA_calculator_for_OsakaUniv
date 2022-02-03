#! /usr/bin/python 
# -*- coding: utf-8 -*- 
#===============================================================================
# Date created  : 2022-02-03T05:43:38+09:00
# Date modified : 2022-02-03T10:15:45+09:00
# Author        : soundscope
# Description   : get score from KOAN by python quickly, MIT License
# Note          : You have to make cookie file from following script in advance.
# If the cookie has expired, please run it again.
# Disclaimer    : NO WARRANTY; Everything is at your own risk.
#===============================================================================

import requests
import re
import os
import pickle
import getpass
import time
start = time.time()
def load_cookies(session):
    try:
        with open('cookies.pkl', 'rb') as f: session.cookies = pickle.load(f)
    except FileNotFoundError: pass
def save_cookies(session):
    try:
        with open('cookies.pkl', 'wb') as f: pickle.dump(session.cookies, f)
    except FileNotFoundError: pass
def set_id_password():
    user = ''
    password = ''
    try:  #default password file
        with open(os.getenv('HOME')+'/.koan_password.txt') as f:
            user = f.readline().strip(' \r\n')
            password = f.readline().strip(' \r\n')
    except FileNotFoundError:
        print('please input userID: ', end = '')
        user = input()
        user = user.strip(' \r\n')
        password = getpass.getpass()
        password = password.strip(' \r\n')
    return (user, password)
#flag, save cookies?
USE_SAVE_LOAD_SESSION = True
#force server to use Japanese
headers = {"Accept-Language": "ja-JP,en-US;q=0.7,en-GB;q=0.3"}

url_koan = 'https://koan.osaka-u.ac.jp/campusweb'
url_flow = 'https://koan.osaka-u.ac.jp/campusweb/campussquare.do?_flowId=SIW0001300-flow'
url_auth = 'https://ou-idp.auth.osaka-u.ac.jp/idp/authnPwd'
url_otp = 'https://ou-idp.auth.osaka-u.ac.jp/idp/otpAuth'
url_role = 'https://ou-idp.auth.osaka-u.ac.jp/idp/roleselect'
url_score = 'https://koan.osaka-u.ac.jp/campusweb/campusportal.do?page=main&tabId=si'
url_SAML = 'https://koan.osaka-u.ac.jp/Shibboleth.sso/SAML2/POST'

s = requests.session()
if USE_SAVE_LOAD_SESSION: load_cookies(s)
res = s.get(url_flow, headers=headers)
res_cookie = res.cookies
login = res

if "<title>単位修得状況照会</title>" not in res.text:
    res = s.get(url_koan, headers=headers)
    res_cookie = res.cookies
    login = res

if "<title>大阪大学 全学 IT 認証基盤サービス</title>" in res.text:
    user, password = set_id_password()
    json_data = {
        "USER_ID": user,
        "USER_PASSWORD" : password,
        "CHECK_BOX": "",
        "IDButton":"Login",
            }
    login = s.post(url_auth, data=json_data, cookies=res_cookie, headers=headers)
    res_cookie = login.cookies
    if USE_SAVE_LOAD_SESSION: save_cookies(s)
    print('finished password authentication')

if "<title>MFA認証</title>" in login.text:
    st = 'お使いの認証アプリに表示されるコードを入力してください(入力は隠されます)\n認証コード:'
    otp = getpass.getpass(prompt=st)
    json_data = {
        "OTP_CODE" : otp,
        "STORE_OTP_AUTH_RESULT": "1",
        }
    login = s.post(url_otp, data=json_data, cookies=res_cookie, headers=headers)
    res_cookie = login.cookies
    if USE_SAVE_LOAD_SESSION: save_cookies(s)
    print('finished MFA')

if "<title>利用者選択</title>" in login.text:
    json_data = { "role" : "self_0", }
    login = s.post(url_role, data=json_data, cookies=res_cookie,headers=headers)
    res_cookie = login.cookies
    saml = re.search(r'name="SAMLResponse"\s\s*value=([^>]*)>', login.text)
    saml = saml.groups()[0].strip('"')
    relay_st = re.search(r'name="RelayState"\s\s*value=([^>]*)>', login.text)
    relay_st = relay_st.groups()[0].strip('"')
    json_data = {
        "SAMLResponse" : saml,
        "RelayState": relay_st,
        "button" : "Send",
        }
    login = s.post(url_SAML, data=json_data, cookies=res_cookie, headers=headers)
    res_cookie = login.cookies
    res = s.get(url_flow)
    if USE_SAVE_LOAD_SESSION: save_cookies(s)
    print('finished SAML Relay')

def get_score(session):
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
    score_data = session.post(url_flow2, data=json_data, cookies=res_cookie, headers=headers)
    with open('score_data.html', 'w') as f: f.write(score_data.text)
    print('finished getting scores!  > score_data.html')

get_score(s)
print(time.time() - start, '(s) finished')
