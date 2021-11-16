from django.db import models
# Create your models here.


class Addresses(models.Model):
    created = models.DateTimeField(auto_now_add=True)
    end_time = models.CharField(max_length=15)
    start_time = models.CharField(max_length=15)
    speaker = models.TextField()

    class Meta:
        ordering = ['created']
