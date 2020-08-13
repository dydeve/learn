# -*- coding:utf-8 -*-


def application(environ, start_response):
    method = environ['REQUEST_METHOD']
    path = environ['PATH_INFO']

    start_response('200 OK', [('Content-Type', 'text/plain;charset=UTF-8')])
    body = '''* xdcs.default.healthCheck: OK
      healthCheck success'''
    return [body.encode('utf-8')]
  #   if method == 'GET' and path == '/openapi-tmall-crawler/healthcheck':
  #       start_response('200 OK', [('Content-Type', 'text/plain;charset=UTF-8')])
  #       body = '''* xdcs.default.healthCheck: OK
  # healthCheck success'''
  #       return [body.encode('utf-8')]
  #   else:
  #       start_response('404 NOT FOUND', [('Content-Type', 'text/plain;charset=UTF-8')])
  #       body = '''404'''
  #       return [body.encode('utf-8')]

