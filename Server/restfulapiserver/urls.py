from django.conf.urls.static import static
from rest_framework.routers import SimpleRouter
from django.urls import path, include
from django.contrib.auth.models import User
from rest_framework import routers, serializers, viewsets
from django.conf.urls import url, include
from addresses import views
from django.conf import settings

urlpatterns = [
    url(r'^addresses', views.address_list),
    url(r'api-auth/', include('rest_framework.urls', namespace='rest_framework')),
    url(r'summarization/', include('summarization.urls')),

]
