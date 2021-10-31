from django.urls import path, include
from django.contrib.auth.models import User
from rest_framework import routers, serializers, viewsets
from django.conf.urls import url, include
from addresses import views


urlpatterns = [
    url(r'^addresses', views.address_list),
    url(r'api-auth/', include('rest_framework.urls', namespace='rest_framework'))
]
