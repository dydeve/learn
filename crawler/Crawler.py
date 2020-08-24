# -*- coding:utf-8 -*-
import time
import re
import sys
import random
import urllib.request
import json
import os

from selenium import webdriver
from selenium.common.exceptions import NoSuchElementException
from selenium.webdriver import ActionChains
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.firefox.options import Options as FireFoxOptions

# 店铺子账号
TB_USERNAME = ''
TB_PASSWORD = ''

# 淘宝登录地址
TB_LOGIN_URL = 'https://login.taobao.com/member/login.jhtml'

# 淘宝订单详情地址前缀
TB_TRADE_URL = 'https://trade.taobao.com/trade/detail/e_ticket_trade_item_detail.htm?bizOrderId='

# 拉取待查询手机号的订单的地址
URL_GET = 'http://mpay.ximalaya.com/openapi-tmall-code/tmall/mobile/get'

# 用户手机号回填地址
URL_SET = 'http://mpay.ximalaya.com/openapi-tmall-code/tmall/mobile/set'

# 60秒内没有需要查询的订单时，刷新浏览器页面，防止 Cookie 过期
IDLE_SECONDS = 60

# 长时间没有订单时重启爬虫 (单位: 秒)。0 表示不重启
RESTART_SECONDS = 0
CHROME_DRIVER = 'chromedriver'

# mitmproxy 地址，用于屏蔽淘宝 js 中的爬虫检测代码
PROXY_ADDRESS = 'http://127.0.0.1:9000'
PROXY_HOST = '127.0.0.1'
PROXY_PORT = 9000

# 浏览器类型 0:chrome 1:firefox
BROWSER_TYPE = 1


class SessionException(Exception):
    """
    会话异常类
    """

    def __init__(self, message):
        super().__init__(self)
        self.message = message

    def __str__(self):
        return self.message


