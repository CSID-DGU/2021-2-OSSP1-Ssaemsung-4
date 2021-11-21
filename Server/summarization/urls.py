from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static

from . import views

app_name = "summarization"

urlpatterns = [
    # two paths: with or without given audio
    path("", views.summarization, name="index"),
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
