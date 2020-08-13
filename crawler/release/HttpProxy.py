# -*- coding:utf-8 -*-
from mitmproxy import ctx

TARGET_URL = 'https://g.alicdn.com/secdev/sufei_data/3.8.1/index.js'
# TARGET_URL = 'https://g.alicdn.com/secdev/sufei_data/3.6.8/index.js'
INJECT_TEXT = 'Object.defineProperties(navigator,{webdriver:{get:() => false}});'


def response(flow):
    # ctx.log.info(str(flow.request.headers))
    # ctx.log.warn(str(flow.request.headers))
    # ctx.log.error(str(flow.request.headers))
    # print("##########" + flow)
    # ctx.log.error(str("##########" + flow.request.url))
    if flow.request.url.startswith(TARGET_URL):
        flow.response.text = INJECT_TEXT + flow.response.text
        print('注入成功')

    if 'um.js' in flow.request.url or '115.js' in flow.request.url:
        # 屏蔽selenium检测
        print(flow.response.text)
        flow.response.text = flow.response.text + 'Object.defineProperties(navigator,{webdriver:{get:() => false}})'
# mitmdump -s HttpProxy.py -p 7377