class Crawler:

    def __init__(self):
        self.browser = None
        self.last_deal_order = time.time()
        self.last_update_time = 0

    def login(self, username, password):
        # print("切换密码登录")
        # if self.browser.find_element_by_id('J_QRCodeLogin').is_displayed():
        #     self.browser.find_element_by_id('J_Quick2Static').click()
        #     time.sleep(random.uniform(1.8, 2.5))

        print("输入用户名")
        username_input_element = self.browser.find_element_by_id('fm-login-id')
        username_input_element.clear()
        username_input_element.send_keys(username)
        time.sleep(random.uniform(1.2, 1.8))

        print("输入密码")
        password_input_element = self.browser.find_element_by_id("fm-login-password")
        password_input_element.clear()
        password_input_element.send_keys(password)
        time.sleep(random.uniform(1.2, 1.8))

        # print("模拟解锁")
        # if self.__lock_exist():
        #     self.__unlock()
        #
        # print("开始登录")


        # 不需要验证码，使用这一段代码
        time.sleep(3)
        self.__submit()


        # 需要验证码时，使用这一段代码
        # time.sleep(60)

        time.sleep(random.uniform(1.5, 2.5))

    def deal_order(self, order):
        print("订单号:" + order + " 开始处理")
        url = TB_TRADE_URL + order

        # 请求订单详情页面
        self.browser.get(url)

        # 判断是否需要登录
        if self.browser.current_url.startswith('https://login.taobao.com/member/login.jhtml'):
            print("开始登录")
            self.login(TB_USERNAME, TB_PASSWORD)

        # 判断是否订单详情页面
        if self.browser.current_url.startswith(TB_TRADE_URL):
            # 解析接收手机号
            # print("订单号:" + order[1] + " 开始解析手机号")
            try:
                # <div class="addr_and_note">
                # 接收手机：\n ****

                # note_element = self.browser.find_element_by_class_name("addr_and_note")
                # note_text = note_element.text;
                # match = re.match("接收手机：\\n(\d+)", note_text)
                dd = self.browser.find_element_by_xpath('//*[@id="J_TabView"]/div/div/div/div/dl[1]/dd')
                mobile = dd.text.strip()
                print("订单号:" + order + " 手机号:" + mobile)
                # 416548034531954182 为测试订单，只用于保持浏览器连接
                if order != '416548034531954182':
                    self.http_set_mobile(order, mobile)
                self.last_update_time = time.time()

            except NoSuchElementException as e:
                print("获取订单手机号失败", e)
        elif self.browser.current_url.startswith("https://login.taobao.com/member/login_unusual.htm"):
            # input("需要身份验证，请按任意键继续")
            print("login....")
            time.sleep(10)
        else:
            print("无法判断当前页面: " + self.browser.current_url)

        # print("获取cookie")
        # cookie= self._get_cookies();

    def start(self):
        print("初始化...")
        self.__init_browser()

        # 主循环
        while True:
            try:
                order = self.http_get_order()
                if order:
                    for i in order:
                        self.last_deal_order = time.time()
                        self.deal_order(i)
                if (RESTART_SECONDS > 0 and self.last_deal_order + RESTART_SECONDS <= time.time()):
                    print(time.strftime('%Y/%m/%d %H:%M:%S', time.localtime(time.time())) + " 长时间没有订单, 重新启动")
                    sys.exit(100)
                if (self.last_update_time + IDLE_SECONDS <= time.time()):
                    print(time.strftime('%Y/%m/%d %H:%M:%S', time.localtime(time.time())) + " 暂无订单, 保持浏览器连接")
                    self.deal_order('416548034531954182')  # 查询测试订单 416548034531954182
            except Exception as e:
                print("主循环异常:", e)
            time.sleep(3)

        print("程序执行完毕！")

    def __lock_exist(self):
        """
        判断是否存在滑动验证
        :return:
        """
        return self.__is_element_exist('#nc_1_wrapper') and self.browser.find_element_by_id(
            'nc_1_wrapper').is_displayed()

    def __unlock(self):
        """
        执行滑动解锁
        :return:
        """
        bar_element = self.browser.find_element_by_id('nc_1_n1z')

        # 剩余偏移量 (初始值: 380)
        remain_offset = 380

        # 获取移动轨迹
        track = self.get_track(remain_offset)
        print('滑动轨迹', track)

        # 拖动滑块
        self.move_to_gap(bar_element, track)

        time.sleep(random.uniform(1.2, 2))
        self.browser.get_screenshot_as_file('error.png')
        if self.__is_element_exist('.errloading > span'):
            error_message_element = self.browser.find_element_by_css_selector('.errloading > span')
            error_message = error_message_element.text
            self.browser.execute_script('noCaptcha.reset(1)')
            raise SessionException('滑动验证失败, message = ' + error_message)

    def get_track(self, distance):
        """
        根据偏移量获取移动轨迹
        :param distance: 偏移量
        :return: 移动轨迹
        """
        # 移动轨迹
        track = []
        # 当前位移
        current = 0
        # 减速阈值
        mid = distance * 4 / 5
        # 计算间隔
        t = random.uniform(4.8, 6.0)
        # 初速度
        v = 0

        while current < distance:
            if current < mid:
                # 加速度为正2
                a = random.uniform(1.8, 2.2)
            else:
                # 加速度为负3
                a = random.uniform(-3.0, -2.0)
            # 初速度v0
            v0 = v
            # 当前速度v = v0 + at
            v = v0 + a * t
            # 移动距离x = v0t + 1/2 * a * t^2
            move = v0 * t + 1 / 2 * a * t * t
            # 当前位移
            current += move
            # 加入轨迹
            track.append(round(move))
        return track

    def move_to_gap(self, slider, track):
        """
        拖动滑块到缺口处
        :param slider: 滑块
        :param track: 轨迹
        :return:
        """
        ActionChains(self.browser).click_and_hold(slider).perform()
        try:
            for x in track:
                ActionChains(self.browser).move_by_offset(xoffset=x, yoffset=0).perform()
        except Exception as e:
            print('拖动滑块异常', e)
        time.sleep(0.5)
        ActionChains(self.browser).release().perform()

    def __submit(self):
        """
        提交登录
        :return:
        """

        self.browser.find_element_by_xpath('//*[@id="login-form"]/div[4]/button').click()
        # .find_element_by_id('fm-login-password').click()
        time.sleep(0.5)
        if self.__is_element_exist("#J_Message"):
            error_message_element = self.browser.find_element_by_css_selector('#J_Message > p')
            error_message = error_message_element.text
            raise SessionException('登录出错, message = ' + error_message)

    def __init_browser(self):
        """
        初始化selenium浏览器
        :return:
        """

        if BROWSER_TYPE == 0:
            # 使用 chromedriver
            prefs = {"profile.managed_default_content_settings.images": 1}
            options = Options()
            # options.add_argument("--headless")
            options.add_argument('--disable-gpu')
            options.add_argument('--ignore-ssl-errors=true')
            options.add_argument('--proxy-server=' + PROXY_ADDRESS)
            options.add_argument('--log-level=3')
            options.add_argument('--no-sandbox')
            options.add_argument('disable-infobars')
            options.add_experimental_option("prefs", prefs)
            options.add_experimental_option('excludeSwitches', ['enable-automation'])
            self.browser = webdriver.Chrome(executable_path=CHROME_DRIVER, options=options)
            self.browser.implicitly_wait(3)
        else:
            options = FireFoxOptions()
            # linux 去除ui界面
            # options.add_argument('-headless')
            # 使用 geckodriver
            profile = webdriver.FirefoxProfile()

            profile.set_preference("browser.privatebrowsing.autostart", False)

            # http
            profile.set_preference('network.proxy.http', PROXY_HOST)
            profile.set_preference('network.proxy.http_port', PROXY_PORT)

            # 启用 https 代理
            profile.set_preference('network.proxy.type', 1)
            profile.set_preference('network.proxy.ssl', PROXY_HOST)
            profile.set_preference('network.proxy.ssl_port', PROXY_PORT)
            profile.update_preferences()
            # 添加下面两个参数防止 SSL 报错，参考自：https://www.guru99.com/ssl-certificate-error-handling-selenium.html#2

            profile.accept_untrusted_certs = True
            profile.assume_untrusted_cert_issuer = False

            self.browser = webdriver.Firefox(firefox_profile=profile,
                                             # executable_path="/usr/local/bin/geckodriver_mac", # os.getcwd() + "/geckodriver_linux64"
                                             # executable_path="/usr/local/bin/geckodriver_linux64",
                                             executable_path="C:\\Users\\Administrator\\AppData\\Local\\Programs\\Python\\Python37\\geckodriver.exe",
                                             service_log_path=os.getcwd() + "\\geckodriver.log",
                                             # service_log_path=os.getcwd() + "/geckodriver.log",
                                             firefox_options=options)

        self.browser.set_page_load_timeout(3)
        self.browser.maximize_window()
        # self.browser.get(TB_LOGIN_URL)
        return self.browser

    def __is_element_exist(self, selector):
        """
        检查是否存在指定元素
        :param selector:
        :return:
        """
        try:
            self.browser.find_element_by_css_selector(selector)
            return True
        except NoSuchElementException:
            return False

    def http_get_order(self):
        try:
            with urllib.request.urlopen(url=URL_GET) as f:
                return json.loads(f.read())
        except Exception as e:
            print("获取订单失败:", e)
            return

    def http_set_mobile(self, id, mobile):
        # data = { 'orderId': id, 'mobile': mobile }
        # req = urllib.request.Request(url = URL_SET, data = json.dumps(data).encode(encoding='UTF8'), method='POST')
        # req.add_header('Content-Type', 'application/json')
        # req.set_proxy(host='127.0.0.1:8888', type='http')
        req = urllib.request.Request(url=URL_SET + '?orderId=' + id + '&mobile=' + mobile)
        with urllib.request.urlopen(req) as f:
            print('订单号:' + id + ' 手机号:' + mobile + ' 保存结果:', f.read())
            return


if __name__ == '__main__':
    Crawler().start()
