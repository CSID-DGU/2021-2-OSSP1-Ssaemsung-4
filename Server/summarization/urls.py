from django.urls import path, include
from django.conf import settings
from django.conf.urls.static import static

from . import views

app_name = "summarization"

urlpatterns = [
    path("evaluation", views.evaluation, name="index"),
    path("", views.summarization, name="index")
] + static(settings.MEDIA_URL, document_root=settings.MEDIA_ROOT)
