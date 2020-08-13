# -*- coding: utf-8 -*-

from wsgiref.simple_server import make_server
from healthcheck import application

httpd=make_server('', 7377, application)

print('server http on port 7377')

httpd.serve_forever()